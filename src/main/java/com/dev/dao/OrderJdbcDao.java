package com.dev.dao;

import com.dev.models.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class OrderJdbcDao {

    NamedParameterJdbcTemplate template;

    @Autowired
    public OrderJdbcDao(NamedParameterJdbcTemplate template) {
        this.template = template;
    }

    private RowMapper<Order> rowMapper = (resultSet, i) -> {
        Order order = new Order();
        order.setOrderId(resultSet.getLong("orderid"));
        order.setOrderStatus(resultSet.getInt("orderstatus"));
        order.setCartId(resultSet.getLong("cartid"));
        order.setDriverId(resultSet.getLong("driverid"));
        order.setTrackingUrl(resultSet.getString("trackingUrl"));
        order.setTotalAmount(resultSet.getLong("amount"));
        order.setOrderTime(resultSet.getTimestamp("ordertime"));
        order.setDeliveryTime(resultSet.getTimestamp("deliveryTime"));
        order.setIsRated(resultSet.getBoolean("isRated"));
        return order;
    };

    public HashMap<String, Object> getParams(Order order){
        HashMap<String, Object> params = new HashMap<>();
        params.put("orderid", order.getOrderId());
        params.put("orderstatus", order.getOrderStatus());
        params.put("cartid", order.getCartId());
        params.put("driverid", order.getDriverId());
        params.put("trackingUrl", order.getTrackingUrl());
        params.put("amount", order.getTotalAmount());
        params.put("ordertime", order.getOrderTime());
        params.put("deliveryTime", order.getDeliveryTime());
        params.put("isRated", order.getIsRated());
        return params;
    }

    public Order queryForOrder(String sql, HashMap<String, Object> params){
        try {
            return template.query(sql, params, rowMapper).get(0);
        } catch (Exception e){
            return null;
        }
    }

    public Order getOrder(String sql, Map<String, Object> params){
        try{
            return template.query(sql, params, rowMapper).get(0);
        }catch (Exception e){
            System.out.println(e);
            return null;
        }
    }
    public List<Order> getOrderByUserId(long id){
        String sql = "SELECT o.* FROM orders o INNER JOIN cart c ON o.CartId = c.CartId WHERE c.UserId = :UserId";
        HashMap<String, Object> params = new HashMap<>();
        params.put("UserId", id);
        return template.query(sql, params, rowMapper);
    }
    public  Order getOrderByCartId(long cartId) {
        String sql = "SELECT * FROM orders WHERE CartId = :cartId";
        Map<String, Object> params = new HashMap<>();
        params.put("cartId", cartId);
        System.out.println("kkkkkkkkkkkkkkkkk");
        System.out.println(template.query(sql, params, rowMapper));
        return getOrder(sql, params);
    }

    public void createOrder(Order order){
        String sql = "INSERT INTO Orders (orderid, orderstatus, cartid, driverid, trackingUrl, amount, ordertime, deliveryTime) VALUES (:orderid, :orderstatus, :cartid, :driverid, :trackingUrl, :amount, :ordertime, :deliveryTime)";
        template.update(sql, getParams(order));
    }

    public Order searchOrderByDriver(long driverid){
        String sql = "SELECT * FROM Orders WHERE driverid = :driverid AND (orderstatus = 2 or orderstatus = 1)";
        HashMap<String, Object> params = new HashMap<>();
        params.put("driverid",driverid);
        return queryForOrder(sql,params);
    }

    public void updateOrderStatusByDriverId(long driverid,long orderstatus){
        String sql = "UPDATE Orders SET orderstatus = :orderstatus WHERE driverid=:driverid";
        HashMap<String, Object> params = new HashMap<>();
        Timestamp deliveryTime = Timestamp.valueOf(LocalDateTime.now());
        if(orderstatus==3){
            sql = "UPDATE Orders SET orderstatus = :orderstatus,deliveryTime = :deliveryTime WHERE driverid=:driverid";
            params.put("deliveryTime",deliveryTime);
        }
        params.put("driverid",driverid);
        params.put("orderstatus",orderstatus);
        template.update(sql,params);
    }

    public List<Order> searchPreviousDeliveredOrdersByDriverId(long driverid){
        String sql = "SELECT * FROM Orders WHERE driverid = :driverid AND orderstatus=3";
        HashMap<String, Object> params = new HashMap<>();
        params.put("driverid",driverid);
        return template.query(sql,params,rowMapper);
    }

    public Order getOrderById(long orderId){
        String sql = "SELECT * FROM Orders WHERE orderid=:orderid";
        return queryForOrder(sql, new HashMap<String, Object>(){{
            put("orderid", orderId);
        }});
    }

    public void updateOrder(Order order){
        try {
            String sql = "UPDATE Orders SET orderstatus=:orderstatus, cartid=:cartid, driverid=:driverid, trackingUrl=:trackingUrl, amount=:amount, ordertime=:ordertime, deliveryTime=:deliveryTime, isRated=:isRated WHERE orderid=:orderid";
            template.update(sql, getParams(order));
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
