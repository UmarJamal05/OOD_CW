package org.example.ahamed_jamal_umar_20221078_2330976;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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

    private static final String FILE_PATH = "users.txt";

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

    private boolean validateLogin(String username, String password) {
        List<User> users = loadUsersFromFile();
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
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

    private void saveUserDetails(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(user.toString());
            writer.newLine();
        } catch (IOException e) {
            showAlert("Error", "Could not save user data.", Alert.AlertType.ERROR);
        }
    }

    private List<User> loadUsersFromFile() {
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
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