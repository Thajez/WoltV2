package com.example.courseprifs.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class FoodOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Double price;
    @ManyToOne(optional = false)
    private BasicUser buyer;
    @ManyToOne
    private Driver driver;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FoodOrderItem> items;
    @OneToOne(mappedBy = "foodOrder", cascade = CascadeType.ALL)
    private Chat chat;
    @ManyToOne
    private Restaurant restaurant;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private LocalDate dateCreated;
    private LocalDate dateUpdated;

    public FoodOrder(Double price, BasicUser buyer, Restaurant restaurant) {
        this.price = price;
        this.buyer = buyer;
        this.restaurant = restaurant;
        this.orderStatus = OrderStatus.PENDING;
    }

    @Override
    public String toString() {
        return "ID: " + id + " Price: " + price;
    }
}
