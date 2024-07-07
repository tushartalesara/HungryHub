package com.dev.dao;

import com.dev.models.Cart;
// import com.dev.models.User;
import com.dev.models.User;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class CartJdbcDao {
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private CartItemsJdbcDao cartItemsJdbcDao;


    @Autowired
    public CartJdbcDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate, CartItemsJdbcDao cartItemsJdbcDao) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.cartItemsJdbcDao = cartItemsJdbcDao;
    }

    private final RowMapper<Cart> cartRowMapper = (rs, rowNum) -> {
        Cart cart = new Cart();
        cart.setCartId(rs.getLong("CartId"));
        cart.setUserId(rs.getLong("UserId"));
        cart.setStatus(rs.getBoolean("Status"));
        cart.setRestaurantId(rs.getLong("RestaurantId"));
        cart.setPromocodeId(rs.getLong("PromocodeId"));
        cart.setAddressId(rs.getLong("AddressId"));
        return cart;
    };

    private HashMap<String, Object> getCartMap(Cart cart) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("CartId", cart.getCartId());
        map.put("UserId", cart.getUserId());
        map.put("Status", cart.isStatus());
        map.put("RestaurantId", cart.getRestaurantId());
        map.put("PromocodeId", cart.getPromocodeId()==0?null:cart.getPromocodeId());
        map.put("AddressId", cart.getAddressId()==0?null:cart.getAddressId());
        return map;
    }

    public void createCart(Cart cart) {
        if(cart.getCartId()==0) cart.setCartId(0);// sets it to a random val.
        String sql = "INSERT INTO Cart (CartId, UserId, Status, RestaurantId, PromocodeId, AddressId) VALUES (:CartId,:UserId, :Status, :RestaurantId, :PromocodeId, :AddressId)";
        HashMap<String, Object> params = getCartMap(cart);
        namedParameterJdbcTemplate.update(sql, params);
    }

    public Cart queryForCart(String sql, HashMap<String, Object> params){
        try {
            return namedParameterJdbcTemplate.query(sql, params, cartRowMapper).get(0);
        }
        catch (Exception e){
            return null;
        }
    }

    public List<Cart> queryForCartItems(String sql, HashMap<String, Object> params){
        try {
            return namedParameterJdbcTemplate.query(sql, params, cartRowMapper);
        }
        catch (Exception e){
            return null;
        }
    }

    public Cart getCartById(long id){
        String sql = "SELECT * FROM Cart WHERE CartId = :CartId";
        HashMap<String, Object> params = new HashMap<>();
        params.put("CartId", id);
        return queryForCart(sql, params);
    }

    public List<Cart> getCartByUserId(long id){
        String sql = "SELECT * FROM Cart WHERE UserId = :UserId AND Status = 1";
        HashMap<String, Object> params = new HashMap<>();
        params.put("UserId", id);
        return namedParameterJdbcTemplate.query(sql, params, cartRowMapper);
    }

    public Cart getCartByUserIdAndRestaurantId(long userId, long restaurantId){
        String sql = "SELECT * FROM Cart WHERE UserId = :UserId AND RestaurantId = :RestaurantId AND Status = 0";
        HashMap<String, Object> params = new HashMap<>();
        params.put("UserId", userId);
        params.put("RestaurantId", restaurantId);
        return queryForCart(sql, params);
    }
    public List<Cart> getCartsByRestaurantId( long restaurantId){
        String sql = "SELECT * FROM Cart WHERE  RestaurantId = :RestaurantId AND Status = 1";
        HashMap<String, Object> params = new HashMap<>();

        params.put("RestaurantId", restaurantId);
        return queryForCartItems(sql, params);
    }


    public Cart getCurrentCart(long userId){
        String sql = "SELECT * FROM Cart WHERE UserId = :UserId AND Status = 0";
        HashMap<String, Object> params = new HashMap<>();
        params.put("UserId", userId);
        return queryForCart(sql, params);
    }

    public long deleteCart(Cart cart){
        cartItemsJdbcDao.deleteCartItemsOfCart(cart.getCartId());
        String sql = "DELETE FROM Cart WHERE CartId = :CartId";
        HashMap<String, Object> params = getCartMap(cart);
        return namedParameterJdbcTemplate.update(sql, params);
    }

    public long updateCart(Cart cart){
        String sql = "UPDATE Cart SET UserId = :UserId, Status = :Status, RestaurantId = :RestaurantId, promocodeId = :PromocodeId, AddressId = :AddressId WHERE CartId = :CartId";
        HashMap<String, Object> params = getCartMap(cart);
        return namedParameterJdbcTemplate.update(sql, params);
    }
}
