package com.example.courseprifs.utils;

import com.example.courseprifs.model.Review;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.format.DateTimeFormatter;

public class ReviewListCell extends ListCell<Review> {
    private final VBox container;
    private final Label ratingLabel;
    private final Label fromLabel;
    private final Label dateLabel;
    private final Label reviewTextLabel;
    private final Label orderLabel;

    public ReviewListCell() {
        container = new VBox(5);
        container.setPadding(new Insets(10));
        container.setStyle("-fx-background-color: #c5c3c3; -fx-border-color: #8d8e91; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");

        ratingLabel = new Label();
        ratingLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        fromLabel = new Label();
        fromLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));

        dateLabel = new Label();
        dateLabel.setFont(Font.font("System", FontWeight.NORMAL, 11));
        dateLabel.setStyle("-fx-text-fill: #6c757d;");

        orderLabel = new Label();
        orderLabel.setFont(Font.font("System", FontWeight.NORMAL, 11));
        orderLabel.setStyle("-fx-text-fill: #495057;");

        reviewTextLabel = new Label();
        reviewTextLabel.setWrapText(true);
        reviewTextLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        reviewTextLabel.setPadding(new Insets(5, 0, 0, 0));

        container.getChildren().addAll(ratingLabel, fromLabel, dateLabel, orderLabel, reviewTextLabel);
    }

    @Override
    protected void updateItem(Review review, boolean empty) {
        super.updateItem(review, empty);

        if (empty || review == null) {
            setGraphic(null);
            setText(null);
        } else {
            String stars = "★".repeat(review.getRating()) + "☆".repeat(5 - review.getRating());
            ratingLabel.setText(stars + " (" + review.getRating() + "/5)");

            if (review.getRating() >= 4) {
                ratingLabel.setStyle("-fx-text-fill: #1b8c2c;");
            } else if (review.getRating() == 3) {
                ratingLabel.setStyle("-fx-text-fill: #dfa603;");
            } else {
                ratingLabel.setStyle("-fx-text-fill: #bc0718;");
            }

            fromLabel.setText("From: " + review.getReviewOwner().getName() + " " + review.getReviewOwner().getSurname());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
            dateLabel.setText("Date: " + review.getDateCreated().format(formatter));

            orderLabel.setText("Order #" + review.getFoodOrder().getId());

            if (review.getReviewText() != null && !review.getReviewText().isEmpty()) {
                reviewTextLabel.setText(review.getReviewText());
            } else {
                reviewTextLabel.setText("(No comment provided)");
                reviewTextLabel.setStyle("-fx-text-fill: #6c757d; -fx-font-style: italic;");
            }

            setGraphic(container);
            setText(null);
        }
    }
}
