package com.example.personalfinancialmanagement;

import android.graphics.Bitmap;

public class User {
    private String username;

    public User(String username, String email, String type, String phone) {
        this.username = username;
        this.email = email;
        this.type = type;
        this.phone = phone;
    }

    private String email;
    private String type;
    private String phone;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public User() {

        this.username = "";
        this.email = "";
        this.phone = "";

    }




}
