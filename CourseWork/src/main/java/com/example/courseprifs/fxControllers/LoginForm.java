package com.example.courseprifs.fxControllers;

import com.example.courseprifs.HelloApplication;
import com.example.courseprifs.hibernateControl.CustomHibernate;
import com.example.courseprifs.model.Restaurant;
import com.example.courseprifs.model.User;
import com.example.courseprifs.utils.FxUtils;
import com.example.courseprifs.utils.JpaContext;
import jakarta.persistence.EntityManagerFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginForm {
    /// INPUT VARIABLES
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;

    private final EntityManagerFactory emf = JpaContext.getEmf();

    /// LOGIN LOGIC
    @FXML
    private void validateAndLoad() throws IOException {
        CustomHibernate customHibernate = new CustomHibernate(emf);
        User user = customHibernate.getUserByCredentials(loginField.getText(), passwordField.getText());
        if (user != null) {

            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main-form.fxml"));
            Parent parent = fxmlLoader.load();

            MainForm mainForm = fxmlLoader.getController();

            if(user instanceof Restaurant restaurant) {
                mainForm.setData(restaurant);
            } else mainForm.setData(user);

            Scene scene = new Scene(parent);
            Stage stage = (Stage) loginField.getScene().getWindow();
            stage.setTitle("Main menu");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } else {
            FxUtils.generateAlert(Alert.AlertType.WARNING, "Warning", "Something went wrong during login", "No such user or wrong credentials");
        }
    }

    /// REGISTER NAVIGATION
    @FXML
    private void registerNewUser() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("user-form.fxml"));
        Parent parent = fxmlLoader.load();

        UserForm userForm = fxmlLoader.getController();
        userForm.setData(null, false, true);

        Stage registerStage = new Stage();
        registerStage.setTitle("Register new user");
        registerStage.setScene(new Scene(parent));
        Stage loginStage = (Stage) loginField.getScene().getWindow();
        registerStage.initOwner(loginStage);
        registerStage.setResizable(false);
        registerStage.show();
    }
}
