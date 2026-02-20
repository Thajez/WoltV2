package com.example.courseprifs.utils;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class ToastUtils {

    public static void generateToast(Alert.AlertType type, String message) {
        Stage owner = Stage.getWindows()
                .stream()
                .filter(window -> window.isFocused() && window instanceof Stage)
                .map(window -> (Stage) window)
                .findFirst()
                .orElse(null);

        if (owner == null) return;

        Label label = new Label(message);
        label.setStyle(getStyleForType(type));

        StackPane root = new StackPane(label);
        root.setMouseTransparent(true);

        Scene scene = new Scene(root);
        scene.setFill(null);

        Stage toastStage = new Stage();
        toastStage.initOwner(owner);
        toastStage.initStyle(StageStyle.TRANSPARENT);
        toastStage.setAlwaysOnTop(true);
        toastStage.setScene(scene);

        toastStage.show();

        // Bottom-right positioning
        toastStage.setX(owner.getX() + owner.getWidth() - root.getWidth() - 20);
        toastStage.setY(owner.getY() + owner.getHeight() - root.getHeight() - 20);

        // Stay visible for 5 seconds
        PauseTransition delay = new PauseTransition(Duration.seconds(5));

        // Fade out smoothly
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), root);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> toastStage.close());

        delay.setOnFinished(e -> fadeOut.play());
        delay.play();
    }

    private static String getStyleForType(Alert.AlertType type) {
        String bgColor = switch (type) {
            case INFORMATION -> "#3498db";
            case CONFIRMATION -> "#2ecc71";
            case WARNING -> "#f39c12";
            case ERROR -> "#e74c3c";
            default -> "#7f8c8d";
        };

        return """
            -fx-background-color: %s;
            -fx-text-fill: white;
            -fx-padding: 12 18;
            -fx-background-radius: 8;
            -fx-font-size: 13px;
            -fx-font-weight: bold;
        """.formatted(bgColor);
    }
}