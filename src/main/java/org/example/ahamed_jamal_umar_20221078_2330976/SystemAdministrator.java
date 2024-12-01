package org.example.ahamed_jamal_umar_20221078_2330976;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class SystemAdministrator extends Person {
    private String name;

    public SystemAdministrator() {
        super("u.a.j.005");
        this.name = "Umar Jamal";
    }

    @Override
    public String getPassword() {
        return super.password;
    }

    public String getName() {
        return name;
    }

    // Method for validating login credentials
    public boolean validateLogin(String username, String password) {
        return this.name.equals(username) && this.password.equals(password);
    }

    // Method to check if a title is a duplicate
    public boolean isTitleDuplicate(String title, String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        for (String line : lines) {
            if (line.startsWith("Title:") && line.substring(6).trim().equalsIgnoreCase(title.trim())) {
                return true;
            }
        }
        return false;
    }

    // Method for adding an article
    public boolean addArticle(String title, String description, String filePath) {
        // Classify category based on the description
        String category = Article.classifyCategory(description);

        String articleData = String.format("%nCategory: %s%nTitle: %s%nDescription: %s", category, title, description);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.newLine();
            writer.write(articleData);
            return true; // Article added successfully
        } catch (IOException e) {
            e.printStackTrace();
            return false; // Failed to add article
        }
    }
}