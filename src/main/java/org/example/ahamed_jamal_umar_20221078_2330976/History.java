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
        // Defining the file path for the user's history
        String historyFilePath = loggedInUsername + "_history.csv";

        // Clearing any existing items in the ListView before loading new data
        historyListView.getItems().clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(historyFilePath))) {
            // Reading the first line to check if the file contains a valid header
            String line = reader.readLine();

            // If the header is invalid or the file is empty an exception is thrown
            if (line == null || !line.equals("Title, Rating")) {
                throw new IOException("Invalid or empty history file.");
            }

            // Looping through the remaining lines of the file and reading the history records
            while ((line = reader.readLine()) != null) {
                // Split the line by comma to extract title and rating
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String title = parts[0];  // Extracting the title
                    String rating = parts[1]; // Extracting the rating

                    // Formatting the record and adding it to the ListView
                    String record = "Title: " + title + ", Rating: " + rating;
                    historyListView.getItems().add(record);
                }
            }
        } catch (IOException e) {
            // Displaying a message if file is not found or invalid format
            historyListView.getItems().add("No history available.");
        }
    }
}
