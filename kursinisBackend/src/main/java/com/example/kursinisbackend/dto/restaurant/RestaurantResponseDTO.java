package com.example.kursinisbackend.dto.restaurant;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;

@Data
@AllArgsConstructor
public class RestaurantResponseDTO {
    private int id;
    private String name;
    private String surname;
    private String phoneNumber;
    private String address;
    private String typeOfRestaurant;
    private LocalTime openingTime;
    private LocalTime closingTime;
}