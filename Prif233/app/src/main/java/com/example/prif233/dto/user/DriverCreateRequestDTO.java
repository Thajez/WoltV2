package com.example.prif233.dto.user;

import com.example.prif233.model.VehicleType;

import java.time.LocalDate;

public class DriverCreateRequestDTO extends ClientCreateRequestDTO {
    private String licence;
    private LocalDate bDate;
    private VehicleType vehicleType;

    public DriverCreateRequestDTO(String login, String password, String name, String surname, String phoneNumber, String address, String licence, LocalDate bDate, VehicleType vehicleType) {
        super(login, password, name, surname, phoneNumber, address);
        this.licence = licence;
        this.bDate = bDate;
        this.vehicleType = vehicleType;
    }

    public DriverCreateRequestDTO() {}

    public String getLicence() { return licence; }
    public void setLicence(String licence) { this.licence = licence; }

    public LocalDate getBDate() { return bDate; }
    public void setBDate(LocalDate bDate) { this.bDate = bDate; }

    public VehicleType getVehicleType() { return vehicleType; }
    public void setVehicleType(VehicleType vehicleType) { this.vehicleType = vehicleType; }
}
