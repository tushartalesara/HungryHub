package com.dev.models;

import com.dev.services.KeyGenerator;

public class UserAddress {
    private long addressId;
    private String streetAddress;
    private long pincode;
    private long userId;

    public UserAddress() {
    }

    public UserAddress(String streetAddress, long pincode, long userId) {
        this.streetAddress = streetAddress;
        this.pincode = pincode;
        this.userId = userId;
        this.addressId = KeyGenerator.generateKey();
    }

    public long getAddressId() {
        return addressId;
    }

    public void setAddressId(long addressId) {
        this.addressId = addressId;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public long getPincode() {
        return pincode;
    }

    public void setPincode(long pincode) {
        this.pincode = pincode;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}