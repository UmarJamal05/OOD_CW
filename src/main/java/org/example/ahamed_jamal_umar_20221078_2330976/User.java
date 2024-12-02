package org.example.ahamed_jamal_umar_20221078_2330976;

import java.io.*;
import java.util.List;

public class User extends Person {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private List<Article> likedArticles; // Association: User interacts with Articles
    private RecommendationEngine recommendationEngine; // Association: User interacts with RecommendationEngine
    private History history; // Association: User interacts with History
    private static final String USER_FILE_PATH = "users.txt";
    public User(String username, String password, History history, RecommendationEngine recommendationEngine) {
        super(password);
        this.username = username;
        this.history = history;
        this.recommendationEngine = recommendationEngine;
    }

    public User(String firstName, String lastName, String email, String username, String password) {
        super(password);
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return super.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Login method
    public static boolean login(String username, String password, String usersFilePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(usersFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String storedUsername = parts[0];
                    String storedPassword = parts[1];
                    if (storedUsername.equals(username) && storedPassword.equals(password)) {
                        return true; // Login successful
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false; // Login failed
    }

    // Register method
    public static boolean register(User user, String usersFilePath) {
        if (isUsernameTaken(user.getUsername(), usersFilePath)) {
            return false; // Registration failed if the username is already taken
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(usersFilePath, true))) {
            writer.write(user.toString());
            writer.newLine();
            return true; // Registration successful
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false; // Registration failed
    }

    // Helper method to check if a username is taken
    public static boolean isUsernameTaken(String username, String usersFilePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(usersFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String storedUsername = parts[0];
                    if (storedUsername.equals(username)) {
                        return true; // Username is taken
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getUserDetails(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE_PATH))) {
            // Skip the header
            reader.readLine();
            String line;

            while ((line = reader.readLine()) != null) {
                String[] userDetailsArray = line.split(",");

                if (userDetailsArray.length == 5 && userDetailsArray[0].equals(username)) {
                    return "Username: " + userDetailsArray[0] + "\n" +
                            "First Name: " + userDetailsArray[2] + "\n" +
                            "Last Name: " + userDetailsArray[3] + "\n" +
                            "Email: " + userDetailsArray[4] + "\n";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "No details found for the username: " + username;
    }


    // ToString method for User details
    @Override
    public String toString() {
        return username + "," + password + "," + firstName + "," + lastName + "," + email;
    }
}