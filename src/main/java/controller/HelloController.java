package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import ucr.lab.laboratory11.HelloApplication;

import java.io.IOException;

public class HelloController {
    @FXML
    private void load(String form) {
        try {

            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/"+form));


            if (fxmlLoader.getLocation() == null) {
                System.err.println("No se puede encontrar el archivo FXML: " + form);
                return;
            }

            this.bp.setCenter(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace(); // Muestra el stack trace completo
            throw new RuntimeException("Error al cargar el FXML: " + form, e);
        }
    }

    @FXML
    private BorderPane bp;

    @FXML
    private AnchorPane contentPane;

    @FXML
    private Text txtMessage;

    @FXML
    void Exit(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void Home(ActionEvent event) {
        txtMessage.setText("Laboratory No. 11");
        contentPane.getChildren().clear();
        contentPane.getChildren().add(txtMessage);
    }

    @FXML
    void bubbleSortOnAction(ActionEvent event) {

        load("bubbleSorting.fxml");
    }


    @FXML
    void impBubbleSortOnAction(ActionEvent event) {
        load("improvedBubbleSorting.fxml");
    }


    @FXML
    void selectionSortOnAction(ActionEvent event) {

        load("selectionSorting.fxml");
    }

}
