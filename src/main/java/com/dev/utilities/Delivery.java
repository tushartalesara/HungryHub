package com.dev.utilities;

import com.dev.models.CartItems;
import com.dev.models.Item;
import kotlin.Pair;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class Delivery {
    private String buyerName;
    private String buyerAddress;
    private List<Pair<Item, CartItems>> items;

    public Delivery(String buyerName, String buyerAddress, List<Pair<Item, CartItems>> items, Timestamp deliveryTime) {
        this.buyerName = buyerName;
        this.buyerAddress = buyerAddress;
        this.items = items;
        this.deliveryTime = deliveryTime;
    }

    public List<Pair<Item, CartItems>> getItems() {
        return items;
    }

    public void setItems(List<Pair<Item, CartItems>> items) {
        this.items = items;
    }

    public void setDeliveryTime(Timestamp deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    private Timestamp deliveryTime;

    public Timestamp getDeliveryTime() {
        return deliveryTime;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public void setBuyerAddress(String buyerAddress) {
        this.buyerAddress = buyerAddress;
    }



    public Delivery() {
    }

    public String getBuyerName() {
        return buyerName;
    }

    public String getBuyerAddress() {
        return buyerAddress;
    }

    @Override
    public String toString() {
        return "Delivery{" +
                "buyerName='" + buyerName + '\'' +
                ", buyerAddress='" + buyerAddress + '\'' +
                ", items=" + items +
                ", deliveryTime=" + deliveryTime +
                '}';
    }
}