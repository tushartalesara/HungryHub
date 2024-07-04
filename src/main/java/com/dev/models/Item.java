package com.dev.models;

import com.dev.services.KeyGenerator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class Item {
    private long itemId;
    @NotNull
    @Size(min = 1, max = 50)
    private String itemName;
    private long restaurantId;
    @NotNull
    private boolean isVegeterian;
    @NotNull
    private long price;
    private String itemDescription;
    private boolean isAvailable = true;
    private double rating = 0.0;
    private long numberOfUsersRated = 0;
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public boolean getIsVegeterian() {
        return isVegeterian;
    }

    public void setIsVegeterian(boolean vegeterian) {
        isVegeterian = vegeterian;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
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

    public Item() {
        this.itemId = KeyGenerator.generateKey();
    }

    public Item(String itemName, long restaurantId, boolean isVegeterian, long price, String itemDescription, boolean isAvailable, double rating, long numberOfUsersRated) {
        this.itemName = itemName;
        this.restaurantId = restaurantId;
        this.isVegeterian = isVegeterian;
        this.price = price;
        this.itemDescription = itemDescription;
        this.isAvailable = isAvailable;
        this.rating = rating;
        this.numberOfUsersRated = numberOfUsersRated;
        this.itemId = KeyGenerator.generateKey();
    }

    @Override
    public String toString() {
        return "Item{" +
                "itemId=" + itemId +
                ", itemName='" + itemName + '\'' +
                ", restaurantId=" + restaurantId +
                ", isVegeterian=" + isVegeterian +
                ", price=" + price +
                ", itemDescription='" + itemDescription + '\'' +
                ", isAvailable=" + isAvailable +
                ", rating=" + rating +
                ", numberOfUsersRated=" + numberOfUsersRated +
                '}';
    }
}
