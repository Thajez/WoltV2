package com.example.kursinisbackend.dto.restaurant;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CuisineResponseDTO {
    private int id;
    private String name;
    private String ingredients;
    private Double price;
    private boolean spicy;
    private boolean vegan;
}
