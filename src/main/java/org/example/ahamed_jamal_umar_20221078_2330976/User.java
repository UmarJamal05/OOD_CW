package org.example.ahamed_jamal_umar_20221078_2330976;

import java.io.*;

public class User extends Person {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private static final String USER_FILE_PATH = "users.txt";

    public User(String firstName, String lastName, String email, String username, String password) {
        super(password);
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    // Getters
    public String getUsername() {
        return username;
    }
    @Override
    public String getPassword() {
        return super.password;
    }
    public void setPassword(String password) { this.password = password; }

    // Login method
    public static boolean login(String username, String password, String usersFilePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(usersFilePath))) {
            String line = reader.readLine(); // Read the first line (header)
            while ((line = reader.readLine()) != null) { // Skip the header and read subsequent lines
                String[] parts = line.split(","); // Split by comma (",")
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
        // Check if the username is already taken
        if (isUsernameTaken(user.getUsername(), usersFilePath)) {
            return false; // Registration failed if the username is already taken
        }

        // Check if the file is empty (or doesn't exist)
        File file = new File(usersFilePath);
        boolean isFileEmpty = !file.exists() || file.length() == 0;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(usersFilePath, true))) {
            // If the file is empty, write the header first
            if (isFileEmpty) {
                writer.write("Username:Password:Firstname:Lastname:Email Address");
                writer.newLine();  // Move to the next line
            }

            // Write the user details to the file
            writer.write(user.toString());
            writer.newLine();
            return true; // Registration successful
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false; // Registration failed
    }

    // Helper method to check if a username is already taken
    public static boolean isUsernameTaken(String username, String usersFilePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(usersFilePath))) {
            String line = reader.readLine(); // Read the first line (header)
            while ((line = reader.readLine()) != null) { // Skip the header and read subsequent lines
                String[] parts = line.split(","); // Split by comma (",")
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

    @Override
    public String toString() {
        return username + "," + password + "," + firstName + "," + lastName + "," + email;
    }
}