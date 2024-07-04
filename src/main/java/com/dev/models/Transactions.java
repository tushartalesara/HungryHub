package com.dev.models;

import com.dev.services.KeyGenerator;

import java.sql.Timestamp;

public class Transactions {
    private String transactionId;
    private long cartId;
    private long transactionStatus; // 0 for pending, 1 for success, 2 for failed
    private String transactionType;
    private Timestamp transactionTime;
//    private String transactionTime; TODO: add this to schema

    public Transactions(){

    }

    public Timestamp getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(Timestamp transactionTime) {
        this.transactionTime = transactionTime;
    }

    public Transactions(long cartId, long transactionStatus, String transactionType, Timestamp transactionTime, String transactionId) {
        this.cartId = cartId;
        this.transactionStatus = transactionStatus;
        this.transactionType = transactionType;
        this.transactionId = transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public long getCartId() {
        return cartId;
    }

    public void setCartId(long cartId) {
        this.cartId = cartId;
    }

    public long getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(long transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    @Override
    public String toString() {
        return "Transactions{" +
                "transactionId=" + transactionId +
                ", cartId=" + cartId +
                ", transactionStatus='" + transactionStatus + '\'' +
                ", transactionType='" + transactionType + '\'' +
                '}';
    }
}
