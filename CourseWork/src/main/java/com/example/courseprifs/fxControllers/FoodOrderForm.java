package com.example.courseprifs.fxControllers;

import com.example.courseprifs.hibernateControl.CustomHibernate;
import com.example.courseprifs.model.*;
import com.example.courseprifs.utils.JpaContext;
import jakarta.persistence.EntityManagerFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import static com.example.courseprifs.utils.Alerts.createSuccessAlert;
import static com.example.courseprifs.utils.Alerts.updateSuccessAlert;
import static com.example.courseprifs.utils.FxUtils.generateAlert;
import static com.example.courseprifs.utils.FxUtils.roundToTwoDecimals;

public class FoodOrderForm implements Initializable {
    @FXML
    private ComboBox<Driver> driverInput;
    @FXML
    private ComboBox<BasicUser> clientInput;
    @FXML
    private TextField totalPriceField;
    @FXML
    private ComboBox<Restaurant> restaurantInput;
    @FXML
    private ComboBox<Cuisine> cuisineInput;
    @FXML
    private Spinner<Integer> quantityInput;
    @FXML
    private TextField cuisinePriceField;
    @FXML
    private Button addCuisineButton;
    @FXML
    private Button removeCuisineButton;
    @FXML
    private TableView<FoodOrderItem> orderItemsTable;
    @FXML
    private TableColumn<FoodOrderItem, Cuisine> cuisineCol;
    @FXML
    private TableColumn<FoodOrderItem, Integer> quantityCol;
    @FXML
    private TableColumn<FoodOrderItem, Double> priceCol;
    @FXML
    private Button createOrderButton;
    @FXML
    private Button updateOrderButton;

    private final EntityManagerFactory emf = JpaContext.getEmf();
    private CustomHibernate customHibernate;
    private Runnable onOrderUpdate;
    private Cuisine selectedCuisine;
    private FoodOrder currentOrder;
    private boolean disabledDelete = false;
    private boolean isRestaurantUser = false;

    public void setOnOrderUpdated(Runnable callback) {
        this.onOrderUpdate = callback;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        customHibernate = new CustomHibernate(emf);
        updateOrderButton.setDisable(true);
        updateOrderButton.setVisible(false);
        addCuisineButton.setDisable(true);
        removeCuisineButton.setDisable(true);
        quantityInput.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100));
        quantityInput.setDisable(true);
        cuisineInput.setDisable(true);
        restaurantInput.getItems().addAll(customHibernate.getAllRecords(Restaurant.class));
        driverInput.getItems().add(null);
        driverInput.setPromptText("No driver");
        driverInput.getItems().addAll(customHibernate.getAllRecords(Driver.class));
        orderItemsTable.setEditable(true);

        List<BasicUser> allUsers = customHibernate.getAllRecords(BasicUser.class);
        List<BasicUser> clients = allUsers.stream()
                .filter(u -> u.getClass() == BasicUser.class)
                .toList();

        clientInput.getItems().addAll(clients);

        quantityCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        quantityCol.setOnEditCommit(event -> {
            FoodOrderItem item = event.getRowValue();
            Integer newQuantity = event.getNewValue();

            if (newQuantity == null || newQuantity <= 0) {
                generateAlert(Alert.AlertType.WARNING,
                        "Invalid quantity", null, "Quantity must be greater than 0");
                orderItemsTable.refresh();
                return;
            }

            item.setQuantity(newQuantity);
            item.setTotalPrice(
                    roundToTwoDecimals(newQuantity * item.getCuisine().getPrice())
            );

            updateTotalPrice();
            orderItemsTable.refresh();
        });

        cuisineCol.setCellValueFactory(new PropertyValueFactory<>("cuisine"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        orderItemsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            removeCuisineButton.setDisable(newSelection == null && disabledDelete);
        });
    }

    public void setData(boolean isEditing, FoodOrder currentOrder, boolean isRestaurantUser) {
        customHibernate = new CustomHibernate(emf);
        this.currentOrder = currentOrder;
        this.isRestaurantUser = isRestaurantUser;

        if (isEditing && currentOrder != null) {
            updateOrderButton.setVisible(true);
            updateOrderButton.setDisable(false);
            createOrderButton.setDisable(true);
            createOrderButton.setVisible(false);
            fillOrderFields(currentOrder);
        } else {
            updateOrderButton.setVisible(false);
            updateOrderButton.setDisable(true);
            createOrderButton.setDisable(false);
            createOrderButton.setVisible(true);
            clearFields();
        }
    }

    @FXML
    private void addCuisine() {
        if (selectedCuisine == null) {
            generateAlert(Alert.AlertType.ERROR, "Error", null, "Please select a cuisine");
            return;
        }

        if (isRestaurantUser) {
            generateAlert(Alert.AlertType.WARNING, "Action not allowed",
                    null, "Restaurant users cannot add new items to an order");
            return;
        }

        int quantity = quantityInput.getValue();
        if (quantity <= 0) {
            generateAlert(Alert.AlertType.ERROR, "Error", null, "Quantity must be positive");
            return;
        }

        for (FoodOrderItem item : orderItemsTable.getItems()) {
            if (item.getCuisine().getId() == selectedCuisine.getId()) {
                item.setQuantity(item.getQuantity() + quantity);
                item.setTotalPrice(roundToTwoDecimals(item.getQuantity() * selectedCuisine.getPrice()));
                orderItemsTable.refresh();
                updateTotalPrice();
                return;
            }
        }

        FoodOrderItem newItem = new FoodOrderItem();
        newItem.setCuisine(selectedCuisine);
        newItem.setQuantity(quantity);
        newItem.setTotalPrice(roundToTwoDecimals(quantity * selectedCuisine.getPrice()));
        orderItemsTable.getItems().add(newItem);
        quantityInput.getValueFactory().setValue(1);
        updateTotalPrice();
    }

    @FXML
    private void removeCuisine() {
        FoodOrderItem selected = orderItemsTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        orderItemsTable.getItems().remove(selected);
        updateTotalPrice();
    }

    @FXML
    private void createOrder() {
        try{
            Driver driver = driverInput.getValue();
            BasicUser client = clientInput.getValue();
            Restaurant restaurant = restaurantInput.getValue();

            if (client == null || restaurant == null) {
                generateAlert(Alert.AlertType.WARNING, "Validation error", null, "Client and restaurant must be selected");
                return;
            }

            if (orderItemsTable.getItems().isEmpty()) {
                generateAlert(Alert.AlertType.WARNING, "Validation error", null, "Order must contain at least one item");
                return;
            }

            double totalPrice = roundToTwoDecimals(
                    orderItemsTable.getItems()
                    .stream()
                    .mapToDouble(FoodOrderItem::getTotalPrice)
                    .sum()
            );

            FoodOrder order = new FoodOrder();
            order.setBuyer(client);
            order.setRestaurant(restaurant);
            order.setDriver(driver);
            order.setPrice(totalPrice);
            order.setOrderStatus(OrderStatus.PENDING);
            order.setDateCreated(java.time.LocalDate.now());
            order.setDateUpdated(java.time.LocalDate.now());

            for (FoodOrderItem item : orderItemsTable.getItems()) {
                item.setOrder(order);
            }
            order.setItems(orderItemsTable.getItems());
            order.setDateCreated(LocalDate.now());
            order.setDateUpdated(LocalDate.now());

            customHibernate.create(order);
            createSuccessAlert();
            if (onOrderUpdate != null) onOrderUpdate.run();
            closeWindow();
            clearFields();
        } catch (Exception e) {
            generateAlert(Alert.AlertType.ERROR, "Error", null,"Failed to create order: " +  e.getMessage());
        }
    }

    @FXML
    private void updateOrder() {
        if (currentOrder == null) {
            generateAlert(Alert.AlertType.WARNING, "Error", null, "No order selected for update");
            return;
        }

        if (currentOrder.getOrderStatus() == OrderStatus.IN_DELIVERY
                || currentOrder.getOrderStatus() == OrderStatus.COMPLETED) {
            generateAlert(Alert.AlertType.WARNING, "Update prohibited", null,
                    "This order has been picked up or completed and cannot be edited");
            return;
        }

        try{
            Driver selectedDriver = driverInput.getValue();
            Restaurant selectedRestaurant = restaurantInput.getValue();

            if (selectedRestaurant == null) {
                generateAlert(Alert.AlertType.WARNING, "Validation error", null, "Restaurant must be selected");
                return;
            }

            List<FoodOrderItem> newItems = orderItemsTable.getItems();
            if (newItems.isEmpty()) {
                generateAlert(Alert.AlertType.WARNING, "Validation error", null, "Order must contain at least one item");
                return;
            }

            List<FoodOrderItem> oldItems = customHibernate.getOrderItems(currentOrder);

            for (FoodOrderItem oldItem : oldItems) {
                boolean stillExists = newItems
                        .stream()
                        .anyMatch(i -> i.getId() != null && i.getId().equals(oldItem.getId()));
                if (!stillExists) customHibernate.delete(FoodOrderItem.class, oldItem.getId().intValue());
            }

            double totalPrice = 0;
            for (FoodOrderItem item : newItems) {
                item.setOrder(currentOrder);
                item.setTotalPrice(item.getQuantity() * item.getCuisine().getPrice());
                if (item.getId() != null) {
                    customHibernate.update(item);
                } else customHibernate.create(item);
                totalPrice += item.getTotalPrice();
            }
            totalPrice = roundToTwoDecimals(totalPrice);
            currentOrder.setItems(newItems);
            currentOrder.setDriver(selectedDriver);
            currentOrder.setRestaurant(selectedRestaurant);
            currentOrder.setPrice(totalPrice);
            currentOrder.setDateUpdated(LocalDate.now());

            customHibernate.update(currentOrder);
            updateSuccessAlert();
            if (onOrderUpdate != null) onOrderUpdate.run();
            closeWindow();
            clearFields();
        } catch (Exception e) {
            generateAlert(Alert.AlertType.ERROR, "Error", null,"Failed to update order: " +  e.getMessage());
        }
    }

    private void fillOrderFields(FoodOrder order) {
        if (order == null) {
            generateAlert(Alert.AlertType.WARNING, "Error", null, "No order selected for update");
            return;
        }

        clientInput.setValue(order.getBuyer());
        driverInput.setValue(order.getDriver());
        restaurantInput.setValue(order.getRestaurant());
        totalPriceField.setText(String.format("%.2f", order.getPrice()));

        orderItemsTable.getItems().clear();
        if (order.getItems() != null) {
            orderItemsTable.getItems().addAll(customHibernate.getOrderItems(order));
        }
        loadMenu();

        boolean uneditable = order.getOrderStatus() == OrderStatus.IN_DELIVERY
                || order.getOrderStatus() == OrderStatus.COMPLETED;

        if (isRestaurantUser) {
            uneditable = order.getOrderStatus() != OrderStatus.PENDING;
            clientInput.setDisable(true);
            driverInput.setDisable(true);
            restaurantInput.setDisable(true);
            cuisineInput.setVisible(false);
            quantityInput.setVisible(false);
            addCuisineButton.setVisible(false);
            cuisinePriceField.setVisible(false);
            removeCuisineButton.setVisible(!uneditable);
            orderItemsTable.setEditable(!uneditable);
            updateOrderButton.setVisible(!uneditable);
        } else {
            clientInput.setDisable(true);
            driverInput.setDisable(uneditable);
            restaurantInput.setDisable(true);
            cuisineInput.setVisible(!uneditable);
            quantityInput.setVisible(!uneditable);
            addCuisineButton.setVisible(!uneditable);
            cuisinePriceField.setVisible(!uneditable);
            updateOrderButton.setVisible(!uneditable);
            orderItemsTable.setEditable(!uneditable);
            disabledDelete = uneditable;
        }
    }

    @FXML
    private void loadMenu() {
        Restaurant selectedRestaurant = restaurantInput.getValue();
        if (selectedRestaurant == null) return;

        List<Cuisine> menu = customHibernate.getRestaurantCuisine(selectedRestaurant);
        cuisineInput.getItems().setAll(menu);
        cuisineInput.setDisable(false);
        quantityInput.setDisable(false);
        addCuisineButton.setDisable(false);
    }

    public void loadCuisine() {
        selectedCuisine = cuisineInput.getValue();
        if (selectedCuisine != null) cuisinePriceField.setText(String.format("%.2f", selectedCuisine.getPrice()));
    }

    private void updateTotalPrice() {
        double totalPrice = roundToTwoDecimals(
                orderItemsTable.getItems()
                .stream()
                .mapToDouble(FoodOrderItem::getTotalPrice)
                .sum()
        );
        totalPriceField.setText(String.format("%.2f", totalPrice));
    }

    private void clearFields() {
        orderItemsTable.getItems().clear();
        cuisineInput.getItems().clear();
        cuisineInput.setDisable(true);
        quantityInput.setDisable(true);
        addCuisineButton.setDisable(true);
        removeCuisineButton.setDisable(true);
        cuisinePriceField.setText("");
        totalPriceField.setText("");
    }

    private void closeWindow() {
        Stage stage = (Stage) cuisinePriceField.getScene().getWindow();
        stage.close();
    }
}
