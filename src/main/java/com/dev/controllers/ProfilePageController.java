package com.dev.controllers;

import com.dev.dao.UserJdbcDao;
import com.dev.dao.CartItemsJdbcDao;
import com.dev.dao.ItemJdbcDao;
import com.dev.dao.OrderJdbcDao;
import com.dev.dao.UserAddressJdbcDao;
import com.dev.models.User;
import com.dev.models.Item;
import com.dev.models.CartItems;

import com.dev.models.Order;
import com.dev.models.UserAddress;
import com.dev.services.AuthenticatedUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
// import java.util.List;

@Controller
public class ProfilePageController {

    private UserJdbcDao userJdbcDao;
    private OrderJdbcDao OrderJdbcDao;
    private UserAddressJdbcDao userAddressJdbcDao;
    private AuthenticatedUser authenticatedUser;
    private CartItemsJdbcDao CartItemsJdbcDao;
    private ItemJdbcDao ItemJdbcDao;

    @Autowired
    public ProfilePageController(ItemJdbcDao ItemJdbcDao,CartItemsJdbcDao CartItemsJdbcDao,UserAddressJdbcDao userAddressJdbcDao,OrderJdbcDao OrderJdbcDao,UserJdbcDao userJdbcDao,AuthenticatedUser authenticatedUser) {
        this.userJdbcDao = userJdbcDao;
        this.authenticatedUser = authenticatedUser;
        this.OrderJdbcDao  = OrderJdbcDao;
        this.CartItemsJdbcDao = CartItemsJdbcDao;
        this.userAddressJdbcDao = userAddressJdbcDao;
        this.ItemJdbcDao = ItemJdbcDao;
    }

    @GetMapping("/profile")
    public String profileUser(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = authenticatedUser.getAuthenticatedUser(userDetails);
        if (user==null){// cant add. user not logged in. Actually should be redirected to login page.
            return "redirect:/login";
        }

        model.addAttribute("userAddresses",userAddressJdbcDao.getUserAddressesByUserId(user.getUserId()) );
        model.addAttribute("user",userJdbcDao.getUserById(user.getUserId()) );
        List<Order> orders = OrderJdbcDao.getOrderByUserId(user.getUserId());

        Map<Order, Map<Item,Long>> orderCartItemsMap = new HashMap<>();
        for (Order order : orders) {
            List<CartItems> cartItems = CartItemsJdbcDao.getCartItemsByCartId(order.getCartId());

            Map<Item, Long> cartItemsQuantityMap = new HashMap<>();
            for(CartItems cartItem:cartItems){
                Item item = ItemJdbcDao.getItemById(cartItem.getItemId());

                cartItemsQuantityMap.put(item,cartItem.getQuantity() );
            }

            orderCartItemsMap.put(order, cartItemsQuantityMap);

        }
        model.addAttribute("orders", orderCartItemsMap);

        return "profilepage";
    }

    @PostMapping(value = "/profile/deleteAddress/{id}")
    public String deleteUserAddress(@PathVariable("id") long id) {
        userAddressJdbcDao.deleteUserAddress(id);
        return "redirect:/profile";
    }

    @PostMapping("/profile/addAddress")
    public String createUserAddress(UserAddress userAddress, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = authenticatedUser.getAuthenticatedUser(userDetails);
            userAddress.setUserId(user.getUserId());
            userAddressJdbcDao.createUserAddress(userAddress);
            return "redirect:/profile";
        }
        catch (Exception e) {
            return "redirect:/profile";
        }
    }
    @PostMapping("/profile/updateAddress")
    public String updateAddress(UserAddress userAddress, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = authenticatedUser.getAuthenticatedUser(userDetails);
            userAddress.setUserId(user.getUserId());
            userAddressJdbcDao.updateUserAddress(userAddress);
            return "redirect:/profile";
        }
        catch (Exception e) {
            return "redirect:/profile";
        }

        //  return "redirect:/profile";
    }

    @PostMapping("/profile/updateUser")
    public String updateUser(User user , @AuthenticationPrincipal UserDetails userDetails) {

        try {
            User user1 = authenticatedUser.getAuthenticatedUser(userDetails);
            user.setUserId(user1.getUserId());
            user.setUserPassword(user1.getUserPassword());
            userJdbcDao.updateUser(user);
            return "redirect:/profile";
        }
        catch (Exception e) {
            return "redirect:/profile";
        }



        // return "redirect:/profile";
    }

    @PostMapping("/profile/updatePassword")
    public String updateUserPassword(User user , @AuthenticationPrincipal UserDetails userDetails, @RequestParam("currentPassword") String currentPassword,
                                     @RequestParam("userPassword") String userPassword, @RequestParam("repeateUserPassword") String repeateUserPassword) {

        try {
            User user1 = authenticatedUser.getAuthenticatedUser(userDetails);
            if (!user1.getUserPassword().equals(currentPassword)) {
                throw new IllegalArgumentException("Current password is incorrect");
            }
            if (!userPassword.equals(repeateUserPassword)) {
                throw new IllegalArgumentException("New password and repeated password do not match");
            }
            user.setEmail(user1.getEmail());
            user.setFirstName(user1.getFirstName());
            user.setLastName(user1.getLastName());
            user.setPhoneNumber(user1.getPhoneNumber());
            user.setUserId(user1.getUserId());

            userJdbcDao.updateUser(user);
            return "redirect:/profile";
        }
        catch (Exception e) {
            return "redirect:/profile";
        }



        // return "redirect:/profile";
    }

    @RequestMapping("/profile/orders")
    public String profileUserOrders(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = authenticatedUser.getAuthenticatedUser(userDetails);
        if (user==null){// cant add. user not logged in. Actually should be redirected to login page.
            return "redirect:/login";
        }
        List<Order> orders = OrderJdbcDao.getOrderByUserId(user.getUserId());
        Map<Order, Map<Item,Long>> orderCartItemsMap = new HashMap<>();
        //  for (Order order : orders) {
        //     List<CartItems> cartItems = CartItemsJdbcDao.getCartItemsByCartId(order.getCartId());
        //     Map<Item, Long> cartItemsQuantityMap = new HashMap<>();
        //     for(CartItems cartItem:cartItems){
        //                 Item item = ItemJdbcDao.getItemById(cartItem.getCartId());
        //                 cartItemsQuantityMap.put(item,cartItem.getQuantity() );
        //     }
        //     orderCartItemsMap.put(order, cartItemsQuantityMap);

        // }
        model.addAttribute("orders", orderCartItemsMap);

        return "redirect:/profile";
    }



}