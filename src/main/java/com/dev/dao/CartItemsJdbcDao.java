package com.dev.dao;

import com.dev.models.CartItems;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class CartItemsJdbcDao {
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
    @Autowired
    public CartItemsJdbcDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    private final RowMapper<CartItems> cartItemsRowMapper = (rs, rowNum) -> {
        CartItems cartItems = new CartItems();
        cartItems.setCartId(rs.getLong("CartId"));
        cartItems.setItemId(rs.getLong("ItemId"));
        cartItems.setQuantity(rs.getInt("Quantity"));
        return cartItems;
    };

    private HashMap<String, Object> getCartItemsMap(CartItems cartItems) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("CartId", cartItems.getCartId());
        map.put("ItemId", cartItems.getItemId());
        map.put("Quantity", cartItems.getQuantity());
        return map;
    }

    public void createCartItems(CartItems cartItems){
        String sql="INSERT INTO cartItems (CartId,ItemId,Quantity) VALUES (:CartId, :ItemId, :Quantity)";
        HashMap<String, Object> params = getCartItemsMap(cartItems);
        namedParameterJdbcTemplate.update(sql, params);
    }

    public CartItems queryForCartItems(String sql, HashMap<String, Object> params){
        try {
            return namedParameterJdbcTemplate.query(sql, params, cartItemsRowMapper).get(0);
        }
        catch (Exception e){
            return null;
        }
    }

    public List<CartItems> getCartItemsByCartId(long id){
        String sql = "SELECT * FROM cartItems WHERE CartId = :CartId";
        HashMap<String, Object> params = new HashMap<>();
        params.put("CartId", id);
        return namedParameterJdbcTemplate.query(sql, params, cartItemsRowMapper);
    }

    public CartItems getCartItemsByCartIdAndItemId(long cartId, long itemId){
        String sql = "SELECT * FROM cartItems WHERE CartId = :CartId AND ItemId = :ItemId";
        HashMap<String, Object> params = new HashMap<>();
        params.put("CartId", cartId);
        params.put("ItemId", itemId);
        return queryForCartItems(sql, params);
    }

    public long updateCartItems(CartItems cartItems){
        String sql = "UPDATE cartItems SET Quantity = :Quantity WHERE CartId = :CartId AND ItemId = :ItemId";
        HashMap<String, Object> params = getCartItemsMap(cartItems);
        return namedParameterJdbcTemplate.update(sql, params);
    }

    public long deleteCartItems(CartItems cartItems){
        String sql = "DELETE FROM cartItems WHERE CartId = :CartId AND ItemId = :ItemId";
        HashMap<String, Object> params = getCartItemsMap(cartItems);
        return namedParameterJdbcTemplate.update(sql, params);
    }

    public long deleteCartItemsOfCart(long cart_id){
        String sql = "DELETE FROM cartItems WHERE CartId = :CartId";
        HashMap<String, Object> params = new HashMap<>();
        params.put("CartId", cart_id);
        return namedParameterJdbcTemplate.update(sql, params);
    }
}
