package com.dev.dao;

import com.dev.models.Restaurant;
// import com.dev.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class RestaurantJdbcDao {
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public RestaurantJdbcDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    private final RowMapper<Restaurant> restaurantRowMapper = (rs, rowNum) -> {
        Restaurant restaurant = new Restaurant();
        restaurant.setUserId(rs.getLong("UserId"));
        restaurant.setRestaurantId(rs.getLong("RestaurantId"));
        restaurant.setRestaurantName(rs.getString("RestaurantName"));
        restaurant.setRestaurantDescription(rs.getString("RestaurantDescription"));
        restaurant.setStreetAddress(rs.getString("StreetAddress"));
        restaurant.setPhoneNumber(rs.getString("PhoneNumber"));
        restaurant.setPincode(rs.getLong("Pincode"));
        restaurant.setStartTime(rs.getTime("StartTime"));
        restaurant.setEndTime(rs.getTime("EndTime"));
        restaurant.setRating(rs.getDouble("Rating"));
        restaurant.setNumberOfUsersRated(rs.getLong("NumberOfUsersRated"));
        restaurant.setIsVegeterian(rs.getBoolean("IsVegeterian"));
        restaurant.setImageUrl(rs.getString("ImageUrl"));
        return restaurant;
    };

    private HashMap<String, Object> getRestaurantMap(Restaurant restaurant) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("UserId", restaurant.getUserId());
        map.put("RestaurantId", restaurant.getRestaurantId());
        map.put("RestaurantName", restaurant.getRestaurantName());
        map.put("RestaurantDescription", restaurant.getRestaurantDescription());
        map.put("StreetAddress", restaurant.getStreetAddress());
        map.put("PhoneNumber", restaurant.getPhoneNumber());
        map.put("Pincode", restaurant.getPincode());
        map.put("StartTime", restaurant.getStartTime());
        map.put("EndTime", restaurant.getEndTime());
        map.put("Rating", restaurant.getRating());
        map.put("NumberOfUsersRated", restaurant.getNumberOfUsersRated());
        map.put("IsVegeterian", restaurant.getIsVegeterian());
        map.put("ImageUrl", restaurant.getImageUrl());
        return map;
    }
    @CacheEvict(value="restaurant_cities", allEntries=true)
    public void createRestaurant(Restaurant restaurant) {
        String sql = "INSERT INTO Restaurant (RestaurantId, UserId, RestaurantName, RestaurantDescription, StreetAddress, PhoneNumber, Pincode, StartTime, EndTime, Rating, NumberOfUsersRated, IsVegeterian, ImageUrl) VALUES (:RestaurantId, :UserId,:RestaurantName, :RestaurantDescription, :StreetAddress, :PhoneNumber, :Pincode, :StartTime, :EndTime, :Rating, :NumberOfUsersRated, :IsVegeterian, :ImageUrl)";
        HashMap<String, Object> params = getRestaurantMap(restaurant);
        namedParameterJdbcTemplate.update(sql, params);
    }

    public Restaurant queryForRestaurant(String sql, HashMap<String, Object> params) {
        try{
            return namedParameterJdbcTemplate.queryForObject(sql, params, restaurantRowMapper);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Restaurant getRestaurantById(long id){
        String sql = "SELECT * FROM Restaurant WHERE RestaurantId = :RestaurantId";
        HashMap<String, Object> params = new HashMap<>();
        params.put("RestaurantId", id);
        return queryForRestaurant(sql, params);
    }

    public List<Restaurant> getAllRestaurants() {
        String sql = "SELECT * FROM Restaurant";
        return namedParameterJdbcTemplate.query(sql, restaurantRowMapper);
    }

    public Restaurant getRestaurantWithName(String name) {
        String sql = "SELECT * FROM Restaurant WHERE RestaurantName = :RestaurantName";
        HashMap<String, Object> params = new HashMap<>();
        params.put("RestaurantName", name);
        return queryForRestaurant(sql, params);
    }

    public List<Restaurant> getRestaurantWithNameLike(String name) {
        String sql = "SELECT * FROM Restaurant WHERE RestaurantName LIKE :RestaurantName";
        HashMap<String, Object> params = new HashMap<>();
        params.put("RestaurantName", "%"+name+"%");
        return namedParameterJdbcTemplate.query(sql, params, restaurantRowMapper);
    }

    public List<Restaurant> getRestaurantByUserId(long id) {
        String sql = "SELECT * FROM Restaurant WHERE UserId = :UserId";
        HashMap<String, Object> params = new HashMap<>();
        params.put("UserId", id);
        return namedParameterJdbcTemplate.query(sql, params, restaurantRowMapper);
    }
    @Cacheable(value="restaurant_cities", key="{#name, #city}")
    public List<Restaurant> getRestaurantWithNameLikeAndCity(String name,String city){
        String sql = "SELECT * FROM Restaurant WHERE RestaurantName LIKE :RestaurantName AND Pincode IN (SELECT Pincode FROM pincodeinfo WHERE City=:City)";
        HashMap<String, Object> params = new HashMap<>();
        params.put("RestaurantName", "%"+name+"%");
        params.put("City",city);
        return namedParameterJdbcTemplate.query(sql, params, restaurantRowMapper);
    }

    public void updateRestaurant(Restaurant restaurant){
        try{
            String sql = "UPDATE Restaurant SET RestaurantName = :RestaurantName, RestaurantDescription = :RestaurantDescription, StreetAddress = :StreetAddress, PhoneNumber = :PhoneNumber, Pincode = :Pincode, StartTime = :StartTime, EndTime = :EndTime, Rating = :Rating, NumberOfUsersRated = :NumberOfUsersRated, IsVegeterian = :IsVegeterian, ImageUrl = :ImageUrl WHERE RestaurantId = :RestaurantId";
            HashMap<String, Object> params = getRestaurantMap(restaurant);
            namedParameterJdbcTemplate.update(sql, params);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public List<Restaurant> getRestaurantWithHighestRatingInCity(String city) {
        String sql = "SELECT * FROM Restaurant WHERE Pincode IN (SELECT Pincode FROM pincodeinfo WHERE City = :City) ORDER BY Rating LIMIT 5";
        HashMap<String, Object> params = new HashMap<>();
        params.put("City",city);
        return namedParameterJdbcTemplate.query(sql, params , restaurantRowMapper);
    }
}
