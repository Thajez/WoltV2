package com.example.prif233.model;

import java.io.Serializable;

public class FoodOrderItem implements Serializable {

    private long id;
    private FoodOrder order;
    private Cuisine cuisine;
    private int quantity;
    private double totalPrice;

    public FoodOrderItem() {}

    public FoodOrderItem(FoodOrder order, Cuisine cuisine, int quantity, double totalPrice) {
        this.order = order;
        this.cuisine = cuisine;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public FoodOrder getOrder() { return order; }
    public void setOrder(FoodOrder order) { this.order = order; }

    public Cuisine getCuisine() { return cuisine; }
    public void setCuisine(Cuisine cuisine) { this.cuisine = cuisine; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    @Override
    public String toString() {
        return cuisine.getName() + " (x" + quantity + ") Price: " + totalPrice;
    }
}
