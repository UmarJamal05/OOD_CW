package org.example.ahamed_jamal_umar_20221078_2330976;

import javafx.scene.control.ListView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class History {
    private String title;
    private String rating;
    private String loggedInUsername;

    public History(String title, String rating) {
        this.title = title;
        this.rating = rating;
    }
    public History(String loggedInUsername){
        this.loggedInUsername = loggedInUsername;
    }
    public void loadHistory(ListView<String> historyListView) {
        String historyFilePath = loggedInUsername + "_history.csv";

        // Clear existing items in the ListView
        historyListView.getItems().clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(historyFilePath))) {
            String line = reader.readLine(); // Read the header line
            if (line == null || !line.equals("Title, Rating")) {
                throw new IOException("Invalid or empty history file.");
            }

            // Load history and add to ListView
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String title = parts[0];
                    String rating = parts[1];

                    // Add the formatted history record as a string to the ListView
                    String record = "Title: " + title + ",      Rating: " + rating;
                    historyListView.getItems().add(record);
                }
            }
        } catch (IOException e) {
            // Handle invalid or missing history file
            historyListView.getItems().add("No history available.");
        }
    }
}
