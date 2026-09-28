package com.library;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;

    @FXML
    private void login() {
        if (usernameField.getText().trim().isEmpty() || passwordField.getText().isEmpty()) {
            messageLabel.setText("Please enter both username and password.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Scene scene = new Scene(loader.load(), 1200, 760);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Library Management System - Dashboard");
        } catch (Exception exception) {
            new Alert(Alert.AlertType.ERROR, "The dashboard could not be opened.").showAndWait();
        }
    }

    @FXML
    private void clearForm() {
        usernameField.clear();
        passwordField.clear();
        messageLabel.setText("");
    }
}
