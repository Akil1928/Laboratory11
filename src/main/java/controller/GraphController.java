package controller;

import domain.*;
import domain.list.ListException;
import domain.queue.QueueException;
import domain.stack.StackException;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.*;

public class GraphController {

    @FXML
    private Canvas graphCanvas;

    @FXML
    private TextArea vertexListArea;

    @FXML
    private TextArea edgeListArea;

    @FXML
    private TextArea tourResultArea;

    @FXML
    private GridPane matrixGrid;

    @FXML
    private Label statusLabel;

    // Grafo principal
    private AdjacencyMatrixGraph graph;
    private List<Character> vertices;
    private Map<Character, Point> vertexPositions;
    private Random random;

    // Clase auxiliar para posiciones
    private static class Point {
        double x, y;
        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    @FXML
    public void initialize() {
        // Inicializar estructuras de datos
        graph = new AdjacencyMatrixGraph(20);
        vertices = new ArrayList<>();
        vertexPositions = new HashMap<>();
        random = new Random();

        // Crear grafo inicial con algunos vértices
        initializeDefaultGraph();
        updateDisplay();
    }

    private void initializeDefaultGraph() {
        try {
            // Agregar vértices A, B, C, D, E
            char[] defaultVertices = {'A', 'B', 'C', 'D', 'E'};
            for (char vertex : defaultVertices) {
                graph.addVertex(vertex);
                vertices.add(vertex);
            }

            // Agregar algunas aristas con pesos
            graph.addEdgeWeight('A', 'B', 5);
            graph.addEdgeWeight('A', 'C', 3);
            graph.addEdgeWeight('B', 'D', 7);
            graph.addEdgeWeight('C', 'E', 4);
            graph.addEdgeWeight('D', 'E', 2);

            // Calcular posiciones para visualización
            calculateVertexPositions();

            statusLabel.setText("Grafo inicializado con " + vertices.size() + " vértices");

        } catch (GraphException | ListException e) {
            showError("Error inicializando grafo: " + e.getMessage());
        }
    }

    @FXML
    private void handleRandomize() {
        try {
            // Limpiar grafo actual
            graph.clear();
            vertices.clear();
            vertexPositions.clear();

            // Generar vértices aleatorios (5-8 vértices)
            int numVertices = random.nextInt(4) + 5;
            Set<Character> usedVertices = new HashSet<>();

            for (int i = 0; i < numVertices; i++) {
                char vertex;
                do {
                    vertex = (char) ('A' + random.nextInt(10)); // A-J
                } while (usedVertices.contains(vertex));

                usedVertices.add(vertex);
                graph.addVertex(vertex);
                vertices.add(vertex);
            }

            // Generar aristas aleatorias
            int numEdges = random.nextInt(vertices.size() * 2) + vertices.size();
            for (int i = 0; i < numEdges; i++) {
                char v1 = vertices.get(random.nextInt(vertices.size()));
                char v2 = vertices.get(random.nextInt(vertices.size()));

                if (v1 != v2 && !graph.containsEdge(v1, v2)) {
                    int weight = random.nextInt(20) + 1;
                    graph.addEdgeWeight(v1, v2, weight);
                }
            }

            calculateVertexPositions();
            updateDisplay();
            statusLabel.setText("Grafo aleatorizado: " + vertices.size() + " vértices");

        } catch (GraphException | ListException e) {
            showError("Error randomizando grafo: " + e.getMessage());
        }
    }

    @FXML
    private void handleContainsVertex() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Buscar Vértice");
        dialog.setHeaderText("Verificar si existe un vértice");
        dialog.setContentText("Ingrese el vértice a buscar:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().isEmpty()) {
            try {
                char vertex = result.get().toUpperCase().charAt(0);
                boolean exists = graph.containsVertex(vertex);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Resultado");
                alert.setHeaderText("Búsqueda de Vértice");
                alert.setContentText("El vértice '" + vertex + "' " +
                        (exists ? "SÍ existe" : "NO existe") + " en el grafo");
                alert.showAndWait();

                statusLabel.setText("Vértice '" + vertex + "' " + (exists ? "encontrado" : "no encontrado"));

            } catch (GraphException | ListException e) {
                showError("Error buscando vértice: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleContainsEdge() {
        TextInputDialog dialog1 = new TextInputDialog();
        dialog1.setTitle("Buscar Arista");
        dialog1.setHeaderText("Verificar si existe una arista");
        dialog1.setContentText("Ingrese el primer vértice:");

        Optional<String> result1 = dialog1.showAndWait();
        if (result1.isPresent() && !result1.get().isEmpty()) {
            TextInputDialog dialog2 = new TextInputDialog();
            dialog2.setTitle("Buscar Arista");
            dialog2.setHeaderText("Verificar si existe una arista");
            dialog2.setContentText("Ingrese el segundo vértice:");

            Optional<String> result2 = dialog2.showAndWait();
            if (result2.isPresent() && !result2.get().isEmpty()) {
                try {
                    char v1 = result1.get().toUpperCase().charAt(0);
                    char v2 = result2.get().toUpperCase().charAt(0);
                    boolean exists = graph.containsEdge(v1, v2);

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Resultado");
                    alert.setHeaderText("Búsqueda de Arista");
                    alert.setContentText("La arista entre '" + v1 + "' y '" + v2 + "' " +
                            (exists ? "SÍ existe" : "NO existe"));
                    alert.showAndWait();

                    statusLabel.setText("Arista " + v1 + "-" + v2 + " " + (exists ? "encontrada" : "no encontrada"));

                } catch (GraphException | ListException e) {
                    showError("Error buscando arista: " + e.getMessage());
                }
            }
        }
    }

    @FXML
    private void handleToString() {
        String graphString = graph.toString();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Contenido del Grafo");
        alert.setHeaderText("Estructura completa del grafo");

        TextArea textArea = new TextArea(graphString);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        alert.getDialogPane().setExpandableContent(textArea);
        alert.getDialogPane().setExpanded(true);
        alert.showAndWait();

        statusLabel.setText("Mostrando estructura del grafo");
    }

    @FXML
    private void handleDFSTour() {
        try {
            if (graph.isEmpty()) {
                showError("El grafo está vacío");
                return;
            }

            String result = graph.dfs();
            tourResultArea.setText("DFS: " + result);
            statusLabel.setText("Recorrido DFS completado");

        } catch (GraphException | StackException | ListException e) {
            showError("Error en recorrido DFS: " + e.getMessage());
        }
    }

    @FXML
    private void handleBFSTour() {
        try {
            if (graph.isEmpty()) {
                showError("El grafo está vacío");
                return;
            }

            String result = graph.bfs();
            tourResultArea.setText("BFS: " + result);
            statusLabel.setText("Recorrido BFS completado");

        } catch (GraphException | QueueException | ListException e) {
            showError("Error en recorrido BFS: " + e.getMessage());
        }
    }

    private void calculateVertexPositions() {
        if (vertices.isEmpty()) return;

        double centerX = graphCanvas.getWidth() / 2;
        double centerY = graphCanvas.getHeight() / 2;
        double radius = Math.min(centerX, centerY) - 50;

        for (int i = 0; i < vertices.size(); i++) {
            double angle = 2 * Math.PI * i / vertices.size();
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            vertexPositions.put(vertices.get(i), new Point(x, y));
        }
    }

    private void updateDisplay() {
        drawGraph();
        updateVertexList();
        updateEdgeList();
        updateAdjacencyMatrix();
    }

    private void drawGraph() {
        GraphicsContext gc = graphCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, graphCanvas.getWidth(), graphCanvas.getHeight());

        // Dibujar aristas
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(2);

        try {
            for (int i = 0; i < vertices.size(); i++) {
                for (int j = i + 1; j < vertices.size(); j++) {
                    char v1 = vertices.get(i);
                    char v2 = vertices.get(j);

                    if (graph.containsEdge(v1, v2)) {
                        Point p1 = vertexPositions.get(v1);
                        Point p2 = vertexPositions.get(v2);

                        gc.strokeLine(p1.x, p1.y, p2.x, p2.y);

                        // Dibujar peso en el medio de la arista
                        double midX = (p1.x + p2.x) / 2;
                        double midY = (p1.y + p2.y) / 2;

                        // Obtener peso (simulado por ahora)
                        gc.setFill(Color.RED);
                        gc.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                        gc.fillText("w", midX - 5, midY - 5);
                    }
                }
            }
        } catch (GraphException | ListException e) {
            showError("Error dibujando aristas: " + e.getMessage());
        }

        // Dibujar vértices
        gc.setFill(Color.LIGHTBLUE);
        gc.setStroke(Color.DARKBLUE);
        gc.setLineWidth(2);

        for (char vertex : vertices) {
            Point pos = vertexPositions.get(vertex);
            if (pos != null) {
                // Círculo del vértice
                gc.fillOval(pos.x - 20, pos.y - 20, 40, 40);
                gc.strokeOval(pos.x - 20, pos.y - 20, 40, 40);

                // Etiqueta del vértice
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
                gc.fillText(String.valueOf(vertex), pos.x - 8, pos.y + 5);
            }
        }
    }

    private void updateVertexList() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vértices del grafo:\n");
        for (int i = 0; i < vertices.size(); i++) {
            sb.append(vertices.get(i));
            if (i < vertices.size() - 1) {
                sb.append(", ");
            }
        }
        vertexListArea.setText(sb.toString());
    }

    private void updateEdgeList() {
        StringBuilder sb = new StringBuilder();
        sb.append("Aristas del grafo:\n");

        try {
            int edgeCount = 0;
            for (int i = 0; i < vertices.size(); i++) {
                for (int j = i + 1; j < vertices.size(); j++) {
                    char v1 = vertices.get(i);
                    char v2 = vertices.get(j);

                    if (graph.containsEdge(v1, v2)) {
                        sb.append(v1).append(" ↔ ").append(v2).append("\n");
                        edgeCount++;
                    }
                }
            }

            if (edgeCount == 0) {
                sb.append("No hay aristas");
            }

        } catch (GraphException | ListException e) {
            sb.append("Error obteniendo aristas: ").append(e.getMessage());
        }

        edgeListArea.setText(sb.toString());
    }

    private void updateAdjacencyMatrix() {
        matrixGrid.getChildren().clear();

        if (vertices.isEmpty()) {
            Label emptyLabel = new Label("Matriz vacía");
            matrixGrid.add(emptyLabel, 0, 0);
            return;
        }

        int size = vertices.size();

        // Agregar encabezados de columnas
        for (int j = 0; j < size; j++) {
            Label headerLabel = new Label(String.valueOf(vertices.get(j)));
            headerLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center; -fx-pref-width: 30; -fx-pref-height: 30;");
            matrixGrid.add(headerLabel, j + 1, 0);
        }

        // Agregar filas con encabezados y valores
        for (int i = 0; i < size; i++) {
            // Encabezado de fila
            Label rowHeaderLabel = new Label(String.valueOf(vertices.get(i)));
            rowHeaderLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center; -fx-pref-width: 30; -fx-pref-height: 30;");
            matrixGrid.add(rowHeaderLabel, 0, i + 1);

            // Valores de la matriz
            for (int j = 0; j < size; j++) {
                try {
                    char v1 = vertices.get(i);
                    char v2 = vertices.get(j);

                    String value = graph.containsEdge(v1, v2) ? "1" : "0";

                    Label cellLabel = new Label(value);
                    cellLabel.setStyle("-fx-alignment: center; -fx-pref-width: 30; -fx-pref-height: 30; " +
                            "-fx-border-color: gray; -fx-border-width: 0.5;");

                    if (value.equals("1")) {
                        cellLabel.setStyle(cellLabel.getStyle() + " -fx-background-color: lightgreen;");
                    }

                    matrixGrid.add(cellLabel, j + 1, i + 1);

                } catch (GraphException | ListException e) {
                    Label errorLabel = new Label("E");
                    errorLabel.setStyle("-fx-alignment: center; -fx-pref-width: 30; -fx-pref-height: 30; " +
                            "-fx-border-color: gray; -fx-border-width: 0.5; -fx-background-color: lightcoral;");
                    matrixGrid.add(errorLabel, j + 1, i + 1);
                }
            }
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Ha ocurrido un error");
        alert.setContentText(message);
        alert.showAndWait();

        statusLabel.setText("Error: " + message);
    }
}