# Library Management System

Beginner-level JavaFX UI coursework project based on the provided assignment. The JavaFX application is the main coursework project. A small browser preview is also included for GitHub Pages.

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
- Static browser preview in the `docs` folder

## Run the application

Requirements: Java 11 or newer and Maven.

```bash
mvn clean javafx:run
```

For the login screen, enter any non-empty username and password. This project focuses on the required UI and basic event handling, so it does not connect to a database.

## GitHub Pages deployment

GitHub Pages cannot run a JavaFX desktop application. The `docs` folder contains a simple HTML, CSS, and JavaScript preview of the same screens.

1. Push this project to a GitHub repository.
2. Open **Settings > Pages** in the repository.
3. Under **Build and deployment**, choose **GitHub Actions**.
4. Push to the `main` or `master` branch, or run **Publish library preview** from the Actions tab.

The workflow in `.github/workflows/pages.yml` publishes the `docs` folder automatically.

## Project structure

- `src/main/java/com/library` - Java application and controllers
- `src/main/resources/fxml` - FXML screens
- `src/main/resources/css` - application stylesheet
- `docs` - GitHub Pages browser preview
- `.github/workflows/pages.yml` - GitHub Pages deployment workflow
