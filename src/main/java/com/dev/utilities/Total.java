package com.dev.utilities;

public class Total {
    public long total = 0;
    public long discountValue = 0;
    public long finalValue = 0;
    public long valid = 0;

    public Total() {
    }

    public Total(long total, long discountValue, long finalValue, long valid) {
        this.total = total;
        this.discountValue = discountValue;
        this.finalValue = finalValue;
        this.valid = valid;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(long discountValue) {
        this.discountValue = discountValue;
    }

    public long getFinalValue() {
        return finalValue;
    }

    public void setFinalValue(long finalValue) {
        this.finalValue = finalValue;
    }

    public long getValid() {
        return valid;
    }

    public void setValid(long valid) {
        this.valid = valid;
    }
}
