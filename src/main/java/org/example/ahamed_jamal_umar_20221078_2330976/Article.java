package org.example.ahamed_jamal_umar_20221078_2330976;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Article {
    private String category;
    private String title;
    private String description;

    public Article() {}

    // Constructor to initialize article properties
    public Article(String title, String description) {
        this.title = title;
        this.description = description;
    }

    // Getters and setters
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Method to append an additional description to the existing description
    public void appendDescription(String additionalDescription) {
        // If the description is null, initializes it as an empty string, otherwise append the new description
        this.description = (this.description == null ? "" : this.description + "\n") + additionalDescription;
    }

    // Method to load articles from the text file and create Article objects
    public static List<Article> loadArticlesFromFile(String articlesFilePath) {
        // List to store all loaded articles
        List<Article> articles = new ArrayList<>();

        // Try-with-resources to safely open and close the file reader
        try (BufferedReader reader = new BufferedReader(new FileReader(articlesFilePath))) {
            String line;
            Article article = null;

            // Read the file line by line
            while ((line = reader.readLine()) != null) {
                // Check if the line is a category header
                if (line.startsWith("Category:")) {
                    // If an article is already there, add it to the list
                    if (article != null) articles.add(article);

                    // Create a new article object
                    article = new Article();
                    String[] parts = line.split(": ", 2);
                    // Setting the category if valid
                    if (parts.length > 1) {
                        article.setCategory(parts[1]);
                    }
                } else if (line.startsWith("Title:")) {
                    // Setting the title if valid
                    if (article != null) {
                        String[] parts = line.split(": ", 2);
                        if (parts.length > 1) {
                            article.setTitle(parts[1]);
                        }
                    }
                } else if (line.startsWith("Description:")) {
                    // Setting the description if valid
                    if (article != null) {
                        String[] parts = line.split(": ", 2);
                        if (parts.length > 1) {
                            article.setDescription(parts[1]);
                        }
                    }
                } else if (article != null) {
                    // Appending the line as additional description to the article
                    article.appendDescription(line);
                }
            }

            // Add the last article if it's not null
            if (article != null) articles.add(article);

        } catch (IOException e) {
            // Handle file reading exceptions
            e.printStackTrace();
        }

        // Return the list of articles
        return articles;
    }

    // Method to classify the category of the article through the description using nlp
    public static String classifyCategory(String description) {
        // Map to hold predefined keywords for each category
        Map<String, List<String>> categoryKeywords = new HashMap<>();

        // Populating the map with categories and their respective keywords
        categoryKeywords.put("Technology", Arrays.asList("technology", "innovation", "tech", "computer", "AI", "software"));
        categoryKeywords.put("Health", Arrays.asList("health", "medicine", "fitness", "wellness", "disease", "treatment"));
        categoryKeywords.put("Sports", Arrays.asList("sports", "football", "basketball", "game", "athlete", "competition"));
        categoryKeywords.put("Travel", Arrays.asList("travel", "tourism", "vacation", "destination", "trip", "explore"));
        categoryKeywords.put("AI", Arrays.asList("artificial intelligence", "AI", "machine learning", "deep learning", "neural networks", "AI model"));
        categoryKeywords.put("Medicine", Arrays.asList("medicine", "healthcare", "doctor", "treatment", "clinical", "pharmacy", "disease", "diagnosis", "medical"));
        categoryKeywords.put("AI and Medicine", Arrays.asList("artificial intelligence", "AI", "machine learning", "medicine", "healthcare", "treatment", "clinical", "neural networks"));

        // Converting description to lowercase for case-insensitive matching
        String descriptionLower = description.toLowerCase();
        boolean containsAI = false;
        boolean containsMedicine = false;

        // Looping through each category and check if its keywords are present in the description
        for (Map.Entry<String, List<String>> entry : categoryKeywords.entrySet()) {
            String category = entry.getKey();
            List<String> keywords = entry.getValue();

            // Check if any keyword is found in the description
            for (String keyword : keywords) {
                if (descriptionLower.contains(keyword)) {
                    // Track if both "AI" and "Medicine" are found in the description
                    if (category.equals("AI")) {
                        containsAI = true;
                    } else if (category.equals("Medicine")) {
                        containsMedicine = true;
                    }

                    // If both "AI" and "Medicine" are found, return the combined category
                    if (containsAI && containsMedicine) {
                        return "AI and Medicine";
                    }

                    // Returning the category if a match is found
                    return category;
                }
            }
        }

        // If no match is found, return "Uncategorized"
        return "Uncategorized";
    }
}