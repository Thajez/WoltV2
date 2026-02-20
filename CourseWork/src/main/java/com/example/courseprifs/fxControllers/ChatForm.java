package com.example.courseprifs.fxControllers;


import com.example.courseprifs.hibernateControl.CustomHibernate;
import com.example.courseprifs.model.*;
import com.example.courseprifs.utils.JpaContext;
import jakarta.persistence.EntityManagerFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.example.courseprifs.utils.FxUtils.generateAlert;

public class ChatForm {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox messageContainer;
    @FXML
    private TextArea messageBody;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button sendButton;

    private User currentUser;
    private FoodOrder currentOrder;
    private Chat currentChat;
    private Message selectedMessage = null;
    private ScheduledExecutorService scheduler;
    private static final int REFRESH_INTERVAL = 5000;

    private CustomHibernate customHibernate;
    private final EntityManagerFactory emf = JpaContext.getEmf();

    public void setData(User currentUser, FoodOrder currentOrder, Chat currentChat) {
        this.currentUser = currentUser;
        this.currentOrder = currentOrder;
        this.currentChat = currentChat;
        this.customHibernate = new CustomHibernate(emf);

        loadMessages();
        configureAccess();
        startAutoRefresh();

        Platform.runLater(() -> {
            Stage stage = (Stage) sendButton.getScene().getWindow();
            if (stage != null) {
                stage.setOnCloseRequest(event -> stopAutoRefresh());
            }
        });
    }

    private void configureAccess() {
        boolean isAdmin = currentUser.getUserType().equals(UserType.ADMIN);
        editButton.setDisable(!isAdmin || selectedMessage == null);
        deleteButton.setDisable(!isAdmin || selectedMessage == null);
        editButton.setVisible(isAdmin);
        deleteButton.setVisible(isAdmin);

        sendButton.setDisable(currentOrder.getOrderStatus().equals(OrderStatus.COMPLETED) || isAdmin);
        messageBody.setEditable(!sendButton.isDisabled());
    }

    private void startAutoRefresh() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            Chat freshChat = customHibernate.getChatWithMessages(currentChat.getId());

            Platform.runLater(() -> {
                currentChat = freshChat;
                loadMessages();
            });
        }, 0, REFRESH_INTERVAL, TimeUnit.MILLISECONDS);
    }

    private void stopAutoRefresh() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
            scheduler = null;
        }
    }

    private void loadMessages() {
        messageContainer.getChildren().clear();

        if (currentChat == null || currentChat.getMessages() == null) return;

        for (Message msg : currentChat.getMessages()) {
            VBox msgBox = createMessageNode(msg);
            messageContainer.getChildren().add(msgBox);
        }

        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }

    private VBox createMessageNode(Message msg) {
        VBox msgBox = new VBox(2);
        msgBox.setStyle("-fx-background-radius: 5; -fx-padding: 8;");

        Label sender = new Label(msg.getSender().getName() + " " + msg.getSender().getSurname());
        sender.setStyle("-fx-font-weight: bold;");

        Label text = new Label(msg.getText());
        text.setWrapText(true);

        Label timestamp = new Label(msg.getSentAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        timestamp.setStyle("-fx-font-size: 10; -fx-text-fill: #555555;");

        msgBox.getChildren().addAll(sender, text, timestamp);

        if (msg.getSender().getId() == currentUser.getId()) {
            msgBox.setAlignment(Pos.CENTER_RIGHT);
            msgBox.setStyle("-fx-background-color: #c8e6c9; -fx-background-radius: 5; -fx-padding: 8;");
        } else {
            msgBox.setAlignment(Pos.CENTER_LEFT);
            msgBox.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 5; -fx-padding: 8;");
        }

        if (currentUser.getUserType() == UserType.ADMIN) {
            msgBox.setOnMouseClicked(event -> {
                selectedMessage = msg;
                highlightSelectedMessage(msgBox);
            });
        }

        return msgBox;
    }

    @FXML
    private void sendMessage() {
        String text = messageBody.getText().trim();
        if (text.isEmpty()) return;

        if (currentOrder.getOrderStatus() == OrderStatus.COMPLETED) {
            generateAlert(Alert.AlertType.WARNING, "Cannot send message", null, "Order is completed.");
            return;
        }

        Message msg = new Message(text, (BasicUser) currentUser, currentChat);
        customHibernate.create(msg);

        addMessageNode(msg);
        messageBody.clear();
    }

    private void addMessageNode(Message msg) {
        VBox msgBox = createMessageNode(msg);
        messageContainer.getChildren().add(msgBox);
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }

    private void highlightSelectedMessage(VBox selectedBox) {
        for (Node node : messageContainer.getChildren()) {
            if (node instanceof VBox) {
                node.setStyle(node.getStyle().replace("-fx-border-color: blue;", ""));
            }
        }
        selectedBox.setStyle(selectedBox.getStyle() + "-fx-border-color: blue; -fx-border-width: 2;");
        configureAccess();
    }

    @FXML
    private void editMessage() {
        if (selectedMessage == null) {
            generateAlert(Alert.AlertType.WARNING, "No message selected", null, "Please select a message to edit.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selectedMessage.getText());
        dialog.setTitle("Edit Message");
        dialog.setHeaderText("Editing message by: " + selectedMessage.getSender());
        dialog.setContentText("New message text:");

        dialog.showAndWait().ifPresent(newText -> {
            selectedMessage.setText(newText);
            customHibernate.update(selectedMessage);
            currentChat = customHibernate.getChatWithMessages(currentChat.getId());
            loadMessages();
            selectedMessage = null;
            configureAccess();
        });
    }

    @FXML
    private void deleteMessage() {
        if (selectedMessage == null) {
            generateAlert(Alert.AlertType.WARNING, "No message selected", null, "Please select a message to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Message");
        confirm.setHeaderText("Are you sure you want to delete this message?");
        confirm.setContentText(selectedMessage.getText());

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                customHibernate.delete(Message.class, selectedMessage.getId());
                currentChat = customHibernate.getChatWithMessages(currentChat.getId());
                loadMessages();
                selectedMessage = null;
                configureAccess();
            }
        });
    }
}
