package com.example.kursinisbackend.dto.restaurant;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequestDTO {
    @NotNull
    private int buyerId;
    @NotNull
    private int restaurantId;
    @NotNull
    private List<OrderItemRequestDTO> items;
}