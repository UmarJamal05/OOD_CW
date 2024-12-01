module org.example.ahamed_jamal_umar_20221078_2330976 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.apache.commons.text;

    // Open packages for JavaFX reflection
    opens org.example.ahamed_jamal_umar_20221078_2330976 to javafx.fxml;

    // Export your main package
    exports org.example.ahamed_jamal_umar_20221078_2330976;
}
