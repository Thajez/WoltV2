package com.example.kursinisbackend.dto.restaurant;

import com.example.kursinisbackend.model.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OrderResponseDTO {
    private int id;
    @NotNull private Double price;
    @NotNull private int buyerId;
    @NotNull private String buyerName;
    @NotNull private String buyerSurname;
    @NotNull private int restaurantId;
    @NotNull private String restaurantName;
    private Integer driverId;
    private String driverName;
    private String driverSurname;
    @NotNull private String restaurantSurname;
    @NotNull private OrderStatus orderStatus;
    @NotNull private List<OrderItemResponseDTO> items;
    @NotNull private String dateCreated;
    @NotNull private String dateUpdated;
}
