package com.example.personalfinancialmanagement;

public class Income {

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getdate() {
        return date;
    }

    public void setdate(String date) {
        this.date = date;
    }

    public String getcategories() {
        return categories;
    }

    public void setcategories(String categories) {
        this.categories = categories;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    public Income(String amount, String date, String categories, String description) {
        this.amount = amount;
        this.date = date;
        this.categories = categories;
        this.description = description;
    }

    public Income() {
        this.date = "";
        this.categories = "";
        this.description = "";
        this.amount = "";
    }

    private String amount;
    private String date;
    private String categories;
    private String description;
}
