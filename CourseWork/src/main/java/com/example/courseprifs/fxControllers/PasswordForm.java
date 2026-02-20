package com.example.courseprifs.fxControllers;

import com.example.courseprifs.hibernateControl.CustomHibernate;
import com.example.courseprifs.model.User;
import com.example.courseprifs.utils.FxUtils;
import com.example.courseprifs.utils.JpaContext;
import jakarta.persistence.EntityManagerFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import static com.example.courseprifs.utils.FxUtils.*;

public class PasswordForm {
    @FXML
    private Button confirmButton;
    @FXML
    private PasswordField oldPass;
    @FXML
    private PasswordField newPass1;
    @FXML
    private PasswordField newPass2;

    private User currentUser;

    public void setData(User currentUser) {
        this.currentUser = currentUser;
    };

    @FXML
    private void changePass() {
        String oldPassword = oldPass.getText().trim();
        String newPassword1 = newPass1.getText().trim();
        String newPassword2 = newPass2.getText().trim();

        if (oldPassword.isEmpty() || newPassword1.isEmpty() || newPassword2.isEmpty()) {
            generateAlert(Alert.AlertType.ERROR, "Error", null, "All fields must be filled");
            return;
        }

        if (!newPassword1.equals(newPassword2)) {
            generateAlert(Alert.AlertType.ERROR, "Error", null, "New passwords do not match");
            return;
        }

        if (!checkPassword(oldPassword, currentUser.getPassword())) {
            generateAlert(Alert.AlertType.ERROR, "Error", null, "Old password is incorrect");
            return;
        }

        String hashedNewPassword = hashPassword(newPassword1);

        if (checkPassword(hashedNewPassword, currentUser.getPassword())) {
            generateAlert(Alert.AlertType.ERROR, "Error", null, "New password cannot be the same as the old one");
            return;
        }

        currentUser.setPassword(hashedNewPassword);

        EntityManagerFactory emf = JpaContext.getEmf();
        CustomHibernate customHibernate = new CustomHibernate(emf);
        customHibernate.update(currentUser);

        generateAlert(Alert.AlertType.INFORMATION, "Success", null, "Password has been changed successfully");

        oldPass.clear();
        newPass1.clear();
        newPass2.clear();
        closeWindow();
    }

    public void closeWindow() {
        Stage stage = (Stage) confirmButton.getScene().getWindow();
        stage.close();
    }
}
