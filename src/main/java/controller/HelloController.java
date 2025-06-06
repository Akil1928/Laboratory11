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
     * Carga la vista de gráficos para Adjacency List Graph
     */
    @FXML
    public void graphicOnAction(ActionEvent actionEvent) {
        load("AdjencyListGraph.fxml");
    }

    /**
     * Carga la vista de operaciones de grafos con listas de adyacencia
     */
    @FXML
    public void operationsOnAction(ActionEvent actionEvent) {
        load("AdjacencyListOperations.fxml");
    }

    /**
     * Carga la vista de recorridos de grafos
     */
    @FXML
    public void tourOnAction(ActionEvent actionEvent) {
        load("SinglyListListGraph.fxml");
    }

    /**
     * Carga la vista de grafo con lista enlazada
     */
    @FXML
    public void linkedGraphOnAction(ActionEvent event) {
        load("LinkedGraphOperations.fxml");
    }

    /**
     * Carga la vista de operaciones de grafo con matriz de adyacencia
     */
    @FXML
    public void matrixOperationsOnAction(ActionEvent event) {
        load("AdjacencyMatrixOperations.fxml");
    }

    /**
     * Carga la vista de grafo con matriz de adyacencia
     */
    @FXML
    public void matrixGraphOnAction(ActionEvent event) {
        load("graph.fxml");
    }

    // Estos métodos se mantienen para compatibilidad pero redirigen a las nuevas vistas

    @FXML
    void linkedOperationOnAction(ActionEvent event) {
        linkedGraphOnAction(event);
    }

    @FXML
    void listGraphOnAction(ActionEvent event) {
        graphicOnAction(event);
    }

    @FXML
    void listOperationsOnAction(ActionEvent event) {
        operationsOnAction(event);
    }
}