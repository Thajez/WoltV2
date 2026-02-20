package com.example.prif233.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Driver extends BasicUser{
    private String licence;
    private LocalDate bDate;
    private VehicleType vehicleType;
    private List<FoodOrder> myDeliveries = new ArrayList<>();

    public Driver() {}

    public Driver(String login, String password, String name, String surname, String phoneNumber, String address, String licence, LocalDate bDate, VehicleType vehicleType, UserType userType) {
        super(login, password, name, surname, phoneNumber, address, userType);
        this.licence = licence;
        this.bDate = bDate;
        this.vehicleType = vehicleType;
    }

    public String getLicence() {
        return licence;
    }
    public void setLicence(String licence) {
        this.licence = licence;
    }

    public LocalDate getbDate() {
        return bDate;
    }
    public void setbDate(LocalDate bDate) {
        this.bDate = bDate;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }
    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public List<FoodOrder> getMyDeliveries() { return myDeliveries; }
    public void setMyDeliveries(List<FoodOrder> myDeliveries) { this.myDeliveries = myDeliveries; }
}
