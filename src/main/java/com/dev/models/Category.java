package com.dev.models;

import com.dev.services.KeyGenerator;

public class Category {
    private long categoryId;
    private String categoryName;

    public Category() {
        this.categoryId = KeyGenerator.generateKey();
    }

    public Category(String categoryName) {
        this.categoryId = KeyGenerator.generateKey();
        this.categoryName = categoryName;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

}
