package com.dev.dao;

import com.dev.models.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import java.util.List;

import java.util.HashMap;

@Component
public class ItemJdbcDao {
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public ItemJdbcDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    private final RowMapper<Item> userRowMapper = (rs, rowNum) -> {
        Item item = new Item();
        item.setItemId(rs.getLong("ItemId"));
        item.setItemName(rs.getString("ItemName"));
        item.setRestaurantId(rs.getLong("RestaurantId"));
        item.setIsVegeterian(rs.getBoolean("IsVegeterian"));
        item.setPrice(rs.getLong("Price"));
        item.setItemDescription(rs.getString("ItemDescription"));
        item.setAvailable(rs.getBoolean("IsAvailable"));
        item.setRating(rs.getDouble("Rating"));
        item.setNumberOfUsersRated(rs.getLong("NumberOfUsersRated"));
        item.setImageUrl(rs.getString("ImageUrl"));
        return item;
    };

    private HashMap<String, Object> getItemMap(Item item) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("ItemId", item.getItemId());
        map.put("ItemName", item.getItemName());
        map.put("RestaurantId", item.getRestaurantId());
        map.put("IsVegeterian", item.getIsVegeterian());
        map.put("Price", item.getPrice());
        map.put("ItemDescription", item.getItemDescription());
        map.put("IsAvailable", item.isAvailable());
        map.put("Rating", item.getRating());
        map.put("NumberOfUsersRated", item.getNumberOfUsersRated());
        map.put("ImageUrl", item.getImageUrl());
        return map;
    }

    public Item queryForItem(String sql, HashMap<String, Object> params){
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, params, userRowMapper);
        }
        catch (Exception e){
            return null;
        }
    }

    public void createItem(Item item) {
        String sql = "INSERT INTO Items (ItemId, ItemName, RestaurantId, IsVegeterian, Price, ItemDescription, IsAvailable, Rating, NumberOfUsersRated, ImageUrl) VALUES (:ItemId, :ItemName, :RestaurantId, :IsVegeterian, :Price, :ItemDescription, :IsAvailable, :Rating, :NumberOfUsersRated, :ImageUrl)";
        HashMap<String, Object> params = getItemMap(item);
        namedParameterJdbcTemplate.update(sql, params);
    }

    public Item getItemById(long id){
        String sql = "SELECT * FROM Items WHERE ItemId = :ItemId";
        HashMap<String, Object> params = new HashMap<>();
        params.put("ItemId", id);
        return queryForItem(sql, params);
    }

    public Item getItemByOrderId(long id){
        String sql = "SELECT * FROM Items WHERE ItemId = :ItemId";
        HashMap<String, Object> params = new HashMap<>();
        params.put("ItemId", id);
        return queryForItem(sql, params);
    }

    public List<Item> getAllItemsbyRestaurantId(long id) {
        String sql = "SELECT * FROM Items WHERE RestaurantId = :RestaurantId";
        HashMap<String, Object> params = new HashMap<>();
        params.put("RestaurantId", id);
        return namedParameterJdbcTemplate.query(sql, params, userRowMapper);
    }

    public List<Item> getItemsWithNameLikeAndCity(String name,String city) {
        String sql = "SELECT * FROM Items WHERE ItemName LIKE :ItemName AND RestaurantId IN (SELECT r.RestaurantId FROM restaurant r  WHERE r.Pincode IN (SELECT Pincode FROM pincodeinfo WHERE City=:City))";
        HashMap<String, Object> params = new HashMap<>();
        params.put("ItemName", "%"+name+"%");
        params.put("City",city);
        return namedParameterJdbcTemplate.query(sql, params, userRowMapper);
    }

    public List<Item> getItemsWithNameLike(String name) {
        String sql = "SELECT * FROM Items WHERE ItemName LIKE :ItemName";
        HashMap<String, Object> params = new HashMap<>();
        params.put("ItemName", "%"+name+"%");
        return namedParameterJdbcTemplate.query(sql, params, userRowMapper);
    }
    public List<Item> getAllItems() {
        String sql = "SELECT * FROM Items";
        return namedParameterJdbcTemplate.query(sql, userRowMapper);
    }

    public List<Item> getAllItemsbyRestaurantIdAndName(long id,String name) {
        String sql = "SELECT * FROM Items WHERE RestaurantId = :RestaurantId AND ItemName = :ItemName";
        HashMap<String, Object> params = new HashMap<>();
        params.put("RestaurantId", id);
        params.put("ItemName", name);
        return namedParameterJdbcTemplate.query(sql, params, userRowMapper);
    }

    public void updateItem(Item item){
        try{
            String sql = "UPDATE Items SET ItemName = :ItemName, RestaurantId = :RestaurantId, IsVegeterian = :IsVegeterian, Price = :Price, ItemDescription = :ItemDescription, IsAvailable = :IsAvailable, Rating = :Rating, NumberOfUsersRated = :NumberOfUsersRated, ImageUrl = :ImageUrl WHERE ItemId = :ItemId";
            HashMap<String, Object> params = getItemMap(item);
            namedParameterJdbcTemplate.update(sql, params);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
