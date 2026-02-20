package com.example.prif233.dto.restaurant;

public class RestaurantResponseDTO {
    private int id;
    private String name;
    private String surname;
    private String phoneNumber;
    private String address;
    private String typeOfRestaurant;
    private String openingTime;
    private String closingTime;

    public RestaurantResponseDTO() {
    }

    public RestaurantResponseDTO(
            int id, String name, String surname, String phoneNumber,
            String address, String typeOfRestaurant,
            String openingTime, String closingTime
    )
    {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.typeOfRestaurant = typeOfRestaurant;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTypeOfRestaurant() {
        return typeOfRestaurant;
    }

    public void setTypeOfRestaurant(String typeOfRestaurant) {
        this.typeOfRestaurant = typeOfRestaurant;
    }

    public String getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }

    public String getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime;
    }
}
