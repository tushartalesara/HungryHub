package com.dev.models;

public class PincodeInfo {
    private long pincode;
    private String city;
    private String state;

    public PincodeInfo() {
    }

    public PincodeInfo(long pincode, String city, String state) {
        this.pincode = pincode;
        this.city = city;
        this.state = state;
    }

    public long getPincode() {
        return pincode;
    }

    public void setPincode(long pincode) {
        this.pincode = pincode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
