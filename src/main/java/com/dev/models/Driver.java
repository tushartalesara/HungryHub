package com.dev.models;

import com.dev.services.KeyGenerator;

public class Driver {
    private long driverId;
    private long userId;
    private long numberOfUsersRated = 0;
    private double rating = 0.0;
    private long pincode;
    private int status = 0; // 0 - free, 1 - busy, 2 - offline
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Driver() {
        this.driverId = KeyGenerator.generateKey();
    }

    public Driver(long userId, long numberOfUsersRated, double rating, long pincode, int status) {
        this.driverId = KeyGenerator.generateKey();
        this.userId = userId;
        this.numberOfUsersRated = numberOfUsersRated;
        this.rating = rating;
        this.pincode = pincode;
        this.status = status;
    }

    public long getDriverId() {
        return driverId;
    }

    public void setDriverId(long driverId) {
        this.driverId = driverId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getNumberOfUsersRated() {
        return numberOfUsersRated;
    }

    public void setNumberOfUsersRated(long numberOfUsersRated) {
        this.numberOfUsersRated = numberOfUsersRated;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public long getPincode() {
        return pincode;
    }

    public void setPincode(long pincode) {
        this.pincode = pincode;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
