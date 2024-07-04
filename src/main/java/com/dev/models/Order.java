package com.dev.models;

import com.dev.services.KeyGenerator;

import java.sql.Timestamp;

public class Order {
    private long orderId;
    // for now 0-failed, 1-preparing, 2-dispatched, 3-delivered
    private int orderStatus;
    private long cartId;
    private long driverId; //
    private String trackingUrl; //
    private Timestamp orderTime;
    private double totalAmount; // for efficiency and not to change total if item value is updated in the future.
    private Timestamp deliveryTime;
    private boolean isRated = false;

    public boolean getIsRated() {
        return isRated;
    }

    public void setIsRated(boolean rated) {
        isRated = rated;
    }

    public Timestamp getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Timestamp deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Timestamp getOrderTime() {
        return orderTime;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Order(int orderStatus, long cartId, long driverId, String trackingUrl,  Timestamp orderTime, double totalAmount) {
        this.orderStatus = orderStatus;
        this.cartId = cartId;
        this.driverId = driverId;
        this.trackingUrl = trackingUrl;
        this.orderTime = orderTime;
        this.orderId = KeyGenerator.generateKey();
        this.totalAmount = totalAmount;
    }

    public Order(){
        this.orderId = KeyGenerator.generateKey();
    }

    public void setOrderTime(Timestamp orderTime) {
        this.orderTime = orderTime;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public long getCartId() {
        return cartId;
    }

    public void setCartId(long cartId) {
        this.cartId = cartId;
    }

    public long getDriverId() {
        return driverId;
    }

    public void setDriverId(long driverId) {
        this.driverId = driverId;
    }

    public String getTrackingUrl() {
        return trackingUrl;
    }

    public void setTrackingUrl(String trackingUrl) {
        this.trackingUrl = trackingUrl;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", orderStatus=" + orderStatus +
                ", cartId=" + cartId +
                ", driverId=" + driverId +
                ", trackingUrl='" + trackingUrl + '\'' +
                '}';
    }
}
