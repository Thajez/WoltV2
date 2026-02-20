package com.example.kursinisbackend.controllers;

import com.example.kursinisbackend.dto.ErrorResponseDTO;
import com.example.kursinisbackend.dto.SuccessResponseDTO;
import com.example.kursinisbackend.dto.review.*;
import com.example.kursinisbackend.model.*;
import com.example.kursinisbackend.repos.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    @Autowired
    private ReviewRepo reviewRepo;
    @Autowired
    private OrdersRepo ordersRepo;
    @Autowired
    private BasicUserRepo basicUserRepo;

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

    @Transactional
    @PostMapping("/create")
    public ResponseEntity<?> createReview(@Valid @RequestBody CreateReviewRequestDTO dto) {
        FoodOrder order = ordersRepo.findById(dto.getOrderId()).orElse(null);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO("Order not found", HttpStatus.NOT_FOUND.value()));
        }

        if (order.getOrderStatus() != OrderStatus.COMPLETED) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO("Can only review completed orders",
                            HttpStatus.BAD_REQUEST.value()));
        }

        BasicUser reviewOwner = basicUserRepo.findById(dto.getReviewOwnerId()).orElse(null);
        BasicUser reviewTarget = basicUserRepo.findById(dto.getReviewTargetId()).orElse(null);

        if (reviewOwner == null || reviewTarget == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO("User not found", HttpStatus.NOT_FOUND.value()));
        }

        boolean ownerIsClient = order.getBuyer().getId() == reviewOwner.getId();
        boolean ownerIsDriver = order.getDriver() != null &&
                order.getDriver().getId() == reviewOwner.getId();
        boolean ownerIsRestaurant = order.getRestaurant().getId() == reviewOwner.getId();

        if (!ownerIsClient && !ownerIsDriver && !ownerIsRestaurant) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponseDTO("You are not part of this order",
                            HttpStatus.FORBIDDEN.value()));
        }

        boolean targetIsClient = order.getBuyer().getId() == reviewTarget.getId();
        boolean targetIsDriver = order.getDriver() != null &&
                order.getDriver().getId() == reviewTarget.getId();
        boolean targetIsRestaurant = order.getRestaurant().getId() == reviewTarget.getId();

        if (!targetIsClient && !targetIsDriver && !targetIsRestaurant) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponseDTO("Target user is not part of this order",
                            HttpStatus.FORBIDDEN.value()));
        }

        boolean isValidReview = false;

        if (ownerIsClient && (targetIsDriver || targetIsRestaurant)) {
            isValidReview = true;
        } else if ((ownerIsDriver || ownerIsRestaurant) && targetIsClient) {
            isValidReview = true;
        }

        if (!isValidReview) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO("Invalid review relationship",
                            HttpStatus.BAD_REQUEST.value()));
        }

        boolean reviewExists = reviewRepo.existsByReviewOwnerAndReviewTargetAndFoodOrder(
                reviewOwner, reviewTarget, order);

        if (reviewExists) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponseDTO("Review already exists for this order",
                            HttpStatus.CONFLICT.value()));
        }

        Review review = new Review(
                dto.getRating(),
                dto.getReviewText(),
                reviewOwner,
                reviewTarget,
                order
        );

        reviewRepo.save(review);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessResponseDTO("Review created successfully"));
    }

    @GetMapping("/owner/{userId}")
    public ResponseEntity<?> getReviewsByOwner(@PathVariable int userId) {
        BasicUser user = basicUserRepo.findById(userId).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO("User not found", HttpStatus.NOT_FOUND.value()));
        }

        List<ReviewResponseDTO> reviews = reviewRepo.findByReviewOwner(user)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/target/{userId}")
    public ResponseEntity<?> getReviewsByTarget(@PathVariable int userId) {
        BasicUser user = basicUserRepo.findById(userId).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO("User not found", HttpStatus.NOT_FOUND.value()));
        }

        List<ReviewResponseDTO> reviews = reviewRepo.findByReviewTarget(user)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getReviewsForOrder(@PathVariable int orderId) {
        FoodOrder order = ordersRepo.findById(orderId).orElse(null);

        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO("Order not found", HttpStatus.NOT_FOUND.value()));
        }

        List<ReviewResponseDTO> reviews = reviewRepo.findByFoodOrder(order)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/average/{userId}")
    public ResponseEntity<?> getAverageRating(@PathVariable int userId) {
        BasicUser user = basicUserRepo.findById(userId).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO("User not found", HttpStatus.NOT_FOUND.value()));
        }

        List<Review> reviews = reviewRepo.findByReviewTarget(user);

        if (reviews.isEmpty()) {
            AverageRatingDTO dto = new AverageRatingDTO();
            dto.setAverageRating(0.0);
            dto.setTotalReviews(0);
            return ResponseEntity.ok(dto);
        }

        double average = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        AverageRatingDTO dto = new AverageRatingDTO();
        dto.setAverageRating(average);
        dto.setTotalReviews(reviews.size());

        return ResponseEntity.ok(dto);
    }

    private ReviewResponseDTO mapToDTO(Review review) {
        ReviewResponseDTO dto = new ReviewResponseDTO();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setReviewText(review.getReviewText());
        dto.setDateCreated(review.getDateCreated().format(formatter));
        dto.setReviewOwnerId(review.getReviewOwner().getId());
        dto.setReviewOwnerName(review.getReviewOwner().getName() + " " + review.getReviewOwner().getSurname());
        dto.setReviewTargetId(review.getReviewTarget().getId());
        dto.setReviewTargetName(review.getReviewTarget().getName() + " " + review.getReviewTarget().getSurname());
        dto.setOrderId(review.getFoodOrder().getId());

        return dto;
    }
}