package ucr.lab;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/hello-view.fxml")));


        Scene scene = new Scene(root, 1006, 694);


        String css = Objects.requireNonNull(getClass().getResource("/stylesheet.css")).toExternalForm();
        scene.getStylesheets().add(css);

        stage.setTitle("Laboratory No. 11");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}