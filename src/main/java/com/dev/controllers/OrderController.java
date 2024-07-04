package com.dev.controllers;

import com.dev.dao.*;
import com.dev.models.*;
import com.dev.services.AuthenticatedUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {
    AuthenticatedUser authenticatedUser;
    OrderJdbcDao orderJdbcDao;
    CartJdbcDao cartJdbcDao;
    CartItemsJdbcDao cartItemsJdbcDao;
    DriverJdbcDao driverJdbcDao;
    RestaurantJdbcDao restaurantJdbcDao;
    ItemJdbcDao itemsJdbcDao;
    UserJdbcDao userJdbcDao;

    @Autowired
    public OrderController(AuthenticatedUser authenticatedUser, OrderJdbcDao orderJdbcDao, CartJdbcDao cartJdbcDao, CartItemsJdbcDao cartItemsJdbcDao, DriverJdbcDao driverJdbcDao, RestaurantJdbcDao restaurantJdbcDao, ItemJdbcDao itemsJdbcDao, UserJdbcDao userJdbcDao) {
        this.authenticatedUser = authenticatedUser;
        this.orderJdbcDao = orderJdbcDao;
        this.cartJdbcDao = cartJdbcDao;
        this.cartItemsJdbcDao = cartItemsJdbcDao;
        this.driverJdbcDao = driverJdbcDao;
        this.restaurantJdbcDao = restaurantJdbcDao;
        this.itemsJdbcDao = itemsJdbcDao;
        this.userJdbcDao = userJdbcDao;
    }
    @GetMapping("/my")
    public String myOrders(@AuthenticationPrincipal UserDetails userDetails){
        User user = authenticatedUser.getAuthenticatedUser(userDetails);

        return "myorders";
    }

    @GetMapping("/{orderId}")
    public String orderDetails(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("orderId") long orderId, Model model){
        User user = authenticatedUser.getAuthenticatedUser(userDetails);
        Order order = orderJdbcDao.getOrderById(orderId);
        if (order==null) return "redirect:/orders/my";
        Cart cart = cartJdbcDao.getCartById(order.getCartId());
        if (cart.getUserId()!=user.getUserId()) return "redirect:/orders/my";
        model.addAttribute("order", order);
        model.addAttribute("cart", cart);
        List<CartItems> cartItems = cartItemsJdbcDao.getCartItemsByCartId(cart.getCartId());
        HashMap<Long, Item> items = new HashMap<>();
        for (CartItems cartItem : cartItems) items.put(cartItem.getItemId(), itemsJdbcDao.getItemById(cartItem.getItemId()));
        model.addAttribute("items", items);
        model.addAttribute("cartItems", cartItems);
        Driver driver = driverJdbcDao.getDriverById(order.getDriverId());
        User driverDetails = userJdbcDao.getUserById(driver.getUserId());
        model.addAttribute("driver", driver);
        model.addAttribute("driverDetails", driverDetails);
        model.addAttribute("restaurant", restaurantJdbcDao.getRestaurantById(cart.getRestaurantId()));
        return "site/order_details";
    }

    @GetMapping("/rate/{orderid}")
    public String rateOrder(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("orderid") long orderId, Model model){
        User user = authenticatedUser.getAuthenticatedUser(userDetails);
        Order order = orderJdbcDao.getOrderById(orderId);
        if (order==null) return "redirect:/orders/my";
        if (order.getIsRated()){
            model.addAttribute("AlreadyRated", true);
            return "site/rate_order";
        }
        model.addAttribute("AlreadyRated", false);
        Cart cart = cartJdbcDao.getCartById(order.getCartId());
        if (cart.getUserId()!=user.getUserId()) return "redirect:/orders/my";
        model.addAttribute("order", order);
        model.addAttribute("cart", cart);
        List<CartItems> cartItems = cartItemsJdbcDao.getCartItemsByCartId(cart.getCartId());
        HashMap<Long, Item> items = new HashMap<>();
        for (CartItems cartItem : cartItems) items.put(cartItem.getItemId(), itemsJdbcDao.getItemById(cartItem.getItemId()));
        model.addAttribute("items", items);
        model.addAttribute("cartItems", cartItems);
        Driver driver = driverJdbcDao.getDriverById(order.getDriverId());
        User driverDetails = userJdbcDao.getUserById(driver.getUserId());
        model.addAttribute("driver", driver);
        model.addAttribute("driverDetails", driverDetails);
        model.addAttribute("restaurant", restaurantJdbcDao.getRestaurantById(cart.getRestaurantId()));
        return "site/rate_order";
    }

    @PostMapping("/rate/{orderid}")
    public String rateOrder(@AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request, @PathVariable("orderid") long orderId){
        System.out.println(request);
        User user = authenticatedUser.getAuthenticatedUser(userDetails);
        Order order = orderJdbcDao.getOrderById(orderId);
        if (order==null) return "redirect:/orders/my";
        if (order.getIsRated()) return "redirect:/orders/my";
        Cart cart = cartJdbcDao.getCartById(order.getCartId());
        if (cart.getUserId()!=user.getUserId()) return "redirect:/orders/my";
        List<CartItems> cartItems = cartItemsJdbcDao.getCartItemsByCartId(cart.getCartId());
        for (var cartItem : cartItems){
            long itemId = cartItem.getItemId(), rating = Long.parseLong(request.getParameter("rate-"+itemId));
            Item item = itemsJdbcDao.getItemById(itemId);
            item.setRating((item.getRating()*item.getNumberOfUsersRated()+rating)/(item.getNumberOfUsersRated()+1));
            item.setNumberOfUsersRated(item.getNumberOfUsersRated()+1);
            itemsJdbcDao.updateItem(item);
        }
        Driver driver = driverJdbcDao.getDriverById(order.getDriverId());
        driver.setRating((driver.getRating()*driver.getNumberOfUsersRated()+Long.parseLong(request.getParameter("rate-driver")))/(driver.getNumberOfUsersRated()+1));
        driver.setNumberOfUsersRated(driver.getNumberOfUsersRated()+1);
        driverJdbcDao.updateDriver(driver);
        Restaurant restaurant = restaurantJdbcDao.getRestaurantById(cart.getRestaurantId());
        restaurant.setRating((restaurant.getRating()*restaurant.getNumberOfUsersRated()+Long.parseLong(request.getParameter("rate-restaurant")))/(restaurant.getNumberOfUsersRated()+1));
        restaurant.setNumberOfUsersRated(restaurant.getNumberOfUsersRated()+1);
        restaurantJdbcDao.updateRestaurant(restaurant);
        order.setIsRated(true);
        orderJdbcDao.updateOrder(order);
        System.out.println("driver rating updated to "+ driver.getRating());
        return "redirect:/order/"+orderId;
    }
}










