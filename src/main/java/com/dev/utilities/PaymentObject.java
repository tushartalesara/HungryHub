package com.dev.utilities;

public class PaymentObject {
    private String stripeToken;
    private long amount;
    private String desc;

    public PaymentObject(String stripeToken, long amount, String desc) {
        this.stripeToken = stripeToken;
        this.amount = amount;
        this.desc = desc;
    }

    public PaymentObject() {
    }

    public String getStripeToken() {
        return stripeToken;
    }

    public void setStripeToken(String stripeToken) {
        this.stripeToken = stripeToken;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
