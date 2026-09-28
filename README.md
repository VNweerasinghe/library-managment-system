# Library Management System

Beginner-level JavaFX UI coursework project based on the provided assignment.

## Run the JavaFX application

Requirements: Java 11 or newer and Maven.

```bash
mvn clean javafx:run
```

Enter any non-empty username and password on the login screen. The project demonstrates JavaFX UI design, FXML, CSS, navigation, validation, tables, dates, and alert messages. It does not use a database.

## GitHub Pages preview

JavaFX is a desktop technology and cannot run directly in a browser. The `docs` folder contains a simple HTML, CSS, and JavaScript preview of the same library screens.

The site is available at:

https://vnweerasinghe.github.io/library-managment-system/

The root `index.html` redirects visitors to the `docs` preview. The workflow in `.github/workflows/pages.yml` also publishes the `docs` folder when GitHub Pages is configured to use GitHub Actions.

## Project structure

- `src/main/java/com/library` - Java application and controllers
- `src/main/resources/fxml` - FXML screens
- `src/main/resources/css` - JavaFX stylesheet
- `docs` - browser preview for GitHub Pages
- `.github/workflows/pages.yml` - Pages deployment workflow
