package com.example.kursinisbackend.dto.restaurant;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderItemResponseDTO {
    private Long id;
    private int quantity;
    private int cuisineId;
    private Double totalPrice;
    private String cuisineName;
}
