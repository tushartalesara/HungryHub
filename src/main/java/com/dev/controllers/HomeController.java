package com.dev.controllers;

import com.dev.dao.ItemJdbcDao;
import com.dev.dao.PromocodeJdbcDao;
import com.dev.dao.RestaurantJdbcDao;
import com.dev.models.User;
import com.dev.services.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    private final AuthenticatedUser authenticatedUser;
    private final RestaurantJdbcDao restaurantJdbcDao;
    private final ItemJdbcDao itemJdbcDao;
    private final PromocodeJdbcDao promocodeJdbcDao;

    @Autowired
    public HomeController(ItemJdbcDao itemJdbcDao, AuthenticatedUser authenticatedUser, RestaurantJdbcDao restaurantJdbcDao, PromocodeJdbcDao promocodeJdbcDao) {
        this.itemJdbcDao = itemJdbcDao;
        this.authenticatedUser = authenticatedUser;
        this.restaurantJdbcDao = restaurantJdbcDao;
        this.promocodeJdbcDao = promocodeJdbcDao;
    }

    @RequestMapping(value={"/","/home"})
    public String home(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = authenticatedUser.getAuthenticatedUser(userDetails);
        model.addAttribute("User", user);
        model.addAttribute("restaurants", restaurantJdbcDao.getAllRestaurants());
        model.addAttribute("items", itemJdbcDao.getAllItems());
        model.addAttribute("promocodes", promocodeJdbcDao.getAllPromocodes());
        return "index";
    }
}
