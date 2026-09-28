# Library Management System

Beginner-level JavaFX UI coursework project based on the provided assignment.

## Features

- Login screen with required-field validation and reset button
- Dashboard with library summary cards and sidebar navigation
- Add Book form
- Add Member form
- Manage Members table with search field and edit/delete buttons
- Issue Book form with member, book, and date controls
- Return Book form with overdue status
- Borrowing History table
- JavaFX alert messages
- CSS styling and FXML layout files

## Run the application

Requirements: Java 11 or newer and Maven.

```bash
mvn clean javafx:run
```

For the login screen, enter any non-empty username and password. This project focuses on the required UI and basic event handling, so it does not connect to a database.

## Project structure

- `src/main/java/com/library` - Java application and controllers
- `src/main/resources/fxml` - FXML screens
- `src/main/resources/css` - application stylesheet
