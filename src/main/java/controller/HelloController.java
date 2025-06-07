package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import ucr.lab.HelloApplication;

import java.io.IOException;

public class HelloController {

    @FXML
    private BorderPane bp;

    @FXML
    private AnchorPane contentPane;

    @FXML
    private Text txtMessage;

    /**
     * Carga un archivo FXML en el centro del BorderPane
     * @param form Nombre del archivo FXML a cargar
     */
    private void load(String form) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/" + form));

            if (fxmlLoader.getLocation() == null) {
                System.err.println("No se puede encontrar el archivo FXML: " + form);
                return;
            }

            this.bp.setCenter(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al cargar el FXML: " + form, e);
        }
    }

    /**
     * Cierra la aplicación
     */
    @FXML
    void Exit(ActionEvent event) {
        System.exit(0);
    }

    /**
     * Muestra la pantalla principal
     */
    @FXML
    void Home(ActionEvent event) {
        txtMessage.setText("Laboratory No. 11");
        contentPane.getChildren().clear();
        contentPane.getChildren().add(txtMessage);
    }

    /**
     * Carga la vista de Matrix Graph
     */
    @FXML
    public void graphicOnAction(ActionEvent actionEvent) {
        load("AdjencyListGraph.fxml");
    }

    /**
     * Carga la vista de List Operations
     */
    @FXML
    public void operationsOnAction(ActionEvent actionEvent) {
        load("AdjacencyListOperations.fxml");
    }

    /**
     * Carga la vista de Matrix Operations
     */
    @FXML
    public void tourOnAction(ActionEvent actionEvent) {
        load("SinglyListListGraph.fxml");
    }

    /**
     * Carga la vista de List Graph
     */
    @FXML
    public void linkedGraphOnAction(ActionEvent event) {
        load("LinkedGraphOperations.fxml");
    }

    /**
     * Carga la vista de Linked Graph
     */
    @FXML
    public void matrixGraphOnAction(ActionEvent event) {
        load("graph.fxml");
    }

    /**
     * Carga la vista de Linked Operations
     */
    @FXML
    public void linkedOperationsOnAction(ActionEvent event) {
        load("AdjacencyMatrixOperations.fxml");
    }

    // Métodos de compatibilidad
    @FXML
    void linkedOperationOnAction(ActionEvent event) {
        linkedOperationsOnAction(event);
    }

    @FXML
    void listGraphOnAction(ActionEvent event) {
        graphicOnAction(event);
    }

    @FXML
    void listOperationsOnAction(ActionEvent event) {
        operationsOnAction(event);
    }

    @FXML
    void matrixOperationsOnAction(ActionEvent event) {
        tourOnAction(event);
    }
}