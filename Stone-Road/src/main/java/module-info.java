module game {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires java.desktop;
    requires javafx.swing;

    opens game to javafx.fxml;
    exports game;
}