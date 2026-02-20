package com.example.kursinisbackend.dto.restaurant;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderItemRequestDTO {
    @NotNull
    private int cuisineId;
    @NotNull
    private int quantity;
}