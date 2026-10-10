package com.library;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;

public class DashboardController {
    @FXML private javafx.scene.layout.StackPane contentArea;

    @FXML
    private void initialize() {
        showDashboard();
    }

    private void setContent(Node content) {
        contentArea.getChildren().setAll(content);
    }

    private VBox page(String title, String description) {
        VBox box = new VBox(18);
        box.setPadding(new Insets(32));
        Label heading = new Label(title);
        heading.getStyleClass().add("page-title");
        Label subheading = new Label(description);
        subheading.getStyleClass().add("page-description");
        box.getChildren().addAll(heading, subheading);
        return box;
    }

    private TextField textField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefHeight(38);
        return field;
    }

    private void addFormRow(GridPane grid, int row, String label, Node field) {
        Label fieldLabel = new Label(label);
        fieldLabel.getStyleClass().add("field-label");
        grid.add(fieldLabel, 0, row);
        grid.add(field, 1, row);
        GridPane.setHgrow(field, Priority.ALWAYS);
    }

    private Button actionButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("primary-button");
        return button;
    }

    private void showMessage(String title, String message) { new Alert(Alert.AlertType.INFORMATION, message).showAndWait(); }

    @FXML
    private void showDashboard() {
        setContent(page("Dashboard", "Welcome to Library Desk."));
    }

    @FXML
    private void showAddBook() {
    }

    @FXML
    private void showAddMember() {
    }

    @FXML
    private void showMembers() {
    }

    @FXML
    private void showIssueBook() {
    }

    @FXML
    private void showReturnBook() {
    }

    @FXML
    private void showHistory() {
    }

    @FXML
    private void logout() {
    }
}
