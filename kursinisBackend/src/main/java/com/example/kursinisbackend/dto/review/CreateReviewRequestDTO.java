package com.example.kursinisbackend.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateReviewRequestDTO {
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    @NotBlank(message = "Review text is required")
    private String reviewText;

    @NotNull(message = "Review owner ID is required")
    private Integer reviewOwnerId;

    @NotNull(message = "Review target ID is required")
    private Integer reviewTargetId;

    @NotNull(message = "Order ID is required")
    private Integer orderId;
}

