package com.example.kursinisbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    private FoodOrder order;
    @ManyToOne(optional = false)
    private Cuisine cuisine;
    private int quantity;
    private double totalPrice;

    public FoodOrderItem(FoodOrder order, Cuisine cuisine, int quantity, double totalPrice) {
        this.order = order;
        this.cuisine = cuisine;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    @Override
    public String toString() {
        return cuisine.getName() + " ( x" + quantity + ") Price: " + totalPrice;
    }
}
