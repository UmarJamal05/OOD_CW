package org.example.ahamed_jamal_umar_20221078_2330976;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class SystemAdministrator extends Person {
    private String name;
    private List<Article> articles;

    // Default constructor initializing the name and password
    public SystemAdministrator() {
        super("u.a.j.005");
        this.name = "Umar Jamal";
    }

    @Override
    public String getPassword() {
        return super.password;
    }// Return the protected password field

    // Getter for the administrator's name
    public String getName() {
        return name;
    }
    // Getter for the list of articles managed by the admin
    public List<Article> getArticles() {
        return articles;
    }

    // Method for validating admin login credentials against stored values
    public boolean validateLogin(String username, String password) {
        return this.name.equals(username) && this.password.equals(password);
    }

    // Method to check if a title is a duplicate
    public boolean isTitleDuplicate(String title, String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        for (String line : lines) {
            if (line.startsWith("Title:") && line.substring(6).trim().equalsIgnoreCase(title.trim())) {
                return true;// Duplicate found
            }
        }
        return false;// No duplicates
    }

    // Adds a new article to the file with automatic category classification
    public boolean addArticle(String title, String description, String filePath) {
        String category = Article.classifyCategory(description);// Classify the category of the article

        String articleData = String.format("%nCategory: %s%nTitle: %s%nDescription: %s", category, title, description);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.newLine();
            writer.write(articleData);// Write article to file
            return true;// Successfully added
        } catch (IOException e) {
            e.printStackTrace();// Handle IO exceptions
            return false;// Addition failed
        }
    }
}