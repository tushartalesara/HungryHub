package com.dev.controllers;

import com.dev.dao.*;
import com.dev.models.*;
import com.dev.services.AuthenticatedUser;
import com.dev.services.CloudinaryImageService;
import com.dev.utilities.Delivery;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import kotlin.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequestMapping("/driver")
@Controller
public class DriverController {

    AuthenticatedUser authenticatedUser;
    CloudinaryImageService cloudinaryImageService;
    DriverJdbcDao driverJdbcDao;
    PrivilegeJdbcDao privilegeJdbcDao;
    UserJdbcDao userJdbcDao;
    @Autowired
    private OrderJdbcDao orderJdbcDao;
    @Autowired
    private CartItemsJdbcDao cartItemsJdbcDao;
    @Autowired
    private ItemJdbcDao itemJdbcDao;
    @Autowired
    private CartJdbcDao cartJdbcDao;
    @Autowired
    private UserAddressJdbcDao userAddressJdbcDao;

    @Autowired
    public DriverController(AuthenticatedUser authenticatedUser, CloudinaryImageService cloudinaryImageService, DriverJdbcDao driverJdbcDao, PrivilegeJdbcDao privilegeJdbcDao, UserJdbcDao userJdbcDao) {
        this.authenticatedUser = authenticatedUser;
        this.cloudinaryImageService = cloudinaryImageService;
        this.driverJdbcDao = driverJdbcDao;
        this.privilegeJdbcDao = privilegeJdbcDao;
        this.userJdbcDao = userJdbcDao;
    }
    @GetMapping("/register")
    public String registerDriver(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = authenticatedUser.getAuthenticatedUser(userDetails);
        model.addAttribute("user", user);
        model.addAttribute("driver", new Driver());
        return "driver_register";
    }

    @PostMapping("/register")
    public String registerDriverPost(@AuthenticationPrincipal UserDetails userDetails, Driver driver, Model model, @RequestParam("image") MultipartFile imageFile) {
        Map fileName = cloudinaryImageService.upload(imageFile);
        String secureUrl = (String) fileName.get("secure_url");
        driver.setImageUrl(secureUrl);
        User user = authenticatedUser.getAuthenticatedUser(userDetails);
        driver.setUserId(user.getUserId());
        if (!user.hasRole("DRIVER")){
            Privilege privilege = privilegeJdbcDao.getPrivilegeByName("ROLE_DRIVER");
            user.getPrivileges().add(privilege);
            userJdbcDao.updateUser(user);
        }
        try {
            driverJdbcDao.createDriver(driver);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "driver_register";
        }
        return "redirect:/driver/dashboard";
    }

    @GetMapping("/dashboard")
    public String driverDashboard(Model model, @AuthenticationPrincipal UserDetails userDetails){
        User user = authenticatedUser.getAuthenticatedUser(userDetails);
        model.addAttribute("User", user);
        Order currentOrder = orderJdbcDao.searchOrderByDriver(driverJdbcDao.getDriverByUserId(user.getUserId()).getDriverId());
        model.addAttribute("driver",driverJdbcDao.getDriverByUserId(user.getUserId()));
        if(currentOrder==null){
            model.addAttribute("show",false);
            return "driver_dashboard";
        }
        else{
            model.addAttribute("show",true);
        }
        model.addAttribute("currentDelivery",orderJdbcDao.searchOrderByDriver(driverJdbcDao.getDriverByUserId(user.getUserId()).getDriverId()));
        List<CartItems> cartItems = cartItemsJdbcDao.getCartItemsByCartId(orderJdbcDao.searchOrderByDriver(driverJdbcDao.getDriverByUserId(user.getUserId()).getDriverId()).getCartId());
        int orderstatus = orderJdbcDao.searchOrderByDriver(driverJdbcDao.getDriverByUserId(user.getUserId()).getDriverId()).getOrderStatus();
        model.addAttribute("orderstatus",orderstatus);
        List<Pair<Item,CartItems>> pairs = new ArrayList<>();
        for(CartItems i:cartItems){
            Pair<Item,CartItems> pair = new Pair<Item,CartItems>(itemJdbcDao.getItemById(i.getItemId()),i);
            pairs.add(pair);
        }
        model.addAttribute("pairs",pairs);
        model.addAttribute("buyer_name",userJdbcDao.getUserById(cartJdbcDao.getCartById(orderJdbcDao.searchOrderByDriver(driverJdbcDao.getDriverByUserId(user.getUserId()).getDriverId()).getCartId()).getUserId()).getFullName());
        model.addAttribute("buyer_address",userAddressJdbcDao.getUserAddressById(cartJdbcDao.getCartById(orderJdbcDao.searchOrderByDriver(driverJdbcDao.getDriverByUserId(user.getUserId()).getDriverId()).getCartId()).getAddressId()).getStreetAddress());
        model.addAttribute("pincode",userAddressJdbcDao.getUserAddressById(cartJdbcDao.getCartById(orderJdbcDao.searchOrderByDriver(driverJdbcDao.getDriverByUserId(user.getUserId()).getDriverId()).getCartId()).getAddressId()).getPincode());
        return "driver_dashboard";
    }
    @GetMapping("/updateStatus/{pincode}")
    public String updateStatus(@PathVariable("pincode")Long pincode, Model model, @AuthenticationPrincipal UserDetails userDetails){
        User user = authenticatedUser.getAuthenticatedUser(userDetails);
        orderJdbcDao.updateOrderStatusByDriverId(driverJdbcDao.getDriverByUserId(user.getUserId()).getDriverId(),3);
        driverJdbcDao.updateDriverPincode(user.getUserId(), pincode);
        model.addAttribute("show",false);
        return  "redirect:/driver/dashboard";
    }
    @GetMapping("/updateStatus/")
    public String updateStatusToOne(Model model, @AuthenticationPrincipal UserDetails userDetails){
        User user = authenticatedUser.getAuthenticatedUser(userDetails);
        orderJdbcDao.updateOrderStatusByDriverId(driverJdbcDao.getDriverByUserId(user.getUserId()).getDriverId(),2);
//        driverJdbcDao.updateDriverPincode(user.getUserId(), pincode);
        model.addAttribute("show",false);
        return  "redirect:/driver/dashboard";
    }

    @GetMapping("/deliveries")
    public String driverDeliveries(Model model,@AuthenticationPrincipal UserDetails userDetails){
        User user = authenticatedUser.getAuthenticatedUser(userDetails);
        model.addAttribute("User", user);
        List<Order> orders = orderJdbcDao.searchPreviousDeliveredOrdersByDriverId(driverJdbcDao.getDriverByUserId(user.getUserId()).getDriverId());
        if(orders.isEmpty()){
            model.addAttribute("show",false);
            return "driver_delivery";
        }
        List<Delivery> deliveries = new ArrayList<>();
        for(Order order:orders){
            Delivery delivery = new Delivery();
            delivery.setBuyerName(userJdbcDao.getUserById(cartJdbcDao.getCartById(order.getCartId()).getUserId()).getFullName());
            delivery.setBuyerAddress(userAddressJdbcDao.getUserAddressById(cartJdbcDao.getCartById(order.getCartId()).getAddressId()).getStreetAddress());
            List<Pair<Item,CartItems>> pairs = new ArrayList<>();
            List<CartItems> cartItems = cartItemsJdbcDao.getCartItemsByCartId(order.getCartId());
            for(CartItems i:cartItems){
                Pair<Item,CartItems> pair = new Pair<Item,CartItems>(itemJdbcDao.getItemById(i.getItemId()),i);
                pairs.add(pair);
            }
            delivery.setItems(pairs);
            delivery.setDeliveryTime(order.getDeliveryTime());
            deliveries.add(delivery);
        }
        model.addAttribute("deliveries",deliveries);
        model.addAttribute("show",true);
        return "driver_delivery";
    }

    @GetMapping("/changeStatus/{query}")
    public String changeDriverStatus(@PathVariable("query")int status,@AuthenticationPrincipal UserDetails userDetails ){
        User user = authenticatedUser.getAuthenticatedUser(userDetails);
        driverJdbcDao.updateDriverStatus(status,user.getUserId());
        return "redirect:/driver/dashboard";
    }
}
