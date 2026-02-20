package com.example.prif233.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BasicUser extends User{
    protected String address;
    protected List<FoodOrder> myOrders = new ArrayList<>();
    protected List<Review> myReviews = new ArrayList<>();
    protected List<Review> feedback = new ArrayList<>();

    public BasicUser() {
    }

    public BasicUser(String login, String password, String name, String surname, String phoneNumber, String address, UserType userType) {
        super(login, password, name, surname, phoneNumber, userType);
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public List<FoodOrder> getMyOrders() { return myOrders; }
    public void setMyOrders(List<FoodOrder> myOrders) { this.myOrders = myOrders; }

    public List<Review> getMyReviews() { return myReviews; }
    public void setMyReviews(List<Review> myReviews) { this.myReviews = myReviews; }

    public List<Review> getFeedback() { return feedback; }
    public void setFeedback(List<Review> feedback) { this.feedback = feedback; }
}
