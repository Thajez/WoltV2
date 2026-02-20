package com.example.kursinisbackend.dto.review;

import lombok.Data;

@Data
public class ReviewResponseDTO {
    private int id;
    private int rating;
    private String reviewText;
    private String dateCreated;
    private int reviewOwnerId;
    private String reviewOwnerName;
    private int reviewTargetId;
    private String reviewTargetName;
    private int orderId;
}