package com.example.courseprifs.utils;

import javafx.scene.control.Alert;

import static com.example.courseprifs.utils.FxUtils.generateAlert;

public class Alerts {
    public static void missingFieldsAlert() {
        generateAlert(Alert.AlertType.ERROR, "Missing fields!",null,"Please fill all fields");
    }

    public static void updateSuccessAlert() {
        generateAlert(Alert.AlertType.INFORMATION, "Success!",null,"Update successful");
    }

    public static void createSuccessAlert() {
        generateAlert(Alert.AlertType.INFORMATION, "Success!",null,"Create successful");
    }

    public static void deleteSuccessAlert() {
        generateAlert(Alert.AlertType.INFORMATION, "Success!",null,"Delete successful");
    }
}
