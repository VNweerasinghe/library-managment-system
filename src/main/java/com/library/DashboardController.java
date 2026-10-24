package com.library;

import com.library.data.LibraryRepository;
import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.Member;
import com.library.service.LibraryService;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

public class DashboardController {
    @FXML private javafx.scene.layout.StackPane contentArea;

    private LibraryService library;

    @FXML
    private void initialize() {
        try {
            Path databasePath = Path.of(System.getProperty("user.home"), ".library-management-system", "library.db");
            library = new LibraryService(new LibraryRepository(databasePath));
            showDashboard();
        } catch (SQLException exception) {
            showError("The library database could not be opened.", exception);
        }
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

    private GridPane formGrid() {
        GridPane form = new GridPane();
        form.setHgap(18);
        form.setVgap(14);
        form.getStyleClass().add("form-panel");
        return form;
    }

    private Button actionButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("primary-button");
        return button;
    }

    private Button secondaryButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("secondary-button");
        return button;
    }

    private <T> TableView<T> table(String... headings) {
        TableView<T> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        for (String heading : headings) {
            TableColumn<T, String> column = new TableColumn<>(heading);
            column.setCellValueFactory(data -> new ReadOnlyStringWrapper(""));
            table.getColumns().add(column);
        }
        return table;
    }

    private <T> void addColumn(TableView<T> table, String heading, Function<T, String> value) {
        TableColumn<T, String> column = new TableColumn<>(heading);
        column.setCellValueFactory(data -> new ReadOnlyStringWrapper(value.apply(data.getValue())));
        table.getColumns().add(column);
    }

    @FXML
    private void showDashboard() {
        if (!ready()) return;
        VBox box = page("Library dashboard", "A live overview of the books and borrowing activity.");
        GridPane cards = new GridPane();
        cards.setHgap(16);
        cards.setVgap(16);
        try {
            String[][] data = {
                    {"Book titles", Long.toString(library.totalBooks()), "In the catalogue"},
                    {"Members", Long.toString(library.totalMembers()), "Registered readers"},
                    {"Books on loan", Long.toString(library.borrowedBooks()), "Awaiting return"},
                    {"Overdue", Long.toString(library.overdueBooks()), "Need follow-up"}
            };
            for (int i = 0; i < data.length; i++) {
                VBox card = new VBox(8, new Label(data[i][0]), new Label(data[i][1]), new Label(data[i][2]));
                card.getStyleClass().add("stat-card");
                card.getChildren().get(0).getStyleClass().add("stat-label");
                card.getChildren().get(1).getStyleClass().add("stat-value");
                card.getChildren().get(2).getStyleClass().add("stat-note");
                cards.add(card, i % 2, i / 2);
                GridPane.setHgrow(card, Priority.ALWAYS);
            }
        } catch (SQLException exception) {
            showError("The dashboard could not load its totals.", exception);
        }
        Label tip = new Label("Quick actions");
        tip.getStyleClass().add("section-title");
        HBox actions = new HBox(12);
        Button addBook = actionButton("Add a book");
        addBook.setOnAction(event -> showAddBook());
        Button addMember = actionButton("Register a member");
        addMember.setOnAction(event -> showAddMember());
        Button issueBook = actionButton("Issue a book");
        issueBook.setOnAction(event -> showIssueBook());
        actions.getChildren().addAll(addBook, addMember, issueBook);
        box.getChildren().addAll(cards, tip, actions);
        setContent(box);
    }

    @FXML
    private void showAddBook() {
        if (!ready()) return;
        VBox box = page("Add a book", "Add a title and its copy count to the catalogue.");
        GridPane form = formGrid();
        TextField id = textField("For example, ISBN or B-104");
        TextField title = textField("Book title");
        TextField author = textField("Author name");
        TextField category = textField("Category");
        TextField year = textField("Published year");
        year.setText(Integer.toString(LocalDate.now().getYear()));
        TextField quantity = textField("Number of copies");
        quantity.setText("1");
        addFormRow(form, 0, "Book ID / ISBN", id);
        addFormRow(form, 1, "Book title", title);
        addFormRow(form, 2, "Author", author);
        addFormRow(form, 3, "Category", category);
        addFormRow(form, 4, "Published year", year);
        addFormRow(form, 5, "Quantity", quantity);
        Button save = actionButton("Add book");
        Button clear = secondaryButton("Clear");
        save.setOnAction(event -> {
            try {
                Book book = new Book(id.getText().trim(), title.getText().trim(), author.getText().trim(),
                        category.getText().trim(), Integer.parseInt(year.getText().trim()),
                        Integer.parseInt(quantity.getText().trim()), 0);
                library.saveBook(book, false);
                showInformation("Book added", "The book has been saved to the catalogue.");
                showAddBook();
            } catch (NumberFormatException exception) {
                showError("Enter a whole number for the year and quantity.", exception);
            } catch (SQLException | IllegalArgumentException exception) {
                showError("The book could not be added.", exception);
            }
        });
        clear.setOnAction(event -> {
            id.clear();
            title.clear();
            author.clear();
            category.clear();
            year.clear();
            quantity.clear();
        });
        box.getChildren().addAll(form, new HBox(10, save, clear));
        setContent(box);
    }

    @FXML
    private void showManageBooks() {
        if (!ready()) return;
        VBox box = page("Manage books", "Search the catalogue, edit book details, or remove unused records.");
        TextField search = textField("Search by ID, title, author, or category");
        TableView<Book> table = table();
        addColumn(table, "Book ID", Book::bookId);
        addColumn(table, "Title", Book::title);
        addColumn(table, "Author", Book::author);
        addColumn(table, "Category", Book::category);
        addColumn(table, "Year", book -> Integer.toString(book.publishedYear()));
        addColumn(table, "Available / Total", book -> book.availableQuantity() + " / " + book.quantity());
        ObservableList<Book> items = FXCollections.observableArrayList();
        FilteredList<Book> filtered = new FilteredList<>(items);
        table.setItems(filtered);
        search.textProperty().addListener((observable, oldValue, value) -> {
            String query = value.trim().toLowerCase();
            filtered.setPredicate(book -> (book.bookId() + " " + book.title() + " " + book.author() + " "
                    + book.category()).toLowerCase().contains(query));
        });
        Button edit = secondaryButton("Edit selected");
        Button delete = secondaryButton("Delete selected");
        edit.setOnAction(event -> {
            Book selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showInformation("Select a book", "Choose a book in the table before editing.");
                return;
            }
            Book edited = editBookDialog(selected);
            if (edited != null) {
                try {
                    library.saveBook(edited, true);
                    refreshBooks(items);
                } catch (SQLException | IllegalArgumentException exception) {
                    showError("The book could not be updated.", exception);
                }
            }
        });
        delete.setOnAction(event -> {
            Book selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showInformation("Select a book", "Choose a book in the table before deleting.");
                return;
            }
            if (confirm("Delete book", "Delete '" + selected.title() + "' from the catalogue?")) {
                try {
                    library.deleteBook(selected.bookId());
                    refreshBooks(items);
                } catch (SQLException exception) {
                    showError("The book could not be deleted.", exception);
                }
            }
        });
        VBox.setVgrow(table, Priority.ALWAYS);
        box.getChildren().addAll(search, table, new HBox(10, edit, delete));
        setContent(box);
        refreshBooks(items);
    }

    private Book editBookDialog(Book book) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit book");
        dialog.setHeaderText("Update " + book.title());
        ButtonType save = new ButtonType("Save");
        dialog.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);
        GridPane form = formGrid();
        TextField title = textField("Book title");
        title.setText(book.title());
        TextField author = textField("Author");
        author.setText(book.author());
        TextField category = textField("Category");
        category.setText(book.category());
        TextField year = textField("Published year");
        year.setText(Integer.toString(book.publishedYear()));
        TextField quantity = textField("Quantity");
        quantity.setText(Integer.toString(book.quantity()));
        addFormRow(form, 0, "Title", title);
        addFormRow(form, 1, "Author", author);
        addFormRow(form, 2, "Category", category);
        addFormRow(form, 3, "Published year", year);
        addFormRow(form, 4, "Quantity", quantity);
        dialog.getDialogPane().setContent(form);
        return dialog.showAndWait().filter(save::equals).map(button -> {
            try {
                return new Book(book.bookId(), title.getText().trim(), author.getText().trim(),
                        category.getText().trim(), Integer.parseInt(year.getText().trim()),
                        Integer.parseInt(quantity.getText().trim()), book.availableQuantity());
            } catch (NumberFormatException exception) {
                showError("Enter whole numbers for year and quantity.", exception);
                return null;
            }
        }).orElse(null);
    }

    private void refreshBooks(ObservableList<Book> books) {
        try {
            books.setAll(library.books());
        } catch (SQLException exception) {
            showError("The catalogue could not be loaded.", exception);
        }
    }

    @FXML
    private void showAddMember() {
        if (!ready()) return;
        VBox box = page("Register a member", "Add a library member and their contact details.");
        GridPane form = formGrid();
        TextField id = textField("For example, M-004");
        TextField name = textField("Full name");
        TextField email = textField("Email address");
        TextField phone = textField("Phone number");
        TextField address = textField("Address");
        addFormRow(form, 0, "Member ID", id);
        addFormRow(form, 1, "Full name", name);
        addFormRow(form, 2, "Email", email);
        addFormRow(form, 3, "Phone", phone);
        addFormRow(form, 4, "Address", address);
        Button save = actionButton("Register member");
        Button clear = secondaryButton("Clear");
        save.setOnAction(event -> {
            try {
                library.saveMember(new Member(id.getText().trim(), name.getText().trim(),
                        email.getText().trim(), phone.getText().trim(), address.getText().trim()), false);
                showInformation("Member registered", "The member has been saved.");
                showAddMember();
            } catch (SQLException | IllegalArgumentException exception) {
                showError("The member could not be registered.", exception);
            }
        });
        clear.setOnAction(event -> {
            id.clear();
            name.clear();
            email.clear();
            phone.clear();
            address.clear();
        });
        box.getChildren().addAll(form, new HBox(10, save, clear));
        setContent(box);
    }

    @FXML
    private void showMembers() {
        if (!ready()) return;
        VBox box = page("Manage members", "Search member records, edit contact details, or remove unused records.");
        TextField search = textField("Search by member ID, name, email, or phone");
        TableView<Member> table = table();
        addColumn(table, "Member ID", Member::memberId);
        addColumn(table, "Full name", Member::fullName);
        addColumn(table, "Email", Member::email);
        addColumn(table, "Phone", Member::phone);
        addColumn(table, "Address", Member::address);
        ObservableList<Member> items = FXCollections.observableArrayList();
        FilteredList<Member> filtered = new FilteredList<>(items);
        table.setItems(filtered);
        search.textProperty().addListener((observable, oldValue, value) -> {
            String query = value.trim().toLowerCase();
            filtered.setPredicate(member -> (member.memberId() + " " + member.fullName() + " "
                    + member.email() + " " + member.phone()).toLowerCase().contains(query));
        });
        Button edit = secondaryButton("Edit selected");
        Button delete = secondaryButton("Delete selected");
        edit.setOnAction(event -> {
            Member selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showInformation("Select a member", "Choose a member in the table before editing.");
                return;
            }
            Member edited = editMemberDialog(selected);
            if (edited != null) {
                try {
                    library.saveMember(edited, true);
                    refreshMembers(items);
                } catch (SQLException | IllegalArgumentException exception) {
                    showError("The member could not be updated.", exception);
                }
            }
        });
        delete.setOnAction(event -> {
            Member selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showInformation("Select a member", "Choose a member in the table before deleting.");
                return;
            }
            if (confirm("Delete member", "Delete " + selected.fullName() + " from the member list?")) {
                try {
                    library.deleteMember(selected.memberId());
                    refreshMembers(items);
                } catch (SQLException exception) {
                    showError("The member could not be deleted.", exception);
                }
            }
        });
        VBox.setVgrow(table, Priority.ALWAYS);
        box.getChildren().addAll(search, table, new HBox(10, edit, delete));
        setContent(box);
        refreshMembers(items);
    }

    private Member editMemberDialog(Member member) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit member");
        dialog.setHeaderText("Update " + member.fullName());
        ButtonType save = new ButtonType("Save");
        dialog.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);
        GridPane form = formGrid();
        TextField name = textField("Full name");
        name.setText(member.fullName());
        TextField email = textField("Email");
        email.setText(member.email());
        TextField phone = textField("Phone");
        phone.setText(member.phone());
        TextField address = textField("Address");
        address.setText(member.address());
        addFormRow(form, 0, "Full name", name);
        addFormRow(form, 1, "Email", email);
        addFormRow(form, 2, "Phone", phone);
        addFormRow(form, 3, "Address", address);
        dialog.getDialogPane().setContent(form);
        return dialog.showAndWait().filter(save::equals).map(button -> new Member(member.memberId(),
                name.getText().trim(), email.getText().trim(), phone.getText().trim(),
                address.getText().trim())).orElse(null);
    }

    private void refreshMembers(ObservableList<Member> members) {
        try {
            members.setAll(library.members());
        } catch (SQLException exception) {
            showError("The member list could not be loaded.", exception);
        }
    }

    @FXML
    private void showIssueBook() {
        if (!ready()) return;
        VBox box = page("Issue a book", "Select a registered member and an available book.");
        GridPane form = formGrid();
        ComboBox<Member> member = new ComboBox<>();
        member.setPromptText("Select a member");
        member.setPrefHeight(38);
        ComboBox<Book> book = new ComboBox<>();
        book.setPromptText("Select an available book");
        book.setPrefHeight(38);
        DatePicker issue = new DatePicker(LocalDate.now());
        DatePicker due = new DatePicker(LocalDate.now().plusDays(14));
        addFormRow(form, 0, "Member", member);
        addFormRow(form, 1, "Book", book);
        addFormRow(form, 2, "Issue date", issue);
        addFormRow(form, 3, "Due date", due);
        Button save = actionButton("Issue book");
        save.setOnAction(event -> {
            try {
                if (member.getValue() == null || book.getValue() == null) {
                    throw new IllegalArgumentException("Select a member and an available book.");
                }
                library.issueBook(member.getValue().memberId(), book.getValue().bookId(),
                        issue.getValue(), due.getValue());
                showInformation("Book issued", "The borrowing record has been saved.");
                showIssueBook();
            } catch (SQLException | IllegalArgumentException exception) {
                showError("The book could not be issued.", exception);
            }
        });
        box.getChildren().addAll(form, save);
        setContent(box);
        try {
            member.setItems(FXCollections.observableArrayList(library.members()));
            book.setItems(FXCollections.observableArrayList(
                    library.books().stream().filter(item -> item.availableQuantity() > 0).toList()));
        } catch (SQLException exception) {
            showError("Members and available books could not be loaded.", exception);
        }
    }

    @FXML
    private void showReturnBook() {
        if (!ready()) return;
        VBox box = page("Return a book", "Select an active borrowing record and confirm the return date.");
        GridPane form = formGrid();
        ComboBox<Loan> loan = new ComboBox<>();
        loan.setPromptText("Select a book on loan");
        loan.setPrefHeight(38);
        DatePicker returned = new DatePicker(LocalDate.now());
        addFormRow(form, 0, "Active loan", loan);
        addFormRow(form, 1, "Return date", returned);
        Button save = actionButton("Record return");
        save.setOnAction(event -> {
            try {
                if (loan.getValue() == null) {
                    throw new IllegalArgumentException("Select a borrowing record to return.");
                }
                library.returnBook(loan.getValue().loanId(), returned.getValue());
                showInformation("Return recorded", "The book's availability has been updated.");
                showReturnBook();
            } catch (SQLException | IllegalArgumentException exception) {
                showError("The return could not be recorded.", exception);
            }
        });
        TableView<Loan> active = loanTable();
        VBox.setVgrow(active, Priority.ALWAYS);
        box.getChildren().addAll(form, save, new Label("Current loans"), active);
        setContent(box);
        try {
            List<Loan> loans = library.activeLoans();
            loan.setItems(FXCollections.observableArrayList(loans));
            active.setItems(FXCollections.observableArrayList(loans));
        } catch (SQLException exception) {
            showError("Active loans could not be loaded.", exception);
        }
    }

    @FXML
    private void showHistory() {
        if (!ready()) return;
        VBox box = page("Borrowing history", "Search all borrowing records, including overdue and returned books.");
        TextField search = textField("Search by member, book, or status");
        TableView<Loan> table = loanTable();
        ObservableList<Loan> items = FXCollections.observableArrayList();
        FilteredList<Loan> filtered = new FilteredList<>(items);
        table.setItems(filtered);
        search.textProperty().addListener((observable, oldValue, value) -> {
            String query = value.trim().toLowerCase();
            filtered.setPredicate(loan -> (loan.memberId() + " " + loan.memberName() + " "
                    + loan.bookId() + " " + loan.bookTitle() + " " + loan.status())
                    .toLowerCase().contains(query));
        });
        VBox.setVgrow(table, Priority.ALWAYS);
        box.getChildren().addAll(search, table);
        setContent(box);
        try {
            items.setAll(library.loans());
        } catch (SQLException exception) {
            showError("Borrowing history could not be loaded.", exception);
        }
    }

    private TableView<Loan> loanTable() {
        TableView<Loan> table = table();
        addColumn(table, "Member ID", Loan::memberId);
        addColumn(table, "Member", Loan::memberName);
        addColumn(table, "Book ID", Loan::bookId);
        addColumn(table, "Book", Loan::bookTitle);
        addColumn(table, "Issue date", loan -> loan.issueDate().toString());
        addColumn(table, "Due date", loan -> loan.dueDate().toString());
        addColumn(table, "Return date", loan -> loan.returnDate() == null ? "—" : loan.returnDate().toString());
        addColumn(table, "Status", Loan::status);
        return table;
    }

    @FXML
    private void logout() {
        Stage stage = (Stage) contentArea.getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(loader.load(), 1100, 700);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Library Management System");
        } catch (java.io.IOException exception) {
            showError("You could not be logged out.", exception);
        }
    }

    private boolean ready() {
        if (library != null) {
            return true;
        }
        showInformation("Database unavailable", "Restart the application after resolving the database error.");
        return false;
    }

    private boolean confirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.CANCEL, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        return alert.showAndWait().filter(ButtonType.OK::equals).isPresent();
    }

    private void showInformation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void showError(String action, Exception exception) {
        String detail = exception.getMessage();
        if (detail == null || detail.isBlank()) {
            detail = exception.getClass().getSimpleName();
        }
        if (exception instanceof SQLException && detail.contains("UNIQUE constraint failed")) {
            detail = "That ID is already in use. Choose a unique ID and try again.";
        }
        Alert alert = new Alert(Alert.AlertType.ERROR, action + "\n\n" + detail);
        alert.setTitle("Unable to complete action");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
