package org.example.ahamed_jamal_umar_20221078_2330976;

import javafx.scene.control.ListView;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class History {
    private String title;
    private String rating;
    private String loggedInUsername;

    // Constructor to initialize history attributes
    public History(String title, String rating) {
        this.title = title;
        this.rating = rating;
    }

    // Constructor to initialize the History object with the username of the logged-in user
    public History(String loggedInUsername) {
        this.loggedInUsername = loggedInUsername;
    }

    // Method to load the specific user's history
    public void loadHistory(ListView<String> historyListView) {
        String historyFilePath = loggedInUsername + "_history.csv";
        historyListView.getItems().clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(historyFilePath))) {
            String line = reader.readLine();
            if (line == null || !line.equals("Title, Rating")) {
                throw new IOException("Invalid or empty history file.");
            }

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String title = parts[0];
                    String rating = parts[1];
                    String record = "Title: " + title + ", Rating: " + rating;
                    historyListView.getItems().add(record);
                }
            }
        } catch (IOException e) {
            historyListView.getItems().add("No history available.");
        }
    }
}
