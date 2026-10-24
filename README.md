# Library Management System

A small desktop app for keeping track of a library's books, members, and loans. It is built with JavaFX and saves information in a local SQLite database.

## What you can do

- Add, search, edit, and remove books.
- Register members and update their details.
- Lend an available book to a member and record its return.
- Check borrowing history and see which books are overdue.
- View book, member, and loan totals on the dashboard.

The sign-in screen is a coursework demonstration. It does not check real user accounts or passwords.

## What you need

- Java Development Kit (JDK) 17 or newer ([download JDK 17](https://adoptium.net/temurin/releases/?version=17))
- Maven 3.8 or newer ([Maven installation guide](https://maven.apache.org/install.html))
- Git, if you want to clone the project from a terminal
- An internet connection the first time you build, so Maven can download the project libraries

To check that Java and Maven are installed, open a terminal and run:

```bash
java -version
mvn -version
```

## Start the desktop app

1. Clone the project:

```bash
git clone https://github.com/VNweerasinghe/library-managment-system.git
cd library-managment-system
```

You can also download the project as a ZIP from GitHub and open a terminal in the extracted folder containing `pom.xml`.

2. Start the desktop app:

```bash
mvn clean javafx:run
```

Maven downloads the required libraries and opens the desktop application. The first build may take a little longer.

## Run the tests

The tests use temporary databases, so they do not change your library records.

```bash
mvn test
```

To run the full build and tests together:

```bash
mvn clean verify
```

## Where your data is saved

The app creates its database automatically the first time it runs:

- Windows: `%USERPROFILE%\.library-management-system\library.db`
- macOS or Linux: `~/.library-management-system/library.db`

Back up `library.db` if you want to keep your records safe. Deleting the file deletes those records.

## How the code is organized

The project separates screens, rules, and database work so each part has a clear job:

- `src/main/java/com/library/` contains the JavaFX app and screen controllers.
- `src/main/java/com/library/model/` describes a book, a member, and a loan.
- `src/main/java/com/library/service/` checks input and applies library rules, such as requiring an available copy before a book is lent.
- `src/main/java/com/library/data/` reads and writes records in SQLite.
- `src/main/resources/fxml/` contains the layouts for the JavaFX screens.
- `src/test/java/` contains tests for saving records and lending and returning books.
- `docs/` contains a separate browser preview.

For example, when a book is lent, the screen asks the service to check the request. The service applies the library rules, then the repository updates the database.

## Browser preview

The [browser preview](https://vnweerasinghe.github.io/library-managment-system/) is a separate demonstration. It does not use the desktop app's database, and its forms do not save records.

## Project limits

This is a local coursework project. It does not include real sign-in, online hosting for library records, encrypted database files, or sharing one database between multiple computers.
