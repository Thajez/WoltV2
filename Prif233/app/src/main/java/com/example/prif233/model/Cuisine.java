package com.example.prif233.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Cuisine implements Serializable {

    private int id;
    private String name;
    private String ingredients;
    private Double price;
    private boolean spicy;
    private boolean vegan;

    private Restaurant restaurant;
    private List<FoodOrderItem> orderItemList = new ArrayList<>();

    public Cuisine() {}

    public Cuisine(String name, String ingredients, Double price,
                   boolean spicy, boolean vegan,
                   Restaurant restaurant) {
        this.name = name;
        this.ingredients = ingredients;
        this.price = price;
        this.spicy = spicy;
        this.vegan = vegan;
        this.restaurant = restaurant;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public boolean isSpicy() { return spicy; }
    public void setSpicy(boolean spicy) { this.spicy = spicy; }

    public boolean isVegan() { return vegan; }
    public void setVegan(boolean vegan) { this.vegan = vegan; }

    public Restaurant getRestaurant() { return restaurant; }
    public void setRestaurant(Restaurant restaurant) { this.restaurant = restaurant; }

    public List<FoodOrderItem> getOrderItemList() { return orderItemList; }
    public void setOrderItemList(List<FoodOrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }

    @Override
    public String toString() {
        return name;
    }
}
