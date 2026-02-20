package com.example.prif233.model;


import java.time.LocalDate;


public class Review {

    private int id;
    private int rating;
    private String reviewText;
    private LocalDate dateCreated;
    private BasicUser reviewOwner;
    private BasicUser reviewTarget;
    private FoodOrder foodOrder;

    public Review() {}

    public Review(int rating, String reviewText, BasicUser reviewOwner, BasicUser reviewTarget, FoodOrder foodOrder) {
        this.rating = rating;
        this.reviewText = reviewText;
        this.reviewOwner = reviewOwner;
        this.reviewTarget = reviewTarget;
        this.foodOrder = foodOrder;
        this.dateCreated = LocalDate.now();
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }
    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }
    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    public BasicUser getReviewOwner() { return reviewOwner; }
    public void setReviewOwner(BasicUser reviewOwner) { this.reviewOwner = reviewOwner; }

    public BasicUser getReviewTarget() { return reviewTarget; }
    public void setReviewTarget(BasicUser reviewTarget) { this.reviewTarget = reviewTarget; }

    public FoodOrder getFoodOrder() { return foodOrder; }
    public void setFoodOrder(FoodOrder foodOrder) { this.foodOrder = foodOrder; }

    @Override
    public String toString() {
        if (dateCreated != null) {
            return reviewText + " (" + dateCreated + ")";
        }
        return reviewText != null ? reviewText : "";
    }
}
