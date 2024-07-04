package com.dev.models;

import com.dev.services.KeyGenerator;

public class Cart {
    private long cartId;
    private long userId;
    private long restaurantId;
    private boolean status = false;
    private long promocodeId; // 0 if no promocode applied
    private long addressId; // null if no address is selected. Although addressId -> UserId, we are storing because cart can be created without address and also if in future address is removed, it shouldn't cause deletion anomaly.

    public long getAddressId() {
        return addressId;
    }

    public void setAddressId(long addressId) {
        this.addressId = addressId;
    }

    public long getPromocodeId() {
        return promocodeId;
    }

    public void setPromocodeId(long promocodeId) {
        this.promocodeId = promocodeId;
    }

    public Cart() {
        this.cartId = KeyGenerator.generateKey();
    }

    public Cart(long userId, boolean status, long restaurantId) {
        this.cartId = KeyGenerator.generateKey();
        this.userId = userId;
        this.status = status;
        this.restaurantId = restaurantId;
    }

    public long getCartId() {
        return cartId;
    }

    public void setCartId(long cartId) {
        if(cartId==0)
            cartId=KeyGenerator.generateKey();
        else this.cartId = cartId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(long restaurantId) {
        this.restaurantId = restaurantId;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "cartId=" + cartId + '\'' +
                ", userId=" + userId + '\'' +
                ", restaurantId=" + restaurantId + '\'' +
                ", status=" + status + '\'' +
                ", promocodeId=" + promocodeId + '\'' +
                '}';
    }
}
