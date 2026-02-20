package com.example.courseprifs.fxControllers;

import com.example.courseprifs.hibernateControl.CustomHibernate;
import com.example.courseprifs.model.BasicUser;
import com.example.courseprifs.model.FoodOrder;
import com.example.courseprifs.model.Review;
import com.example.courseprifs.utils.JpaContext;
import jakarta.persistence.EntityManagerFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import static com.example.courseprifs.utils.FxUtils.generateAlert;

public class CreateReviewForm implements Initializable {
    @FXML
    private Slider ratingInput;
    @FXML
    private TextField targetField;
    @FXML
    private TextArea descriptionInput;
    @FXML
    private Button createButton;
    @FXML
    private Button updateButton;

    private BasicUser targetUser;
    private BasicUser ownerUser;
    private FoodOrder order;
    private Review currentReview;
    private final EntityManagerFactory emf = JpaContext.getEmf();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ratingInput.setValue(5);
    }

    public void setData(BasicUser targetUser, BasicUser ownerUser, FoodOrder order, Review currentReview , boolean isForUpdate) {
        clearFields();
        this.targetUser = targetUser;
        this.ownerUser = ownerUser;
        this.order = order;
        this.currentReview = currentReview;

        targetField.setText(targetUser.getName() + " " + targetUser.getSurname());
        targetField.setEditable(false);

        if (isForUpdate) {
            updateButton.setVisible(true);
            createButton.setVisible(false);
            fillReviewData();
        } else {
            updateButton.setVisible(false);
            createButton.setVisible(true);
        }
    }

    private void clearFields() {
        ratingInput.setValue(5);
        descriptionInput.clear();
        targetField.clear();
    }

    private void fillReviewData() {
        if (currentReview != null) {
            ratingInput.setValue(currentReview.getRating());
            descriptionInput.setText(currentReview.getReviewText());
        }
    }

    @FXML
    private void createReview() {
        if(!validateInput()) return;

        int rating = (int) ratingInput.getValue();
        String reviewText = descriptionInput.getText().trim();

        Review newReview = new Review(rating, reviewText, ownerUser, targetUser, order);

        CustomHibernate customHibernate = new CustomHibernate(emf);
        customHibernate.createReview(newReview);

        closeWindow();
    }

    @FXML
    private void updateReview() {
        if(currentReview == null) {
            generateAlert(Alert.AlertType.ERROR, "Error", null, "No review to update");
        }
        if(!validateInput()) return;

        int rating = (int) ratingInput.getValue();
        String reviewText = descriptionInput.getText().trim();

        CustomHibernate customHibernate = new CustomHibernate(emf);
        customHibernate.updateReview(currentReview.getId(), rating, reviewText);

        closeWindow();
    }

    private boolean validateInput() {
        if (targetUser == null) {
            generateAlert(Alert.AlertType.ERROR, "Validation Error", null, "No target user selected");
            return false;
        }

        if (ownerUser == null) {
            generateAlert(Alert.AlertType.ERROR, "Validation Error", null, "No owner user found");
            return false;
        }

        if (order == null) {
            generateAlert(Alert.AlertType.ERROR, "Validation Error", null, "No order associated");
            return false;
        }

        String reviewText = descriptionInput.getText();
        if (reviewText == null || reviewText.trim().isEmpty()) {
            generateAlert(Alert.AlertType.WARNING, "Validation Error", null, "Please enter review text");
            return false;
        }

        return true;
    }

    public void closeWindow() {
        Stage stage = (Stage) targetField.getScene().getWindow();
        stage.close();
    }
}
