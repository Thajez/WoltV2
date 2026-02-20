package com.example.kursinisbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BasicUser extends User{
    protected String address;
    @JsonIgnore
    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    protected List<FoodOrder> myOrders;
    @JsonIgnore
    @OneToMany(mappedBy = "reviewOwner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    protected List<Review> myReviews;
    @JsonIgnore
    @OneToMany(mappedBy = "reviewTarget", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    protected List<Review> feedback;

    public BasicUser(String login, String password, String name, String surname, String phoneNumber, String address, UserType userType) {
        super(login, password, name, surname, phoneNumber, userType);
        this.address = address;
        this.myReviews = new ArrayList<>();
        this.feedback = new ArrayList<>();
        this.myOrders = new ArrayList<>();
    }
}
