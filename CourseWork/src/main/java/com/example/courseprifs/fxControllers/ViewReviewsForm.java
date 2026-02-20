package com.example.courseprifs.fxControllers;

import com.example.courseprifs.HelloApplication;
import com.example.courseprifs.hibernateControl.CustomHibernate;
import com.example.courseprifs.model.BasicUser;
import com.example.courseprifs.model.Review;
import com.example.courseprifs.model.User;
import com.example.courseprifs.model.UserType;
import com.example.courseprifs.utils.JpaContext;
import com.example.courseprifs.utils.ReviewListCell;
import jakarta.persistence.EntityManagerFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ViewReviewsForm implements Initializable {
    @FXML
    private Label reviewLabel;
    @FXML
    private ListView<Review> reviewListView;
    @FXML
    private Button editReviewButton;
    @FXML
    private Button removeReviewButton;

    private BasicUser currentUser;
    private Review selectedReview;
    private final EntityManagerFactory emf = JpaContext.getEmf();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        editReviewButton.setDisable(true);
        removeReviewButton.setDisable(true);

        reviewListView.setCellFactory(param -> new ReviewListCell());

        reviewListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedReview = newValue;
            editReviewButton.setDisable(newValue == null);
            removeReviewButton.setDisable(newValue == null);
        });
    }

    public void setData(User user, boolean isAdmin) {
        currentUser = (BasicUser) user;
        editReviewButton.setVisible(isAdmin);
        removeReviewButton.setVisible(isAdmin);
        loadReviews();
    }

    @FXML
    private void loadReviews() {
        if (currentUser == null || currentUser.getUserType() == UserType.ADMIN) {
            reviewListView.getItems().clear();
            return;
        }

        CustomHibernate customHibernate = new CustomHibernate(emf);
        List<Review> reviews = customHibernate.getUserReviews((BasicUser) currentUser);

        reviewListView.getItems().setAll(reviews);
    }

    @FXML
    private void updateReview() throws IOException {
        if (selectedReview == null) return;

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("create-review-form.fxml"));
        Parent parent = fxmlLoader.load();

        CreateReviewForm createReviewForm = fxmlLoader.getController();

        CustomHibernate customHibernate = new CustomHibernate(emf);
        Review fullReview = customHibernate.getReviewById(selectedReview.getId());

        if (fullReview != null) {
            createReviewForm.setData(
                    fullReview.getReviewTarget(),
                    fullReview.getReviewOwner(),
                    fullReview.getFoodOrder(),
                    fullReview,
                    true
            );
        }

        Stage reviewStage = new Stage();
        reviewStage.setTitle("Update Review");
        reviewStage.setScene(new Scene(parent));
        reviewStage.setResizable(false);
        Stage mainStage = (Stage) reviewListView.getScene().getWindow();
        reviewStage.initOwner(mainStage);
        reviewStage.showAndWait();
        loadReviews();
    }

    @FXML
    private void deleteReview() {
        if (selectedReview == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Review");
        alert.setContentText("Are you sure you want to delete Review: " + selectedReview.getId() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK && selectedReview != null) {
                CustomHibernate customHibernate = new CustomHibernate(emf);
                customHibernate.delete(Review.class, selectedReview.getId());
                loadReviews();
                selectedReview = null;
                editReviewButton.setDisable(true);
                removeReviewButton.setDisable(true);
            }
        });
    }
}
