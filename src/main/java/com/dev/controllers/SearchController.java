package com.dev.controllers;

import com.dev.dao.ItemJdbcDao;
import com.dev.dao.RestaurantJdbcDao;
import com.dev.models.Restaurant;
import com.dev.models.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {

    @Autowired
    private RestaurantJdbcDao restaurantJdbcDao;
    @Autowired
    private ItemJdbcDao itemJdbcDao;
    // search handler
    @GetMapping("search/{query}")
    public ResponseEntity<?> search(@PathVariable("query") String query){
        String[] queries = query.split("_");
        // SQL injection is not possible because jdbc template uses prepared statements and it automatically takes care of escaping those.
        List<Restaurant> restaurants = this.restaurantJdbcDao.getRestaurantWithNameLikeAndCity(queries[0],queries[1]);
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("searchItem/{query}")
    public ResponseEntity<?> searchItem(@PathVariable("query") String query){
        String[] queries = query.split("_");
        List<Item> items = this.itemJdbcDao.getItemsWithNameLikeAndCity(queries[0],queries[1]);
        return ResponseEntity.ok(items);
    }
}