package org.example.ahamed_jamal_umar_20221078_2330976;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import org.apache.commons.text.similarity.CosineSimilarity;

public class RecommendationEngine {

    private static String loggedInUsername;

    public RecommendationEngine(String username) {
        loggedInUsername = username;
    }

    // Method to give recommended articles
    public List<String> recommendArticles(String articlesFilePath) throws Exception {
        String historyFilePath = loggedInUsername + "_history.csv";

        // Load articles and user history
        List<Article> articles = parseArticles(articlesFilePath);
        Map<String, String> userHistory = parseUserHistory(historyFilePath);

        // Filter articles: exclude already liked, disliked, or skipped
        Set<String> excludedTitles = userHistory.keySet();
        List<Article> candidateArticles = articles.stream()
                .filter(article -> !excludedTitles.contains(article.getTitle()))
                .collect(Collectors.toList());

        // Get liked articles
        List<Article> likedArticles = articles.stream()
                .filter(article -> "like".equals(userHistory.get(article.getTitle())))
                .collect(Collectors.toList());

        // Compute recommendations
        List<String> recommendations = rankArticles(candidateArticles, likedArticles);

        // Save preferences to a file
        savePreferences(recommendations);

        return recommendations;
    }


    // Method to go through the articles file
    private List<Article> parseArticles(String filePath) throws Exception {
        // List to store all parsed articles
        List<Article> articles = new ArrayList<>();

        // Read all lines from the file
        List<String> lines = Files.readAllLines(Paths.get(filePath));

        // Variables to hold the current title and description being processed
        String title = "";
        StringBuilder description = new StringBuilder();

        // Iterate through each line in the file
        for (String line : lines) {
            // If the line starts with "Title:", it indicates the start of a new article
            if (line.startsWith("Title:")) {
                // If there is an existing title, add the previous article to the list
                if (!title.isEmpty()) {
                    articles.add(new Article(title, description.toString().trim()));
                }
                // Set the new title and reset the description
                title = line.substring(6).trim();
                description.setLength(0);
            } else if (line.startsWith("Description:")) {
                // If the line starts with "Description:", append the description text
                description.append(line.substring(12).trim()).append(" ");
            }
        }

        // After reading all lines, add the last article if it exists
        if (!title.isEmpty()) {
            articles.add(new Article(title, description.toString().trim()));
        }

        // Returning the list of articles parsed from the file
        return articles;
    }

    // Method to parse through user history from a CSV file and return a map of article titles and ratings
    private Map<String, String> parseUserHistory(String filePath) throws Exception {
        // Map to store user ratings for articles
        Map<String, String> userRatings = new HashMap<>();

        // To check if the history file exists
        if (!Files.exists(Paths.get(filePath))) {
            // Return empty map if the file does not exist
            return userRatings;
        }

        // Read all the lines from the user history file
        List<String> lines = Files.readAllLines(Paths.get(filePath));

        // Iterate through each line in the file
        for (String line : lines) {
            // Split the line into two parts, article title and user rating
            String[] parts = line.split(",");
            if (parts.length == 2) {
                // Store the title and rating in the map
                userRatings.put(parts[0].trim(), parts[1].trim().toLowerCase());
            }
        }

        // Returning the map containing article titles and user ratings
        return userRatings;
    }

    // Rank articles based on cosine similarity
    private List<String> rankArticles(List<Article> candidates, List<Article> likedArticles) throws InterruptedException, ExecutionException {
        // ExecutorService to handle concurrency
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        // Map to store similarity scores for each candidate article
        ConcurrentMap<String, Double> similarityScores = new ConcurrentHashMap<>();
        CosineSimilarity cosineSimilarity = new CosineSimilarity();

        // Tasks for computing similarity scores
        List<Callable<Void>> tasks = new ArrayList<>();

        for (Article candidate : candidates) {
            tasks.add(() -> {
                String candidateText = candidate.getTitle() + " " + candidate.getDescription();
                double maxSimilarity = 0.0;

                for (Article liked : likedArticles) {
                    String likedText = liked.getTitle() + " " + liked.getDescription();

                    // Tokenize both texts
                    Map<CharSequence, Integer> candidateTokens = tokenizeText(candidateText);
                    Map<CharSequence, Integer> likedTokens = tokenizeText(likedText);

                    // Compute cosine similarity
                    if (!candidateTokens.isEmpty() && !likedTokens.isEmpty()) {
                        double similarity = cosineSimilarity.cosineSimilarity(candidateTokens, likedTokens);
                        maxSimilarity = Math.max(maxSimilarity, similarity);
                    }
                }

                // Store the maximum similarity score for this candidate article
                similarityScores.put(candidate.getTitle(), maxSimilarity);
                return null;
            });
        }

        // Execute all tasks
        executor.invokeAll(tasks);

        // Shut down the executor
        executor.shutdown();

        // Sort articles by similarity score in descending order and return the ranked titles
        return similarityScores.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // Method to tokenizes the given text into words and counts their occurrences
    private Map<CharSequence, Integer> tokenizeText(String text) {
        Map<CharSequence, Integer> tokenizedText = new HashMap<>();
        String[] words = text.split("\\W+");  // Split by non-word characters

        for (String word : words) {
            if (!word.isEmpty()) {
                tokenizedText.put(word, tokenizedText.getOrDefault(word, 0) + 1);
            }
        }

        return tokenizedText;
    }

    // Method to save preferences to a text file for a specific user
    public static void savePreferences(List<String> recommendations) throws IOException, InterruptedException, ExecutionException {
        String preferencesFileName = loggedInUsername + "_preferences.txt";
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // Task to write preferences to a file
        Callable<Void> saveTask = () -> {
            List<String> lines = recommendations.stream()
                    .map(title -> "Recommended: " + title)
                    .collect(Collectors.toList());

            Files.write(Paths.get(preferencesFileName), lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return null;
        };

        // Submit task to executor
        Future<Void> future = executor.submit(saveTask);

        // Wait for the task to complete
        future.get();

        // Shutdown the executor
        executor.shutdown();
    }
}