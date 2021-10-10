package com.example.personalfinancialmanagement.ui.notifications;

public class Product {

    private String categories;

    public Product(String categories, double amount) {
        this.categories = categories;
        this.amount = amount;
    }

    public Product() {
        this.categories = "";
        this.amount = 0;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    private double amount;
}
