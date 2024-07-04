package com.dev.services;

import com.dev.dao.OrderJdbcDao;
import com.dev.dao.RestaurantJdbcDao;
import com.dev.models.*;
import com.dev.utilities.Total;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class OrderService {

    DriverService driverService;
    RestaurantJdbcDao restaurantJdbcDao;
    OrderJdbcDao orderJdbcDao;

    @Autowired
    public OrderService(DriverService driverService, RestaurantJdbcDao restaurantJdbcDao, OrderJdbcDao orderJdbcDao) {
        this.driverService = driverService;
        this.restaurantJdbcDao = restaurantJdbcDao;
        this.orderJdbcDao = orderJdbcDao;
    }
    public long createOrder(User user, Cart cart, Total total){
        Restaurant restaurant = restaurantJdbcDao.getRestaurantById(cart.getRestaurantId());
        Driver activeDriver = driverService.availableDriverByPincode(restaurant.getPincode());
        if (activeDriver==null){
            // no nearby driver available!
            activeDriver = driverService.availableDriverByCity(restaurant.getPincode());
        }
        if (activeDriver==null) return -1;
        driverService.setDriverActive(activeDriver.getDriverId());
        Order order = new Order(1, cart.getCartId(), activeDriver.getDriverId(), "url", new Timestamp(System.currentTimeMillis()), total.getFinalValue());
        orderJdbcDao.createOrder(order);
        System.out.println("Created order successfully");
        return 0;
    }
}
