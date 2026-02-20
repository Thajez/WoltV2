package com.example.courseprifs.fxControllers;

import com.example.courseprifs.hibernateControl.GenericHibernate;
import com.example.courseprifs.model.*;
import com.example.courseprifs.utils.FxUtils;
import com.example.courseprifs.utils.JpaContext;
import jakarta.persistence.EntityManagerFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.converter.LocalTimeStringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import static com.example.courseprifs.utils.Alerts.*;
import static com.example.courseprifs.utils.FxUtils.generateAlert;

public class UserForm implements Initializable {
    @FXML
    private Spinner<LocalTime> openingTimeSpinner;
    @FXML
    private Spinner<LocalTime> closingTimeSpinner;
    @FXML
    private RadioButton userRadio;
    @FXML
    private RadioButton restaurantRadio;
    @FXML
    private RadioButton clientRadio;
    @FXML
    private RadioButton driverRadio;
    @FXML
    private ToggleGroup Select;
    @FXML
    private TextField addressField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField surnameField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField licenceField;
    @FXML
    private ComboBox<VehicleType> vehicleTypeList;
    @FXML
    private DatePicker bDateField;
    @FXML
    private Button updateButton;
    @FXML
    private Button saveButton;
    @FXML
    private TextField restaurantTypeField;

    private final EntityManagerFactory emf = JpaContext.getEmf();
    private GenericHibernate genericHibernate;
    private User userForUpdate;
    private boolean isForUpdate;
    private Runnable onUserUpdate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        disableFields();
        clearFields();
        vehicleTypeList.getItems().addAll(VehicleType.values());
        saveButton.setDisable(false);
        saveButton.setVisible(true);
        updateButton.setDisable(true);
        updateButton.setVisible(false);
        setupTimeSpinners();
    }

    public void setData(User user, boolean isForUpdate, boolean fromLoginForm) {
        this.genericHibernate = new GenericHibernate(emf);
        this.userForUpdate = user;
        this.isForUpdate = isForUpdate;

        userRadio.setDisable(true);
        restaurantRadio.setDisable(true);
        clientRadio.setDisable(true);
        driverRadio.setDisable(true);

        if (userForUpdate != null || isForUpdate) {
            disableFields();
        } else if (fromLoginForm) {
            restaurantRadio.setSelected(true);
            disableFields();
        } else {
            disableFields();
            userRadio.setDisable(false);
            restaurantRadio.setDisable(false);
            clientRadio.setDisable(false);
            driverRadio.setDisable(false);
        }
        fillUserDataForUpdate();
    }

    public void setOnUserUpdated(Runnable callback) {
        this.onUserUpdate = callback;
    }

    @FXML
    private void createNewUser() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String name = nameField.getText();
        String surname = surnameField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();

        if (userRadio.isSelected()) {
            if (validateFields(UserType.ADMIN, username, password, name, surname, phone, null, null, null, null, false)) {
                password = FxUtils.hashPassword(password);
                User user = new User(username, password, name, surname, phone, UserType.ADMIN);
                user.setDateCreated(LocalDateTime.now());
                user.setDateUpdated(LocalDateTime.now());

                genericHibernate.create(user);
                createSuccessAlert();
                if (onUserUpdate != null) onUserUpdate.run();
                closeWindow(saveButton);
                clearFields();
            }
        } else if (restaurantRadio.isSelected()) {
            if (validateFields(UserType.RESTAURANT, username, password, name, surname, phone, address, null, null, null, false)) {
                String typeOfRestaurant = restaurantTypeField.getText();
                LocalTime openingHours = openingTimeSpinner.getValue();
                LocalTime closingHours = closingTimeSpinner.getValue();
                if (!(validateRestaurantFields(typeOfRestaurant,openingHours,closingHours))) {
                    generateAlert(Alert.AlertType.ERROR, "Incorrect restaurant data!",null,"Please fill all required fields");
                    return;
                }
                password = FxUtils.hashPassword(password);
                Restaurant restaurant = new Restaurant(username, password, name, surname, phone, addressField.getText(), UserType.RESTAURANT, typeOfRestaurant, openingHours, closingHours);
                restaurant.setDateCreated(LocalDateTime.now());
                restaurant.setDateUpdated(LocalDateTime.now());

                genericHibernate.create(restaurant);
                createSuccessAlert();
                if (onUserUpdate != null) onUserUpdate.run();
                closeWindow(saveButton);
                clearFields();
            }
        } else if (clientRadio.isSelected()) {
            if (validateFields(UserType.BASIC, username, password, name, surname, phone, address, null, null, null, false)) {
                password = FxUtils.hashPassword(password);
                BasicUser basicUser = new BasicUser(username, password, name, surname, phone, addressField.getText(), UserType.BASIC);
                basicUser.setDateCreated(LocalDateTime.now());
                basicUser.setDateUpdated(LocalDateTime.now());

                genericHibernate.create(basicUser);
                createSuccessAlert();
                if (onUserUpdate != null) onUserUpdate.run();
                closeWindow(saveButton);
                clearFields();
            }
        } else if (driverRadio.isSelected()){
            LocalDate birthDate = bDateField.getValue();
            String licence = licenceField.getText();
            VehicleType vehicleType = vehicleTypeList.getValue();

            if (validateFields(UserType.DRIVER, username, password, name, surname, phone, address, licence, birthDate, vehicleType,  false)) {
                password = FxUtils.hashPassword(password);
                Driver driver = new Driver(username, password, name, surname, phone, address, UserType.DRIVER, licence, birthDate, vehicleType);
                driver.setDateCreated(LocalDateTime.now());
                driver.setDateUpdated(LocalDateTime.now());

                genericHibernate.create(driver);
                createSuccessAlert();
                if (onUserUpdate != null) onUserUpdate.run();
                closeWindow(saveButton);
                clearFields();
            }
        } else {
            generateAlert(Alert.AlertType.ERROR, "Incorrect user type!",null,"Please select a user type");
        }
    }

    @FXML
    private void updateUser() {
        UserType userType = userForUpdate.getUserType();
        String username = usernameField.getText();
        String name = nameField.getText();
        String surname = surnameField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();
        String licence = licenceField.getText();
        LocalDate birthDate = bDateField.getValue();
        VehicleType vehicleType = vehicleTypeList.getValue();

        if (userType.equals(UserType.ADMIN)) {
            if (validateFields(userType, username, null, name, surname, phone, null, null, null, null, true)) {
                userForUpdate.setLogin(username);
                userForUpdate.setName(name);
                userForUpdate.setSurname(surname);
                userForUpdate.setPhoneNumber(phone);
                userForUpdate.setDateUpdated(LocalDateTime.now());

                genericHibernate.update(userForUpdate);
                updateSuccessAlert();
                if (onUserUpdate != null) onUserUpdate.run();
                closeWindow(updateButton);
                clearFields();
            }
        } else if (userType.equals(UserType.RESTAURANT)) {
            if (validateFields(userType, username, null, name, surname, phone, address, null, null, null, true)) {
                LocalTime openingTime = openingTimeSpinner.getValue();
                LocalTime closingTime = closingTimeSpinner.getValue();
                String typeOfRestaurant = restaurantTypeField.getText();

                if (!validateRestaurantFields(typeOfRestaurant, openingTime, closingTime)) {
                    generateAlert(Alert.AlertType.ERROR, "Incorrect restaurant data!", null, "Please fill all required fields");
                    return;
                }

                Restaurant restaurant = (Restaurant) userForUpdate;
                restaurant.setLogin(username);
                restaurant.setName(name);
                restaurant.setSurname(surname);
                restaurant.setPhoneNumber(phone);
                restaurant.setAddress(address);
                restaurant.setTypeOfRestaurant(typeOfRestaurant);
                restaurant.setOpeningTime(openingTime);
                restaurant.setClosingTime(closingTime);
                restaurant.setDateUpdated(LocalDateTime.now());

                genericHibernate.update(restaurant);
                updateSuccessAlert();
                if (onUserUpdate != null) onUserUpdate.run();
                closeWindow(updateButton);
                clearFields();
            }
        } else if (userType.equals(UserType.BASIC)) {
            if (validateFields(userType, username, null, name, surname, phone, address, null, null, null, true)) {
                BasicUser basicUser = (BasicUser) userForUpdate;
                basicUser.setLogin(username);
                basicUser.setName(name);
                basicUser.setSurname(surname);
                basicUser.setPhoneNumber(phone);
                basicUser.setAddress(address);
                basicUser.setDateUpdated(LocalDateTime.now());

                genericHibernate.update(basicUser);
                updateSuccessAlert();
                if (onUserUpdate != null) onUserUpdate.run();
                closeWindow(updateButton);
                clearFields();
            }
        } else if (userType.equals(UserType.DRIVER)) {
            if (validateFields(userType, username, null, name, surname, phone, address, licence, birthDate, vehicleType, true)) {
                Driver driver = (Driver) userForUpdate;
                driver.setLogin(username);
                driver.setName(name);
                driver.setSurname(surname);
                driver.setPhoneNumber(phone);
                driver.setAddress(address);
                driver.setLicence(licence);
                driver.setBDate(birthDate);
                driver.setVehicleType(vehicleType);
                driver.setDateUpdated(LocalDateTime.now());

                genericHibernate.update(driver);
                updateSuccessAlert();
                if (onUserUpdate != null) onUserUpdate.run();
                closeWindow(updateButton);
                clearFields();
            }
        }
    }

    @FXML
    private void fillUserDataForUpdate() {
        if (userForUpdate == null || !isForUpdate || userForUpdate.getUserType() == null) {
            updateButton.setVisible(false);
            updateButton.setDisable(true);
            saveButton.setDisable(false);
            saveButton.setVisible(true);
            passwordField.setDisable(false);
            return;
        }

        updateButton.setVisible(true);
        updateButton.setDisable(false);
        saveButton.setDisable(true);
        saveButton.setVisible(false);
        passwordField.setDisable(true);

        if (userForUpdate.getUserType().equals(UserType.ADMIN)) {
            userRadio.setSelected(true);
            usernameField.setText(userForUpdate.getLogin());
            nameField.setText(userForUpdate.getName());
            surnameField.setText(userForUpdate.getSurname());
            phoneField.setText(userForUpdate.getPhoneNumber());
        } else if (userForUpdate.getUserType().equals(UserType.RESTAURANT)) {
            restaurantRadio.setSelected(true);
            Restaurant restaurant = (Restaurant) userForUpdate;
            usernameField.setText(restaurant.getLogin());
            nameField.setText(restaurant.getName());
            surnameField.setText(restaurant.getSurname());
            phoneField.setText(restaurant.getPhoneNumber());
            addressField.setText(restaurant.getAddress());
            restaurantTypeField.setText(restaurant.getTypeOfRestaurant());
            openingTimeSpinner.getValueFactory().setValue(restaurant.getOpeningTime());
            closingTimeSpinner.getValueFactory().setValue(restaurant.getClosingTime());
        } else if (userForUpdate.getUserType().equals(UserType.BASIC)) {
            clientRadio.setSelected(true);
            BasicUser basicUser = (BasicUser) userForUpdate;
            usernameField.setText(basicUser.getLogin());
            nameField.setText(basicUser.getName());
            surnameField.setText(basicUser.getSurname());
            phoneField.setText(basicUser.getPhoneNumber());
            addressField.setText(basicUser.getAddress());
        } else if (userForUpdate.getUserType().equals(UserType.DRIVER)) {
            driverRadio.setSelected(true);
            Driver driver = (Driver) userForUpdate;
            usernameField.setText(driver.getLogin());
            nameField.setText(driver.getName());
            surnameField.setText(driver.getSurname());
            phoneField.setText(driver.getPhoneNumber());
            addressField.setText(driver.getAddress());
            licenceField.setText(driver.getLicence());
            bDateField.setValue(driver.getBDate());
            vehicleTypeList.setValue(driver.getVehicleType());
        }
        disableFields();
    }

    @FXML
    private void disableFields() {
        addressField.setDisable(true);
        licenceField.setDisable(true);
        bDateField.setDisable(true);
        vehicleTypeList.setDisable(true);
        restaurantTypeField.setDisable(true);
        openingTimeSpinner.setDisable(true);
        closingTimeSpinner.setDisable(true);

        if (restaurantRadio.isSelected() || clientRadio.isSelected()) {
            addressField.setDisable(false);
            if (restaurantRadio.isSelected()) {
                restaurantTypeField.setDisable(false);
                openingTimeSpinner.setDisable(false);
                closingTimeSpinner.setDisable(false);
            }
        } else if (driverRadio.isSelected()) {
            addressField.setDisable(false);
            licenceField.setDisable(false);
            bDateField.setDisable(false);
            vehicleTypeList.setDisable(false);
        }
    }

    private boolean validateFields(UserType type, String username, String password, String name, String surname, String phone, String address, String licence, LocalDate birthDate, VehicleType vehicleType, boolean isUpdate) {
        boolean requirePassword = !isUpdate;
        if(type.equals(UserType.ADMIN)) {
            if (username.trim().isEmpty() || (requirePassword && password.trim().isEmpty()) || name.trim().isEmpty() || surname.trim().isEmpty() || phone.trim().isEmpty()) {
                missingFieldsAlert();
                return false;
            }
            return true;
        } else if (type.equals(UserType.RESTAURANT) || type.equals(UserType.BASIC)) {
            if (username.trim().isEmpty() || (requirePassword && password.trim().isEmpty()) || name.trim().isEmpty() || surname.trim().isEmpty() || phone.trim().isEmpty() || address.trim().isEmpty()) {
                missingFieldsAlert();
                return false;
            }
            return true;
        } else if (type.equals(UserType.DRIVER)) {
            if (username.trim().isEmpty() || (requirePassword && password.trim().isEmpty()) || name.trim().isEmpty() || surname.trim().isEmpty() || phone.trim().isEmpty()
                    || address.trim().isEmpty() || licence.trim().isEmpty() || birthDate == null || vehicleType == null) {
                missingFieldsAlert();
                return false;
            } else {
                int age = Period.between(birthDate, LocalDate.now()).getYears();
                if (age < 18 || birthDate.isAfter(LocalDate.now())) {
                    generateAlert(Alert.AlertType.ERROR, "Incorrect date!",null,"Birth date must be in the past and at least 18 years old");
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    private boolean validateRestaurantFields(String typeOfRestaurant, LocalTime openingTime, LocalTime closingTime) {
        if (typeOfRestaurant.trim().isEmpty() || openingTime == null || closingTime == null) {
            missingFieldsAlert();
            return false;
        } return true;
    }

    private void setupTimeSpinners() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        SpinnerValueFactory<LocalTime> openingFactory = new SpinnerValueFactory<LocalTime>() {
            {
                setConverter(new LocalTimeStringConverter(formatter, null));
                setValue(LocalTime.of(9, 0));
            }

            @Override
            public void decrement(int steps) {
                setValue(getValue().minusMinutes(15L * steps));
            }

            @Override
            public void increment(int steps) {
                setValue(getValue().plusMinutes(15L * steps));
            }
        };

        SpinnerValueFactory<LocalTime> closingFactory = new SpinnerValueFactory<LocalTime>() {
            {
                setConverter(new LocalTimeStringConverter(formatter, null));
                setValue(LocalTime.of(21, 0));
            }

            @Override
            public void decrement(int steps) {
                setValue(getValue().minusMinutes(15L * steps));
            }

            @Override
            public void increment(int steps) {
                setValue(getValue().plusMinutes(15L * steps));
            }
        };

        openingTimeSpinner.setValueFactory(openingFactory);
        closingTimeSpinner.setValueFactory(closingFactory);

        openingTimeSpinner.setEditable(false);
        closingTimeSpinner.setEditable(false);
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        nameField.clear();
        surnameField.clear();
        phoneField.clear();
        addressField.clear();
        licenceField.clear();
        bDateField.setValue(null);
        vehicleTypeList.setValue(null);
    }

    private void closeWindow(Button button) {
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }
}
