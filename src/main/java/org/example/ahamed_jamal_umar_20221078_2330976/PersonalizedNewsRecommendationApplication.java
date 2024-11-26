package org.example.ahamed_jamal_umar_20221078_2330976;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PersonalizedNewsRecommendationApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(PersonalizedNewsRecommendationApplication.class.getResource("WelcomePage.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 486, 284);
        stage.setTitle("Personalized News Recommendation System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}