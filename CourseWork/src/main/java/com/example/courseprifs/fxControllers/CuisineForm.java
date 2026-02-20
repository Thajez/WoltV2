package com.example.courseprifs.fxControllers;

import com.example.courseprifs.hibernateControl.GenericHibernate;
import com.example.courseprifs.model.Cuisine;
import com.example.courseprifs.model.Restaurant;
import com.example.courseprifs.utils.JpaContext;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import jakarta.persistence.EntityManagerFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import static com.example.courseprifs.utils.Alerts.*;
import static com.example.courseprifs.utils.FxUtils.generateAlert;
import static com.example.courseprifs.utils.FxUtils.roundToTwoDecimals;

public class CuisineForm implements Initializable {
    @FXML
    private TextField cuisineTitleInput;
    @FXML
    private TextField cuisinePriceInput;
    @FXML
    private CheckBox isSpicy;
    @FXML
    private CheckBox isVegan;
    @FXML
    private TextArea ingredientInput;
    @FXML
    private Button saveFoodButton;
    @FXML
    private Button updateFoodButton;

    private final EntityManagerFactory emf = JpaContext.getEmf();
    private GenericHibernate genericHibernate;
    private Cuisine currentCuisine;
    private Restaurant currentRestaurant;
    private Runnable onCuisineUpdate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateFoodButton.setVisible(false);
        updateFoodButton.setDisable(true);
    }

    public void setData(Restaurant restaurant, boolean isEditing, Cuisine currentCuisine) {
        this.genericHibernate = new GenericHibernate(emf);
        currentRestaurant = restaurant;

        if (isEditing) {
            saveFoodButton.setVisible(false);
            saveFoodButton.setDisable(true);
            updateFoodButton.setVisible(true);
            updateFoodButton.setDisable(false);
            fillCuisineForEdit(currentCuisine);
        } else {
            saveFoodButton.setVisible(true);
            saveFoodButton.setDisable(false);
            updateFoodButton.setVisible(false);
            updateFoodButton.setDisable(true);
        }
    }

    public void setOnCuisineUpdated(Runnable callback) {
        this.onCuisineUpdate = callback;
    }

    @FXML
    private void createNewCuisine() {
        try {
            String name = cuisineTitleInput.getText();
            String ingredients = ingredientInput.getText();
            String priceText = cuisinePriceInput.getText();
            boolean spicy = isSpicy.isSelected();
            boolean vegan = isVegan.isSelected();

            double price;
            try {
                price = Double.parseDouble(priceText);
            }catch (NumberFormatException e) {
                generateAlert(Alert.AlertType.ERROR, "Error",null, "Invalid price format");
                return;
            }
            if (validateCuisine(name, ingredients, price)) return;

            price = roundToTwoDecimals(price);
            Cuisine cuisine = new Cuisine(name, ingredients, price, spicy, vegan, currentRestaurant);
            genericHibernate.create(cuisine);

            createSuccessAlert();
            if (onCuisineUpdate != null) onCuisineUpdate.run();
            closeWindow();
            clearFields();
        } catch (Exception e) {
            generateAlert(Alert.AlertType.ERROR, "Error",null, "Failed to create cuisine: " + e.getMessage());
        }
    }

    @FXML
    private void updateCuisine() {
        try {
            if (currentCuisine == null) {
                generateAlert(Alert.AlertType.WARNING, "Error", null, "No cuisine selected for update");
                return;
            }

            String name = cuisineTitleInput.getText();
            String ingredients = ingredientInput.getText();
            String priceText = cuisinePriceInput.getText();
            boolean spicy = isSpicy.isSelected();
            boolean vegan = isVegan.isSelected();

            double price;
            try {
                price = Double.parseDouble(priceText);
            } catch (NumberFormatException e) {
                generateAlert(Alert.AlertType.ERROR, "Error", null, "Invalid price format");
                return;
            }
            if (validateCuisine(name, ingredients, price)) return;

            price = roundToTwoDecimals(price);
            currentCuisine.setName(name);
            currentCuisine.setIngredients(ingredients);
            currentCuisine.setPrice(price);
            currentCuisine.setSpicy(spicy);
            currentCuisine.setVegan(vegan);

            genericHibernate.update(currentCuisine);

            updateSuccessAlert();
            if (onCuisineUpdate != null) {
                onCuisineUpdate.run();
            }
            closeWindow();
            clearFields();
        } catch (Exception e) {
            generateAlert(Alert.AlertType.ERROR, "Error", null, "Failed to update cuisine: " + e.getMessage());
        }
    }

    private void fillCuisineForEdit(Cuisine cuisine) {
        this.currentCuisine = cuisine;
        cuisineTitleInput.setText(cuisine.getName());
        cuisinePriceInput.setText(String.format("%.2f", cuisine.getPrice()));
        ingredientInput.setText(cuisine.getIngredients());
        isSpicy.setSelected(cuisine.isSpicy());
        isVegan.setSelected(cuisine.isVegan());

        saveFoodButton.setVisible(false);
        saveFoodButton.setManaged(false);
        updateFoodButton.setVisible(true);
        updateFoodButton.setManaged(true);
    }

    private boolean validateCuisine(String name, String ingredients, double price) {
        if (name.isEmpty() || ingredients.isEmpty()) {
            missingFieldsAlert();
            return true;
        }
        if (price <= 0) {
            generateAlert(Alert.AlertType.ERROR, "Error",null, "Price must be positive");
            return true;
        }
        return false;
    }

    private void clearFields() {
        cuisineTitleInput.clear();
        cuisinePriceInput.clear();
        ingredientInput.clear();
        isSpicy.setSelected(false);
        isVegan.setSelected(false);
        currentCuisine = null;

        saveFoodButton.setVisible(true);
        saveFoodButton.setManaged(true);
        updateFoodButton.setVisible(false);
        updateFoodButton.setManaged(false);
    }

    private void closeWindow() {
        Stage stage = (Stage) cuisineTitleInput.getScene().getWindow();
        stage.close();
    }
}