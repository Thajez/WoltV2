package com.example.courseprifs.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int rating;
    private String reviewText;
    private LocalDate dateCreated;
    @ManyToOne(optional = false)
    private BasicUser reviewOwner;
    @ManyToOne(optional = false)
    private BasicUser reviewTarget;
    @ManyToOne(optional = false)
    private FoodOrder foodOrder;

    public Review(Integer rating, String reviewText, BasicUser reviewOwner, BasicUser reviewTarget, FoodOrder foodOrder) {
        this.rating = rating;
        this.reviewText = reviewText;
        this.reviewOwner = reviewOwner;
        this.reviewTarget = reviewTarget;
        this.foodOrder = foodOrder;
        this.dateCreated = LocalDate.now();
    }
}
