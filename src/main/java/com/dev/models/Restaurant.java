package com.dev.models;

import com.dev.services.KeyGenerator;

import java.sql.Time;

public class Restaurant {
    private long restaurantId;
    private long userId;
    private String restaurantName;
    private Time startTime;
    private Time endTime;
    private String phoneNumber;
    private String streetAddress;
    private long pincode;
    private double rating = 0;
    private long numberOfUsersRated = 0;
    private String restaurantDescription;
    private boolean isVegeterian = false;
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Restaurant(long restaurantId, long userId, String restaurantName, Time startTime, Time endTime, String phoneNumber, String streetAddress, long pincode, double rating, long numberOfUsersRated, String restaurantDescription, Boolean isVegeterian) {
        this.restaurantId = restaurantId;
        this.userId = userId;
        this.restaurantName = restaurantName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.phoneNumber = phoneNumber;
        this.streetAddress = streetAddress;
        this.pincode = pincode;
        this.rating = rating;
        this.numberOfUsersRated = numberOfUsersRated;
        this.restaurantDescription = restaurantDescription;
        this.isVegeterian = isVegeterian;
    }

    public Restaurant() {
        this.restaurantId = KeyGenerator.generateKey();
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public long getNumberOfUsersRated() {
        return numberOfUsersRated;
    }

    public void setNumberOfUsersRated(long numberOfUsersRated) {
        this.numberOfUsersRated = numberOfUsersRated;
    }

    public String getRestaurantDescription() {
        return restaurantDescription;
    }

    public void setRestaurantDescription(String restaurantDescription) {
        this.restaurantDescription = restaurantDescription;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Boolean getIsVegeterian() {
        return isVegeterian;
    }

    public void setIsVegeterian(Boolean vegeterian) {
        isVegeterian = vegeterian;
    }

    @Override
    public String toString() {
        return "restaurant{" +
                "restaurantId=" + restaurantId +
                ", restaurantName='" + restaurantName + '\'' +
                ", userId=" + userId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", PhoneNumber=" + phoneNumber +
                ", streetAddress='" + streetAddress + '\'' +
                ", pincode=" + pincode +
                ", rating=" + rating +
                ", numberOfUsersRated=" + numberOfUsersRated +
                ", restaurantDescription='" + restaurantDescription + '\'' +
                ", isVegeterian=" + isVegeterian +
                '}';
    }
}
