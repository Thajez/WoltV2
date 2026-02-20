package com.example.kursinisbackend.repos;

import com.example.kursinisbackend.model.BasicUser;
import com.example.kursinisbackend.model.FoodOrder;
import com.example.kursinisbackend.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepo extends JpaRepository<Review, Integer> {
    List<Review> findByReviewOwner(BasicUser reviewOwner);
    List<Review> findByReviewTarget(BasicUser reviewTarget);
    List<Review> findByFoodOrder(FoodOrder foodOrder);
    boolean existsByReviewOwnerAndReviewTargetAndFoodOrder(
            BasicUser reviewOwner,
            BasicUser reviewTarget,
            FoodOrder foodOrder
    );
}
