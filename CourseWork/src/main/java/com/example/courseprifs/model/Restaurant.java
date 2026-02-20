package com.example.courseprifs.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Restaurant extends BasicUser {
    @Transient
    private List<FoodOrder> myOrders;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Cuisine> menu;
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FoodOrder> foodOrders;

    protected String typeOfRestaurant;
    protected LocalTime openingTime;
    protected LocalTime closingTime;

    public Restaurant(String login, String password, String name, String surname, String phoneNumber, String address, UserType userType, String typeOfRestaurant, LocalTime openingTime, LocalTime closingTime) {
        super(login, password, name, surname, phoneNumber, address, userType);
        this.typeOfRestaurant = typeOfRestaurant;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }
}
