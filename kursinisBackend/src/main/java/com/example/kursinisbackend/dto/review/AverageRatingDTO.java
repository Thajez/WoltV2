package com.example.kursinisbackend.dto.review;

import lombok.Data;

@Data
public class AverageRatingDTO {
    private double averageRating;
    private int totalReviews;
}