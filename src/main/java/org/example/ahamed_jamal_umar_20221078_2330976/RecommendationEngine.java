package org.example.ahamed_jamal_umar_20221078_2330976;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import org.apache.commons.text.similarity.CosineSimilarity;

public class RecommendationEngine {

    private String loggedInUsername;

    public RecommendationEngine(String username) {
        this.loggedInUsername = username;
    }

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

    private List<Article> parseArticles(String filePath) throws Exception {
        List<Article> articles = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(filePath));

        String title = "";
        StringBuilder description = new StringBuilder();

        for (String line : lines) {
            if (line.startsWith("Title:")) {
                if (!title.isEmpty()) {
                    articles.add(new Article(title, description.toString().trim()));
                }
                title = line.substring(6).trim();
                description.setLength(0);
            } else if (line.startsWith("Description:")) {
                description.append(line.substring(12).trim()).append(" ");
            }
        }

        // Add the last article
        if (!title.isEmpty()) {
            articles.add(new Article(title, description.toString().trim()));
        }

        return articles;
    }

    private Map<String, String> parseUserHistory(String filePath) throws Exception {
        Map<String, String> userRatings = new HashMap<>();
        if (!Files.exists(Paths.get(filePath))) {
            return userRatings; // Return empty map if file does not exist
        }

        List<String> lines = Files.readAllLines(Paths.get(filePath));
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length == 2) {
                userRatings.put(parts[0].trim(), parts[1].trim().toLowerCase());
            }
        }

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

    private void savePreferences(List<String> recommendations) throws Exception {
        String preferencesFileName = loggedInUsername + "_preferences.txt";
        List<String> lines = recommendations.stream()
                .map(title -> "Recommended: " + title)
                .collect(Collectors.toList());

        Files.write(Paths.get(preferencesFileName), lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}