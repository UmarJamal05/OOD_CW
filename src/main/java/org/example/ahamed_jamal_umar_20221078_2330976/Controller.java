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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Controller {
    private String loggedInUsername; // Stores the logged-in user's username
    public Button signupButton; // Button to trigger the signup page
    @FXML
    private Button exitButton; // Button to close the application
    @FXML
    private TextField firstnameField, lastnameField, emailaddressField, usernameField, passwordField, confirmpasswordField; // Fields for user registration
    @FXML
    private TextField UsernameLoginInput, PasswordLoginInput; // Fields for user login
    @FXML
    private TextField SystemAdminName, SystemAdminPassword; // Fields for system admin login
    @FXML
    private TextField newPasswordField, newConfirmPasswordField; // Fields for password change
    @FXML
    private TextField addTitleField; // Title input for adding articles
    @FXML
    private TextArea addDescriptionArea; // Description area for adding articles
    @FXML
    private TextArea userDetailsTextArea; // Display user details
    @FXML
    private ListView<String> historyListView; // Displays user history
    @FXML
    private ListView<String> preferenceListView; // Displays user preferences
    private static final String USER_FILE_PATH = "users.txt"; // File path for user data
    private static final String ARTICLES_FILE_PATH = "articles.txt"; // File path for article data
    private History history; // Object for user history

    // Method to set the logged-in user's username
    private void setLoggedInUsername(String username) {
        this.loggedInUsername = username;
    }
    // Method to set up the history with the loggedInUsername
    public void setHistory(String loggedInUsername) {
        history = new History(loggedInUsername);
        history.loadHistory(historyListView); // Load the history into the ListView
    }
    // Method to transition to the Welcome page
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
    // Method to transition to the System Admin login page
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
    // Method to transition to the User login page
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
    // Method to handle the System Admin login process
    @FXML
    private void onAdminLogin(ActionEvent event) {
        String username = SystemAdminName.getText();
        String password = SystemAdminPassword.getText();

        // Check if fields are empty
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Login Failed", "All fields must be filled.", Alert.AlertType.ERROR);
            return;
        }

        // Create an instance of SystemAdministrator
        SystemAdministrator admin = new SystemAdministrator();

        // Validate login credentials using the SystemAdministrator method
        if (admin.validateLogin(username, password)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminDashboard.fxml"));
                Parent dashboardRoot = loader.load();

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                Scene scene = new Scene(dashboardRoot);
                stage.setScene(scene);

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
    // Method to handle User login process
    @FXML
    private void onUserLogin(ActionEvent event) {
        String name = UsernameLoginInput.getText();
        String password = PasswordLoginInput.getText();

        // Check if fields are empty
        if (name.isEmpty() || password.isEmpty()) {
            showAlert("Login Failed", "All fields must be filled.", Alert.AlertType.ERROR);
            return;
        }

        // Validate login using the User class
        if (User.login(name, password, USER_FILE_PATH)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("UserDashboard.fxml"));
                Parent dashboardRoot = loader.load();

                Controller controller = loader.getController();
                controller.setLoggedInUsername(name);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(dashboardRoot);
                stage.setScene(scene);
                stage.setTitle("Dashboard");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to load the Dashboard.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Login Failed", "Invalid username or password.", Alert.AlertType.ERROR);
        }
    }
    // Method to transition to the add article page
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
    // Method to handle adding a new article
    @FXML
    private void onAddEnter() {
        String title = addTitleField.getText().trim();
        String description = addDescriptionArea.getText().trim();

        // Validate input fields
        if (title.isEmpty() || description.isEmpty()) {
            showAlert("Input Error", "All fields must be filled.", Alert.AlertType.ERROR);
            return;
        }

        // Creating an instance of SystemAdministrator
        SystemAdministrator admin = new SystemAdministrator();
        String filePath = "articles.txt";

        try {
            // Check for duplicate title using SystemAdministrator's method
            if (admin.isTitleDuplicate(title, filePath)) {
                showAlert("Duplicate Title", "An article with this title already exists.", Alert.AlertType.ERROR);
                return;
            }

            // Add the article using SystemAdministrator's method
            if (admin.addArticle(title, description, filePath)) {
                showAlert("Success", "Article added successfully.", Alert.AlertType.INFORMATION);

                // Clear the input fields
                addTitleField.clear();
                addDescriptionArea.clear();
            } else {
                showAlert("Error", "Failed to save the article. Try again.", Alert.AlertType.ERROR);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "An unexpected error occurred.", Alert.AlertType.ERROR);
        }
    }
    // Method to handle transitioning back to Admin Dashboard
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
    // Method to handle user signup
    @FXML
    public void onSignup(ActionEvent event) {
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
    // Method to handle registration of new users
    @FXML
    private void onRegisterSignup(ActionEvent event) {
        // To get users details from the text fields
        String firstName = firstnameField.getText();
        String lastName = lastnameField.getText();
        String email = emailaddressField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmpasswordField.getText();

        // Methods to check for validations
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
                username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Registration Failed", "All fields must be filled.", Alert.AlertType.ERROR);
            return;
        }

        // Calling the isValidEmail to check if email is not valid
        if (!isValidEmail(email)) {
            showAlert("Registration Failed", "Please enter a valid email address.", Alert.AlertType.ERROR);
            return;
        }

        // Validation for username
        if (username.length() < 5) {
            showAlert("Registration Failed", "Username must be at least 5 characters long.", Alert.AlertType.ERROR);
            return;
        }

        // Validation for password
        if (password.length() < 8) {
            showAlert("Registration Failed", "Password must be at least 8 characters long.", Alert.AlertType.ERROR);
            return;
        }
        if (!password.equals(confirmPassword)) {
            showAlert("Registration Failed", "Passwords do not match.", Alert.AlertType.ERROR);
            return;
        }

        // Creating an instance of User
        User newUser = new User(firstName, lastName, email, username, password);

        if (User.register(newUser, USER_FILE_PATH)) {
            showAlert("Registration Successful", "User registered successfully!", Alert.AlertType.INFORMATION);
            // Fields are cleared after successful registration
            firstnameField.clear();
            lastnameField.clear();
            emailaddressField.clear();
            usernameField.clear();
            passwordField.clear();
            confirmpasswordField.clear();
        } else {
            showAlert("Registration Failed", "Username is already taken. Please choose a different one.", Alert.AlertType.ERROR);
        }
    }
    // Method to go back to login page for user
    @FXML
    private void onHandleSuccessfulRegistration() {
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
    // Method to handle view articles button click
    @FXML
    public void onViewArticle(ActionEvent event) {
        try {
            // Create a VBox to hold article buttons
            VBox articlesVBox = new VBox(10);

            // Load articles from the file
            List<Article> articles = Article.loadArticlesFromFile(ARTICLES_FILE_PATH);

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
    // Method to go to view history page
    @FXML
    public void onViewHistory(ActionEvent event) {
        try {
            // Load the UserHistory file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserHistory.fxml"));
            Parent root = loader.load();

            Controller Controller = loader.getController();
            Controller.setHistory(loggedInUsername); // Passing the logged-in username

            Stage stage = new Stage();
            stage.setTitle("User History");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open user history.", Alert.AlertType.ERROR);
        }
    }
    // Method to go from user profile to dashboard
    @FXML
    public void onProfileToDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserDashboard.fxml"));
            Parent root = loader.load();

            Controller Controller = loader.getController();
            Controller.setLoggedInUsername(loggedInUsername);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not load the dashboard.", Alert.AlertType.ERROR);
        }
    }
    // Method to go to manage profile page
    @FXML
    public void onManageProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ManageProfile.fxml"));
            Parent root = loader.load();

            Controller Controller = loader.getController();
            Controller.showUserDetails(loggedInUsername);
            Controller.setLoggedInUsername(loggedInUsername);


            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            stage.setTitle("User Profile");

            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Method to go to change password page
    @FXML
    public void onPasswordChange(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PasswordChange.fxml"));
            Parent root = loader.load();

            Controller Controller = loader.getController();
            Controller.setLoggedInUsername(loggedInUsername);


            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            stage.setTitle("Change Password");

            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the error appropriately
        }
    }
    // Method to change password for user
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

            // Skips the first line (header line)
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
            // Fields are cleared after successful password change
            newPasswordField.clear();
            newConfirmPasswordField.clear();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while updating the password.", Alert.AlertType.ERROR);
        }
    }
    // Method to go from preferences to dashboard
    @FXML
    private void onPreferenceToDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserDashboard.fxml"));
            Parent root = loader.load();

            Controller Controller = loader.getController();
            Controller.setLoggedInUsername(loggedInUsername);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            stage.setTitle("Dashboard");

            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the error appropriately
        }
    }
    // Method to transition to preferences page
    @FXML
    private void onPreferences(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserPreference.fxml"));
            Parent root = loader.load();

            Controller preferencesController = loader.getController();
            preferencesController.setLoggedInUsername(loggedInUsername);

            // Generate preferences and recommendations
            preferencesController.generatePreferences("articles.txt");
            List<String> recommendations = preferencesController.readPreferences();

            // Display the recommendations
            Platform.runLater(() -> preferencesController.displayRecommendations(recommendations));

            // Get the current stage
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Preference");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Method to log out from application
    @FXML
    private void onLogoutButton(ActionEvent event) {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
    // Method to check if email is valid
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
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
    // Adding the user history to their specific file
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
    // This method is called to generate preferences and load them into the ListView
    public void generatePreferences(String articlesFilePath) {
        try {
                RecommendationEngine recommendationEngine = new RecommendationEngine(loggedInUsername);
                List<String> recommendations = recommendationEngine.recommendArticles(articlesFilePath);
                displayRecommendations(recommendations);  // Display recommendations
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
    // Read preferences from the file
    public List<String> readPreferences() {
        try {
            String preferencesFileName = loggedInUsername + "_preferences.txt";
            Path filePath = Paths.get(preferencesFileName);
            if (!Files.exists(filePath)) {
                showAlert("Warning", "Read some articles first to get personalized Recommendations.", Alert.AlertType.ERROR);
            }
            return Files.readAllLines(filePath);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); // Return an empty list on failure
        }
    }
    // Display the recommendations in the ListView
    public void displayRecommendations(List<String> recommendations) {
        if (recommendations != null) {
            preferenceListView.getItems().clear();  // Clear any existing items in the ListView
            preferenceListView.getItems().addAll(recommendations);  // Add the recommendations to the ListView
        }
    }
    // Method to show user details
    private void showUserDetails(String loggedInUsername) {
        String details = User.getUserDetails(loggedInUsername);
        userDetailsTextArea.setText(details);
        userDetailsTextArea.setEditable(false);
    }
    // Utility method to show feedback
    private void showFeedback(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Feedback");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    // Utility method to show alerts
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}