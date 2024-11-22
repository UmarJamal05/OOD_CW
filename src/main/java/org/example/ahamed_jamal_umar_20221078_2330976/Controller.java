package org.example.ahamed_jamal_umar_20221078_2330976;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
public class Controller {

    public Button signupButton;
    @FXML
    private TextField firstnameField, lastnameField, emailaddressField, usernameField, passwordField, confirmpasswordField;
    @FXML
    private TextField UsernameLoginInput, PasswordLoginInput;
    @FXML
    private Button exitButton;
    private String loggedInUsername;

    @FXML
    private Button backButton;
    @FXML
    private ListView<String> historyListView;

    private static final String USER_FILE_PATH = "users.txt";

    @FXML
    private void loginButton(ActionEvent event) {
        String username = UsernameLoginInput.getText();
        String password = PasswordLoginInput.getText();

        // Check if fields are empty
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Login Failed", "All fields must be filled.", Alert.AlertType.ERROR);
            return; // Stop further execution
        }

        // Validate login
        if (validateLogin(username, password)) {
            try {
                // Load the Dashboard.fxml file
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
                Parent dashboardRoot = loader.load();

                Controller Controller = loader.getController();
                Controller.setLoggedInUsername(loggedInUsername);

                // Get the current stage from the event source
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                // Set the new scene with Dashboard.fxml
                Scene scene = new Scene(dashboardRoot);
                stage.setScene(scene);

                // Optional: Set the title for the new stage
                stage.setTitle("Dashboard");

                // Show the stage
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to load the Dashboard.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Login Failed", "Invalid username or password.", Alert.AlertType.ERROR);
        }
    }

    private void setLoggedInUsername(String username) {
        this.loggedInUsername = username;
    }

    private boolean validateLogin(String username, String password) {
        List<User> users = loadUsersFromFile();
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                loggedInUsername = username;
                return true;
            }
        }
        return false;
    }

    @FXML
    public void signupButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserRegister.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void registersignupButton(ActionEvent event) {
        String firstName = firstnameField.getText();
        String lastName = lastnameField.getText();
        String email = emailaddressField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmpasswordField.getText();

        // Check if any field is empty
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
                username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Registration Failed", "All fields must be filled.", Alert.AlertType.ERROR);
            return;
        }

        // Validate email
        if (!isValidEmail(email)) {
            showAlert("Registration Failed", "Please enter a valid email address.", Alert.AlertType.ERROR);
            return;
        }

        // Validate password length
        if (password.length() < 8) {
            showAlert("Registration Failed", "Password must be at least 8 characters long.", Alert.AlertType.ERROR);
            return;
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            showAlert("Registration Failed", "Passwords do not match.", Alert.AlertType.ERROR);
            return;
        }

        // If all checks pass, register the user
        User newUser = new User(firstName, lastName, email, username, password);
        saveUserDetails(newUser);
        showAlert("Registration Successful", "User registered successfully!", Alert.AlertType.INFORMATION);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }

    @FXML
    private void handleSuccessfulRegistration() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserLogin.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) signupButton.getScene().getWindow();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final String ARTICLES_FILE_PATH = "articles.txt";

    // Method to handle View Articles button click
    @FXML
    public void viewButton(ActionEvent event) {
        try {
            // Create a VBox to hold article buttons
            VBox articlesVBox = new VBox(10);

            // Load articles from the file
            List<Article> articles = loadArticlesFromFile();

            // Generates buttons dynamically for each article
            for (Article article : articles) {
                Button button = new Button(article.getTitle());
                button.setOnAction(e -> openArticle(article));
                articlesVBox.getChildren().add(button);
            }

            // Create a ScrollPane to display the articles
            ScrollPane scrollPane = new ScrollPane(articlesVBox);

            // Load articles into a new stage
            Stage stage = new Stage();
            Scene scene = new Scene(scrollPane, 400, 600);
            stage.setScene(scene);
            stage.setTitle("Articles");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to load articles from the file
    private List<Article> loadArticlesFromFile() {
        List<Article> articles = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ARTICLES_FILE_PATH))) {
            String line;
            Article article = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Category:")) {
                    if (article != null) articles.add(article);
                    article = new Article();
                    String[] parts = line.split(": ", 2);
                    if (parts.length > 1) {
                        article.setCategory(parts[1]);
                    } else {
                        System.err.println("Invalid category line: " + line);
                    }
                } else if (line.startsWith("Title:")) {
                    if (article != null) {
                        String[] parts = line.split(": ", 2);
                        if (parts.length > 1) {
                            article.setTitle(parts[1]);
                        } else {
                            System.err.println("Invalid title line: " + line);
                        }
                    }
                } else if (line.startsWith("Description:")) {
                    if (article != null) {
                        String[] parts = line.split(": ", 2);
                        if (parts.length > 1) {
                            article.setDescription(parts[1]);
                        } else {
                            article.setDescription("");
                            System.err.println("Invalid description line: " + line);
                        }
                    }
                } else if (article != null) {
                    article.appendDescription(line);
                }
            }

            // Add the last article if one exists
            if (article != null) articles.add(article);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return articles;
    }


    // Method to open an article's content
    private void openArticle(Article article) {
        try {
            VBox articleBox = new VBox(10);

            Label categoryLabel = new Label("Category: " + article.getCategory());
            Label titleLabel = new Label("Title: " + article.getTitle());
            titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            TextArea descriptionArea = new TextArea(article.getDescription());
            descriptionArea.setWrapText(true);
            descriptionArea.setEditable(false);

            // Buttons to Like and Dislike and Skip
            Button likeButton = new Button("Like");
            Button dislikeButton = new Button("Dislike");
            Button skipButton = new Button("Skip");

            // Handling Like and Dislike actions
            likeButton.setOnAction(e -> {
                appendHistoryToCSV("like", article.getTitle());
                showFeedback("Liked!");
            });

            dislikeButton.setOnAction(e -> {
                appendHistoryToCSV("dislike", article.getTitle());
                showFeedback("Disliked!");
            });

            skipButton.setOnAction(e -> {
                appendHistoryToCSV("skipped", article.getTitle());
                showFeedback("Article skipped.");
            });

            HBox buttonsBox = new HBox(10, likeButton, dislikeButton, skipButton);
            buttonsBox.setAlignment(Pos.CENTER);

            articleBox.getChildren().addAll(categoryLabel, titleLabel, descriptionArea, buttonsBox);

            Scene scene = new Scene(articleBox, 600, 350);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle(article.getTitle());
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void appendHistoryToCSV(String action, String articleTitle) {
        // Construct the file path using the username
        String historyFilePath = loggedInUsername + "_history.csv";

        try {
            File historyFile = new File(historyFilePath);
            boolean isNewFile = !historyFile.exists();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(historyFilePath, true))) {
                // The header is written, if the file is new
                if (isNewFile) {
                    writer.write("Title, Rating\n");
                }

                // Write the data
                String record = articleTitle + "," + action + "\n";
                writer.write(record);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showFeedback(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Feedback");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void loadHistory(String loggedInUsername) {
        this.loggedInUsername = loggedInUsername;
        String historyFilePath = loggedInUsername + "_history.csv";

        // Clear existing items in the ListView
        historyListView.getItems().clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(historyFilePath))) {
            String line = reader.readLine(); // Read the header line
            if (line == null || !line.equals("Title, Rating")) {
                showAlert("Error", "Invalid or empty history file.", Alert.AlertType.ERROR);
                return;
            }

            // Load history and add to ListView
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String title = parts[0];
                    String rating = parts[1];

                    // Add the formatted history record as a string to the ListView
                    String record = "Title: " + title + ",      Rating: " + rating;
                    historyListView.getItems().add(record);
                }
            }
        } catch (IOException e) {
            showAlert("Error", "Failed to load history file.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void viewHistory(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserHistory.fxml"));
            Parent root = loader.load();

            Controller Controller = loader.getController();
            Controller.loadHistory(loggedInUsername); // Passing the logged-in username

            Stage stage = new Stage();
            stage.setTitle("User History");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open user history.", Alert.AlertType.ERROR);
        }
    }

    private void saveUserDetails(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE_PATH, true))) {
            writer.write(user.toString());
            writer.newLine();
        } catch (IOException e) {
            showAlert("Error", "Could not save user data.", Alert.AlertType.ERROR);
        }
    }

    private List<User> loadUsersFromFile() {
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userDetails = line.split(",");
                if (userDetails.length >= 5) {
                    User user = new User(
                            userDetails[2], userDetails[3], userDetails[4],
                            userDetails[0], userDetails[1]
                    );
                    users.add(user);
                }
            }
        } catch (IOException e) {
            showAlert("Error", "Could not load user data.", Alert.AlertType.ERROR);
        }
        return users;
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void logoutButton(ActionEvent event) {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
}