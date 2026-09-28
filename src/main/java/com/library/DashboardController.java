package com.library;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
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
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

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
        VBox box = page("Manage Members", "Search, edit, or delete registered members.");
        TextField search = textField("Search members by name or ID");
        TableView<List<String>> table = new TableView<>();
        String[] headings = {"Member ID", "Full Name", "Email", "Phone"};
        for (int i = 0; i < headings.length; i++) {
            final int index = i;
            TableColumn<List<String>, String> column = new TableColumn<>(headings[i]);
            column.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().get(index)));
            column.setPrefWidth(180);
            table.getColumns().add(column);
        }
        javafx.collections.ObservableList<List<String>> members = FXCollections.observableArrayList(
                Arrays.asList("M-001", "Amal Perera", "amal@email.com", "077 123 4567"),
                Arrays.asList("M-002", "Nimal Silva", "nimal@email.com", "071 555 2211"),
            Arrays.asList("M-003", "Sara Fernando", "sara@email.com", "076 900 1122"));
        FilteredList<List<String>> filteredMembers = new FilteredList<>(members);
        table.setItems(filteredMembers);
        search.textProperty().addListener((observable, oldValue, newValue) -> {
            String searchText = newValue.toLowerCase();
            filteredMembers.setPredicate(member -> member.toString().toLowerCase().contains(searchText));
        });
        VBox.setVgrow(table, Priority.ALWAYS);
        HBox buttons = new HBox(10, actionButton("Edit Member"), actionButton("Delete Member"));
        box.getChildren().addAll(search, table, buttons); setContent(box);
    }

    @FXML
    private void showIssueBook() {
        VBox box = page("Issue Book", "Create a borrowing record for a member.");
        GridPane form = new GridPane(); form.setHgap(18); form.setVgap(14); form.getStyleClass().add("form-panel");
        ComboBox<String> member = new ComboBox<>(FXCollections.observableArrayList("M-001 - Amal Perera", "M-002 - Nimal Silva")); member.setPromptText("Select member"); member.setPrefHeight(38);
        ComboBox<String> book = new ComboBox<>(FXCollections.observableArrayList("B-101 - Clean Code", "B-102 - Java Basics")); book.setPromptText("Select book"); book.setPrefHeight(38);
        DatePicker issue = new DatePicker(LocalDate.now()); DatePicker due = new DatePicker(LocalDate.now().plusDays(14));
        addFormRow(form, 0, "Select Member", member); addFormRow(form, 1, "Select Book", book); addFormRow(form, 2, "Issue Date", issue); addFormRow(form, 3, "Due Date", due);
        Button save = actionButton("Issue Book"); save.setOnAction(event -> showMessage("Book issued", "The borrowing record has been created."));
        box.getChildren().addAll(form, save); setContent(box);
    }

    @FXML
    private void showReturnBook() {
        VBox box = page("Return Book", "Select a borrowed book and record its return.");
        GridPane form = new GridPane(); form.setHgap(18); form.setVgap(14); form.getStyleClass().add("form-panel");
        ComboBox<String> loan = new ComboBox<>(FXCollections.observableArrayList("B-101 - Clean Code / M-001", "B-102 - Java Basics / M-002")); loan.setPromptText("Select borrowed book"); loan.setPrefHeight(38);
        TextField member = textField("Member information"); member.setText("M-001 - Amal Perera"); member.setEditable(false);
        TextField borrowed = textField("Borrowed date"); borrowed.setText("2026-09-15"); borrowed.setEditable(false);
        TextField due = textField("Due date"); due.setText("2026-09-29"); due.setEditable(false);
        DatePicker returned = new DatePicker(LocalDate.now());
        addFormRow(form, 0, "Borrowed Book", loan); addFormRow(form, 1, "Member", member); addFormRow(form, 2, "Borrowed Date", borrowed); addFormRow(form, 3, "Due Date", due); addFormRow(form, 4, "Return Date", returned);
        Label status = new Label("Status: On time"); status.getStyleClass().add("success-label");
        Button save = actionButton("Return Book"); save.setOnAction(event -> showMessage("Book returned", "The return has been recorded."));
        box.getChildren().addAll(form, status, save); setContent(box);
    }

    @FXML
    private void showHistory() {
        VBox box = page("Borrowing History", "Review borrowed, returned, and overdue books.");
        TableView<List<String>> table = new TableView<>();
        String[] headings = {"Member ID", "Book Title", "Issue Date", "Due Date", "Return Date", "Status"};
        for (int i = 0; i < headings.length; i++) {
            final int index = i;
            TableColumn<List<String>, String> column = new TableColumn<>(headings[i]);
            column.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().get(index)));
            column.setPrefWidth(145);
            table.getColumns().add(column);
        }
        table.setItems(FXCollections.observableArrayList(
            Arrays.asList("M-001", "Clean Code", "2026-09-15", "2026-09-29", "-", "Borrowed"),
            Arrays.asList("M-002", "Java Basics", "2026-09-01", "2026-09-15", "2026-09-14", "Returned"),
            Arrays.asList("M-003", "Effective Java", "2026-08-20", "2026-09-03", "-", "Overdue")));
        VBox.setVgrow(table, Priority.ALWAYS); box.getChildren().add(table); setContent(box);
    }

    @FXML
    private void logout() {
        Stage stage = (Stage) contentArea.getScene().getWindow();
        try {
            Scene scene = new Scene(new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/login.fxml")).load(), 1100, 700);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm()); stage.setScene(scene); stage.setTitle("Library Management System");
        } catch (Exception exception) { showMessage("Logout", "You have been logged out."); }
    }

    private void showMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
