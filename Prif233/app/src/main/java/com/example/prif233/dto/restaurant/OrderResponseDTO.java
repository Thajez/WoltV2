package com.example.prif233.dto.restaurant;

import java.util.List;

public class OrderResponseDTO {
    private int id;
    private double price;
    private int buyerId;
    private String buyerName;
    private String buyerSurname;
    private int restaurantId;
    private String restaurantName;
    private Integer driverId;
    private String driverName;
    private String driverSurname;
    private String restaurantSurname;
    private String orderStatus;
    private List<OrderItemResponseDTO> items;
    private String dateCreated;
    private String dateUpdated;

    public OrderResponseDTO() {}

    public OrderResponseDTO(
            int id, double price, int buyerId,
            String buyerName, String buyerSurname , int restaurantId, String restaurantName,
            Integer driverId, String driverName, String driverSurname, String restaurantSurname,
            String orderStatus, List<OrderItemResponseDTO> items,
            String dateCreated, String dateUpdated
    ) {
        this.id = id;
        this.price = price;
        this.buyerId = buyerId;
        this.buyerName = buyerName;
        this.buyerSurname = buyerSurname;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.driverId = driverId;
        this.driverName = driverName;
        this.driverSurname = driverSurname;
        this.restaurantSurname = restaurantSurname;
        this.orderStatus = orderStatus;
        this.items = items;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public int getBuyerId() {
        return buyerId;
    }
    public void setBuyerId(int buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyerName() {
        return buyerName;
    }
    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerSurname() {
        return buyerSurname;
    }
    public void setBuyerSurname(String buyerSurname) {
        this.buyerSurname = buyerSurname;
    }

    public int getRestaurantId() {
        return restaurantId;
    }
    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }
    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public Integer getDriverId() {
        return driverId;
    }
    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }
    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverSurname() {
        return driverSurname;
    }
    public void setDriverSurname(String driverSurname) {
        this.driverSurname = driverSurname;
    }


    public String getRestaurantSurname() {
        return restaurantSurname;
    }
    public void setRestaurantSurname(String restaurantSurname) {
        this.restaurantSurname = restaurantSurname;
    }

    public String getOrderStatus() {
        return orderStatus;
    }
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public List<OrderItemResponseDTO> getItems() {
        return items;
    }
    public void setItems(List<OrderItemResponseDTO> items) {
        this.items = items;
    }

    public String getDateCreated() {
        return dateCreated;
    }
    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }
    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }
}
