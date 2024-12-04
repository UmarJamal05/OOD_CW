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

    public void appendDescription(String additionalDescription) {
        this.description = (this.description == null ? "" : this.description + "\n") + additionalDescription;
    }

    // Method to load articles from the text file
    public static List<Article> loadArticlesFromFile(String articlesFilePath) {
        List<Article> articles = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(articlesFilePath))) {
            String line;
            Article article = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Category:")) {
                    if (article != null) articles.add(article);
                    article = new Article();
                    String[] parts = line.split(": ", 2);
                    if (parts.length > 1) {
                        article.setCategory(parts[1]);
                    }
                } else if (line.startsWith("Title:")) {
                    if (article != null) {
                        String[] parts = line.split(": ", 2);
                        if (parts.length > 1) {
                            article.setTitle(parts[1]);
                        }
                    }
                } else if (line.startsWith("Description:")) {
                    if (article != null) {
                        String[] parts = line.split(": ", 2);
                        if (parts.length > 1) {
                            article.setDescription(parts[1]);
                        }
                    }
                } else if (article != null) {
                    article.appendDescription(line);
                }
            }
            if (article != null) articles.add(article);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return articles;
    }

    // Method to classify the category of the article through the description using nlp
    public static String classifyCategory(String description) {
        Map<String, List<String>> categoryKeywords = new HashMap<>();

        categoryKeywords.put("Technology", Arrays.asList("technology", "innovation", "tech", "computer", "AI", "software"));
        categoryKeywords.put("Health", Arrays.asList("health", "medicine", "fitness", "wellness", "disease", "treatment"));
        categoryKeywords.put("Sports", Arrays.asList("sports", "football", "basketball", "game", "athlete", "competition"));
        categoryKeywords.put("Travel", Arrays.asList("travel", "tourism", "vacation", "destination", "trip", "explore"));
        categoryKeywords.put("AI", Arrays.asList("artificial intelligence", "AI", "machine learning", "deep learning", "neural networks", "AI model"));
        categoryKeywords.put("Medicine", Arrays.asList("medicine", "healthcare", "doctor", "treatment", "clinical", "pharmacy", "disease", "diagnosis", "medical"));
        categoryKeywords.put("AI and Medicine", Arrays.asList("artificial intelligence", "AI", "machine learning", "medicine", "healthcare", "treatment", "clinical", "neural networks"));

        String descriptionLower = description.toLowerCase();
        boolean containsAI = false;
        boolean containsMedicine = false;

        for (Map.Entry<String, List<String>> entry : categoryKeywords.entrySet()) {
            String category = entry.getKey();
            List<String> keywords = entry.getValue();

            for (String keyword : keywords) {
                if (descriptionLower.contains(keyword)) {
                    if (category.equals("AI")) {
                        containsAI = true;
                    } else if (category.equals("Medicine")) {
                        containsMedicine = true;
                    }

                    if (containsAI && containsMedicine) {
                        return "AI and Medicine";
                    }
                    return category;
                }
            }
        }
        return "Uncategorized";
    }
}