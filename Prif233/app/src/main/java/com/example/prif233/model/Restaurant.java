package com.example.prif233.model;


import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Restaurant extends BasicUser {
    private List<Cuisine> menu = new ArrayList<>();
    private List<FoodOrder> foodOrders = new ArrayList<>();
    private String typeOfRestaurant;
    private LocalTime openingTime;
    private LocalTime closingTime;

    public Restaurant() {}

    public Restaurant(String login, String password, String name, String surname, String phoneNumber, String address, UserType userType) {
        super(login, password, name, surname, phoneNumber, address, userType);
    }

    public List<Cuisine> getMenu() { return menu; }
    public void setMenu(List<Cuisine> menu) { this.menu = menu; }

    public List<FoodOrder> getFoodOrders() { return foodOrders; }
    public void setFoodOrders(List<FoodOrder> foodOrders) { this.foodOrders = foodOrders; }

    public String getTypeOfRestaurant() { return typeOfRestaurant; }
    public void setTypeOfRestaurant(String typeOfRestaurant) { this.typeOfRestaurant = typeOfRestaurant; }

    public LocalTime getOpeningTime() { return openingTime; }
    public void setOpeningTime(LocalTime openingTime) { this.openingTime = openingTime; }

    public LocalTime getClosingTime() { return closingTime; }
    public void setClosingTime(LocalTime closingTime) { this.closingTime = closingTime; }
}
