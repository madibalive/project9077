package com.venu.venutheta.models;

/**
 * Created by Madiba on 11/3/2016.
 */
public class PhoneContact {

    private String id;
    private String username;
    private String phoneNumber;

    public PhoneContact() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
