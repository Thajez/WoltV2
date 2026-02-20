package com.example.courseprifs.fxControllers;

import com.example.courseprifs.HelloApplication;
import com.example.courseprifs.hibernateControl.CustomHibernate;
import com.example.courseprifs.model.*;
import com.example.courseprifs.utils.JpaContext;
import jakarta.persistence.EntityManagerFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import static com.example.courseprifs.utils.Alerts.deleteSuccessAlert;
import static com.example.courseprifs.utils.FxUtils.generateAlert;
import static com.example.courseprifs.utils.ToastUtils.generateToast;

public class MainForm implements Initializable {
    /// USER VARIABLES
    @FXML
    private TabPane tabsPane;
    @FXML
    private Tab userTab;
    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User,Integer> idCol;
    @FXML
    private TableColumn<User,UserType> userTypeCol;
    @FXML
    private TableColumn<User,String> loginCol;
    @FXML
    private TableColumn<User,String> nameCol;
    @FXML
    private TableColumn<User,String> surnameCol;
    @FXML
    private TableColumn<User,String> addrCol;
    @FXML
    private TableColumn<User,String> licenceCol;
    @FXML
    private TableColumn<User,String> phoneCol;
    @FXML
    private TableColumn<User, LocalDate> bDateCol;
    @FXML
    private TableColumn<User, VehicleType> vehicleCol;
    @FXML
    private ComboBox<UserType> userTypeFilter;
    @FXML
    private TextField nameFilter;
    @FXML
    private TextField surnameFilter;
    @FXML
    private TextField phoneFilter;
    @FXML
    private TextField idFilter;
    @FXML
    private Button filterUserButton;
    @FXML
    private Button clearUserFilterButton;
    @FXML
    private Button createUserButton;
    @FXML
    private Button editUserButton;
    @FXML
    private Button deleteUserButton;

    private User selectedUser;

    /// CUISINE VARIABLES
    @FXML
    private Label restaurantLabel;
    @FXML
    private CheckBox isSpicy;
    @FXML
    private CheckBox isVegan;
    @FXML
    private TextField cuisinePriceField;
    @FXML
    private ListView<Restaurant> restaurantList;
    @FXML
    private TextArea ingredientsField;
    @FXML
    private TextField titleCuisineField;
    @FXML
    private ListView<Cuisine> cuisineList;
    @FXML
    private Tab foodTab;
    @FXML
    private Button createCuisineButton;
    @FXML
    private Button updateCuisineButton;
    @FXML
    private Button deleteCuisineButton;

    private Restaurant currentRestaurant;
    private Cuisine selectedCuisine;

    /// FOOD ORDER VARIABLES
    @FXML
    private Tab foodOrderTab;
    @FXML
    private ComboBox<BasicUser> clientFilter;
    @FXML
    private ComboBox<Driver> driverFilter;
    @FXML
    private ComboBox<OrderStatus> statusFilter;
    @FXML
    private DatePicker dateFromFilter;
    @FXML
    private DatePicker dateToFilter;
    @FXML
    private TextField orderIdFilter;
    @FXML
    private ComboBox<Restaurant> restaurantFilter;
    @FXML
    private Button filterOrderButton;
    @FXML
    private Button clearOrderFilterButton;
    @FXML
    private TableView<FoodOrder> orderTable;
    @FXML
    private TableColumn<FoodOrder, Integer> orderIdCol;
    @FXML
    private TableColumn<FoodOrder, Double> orderPriceCol;
    @FXML
    private TableColumn<FoodOrder, String> orderClientCol;
    @FXML
    private TableColumn<FoodOrder, String> orderDriverCol;
    @FXML
    private TableColumn<FoodOrder, String> orderRestaurantCol;
    @FXML
    private TableColumn<FoodOrder, LocalDate> orderCreatedCol;
    @FXML
    private TableColumn<FoodOrder, LocalDate> orderUpdatedCol;
    @FXML
    private TableColumn<FoodOrder, OrderStatus> orderStatusCol;
    @FXML
    private ListView<FoodOrderItem> orderedFoodList;
    @FXML
    private Button createOrderButton;
    @FXML
    private Button updateOrderButton;
    @FXML
    private Button deleteOrderButton;
    @FXML
    private Button viewChatButton;
    @FXML
    private Button startOrderButton;
    @FXML
    private Button readyOrderButton;

    private FoodOrder selectedOrder;
    private List<FoodOrderItem> selectedOrderItems;

    /// USER SETTINGS VARIABLES
    @FXML
    private Button logoutButton;
    @FXML
    private Button changePasswordButton;
    @FXML
    private Button editDetailsButton;
    @FXML
    private TextField loginDetail;
    @FXML
    private TextField nameDetail;
    @FXML
    private TextField surnameDetail;
    @FXML
    private TextField phoneDetail;
    @FXML
    private TextField idDetail;
    @FXML
    private TextField addressDetail;
    @FXML
    private Label addressLabel;
    @FXML
    private TextField restaurantTypeDetail;
    @FXML
    private TextField openTimeDetail;
    @FXML
    private TextField closeTimeDetail;
    @FXML
    private Label typeLabel;
    @FXML
    private Label openingLabel;
    @FXML
    private Label closingLabel;

    /// REVIEW VARIABLES
    @FXML
    private Button viewSelfReviewsButton;
    @FXML
    private Button viewUserReviewsButton;
    @FXML
    private Button leaveReviewButton;

    /// INITIALIZATION
    EntityManagerFactory emf = JpaContext.getEmf();
    User currentUser;
    boolean isAdmin = false;
    boolean isRestaurant = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        CustomHibernate customHibernate = new CustomHibernate(emf);
        userTypeFilter.getItems().add(null);
        userTypeFilter.setPromptText("All user types");
        userTypeFilter.getItems().addAll(UserType.values());
        statusFilter.getItems().add(null);
        statusFilter.setPromptText("Any status");
        statusFilter.getItems().addAll(OrderStatus.values());
        driverFilter.getItems().add(null);
        driverFilter.setPromptText("All drivers");
        driverFilter.getItems().addAll(customHibernate.getAllRecords(Driver.class));
        restaurantFilter.getItems().add(null);
        restaurantFilter.setPromptText("All restaurants");
        restaurantFilter.getItems().addAll(customHibernate.getAllRecords(Restaurant.class));
        clientFilter.getItems().add(null);
        clientFilter.setPromptText("All clients");

        List<BasicUser> allUsers = customHibernate.getAllRecords(BasicUser.class);
        List<BasicUser> clients = allUsers.stream()
                .filter(u -> u.getClass() == BasicUser.class)
                .toList();

        clientFilter.getItems().addAll(clients);

        editUserButton.setDisable(true);
        deleteUserButton.setDisable(true);
        updateCuisineButton.setDisable(true);
        createCuisineButton.setDisable(true);
        deleteCuisineButton.setDisable(true);
        updateOrderButton.setDisable(true);
        deleteOrderButton.setDisable(true);
        startOrderButton.setVisible(false);
        startOrderButton.setDisable(true);
        readyOrderButton.setVisible(false);
        readyOrderButton.setDisable(true);
        viewUserReviewsButton.setDisable(true);
        leaveReviewButton.setDisable(true);

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        userTypeCol.setCellValueFactory(new PropertyValueFactory<>("userType"));
        loginCol.setCellValueFactory(new PropertyValueFactory<>("login"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        surnameCol.setCellValueFactory(new PropertyValueFactory<>("surname"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        addrCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        licenceCol.setCellValueFactory(new PropertyValueFactory<>("licence"));
        bDateCol.setCellValueFactory(new PropertyValueFactory<>("bDate"));
        vehicleCol.setCellValueFactory(new PropertyValueFactory<>("vehicleType"));

        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        orderPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        orderClientCol.setCellValueFactory(new PropertyValueFactory<>("buyer"));
        orderDriverCol.setCellValueFactory(new PropertyValueFactory<>("driver"));
        orderRestaurantCol.setCellValueFactory(new PropertyValueFactory<>("restaurant"));
        orderCreatedCol.setCellValueFactory(new PropertyValueFactory<>("dateCreated"));
        orderUpdatedCol.setCellValueFactory(new PropertyValueFactory<>("dateUpdated"));
        orderStatusCol.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));

        foodTab.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (!isSelected) {
                clearCuisineView();
            } else {
                loadRestaurantMenu();
            }
        });

        orderTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedOrder = newSelection;

            boolean hasSelection = newSelection != null;

            updateOrderButton.setDisable(!hasSelection);
            deleteOrderButton.setDisable(!hasSelection);
            viewChatButton.setDisable(!hasSelection);

            if (hasSelection) {
                boolean reviewExists = customHibernate.reviewExistsForOrder(selectedOrder.getId());

                switch (newSelection.getOrderStatus()) {
                    case COMPLETED -> {
                        startOrderButton.setVisible(false);
                        startOrderButton.setDisable(true);
                        readyOrderButton.setVisible(false);
                        readyOrderButton.setDisable(true);
                        leaveReviewButton.setDisable(reviewExists || isAdmin);
                    }
                    case PENDING -> {
                        startOrderButton.setVisible(true);
                        startOrderButton.setDisable(false);
                        readyOrderButton.setVisible(false);
                        readyOrderButton.setDisable(true);
                        leaveReviewButton.setDisable(true);
                    }
                    case STARTED -> {
                        startOrderButton.setVisible(false);
                        startOrderButton.setDisable(true);
                        readyOrderButton.setVisible(true);
                        readyOrderButton.setDisable(false);
                        leaveReviewButton.setDisable(true);
                    }
                    default -> {
                        startOrderButton.setVisible(false);
                        startOrderButton.setDisable(true);
                        readyOrderButton.setVisible(false);
                        readyOrderButton.setDisable(true);
                        leaveReviewButton.setDisable(true);
                    }
                }

                selectedOrderItems = customHibernate.getOrderItems(newSelection);
                orderedFoodList.getItems().setAll(selectedOrderItems);
            } else {
                startOrderButton.setVisible(false);
                startOrderButton.setDisable(true);
                readyOrderButton.setVisible(false);
                readyOrderButton.setDisable(true);
                leaveReviewButton.setDisable(true);

                selectedOrderItems = null;
                orderedFoodList.getItems().clear();
            }
        });

        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedUser = newSelection;
            editUserButton.setDisable(newSelection == null);
            deleteUserButton.setDisable(newSelection == null);
            viewUserReviewsButton.setDisable(newSelection == null);
        });

        restaurantList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            clearCuisineView();
            currentRestaurant = newSelection;
            createCuisineButton.setDisable(newSelection == null);
            reloadCuisine();
        });

        cuisineList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedCuisine = newSelection;
            updateCuisineButton.setDisable(newSelection == null);
            deleteCuisineButton.setDisable(newSelection == null);
            fillCuisineFields(selectedCuisine);
        });

    }

    public void setData(User user) {
        this.currentUser = user;
        isAdmin = currentUser.getUserType().equals(UserType.ADMIN);

        hideUI(isAdmin);

        if (user instanceof Restaurant restaurantUser) {
            isRestaurant = true;
            currentRestaurant = restaurantUser;
            loadRestaurantMenu();
            loadOrderList();
        }
        filterUsers();
    }

    private void hideUI(boolean isAdmin) {
        if (!isAdmin) {
            tabsPane.getTabs().remove(userTab);
            restaurantList.setVisible(false);
            restaurantList.setDisable(true);
            restaurantLabel.setVisible(false);
            restaurantFilter.setVisible(false);
            restaurantFilter.setDisable(true);
            createOrderButton.setVisible(false);
            createOrderButton.setDisable(true);
            deleteOrderButton.setVisible(false);
            deleteOrderButton.setDisable(true);
        }
    }

    /// USER MANAGEMENT
    @FXML
    private void filterUsers() {
        Integer id = null;
        String idText = idFilter.getText().trim();
        if (!idText.isEmpty()) {
            try {
                id = Integer.parseInt(idText);
            } catch (NumberFormatException e) {
                generateAlert(Alert.AlertType.ERROR, "Invalid ID", null, "ID must be a number");
                return;
            }
        }

        UserType userType = userTypeFilter.getValue();
        String name = nameFilter.getText().trim().isEmpty() ? null : nameFilter.getText().trim();
        String surname = surnameFilter.getText().trim().isEmpty() ? null : surnameFilter.getText().trim();
        String phone = phoneFilter.getText().trim().isEmpty() ? null : phoneFilter.getText().trim();

        CustomHibernate customHibernate = new CustomHibernate(emf);
        if (id == null && userType == null && name == null && surname == null && phone == null) {
            userTable.getItems().setAll(customHibernate.getAllRecords(User.class));
        } else {
            userTable.getItems().setAll(customHibernate.getFilteredUsers(id, userType, name, surname, phone));
        }
    }

    @FXML
    private void createNewUser() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("user-form.fxml"));
        Parent parent = fxmlLoader.load();

        UserForm userForm = fxmlLoader.getController();
        userForm.setData(null, false, false);

        userForm.setOnUserUpdated(this::filterUsers);

        Stage registerStage = new Stage();
        registerStage.setTitle("Register new user");
        registerStage.setScene(new Scene(parent));
        registerStage.setResizable(false);
        Stage mainStage = (Stage) tabsPane.getScene().getWindow();
        registerStage.initOwner(mainStage);
        registerStage.show();
    }

    @FXML
    private void editUser() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("user-form.fxml"));
        Parent parent = fxmlLoader.load();

        UserForm userForm = fxmlLoader.getController();
        userForm.setData(selectedUser, true, false);

        userForm.setOnUserUpdated(this::filterUsers);

        Stage registerStage = new Stage();
        registerStage.setTitle("Update user");
        registerStage.setScene(new Scene(parent));
        registerStage.setResizable(false);
        Stage mainStage = (Stage) tabsPane.getScene().getWindow();
        registerStage.initOwner(mainStage);
        registerStage.show();
    }

    @FXML
    private void deleteUser() {
        if (selectedUser == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete User");
        alert.setContentText("Are you sure you want to delete user: " + selectedUser.getLogin() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK && selectedUser != null) {
                    CustomHibernate customHibernate = new CustomHibernate(emf);
                    customHibernate.delete(User.class, selectedUser.getId());
                    filterUsers();
                    selectedUser = null;
                    editUserButton.setDisable(true);
                    deleteUserButton.setDisable(true);
                    deleteSuccessAlert();
            }
        });
    }

    @FXML
    private void clearUserFilters() {
        idFilter.clear();
        userTypeFilter.setValue(null);
        nameFilter.clear();
        surnameFilter.clear();
        phoneFilter.clear();
        filterUsers();
        selectedUser = null;
        editUserButton.setDisable(true);
        deleteUserButton.setDisable(true);
    }

    /// CUISINE MANAGEMENT
    private void reloadCuisine() {
        if (currentRestaurant == null) return;

        clearCuisineView();
        CustomHibernate customHibernate = new CustomHibernate(emf);
        List<Cuisine> menu = customHibernate.getRestaurantCuisine(currentRestaurant);
        cuisineList.getItems().setAll(menu);
    }

    @FXML
    private void loadRestaurantList() {
        if(isRestaurant) return;
        CustomHibernate customHibernate = new CustomHibernate(emf);
        restaurantList.getItems().setAll(customHibernate.getAllRecords(Restaurant.class));
    }

    private void loadRestaurantMenu() {
        if (currentRestaurant != null) {
            createCuisineButton.setDisable(false);
            reloadCuisine();
        }
    }

    @FXML
    private void createNewCuisine() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("cuisine-form.fxml"));
        Parent parent = fxmlLoader.load();

        CuisineForm cuisineForm = fxmlLoader.getController();
        cuisineForm.setData(currentRestaurant, false, null);

        cuisineForm.setOnCuisineUpdated(this::reloadCuisine);

        Stage cuisineStage = new Stage();
        cuisineStage.setTitle("Create new cuisine");
        cuisineStage.setScene(new Scene(parent));
        cuisineStage.setResizable(false);
        Stage mainStage = (Stage) tabsPane.getScene().getWindow();
        cuisineStage.initOwner(mainStage);
        cuisineStage.show();
        reloadCuisine();
    }

    @FXML
    private void editCuisine() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("cuisine-form.fxml"));
        Parent parent = fxmlLoader.load();

        CuisineForm cuisineForm = fxmlLoader.getController();
        cuisineForm.setData(currentRestaurant,true, selectedCuisine);

        cuisineForm.setOnCuisineUpdated(this::reloadCuisine);

        Stage cuisineStage = new Stage();
        cuisineStage.setTitle("Edit cuisine");
        cuisineStage.setScene(new Scene(parent));
        cuisineStage.setResizable(false);
        Stage mainStage = (Stage) tabsPane.getScene().getWindow();
        cuisineStage.initOwner(mainStage);
        cuisineStage.show();
        reloadCuisine();
    }

    @FXML
    private void deleteCuisine() {
        if (selectedCuisine == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Cuisine");
        alert.setContentText("Are you sure you want to delete Cuisine: " + selectedCuisine.getName() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK && selectedCuisine != null) {
                CustomHibernate customHibernate = new CustomHibernate(emf);
                customHibernate.delete(Cuisine.class, selectedCuisine.getId());
                reloadCuisine();
                selectedCuisine = null;
                updateCuisineButton.setDisable(true);
                deleteCuisineButton.setDisable(true);
                deleteSuccessAlert();
            }
        });
    }

    private void fillCuisineFields(Cuisine cuisine) {
        if (cuisine != null) {
            titleCuisineField.setText(cuisine.getName());
            ingredientsField.setText(cuisine.getIngredients());
            cuisinePriceField.setText(String.format("%.2f", cuisine.getPrice()));
            isSpicy.setSelected(cuisine.isSpicy());
            isVegan.setSelected(cuisine.isVegan());
        }
        else {
            selectedCuisine = null;
        }
    }

    private void clearCuisineView() {
        cuisineList.getSelectionModel().clearSelection();
        cuisineList.getItems().clear();
        titleCuisineField.clear();
        ingredientsField.clear();
        cuisinePriceField.clear();
        isSpicy.setSelected(false);
        isVegan.setSelected(false);
        selectedCuisine = null;
        titleCuisineField.setEditable(false);
        ingredientsField.setEditable(false);
        cuisinePriceField.setEditable(false);
        isSpicy.setMouseTransparent(true);
        isVegan.setMouseTransparent(true);
    }

    /// FOOD ORDER MANAGEMENT
    @FXML
    private void loadChatForm() {
        if (selectedOrder == null) return;

        try {
            CustomHibernate customHibernate = new CustomHibernate(emf);
            Chat chat = customHibernate.getOrCreateChatForOrder(selectedOrder);

            if(chat == null) {
                generateAlert(Alert.AlertType.ERROR, "Error", null, "Could not load chat for order: " + selectedOrder.getId());
                return;
            }

            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("chat-form.fxml"));
            Parent parent = fxmlLoader.load();

            ChatForm chatFormController = fxmlLoader.getController();
            chatFormController.setData(currentUser, selectedOrder, chat);

            Stage stage = new Stage();
            stage.setTitle("Chat for Order #" + selectedOrder.getId());
            stage.setScene(new Scene(parent));
            stage.setResizable(false);
            Stage mainStage = (Stage) tabsPane.getScene().getWindow();
            stage.initOwner(mainStage);
            stage.show();
        } catch (IOException e) {
            generateAlert(Alert.AlertType.ERROR, "Error", null, "Could not load chat form: " + e.getMessage());
        }
    }

    @FXML
    private void filterOrders() {
        Integer id = null;
        String idText = orderIdFilter.getText().trim();
        if (!idText.isEmpty()) {
            try {
                id = Integer.parseInt(idText);
            } catch (NumberFormatException e) {
                generateAlert(Alert.AlertType.ERROR, "Invalid ID", null, "ID must be a number");
                return;
            }
        }
        LocalDate fromDate = dateFromFilter.getValue();
        LocalDate toDate = dateToFilter.getValue();
        OrderStatus status = statusFilter.getValue();
        BasicUser client = clientFilter.getValue();
        Driver driver = driverFilter.getValue();
        Restaurant restaurant;
        if(isAdmin) {
            restaurant = restaurantFilter.getValue();
        } else {
            restaurant = currentRestaurant;
        }

        CustomHibernate customHibernate = new CustomHibernate(emf);
        if (id == null && fromDate == null && toDate == null && status == null && client == null && driver == null) {
            if (isAdmin && restaurant == null) {
                orderTable.getItems().setAll(customHibernate.getAllRecords(FoodOrder.class));
            } else orderTable.getItems().setAll(customHibernate.getRestaurantOrders(restaurant));
        } else {
            orderTable.getItems().setAll(customHibernate.getFilteredRestaurantOrders(id,fromDate,toDate,status,client,driver,restaurant));
        }
    }

    @FXML
    private void clearOrderFilters() {
        orderIdFilter.clear();
        dateToFilter.setValue(null);
        dateFromFilter.setValue(null);
        statusFilter.setValue(null);
        clientFilter.setValue(null);
        driverFilter.setValue(null);
        restaurantFilter.setValue(null);
        filterOrders();
    }

    @FXML
    private void createOrder() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("order-form.fxml"));
        Parent parent = fxmlLoader.load();

        FoodOrderForm orderForm = fxmlLoader.getController();
        orderForm.setData(false, null, false);

        orderForm.setOnOrderUpdated(this::loadOrderList);

        Stage orderStage = new Stage();
        orderStage.setTitle("Create new order");
        orderStage.setScene(new Scene(parent));
        orderStage.setResizable(false);
        Stage mainStage = (Stage) tabsPane.getScene().getWindow();
        orderStage.initOwner(mainStage);
        orderStage.show();
    }

    @FXML
    private void updateOrder() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("order-form.fxml"));
        Parent parent = fxmlLoader.load();

        FoodOrderForm orderForm = fxmlLoader.getController();
        boolean isRestaurantUser = currentUser instanceof Restaurant restaurantUser && restaurantUser.equals(currentRestaurant);
        orderForm.setData(true, orderTable.getSelectionModel().getSelectedItem(), isRestaurantUser);

        orderForm.setOnOrderUpdated(this::loadOrderList);

        Stage orderStage = new Stage();
        orderStage.setTitle("Edit order");
        orderStage.setScene(new Scene(parent));
        orderStage.setResizable(false);
        Stage mainStage = (Stage) tabsPane.getScene().getWindow();
        orderStage.initOwner(mainStage);
        orderStage.show();
    }

    @FXML
    private void deleteOrder() {
        if (selectedOrder == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Order");
        alert.setContentText("Are you sure you want to delete Order: " + selectedOrder.getId() + " and it's history?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK && selectedOrder != null) {
                CustomHibernate customHibernate = new CustomHibernate(emf);
                customHibernate.delete(FoodOrder.class, selectedOrder.getId());
                loadOrderList();
                selectedOrder = null;
                updateOrderButton.setDisable(true);
                deleteOrderButton.setDisable(true);
                deleteSuccessAlert();
            }
        });
    }

    @FXML
    private void loadOrderList() {
        if (!isAdmin && currentRestaurant == null) {
            return;
        }

        CustomHibernate customHibernate = new CustomHibernate(emf);
        List<FoodOrder> orders;
        if(isAdmin) {
            orders = customHibernate.getAllRecords(FoodOrder.class);
        } else {
            orders = customHibernate.getRestaurantOrders(currentRestaurant);
        }
        orderTable.getItems().setAll(orders);
    }

    @FXML
    private void startOrder() {
        if (selectedOrder != null) {
            selectedOrder.setOrderStatus(OrderStatus.STARTED);
            CustomHibernate customHibernate = new CustomHibernate(emf);
            customHibernate.update(selectedOrder);
            generateToast(Alert.AlertType.INFORMATION, "Order has been started");
            loadOrderList();
        }
    }

    @FXML
    private void readyOrder() {
        if (selectedOrder != null) {
            selectedOrder.setOrderStatus(OrderStatus.READY);
            CustomHibernate customHibernate = new CustomHibernate(emf);
            customHibernate.update(selectedOrder);
            generateToast(Alert.AlertType.INFORMATION, "Order is ready for pickup");
            loadOrderList();
        }
    }


    /// USER DETAIL MANAGEMENT
    @FXML
    private void logout() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-form.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = (Stage) tabsPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.setResizable(false);
        } catch (IOException e) {
            generateAlert(Alert.AlertType.ERROR, "Error", null, "Could not load login form.");
        }
    }

    @FXML
    private void changePassword() {
        if (currentUser == null) return;

        try{
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("change-pass-form.fxml"));
            Parent parent = fxmlLoader.load();

            PasswordForm passwordForm = fxmlLoader.getController();
            passwordForm.setData(currentUser);

            Stage stage = new Stage();
            stage.setTitle("Change Password");
            stage.setScene(new Scene(parent));
            stage.setResizable(false);
            Stage mainStage = (Stage) tabsPane.getScene().getWindow();
            stage.initOwner(mainStage);
            stage.show();
        } catch (Exception e) {
            generateAlert(Alert.AlertType.ERROR, "Error", null, "Could not change password. a" + e.getMessage());
        }
    }

    @FXML
    private void loadDetails() {
        if (currentUser == null) return;

        loginDetail.setText(currentUser.getLogin());
        nameDetail.setText(currentUser.getName());
        surnameDetail.setText(currentUser.getSurname());
        phoneDetail.setText(currentUser.getPhoneNumber());
        idDetail.setText(String.valueOf(currentUser.getId()));

        if (currentUser instanceof Restaurant restaurant) {
            addressDetail.setText(restaurant.getAddress());
            addressDetail.setVisible(true);
            addressLabel.setVisible(true);
            restaurantTypeDetail.setText(restaurant.getTypeOfRestaurant());
            restaurantTypeDetail.setVisible(true);
            typeLabel.setVisible(true);
            openTimeDetail.setText(restaurant.getOpeningTime().toString());
            openTimeDetail.setVisible(true);
            openingLabel.setVisible(true);
            closeTimeDetail.setText(restaurant.getClosingTime().toString());
            closeTimeDetail.setVisible(true);
            closingLabel.setVisible(true);
            viewSelfReviewsButton.setVisible(true);
        } else {
            addressDetail.clear();
            addressDetail.setVisible(false);
            addressLabel.setVisible(false);
            restaurantTypeDetail.clear();
            restaurantTypeDetail.setVisible(false);
            typeLabel.setVisible(false);
            openTimeDetail.clear();
            openTimeDetail.setVisible(false);
            openingLabel.setVisible(false);
            closeTimeDetail.clear();
            closeTimeDetail.setVisible(false);
            closingLabel.setVisible(false);
            viewSelfReviewsButton.setVisible(false);
        }
    }

    @FXML
    private void editUserDetails() throws IOException {
        if (currentUser == null) return;

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("user-form.fxml"));
        Parent parent = fxmlLoader.load();

        UserForm userForm = fxmlLoader.getController();
        userForm.setData(currentUser, true, false);

        userForm.setOnUserUpdated(this::loadDetails);

        Stage stage = new Stage();
        stage.setTitle("Edit User Details");
        stage.setScene(new Scene(parent));
        stage.setResizable(false);
        Stage mainStage = (Stage) tabsPane.getScene().getWindow();
        stage.initOwner(mainStage);
        stage.show();
    }

    /// REVIEW MANAGEMENT
    @FXML
    private void viewReviews() throws IOException {
        if (selectedUser == null) {
            if (currentUser instanceof Restaurant restaurant) {
                try {
                    navigateToReviews(restaurant);
                } catch (IOException e) {
                    generateAlert(Alert.AlertType.ERROR, "Error", null, "Could not load reviews form.");
                }
            }
        } else if (!(selectedUser.getUserType().equals(UserType.ADMIN))) {
            navigateToReviews(selectedUser);
        } else {
            generateAlert(Alert.AlertType.ERROR, "Error", null, "Admins do not have reviews.");
        }
    }

    private void navigateToReviews(User user) throws IOException {
        if (user == null) return;

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("view-reviews-form.fxml"));
        Parent parent = fxmlLoader.load();

        boolean admin = currentUser.getUserType().equals(UserType.ADMIN);

        ViewReviewsForm viewReviewsForm = fxmlLoader.getController();
        viewReviewsForm.setData(user, admin);

        Stage stage = new Stage();
        stage.setTitle("View Reviews");
        stage.setScene(new Scene(parent));
        stage.setResizable(false);
        Stage mainStage = (Stage) tabsPane.getScene().getWindow();
        stage.initOwner(mainStage);
        stage.show();
    }

    @FXML
    private void leaveReview() throws IOException {
        if (selectedOrder == null || !selectedOrder.getOrderStatus().equals(OrderStatus.COMPLETED)) return;

        CustomHibernate customHibernate = new CustomHibernate(emf);
        if (customHibernate.reviewExistsForOrder(selectedOrder.getId())) {
            generateAlert(Alert.AlertType.WARNING, "Review Already Exists", null,
                    "A review has already been submitted for this order.");
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("create-review-form.fxml"));
        Parent parent = fxmlLoader.load();

        CreateReviewForm createReviewForm = fxmlLoader.getController();
        createReviewForm.setData(
                selectedOrder.getBuyer(),
                currentRestaurant,
                selectedOrder,
                null,
                false
        );

        Stage stage = new Stage();
        stage.setTitle("Leave Review");
        stage.setScene(new Scene(parent));
        stage.setResizable(false);
        Stage mainStage = (Stage) tabsPane.getScene().getWindow();
        stage.initOwner(mainStage);
        stage.showAndWait();
        loadOrderList();
    }
}
