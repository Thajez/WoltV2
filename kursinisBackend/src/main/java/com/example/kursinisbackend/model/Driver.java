package com.example.kursinisbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Driver extends BasicUser{
    @Transient
    private List<FoodOrder> myOrders;

    private String licence;
    private LocalDate bDate;
    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;
    @JsonIgnore
    @OneToMany(mappedBy = "driver", fetch = FetchType.LAZY)
    protected List<FoodOrder> myDeliveries;

    public Driver(String login, String password, String name, String surname, String phoneNumber, String address, UserType userType, String licence, LocalDate bDate, VehicleType vehicleType) {
        super(login, password, name, surname, phoneNumber, address, userType);
        this.licence = licence;
        this.bDate = bDate;
        this.vehicleType = vehicleType;
    }
}
