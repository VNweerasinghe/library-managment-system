# Library Management System

A JavaFX desktop application for managing a small library's books, members, and borrowing records. The desktop app stores its data in a local SQLite database.

## Features

- Add, search, edit, and delete books and members.
- Issue available copies to registered members and record returns.
- View searchable borrowing history, including overdue status.
- See live catalogue, membership, and borrowing totals on the dashboard.
- Validate required fields, email format, book availability, and issue/return dates.
- Keep records between launches in the local database.

The sign-in screen is a coursework demonstration, not real authentication. The application is intended for local use and does not provide user accounts, database encryption, or multi-user synchronization.

## Requirements

- Java 25 or newer
- Maven 3.8 or newer

## Run

From the project directory:

```bash
mvn clean javafx:run
```

Run the automated database and borrowing tests with:

```bash
mvn test
```

Build the application JAR with:

```bash
mvn clean package
```

## Data storage

The database is created automatically on first launch at:

```text
~/.library-management-system/library.db
```

Back up this file to preserve the library records. Removing it permanently deletes the locally stored data.

## Browser preview

The [GitHub Pages preview](https://vnweerasinghe.github.io/library-managment-system/) is a separate static demonstration. It does not connect to the desktop application's SQLite database and does not persist form submissions.

## Project layout

```text
src/main/java/com/library/           JavaFX application and controllers
src/main/java/com/library/model/     Book, member, and loan records
src/main/java/com/library/data/      SQLite repository
src/main/java/com/library/service/   Validation and library workflows
src/main/resources/fxml/             JavaFX screens
src/main/resources/css/              Desktop app styles
src/test/java/                       Automated tests
docs/                                Static browser preview
```
