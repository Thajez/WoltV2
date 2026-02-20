package com.example.prif233.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FoodOrder implements Serializable {
    private int id;
    private Double price;
    private BasicUser buyer;
    private Driver driver;
    private Restaurant restaurant;
    private OrderStatus orderStatus;
    private LocalDate dateCreated;
    private LocalDate dateUpdated;

    private List<FoodOrderItem> items = new ArrayList<>();
    private Chat chat;

    public FoodOrder() {}

    public FoodOrder(Double price, BasicUser buyer, Restaurant restaurant) {
        this.price = price;
        this.buyer = buyer;
        this.restaurant = restaurant;
        this.orderStatus = OrderStatus.PENDING;
        this.dateCreated = LocalDate.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public BasicUser getBuyer() { return buyer; }
    public void setBuyer(BasicUser buyer) { this.buyer = buyer; }

    public Driver getDriver() { return driver; }
    public void setDriver(Driver driver) { this.driver = driver; }

    public Restaurant getRestaurant() { return restaurant; }
    public void setRestaurant(Restaurant restaurant) { this.restaurant = restaurant; }

    public OrderStatus getOrderStatus() { return orderStatus; }
    public void setOrderStatus(OrderStatus orderStatus) { this.orderStatus = orderStatus; }

    public LocalDate getDateCreated() { return dateCreated; }
    public void setDateCreated(LocalDate dateCreated) { this.dateCreated = dateCreated; }

    public LocalDate getDateUpdated() { return dateUpdated; }
    public void setDateUpdated(LocalDate dateUpdated) { this.dateUpdated = dateUpdated; }

    public List<FoodOrderItem> getItems() { return items; }
    public void setItems(List<FoodOrderItem> items) { this.items = items; }

    public Chat getChat() { return chat; }
    public void setChat(Chat chat) { this.chat = chat; }

    @Override
    public String toString() {
        return "ID: " + id + " Price: " + price;
    }
}
