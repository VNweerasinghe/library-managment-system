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
        VBox box = page("Good morning, Librarian", "Here is today's library overview.");
        GridPane cards = new GridPane();
        cards.setHgap(16);
        cards.setVgap(16);
        String[][] data = {{"Total Books", "248", "Available collection"}, {"Total Members", "96", "Registered readers"}, {"Borrowed Books", "32", "Currently on loan"}, {"Overdue Books", "05", "Need attention"}};
        for (int i = 0; i < data.length; i++) {
            VBox card = new VBox(8, new Label(data[i][0]), new Label(data[i][1]), new Label(data[i][2]));
            card.getStyleClass().add("stat-card");
            card.getChildren().get(0).getStyleClass().add("stat-label");
            card.getChildren().get(1).getStyleClass().add("stat-value");
            card.getChildren().get(2).getStyleClass().add("stat-note");
            cards.add(card, i % 2, i / 2);
            GridPane.setHgrow(card, Priority.ALWAYS);
        }
        Label tip = new Label("Quick actions");
        tip.getStyleClass().add("section-title");
        HBox actions = new HBox(12);
        Button addBook = actionButton("Add a Book");
        addBook.setOnAction(event -> showAddBook());
        Button addMember = actionButton("Register Member");
        addMember.setOnAction(event -> showAddMember());
        actions.getChildren().addAll(addBook, addMember);
        box.getChildren().addAll(cards, tip, actions);
        setContent(box);
    }

    @FXML
    private void showAddBook() {
        VBox box = page("Add Book", "Enter the details of a new book in the library.");
        GridPane form = new GridPane();
        form.setHgap(18); form.setVgap(14); form.getStyleClass().add("form-panel");
        TextField id = textField("Book ID or ISBN"); TextField title = textField("Book title");
        TextField author = textField("Author name"); TextField category = textField("Category");
        TextField year = textField("Published year"); TextField quantity = textField("Quantity");
        addFormRow(form, 0, "Book ID / ISBN", id); addFormRow(form, 1, "Book Title", title);
        addFormRow(form, 2, "Author", author); addFormRow(form, 3, "Category", category);
        addFormRow(form, 4, "Published Year", year); addFormRow(form, 5, "Quantity", quantity);
        Button save = actionButton("Add Book"); Button clear = new Button("Clear"); clear.getStyleClass().add("secondary-button");
        save.setOnAction(event -> showMessage("Book added", "The book has been added successfully."));
        clear.setOnAction(event -> { id.clear(); title.clear(); author.clear(); category.clear(); year.clear(); quantity.clear(); });
        HBox buttons = new HBox(10, save, clear);
        box.getChildren().addAll(form, buttons); setContent(box);
    }

    @FXML
    private void showAddMember() {
        VBox box = page("Add Member", "Register a new library member.");
        GridPane form = new GridPane(); form.setHgap(18); form.setVgap(14); form.getStyleClass().add("form-panel");
        TextField id = textField("Member ID"); TextField name = textField("Full name"); TextField email = textField("Email address");
        TextField phone = textField("Phone number"); TextField address = textField("Address");
        addFormRow(form, 0, "Member ID", id); addFormRow(form, 1, "Full Name", name); addFormRow(form, 2, "Email", email);
        addFormRow(form, 3, "Phone Number", phone); addFormRow(form, 4, "Address", address);
        Button save = actionButton("Register Member");
        save.setOnAction(event -> showMessage("Member registered", "The member has been registered successfully."));
        box.getChildren().addAll(form, save); setContent(box);
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
