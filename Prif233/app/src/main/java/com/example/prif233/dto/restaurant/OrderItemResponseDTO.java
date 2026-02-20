package com.example.prif233.dto.restaurant;

public class OrderItemResponseDTO {
    private long id;
    private int quantity;
    private int cuisineId;
    private double totalPrice;
    private String cuisineName;

    public OrderItemResponseDTO(long id, int quantity, int cuisineId, double totalPrice, String cuisineName) {
        this.id = id;
        this.quantity = quantity;
        this.cuisineId = cuisineId;
        this.totalPrice = totalPrice;
        this.cuisineName = cuisineName;
    }

    public OrderItemResponseDTO() {}

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getCuisineId() {
        return cuisineId;
    }
    public void setCuisineId(int cuisineId) {
        this.cuisineId = cuisineId;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCuisineName() {
        return cuisineName;
    }
    public void setCuisineName(String cuisineName) {
        this.cuisineName = cuisineName;
    }
}
