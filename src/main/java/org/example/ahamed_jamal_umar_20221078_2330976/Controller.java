package org.example.ahamed_jamal_umar_20221078_2330976;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Controller {

    public Button signupButton;
    @FXML
    private TextField firstnameField, lastnameField, emailaddressField, usernameField, passwordField, confirmpasswordField;
    @FXML
    private TextField UsernameLoginInput, PasswordLoginInput;
    @FXML
    private TextField SystemAdminName, SystemAdminPassword;
    @FXML
    private Button exitButton;
    private String loggedInUsername;
    @FXML
    private TextField newPasswordField, newConfirmPasswordField;
    @FXML
    private TextField addCategoryField, addTitleField;
    @FXML
    private TextArea addDescriptionArea;

    @FXML
    private Button backButton;
    @FXML
    private ListView<String> historyListView;
    @FXML
    private TextArea userDetailsTextArea;
    @FXML
    private ListView<String> preferenceListView;

    private static final String USER_FILE_PATH = "users.txt";
    @FXML
    public void onWelcomePage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("WelcomePage.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            stage.setTitle("Personalized News Recommendation System");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void onSystemAdminPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminLogin.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            stage.setTitle("Login Page");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void onUserPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserLogin.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            stage.setTitle("Login Page");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void onAdminLogin(ActionEvent event) {
        String username = SystemAdminName.getText();
        String password = SystemAdminPassword.getText();

        // Check if fields are empty
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Login Failed", "All fields must be filled.", Alert.AlertType.ERROR);
            return; // Stop further execution
        }

        // Create an instance of SystemAdministrator
        SystemAdministrator admin = new SystemAdministrator();

        // Validate login credentials directly
        if (username.equals(admin.getName()) && password.equals(admin.getPassword())) {
            try {
                // Load the Dashboard.fxml file
                FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminDashboard.fxml"));
                Parent dashboardRoot = loader.load();

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
            showAlert("Login Failed", "Invalid name or password.", Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void onUserLogin(ActionEvent event) {
        String name = UsernameLoginInput.getText();
        String password = PasswordLoginInput.getText();

        // Check if fields are empty
        if (name.isEmpty() || password.isEmpty()) {
            showAlert("Login Failed", "All fields must be filled.", Alert.AlertType.ERROR);
            return; // Stop further execution
        }

        // Validate login
        if (validateLogin(name, password)) {
            try {
                // Load the Dashboard.fxml file
                FXMLLoader loader = new FXMLLoader(getClass().getResource("UserDashboard.fxml"));
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

    @FXML
    public void onAddArticles(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddArticle.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            stage.setTitle("Add Article");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onAddEnter() {
        String category = addCategoryField.getText().trim();
        String title = addTitleField.getText().trim();
        String description = addDescriptionArea.getText().trim();

        // Validate input fields
        if (category.isEmpty() || title.isEmpty() || description.isEmpty()) {
            showAlert("Input Error", "All fields must be filled.", Alert.AlertType.ERROR);
            return;
        }

        if (isTitleDuplicate(title)) {
            showAlert("Duplicate Title", "An article with this title already exists.", Alert.AlertType.ERROR);
            return;
        }

        // Format the article data
        String articleData = String.format(
                "%nCategory: %s%nTitle: %s%nDescription: %s",
                category, title, description
        );

        // Append to the text file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("articles.txt", true))) {
            writer.newLine();
            writer.write(articleData);
            showAlert("Success", "Article added successfully.", Alert.AlertType.INFORMATION);

            // Clear the input fields
            addCategoryField.clear();
            addTitleField.clear();
            addDescriptionArea.clear();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save the article. Try again.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void onArticleToDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminDashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            stage.setTitle("Dashboard");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
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
        firstnameField.clear();
        lastnameField.clear();
        emailaddressField.clear();
        usernameField.clear();
        passwordField.clear();
        confirmpasswordField.clear();
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
    public void onViewArticle(ActionEvent event) {
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

    private boolean isTitleDuplicate(String title) {
        try (BufferedReader reader = new BufferedReader(new FileReader("articles.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Title: ")) {
                    String existingTitle = line.substring(7).trim(); // Extract title from "Title: <Title>"
                    if (existingTitle.equalsIgnoreCase(title)) {
                        return true; // Duplicate found
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to check for duplicate titles.", Alert.AlertType.ERROR);
        }
        return false; // No duplicate found
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
                Stage stage = (Stage) skipButton.getScene().getWindow();
                stage.close();
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
            showAlert("Warning","Empty History", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void onViewHistory(ActionEvent event) {
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
        try {
            File file = new File(USER_FILE_PATH);
            boolean isFileEmpty = !file.exists() || file.length() == 0;

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE_PATH, true))) {
                if (isFileEmpty) {
                    writer.write("Username:Password:Firstname:Lastname:Email Address");
                    writer.newLine();  // Move to the next line
                }
                writer.write(user.toString());
                writer.newLine();
            }
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
    private void onLogoutButton(ActionEvent event) {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
    @FXML
    public void onProfileToDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserDashboard.fxml"));
            Parent root = loader.load();

            Controller Controller = loader.getController();
            Controller.setLoggedInUsername(loggedInUsername);

            // Get the current stage
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load the dashboard.", Alert.AlertType.ERROR);
        }
    }
    public void showUserDetails(String loggedInUsername) {

        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_PATH))) {
            String line;
            StringBuilder userDetails = new StringBuilder();

            // Skip the first line (header line)
            reader.readLine();

            // Loop through the file to find the matching username
            while ((line = reader.readLine()) != null) {
                String[] userDetailsArray = line.split(",");

                if (userDetailsArray.length == 5 && userDetailsArray[0].equals(loggedInUsername)) {
                    userDetails.append("Username: ").append(userDetailsArray[0]).append("\n")
                            .append("First Name: ").append(userDetailsArray[2]).append("\n")
                            .append("Last Name: ").append(userDetailsArray[3]).append("\n")
                            .append("Email: ").append(userDetailsArray[4]).append("\n");
                    break;
                }
            }

            // If no user is found with the loggedInUsername, display a message
            if (userDetails.isEmpty()) {
                userDetails.append("No details found for the username: ").append(loggedInUsername).append("\n");
            }

            userDetailsTextArea.setText(userDetails.toString());
            userDetailsTextArea.setEditable(false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void onManageProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ManageProfile.fxml"));
            Parent root = loader.load();

            Controller Controller = loader.getController();
            Controller.showUserDetails(loggedInUsername);
            Controller.setLoggedInUsername(loggedInUsername);

            // Get the current stage (window)
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            stage.setTitle("User Profile");
            // Set the new scene for the stage (window)
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the error appropriately (e.g., show an alert)
        }
    }
    @FXML
    public void onPasswordChange(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PasswordChange.fxml"));
            Parent root = loader.load();

            Controller Controller = loader.getController();
            Controller.setLoggedInUsername(loggedInUsername);

            // Get the current stage (window)
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            stage.setTitle("Change Password");
            // Set the new scene for the stage (window)
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the error appropriately (e.g., show an alert)
        }
    }
    @FXML
    private void onChangePassword(ActionEvent event) {
        String newPassword = newPasswordField.getText();
        String confirmPassword = newConfirmPasswordField.getText();

        if (!newPassword.equals(confirmPassword)) {
            showAlert("Error", "Passwords do not match.", Alert.AlertType.ERROR);
            return;
        }

        // Validate input
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Error", "Fields cannot be empty.", Alert.AlertType.ERROR);
            return;
        }

        // Update password in the file
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_PATH))) {
            String line;
            List<String> updatedLines = new ArrayList<>();
            boolean userFound = false;

            // Skip the first line (header line)
            reader.readLine();

            // Read each line and check for the matching username
            while ((line = reader.readLine()) != null) {
                String[] userDetails = line.split(",");

                if (userDetails.length == 5 && userDetails[0].equals(loggedInUsername)) {
                    // If username matches, update the password
                    userDetails[1] = newPassword; // Update the password
                    updatedLines.add(String.join(",", userDetails)); // Save the updated line
                    userFound = true;
                } else {
                    updatedLines.add(line); // If no match, keep the current line
                }
            }

            if (!userFound) {
                showAlert("Error", "User not found.", Alert.AlertType.ERROR);
                return;
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE_PATH))) {
                writer.write("Username:Password:Firstname:Lastname:Email Address"); // Write header line again
                writer.newLine();

                // Write all updated lines
                for (String updatedLine : updatedLines) {
                    writer.write(updatedLine);
                    writer.newLine();
                }
            }

            showAlert("Success", "Password updated successfully!", Alert.AlertType.INFORMATION);
            newPasswordField.clear();
            newConfirmPasswordField.clear();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while updating the password.", Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void onPreferenceToDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserDashboard.fxml"));
            Parent root = loader.load();

            Controller Controller = loader.getController();
            Controller.setLoggedInUsername(loggedInUsername);

            // Get the current stage (window)
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            stage.setTitle("Dashboard");
            // Set the new scene for the stage (window)
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the error appropriately (e.g., show an alert)
        }
    }
    @FXML
    private void onPreferences(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserPreference.fxml"));
            Parent root = loader.load();

            Controller Controller = loader.getController();
            Controller.setLoggedInUsername(loggedInUsername);

            // Generate recommendations for the user
            Map<String, Integer> recommendations = Controller.generateRecommendations(loggedInUsername);
            Platform.runLater(() -> Controller.displayRecommendations(recommendations));

            // Get the current stage (window)
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            stage.setTitle("Preference");
            // Set the new scene for the stage (window)
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the error appropriately (e.g., show an alert)
        }
    }
    // Method to load user history from username_history.csv
    private List<History> loadUserHistory(String loggedInUsername) {
        String historyFilePath = loggedInUsername + "_history.csv";

        List<History> userHistory = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(historyFilePath))) {
            String line;
            Map<String, String> historyMap = new HashMap<>();
            while ((line = reader.readLine()) != null) {
                String[] userHistoryData = line.split(",");
                if (userHistoryData.length == 3) {
                    historyMap.put(userHistoryData[1].trim(), userHistoryData[2].trim());
                }
            }
            userHistory = historyMap.entrySet()
                    .stream()
                    .map(e -> new History(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userHistory;
    }

    // Method to load articles from articles.txt
    private Map<String, String> loadArticles() {
        Map<String, String> articles = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("articles.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] articleData = line.split(",");
                if (articleData.length >= 2) {
                    String topic = articleData[0];
                    String description = articleData[1];
                    articles.put(topic, description);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return articles;
    }

    // Method to extract relevant keywords (could be replaced with more advanced NLP techniques like TF-IDF or Sentiment Analysis)
    private Set<String> extractKeywords(String description) {
            Set<String> keywords = new HashSet<>();
            String[] words = description.toLowerCase().replaceAll("[^a-z0-9\\s]", "").split("\\s+"); // Clean text
            for (String word : words) {
                if (word.length() > 3) { // Ignore very short words
                    keywords.add(word);
                }
            }
            return keywords;
    }

        // Method to generate recommendations based on user history and article topics
    private Map<String, Integer> generateRecommendations(String loggedInUsername) {
        Map<String, Integer> recommendations = new HashMap<>();
        Map<String, String> articles = loadArticles(); // Key: Title, Value: Description
        List<History> userHistory = loadUserHistory(loggedInUsername); // User's article history

        // Extract keywords from articles
        Map<String, Set<String>> articleKeywords = new HashMap<>();
        for (Map.Entry<String, String> entry : articles.entrySet()) {
            articleKeywords.put(entry.getKey(), extractKeywords(entry.getValue()));
        }

        // Extract keywords from user's liked articles
        Set<String> likedKeywords = new HashSet<>();
        for (History history : userHistory) {
            if (history.getRating().equalsIgnoreCase("like")) {
                String likedArticleTitle = history.getTitle();
                likedKeywords.addAll(articleKeywords.getOrDefault(likedArticleTitle, new HashSet<>()));
            }
        }

        // Compare liked keywords with other articles and calculate scores
        for (Map.Entry<String, Set<String>> entry : articleKeywords.entrySet()) {
            String articleTitle = entry.getKey();
            Set<String> articleKeywordSet = entry.getValue();

            // Skip articles the user has already interacted with
            boolean alreadyInteracted = userHistory.stream()
                    .anyMatch(history -> history.getTitle().equalsIgnoreCase(articleTitle));
            if (alreadyInteracted) continue;

            // Calculate score based on keyword matches
            int score = 0;
            for (String keyword : articleKeywordSet) {
                if (likedKeywords.contains(keyword)) {
                    score++; // Increment score for each matching keyword
                }
            }
            recommendations.put(articleTitle, score);
        }

        return recommendations;
    }

    // Method to display the recommendations in the ListView
    private void displayRecommendations(Map<String, Integer> recommendations) {
        // Sort the recommendations by score in descending order
        List<Map.Entry<String, Integer>> sortedRecommendations = new ArrayList<>(recommendations.entrySet());
        sortedRecommendations.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue())); // Sort descending

        // Clear the current list and add sorted article titles
        preferenceListView.getItems().clear();
        for (Map.Entry<String, Integer> entry : sortedRecommendations) {
            if (entry.getValue() > 0) { // Only show articles with a positive score
                preferenceListView.getItems().add(entry.getKey());
            }
        }
    }
}