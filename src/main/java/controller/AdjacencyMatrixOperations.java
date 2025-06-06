package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

import java.net.URL;
import java.util.*;

public class AdjacencyMatrixOperations implements Initializable {

    //Componentes FXML
    @FXML
    private Button randomizeButton, addVertexButton, addEdgesButton, removeVertexButton, removeEdgesButton, clearButton;
    @FXML
    private TextArea contentTextArea;
    @FXML
    private Pane graphPane;
    @FXML
    private Label edgeInfoLabel;

    // Estructuras de Datos del Grafo
    private static final int MAX_VERTICES = 100; // Vértices son números del 0 al 99
    private final int[][] adjacencyMatrix = new int[MAX_VERTICES][MAX_VERTICES];
    private final List<Integer> vertices = new ArrayList<>();
    private final Random random = new Random();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        edgeInfoLabel.setText("Grafo vacío. Agrega vértices para comenzar.");
        updateUI();
    }

    // Manejadores de Eventos de Botones

    @FXML
    private void handleRandomize() {
        handleClear();
        int numVertices = 5 + random.nextInt(8); // Generar entre 5 y 12 vértices
        for (int i = 0; i < numVertices; i++) {
            addRandomVertex();
        }
        int numEdges = numVertices + random.nextInt(numVertices); // Generar algunas aristas
        for (int i = 0; i < numEdges; i++) {
            addRandomEdge();
        }
        edgeInfoLabel.setText("Se ha generado un nuevo grafo aleatorio.");
        updateUI();
    }

    @FXML
    private void handleAddVertex() {
        if (vertices.size() >= MAX_VERTICES) {
            edgeInfoLabel.setText("No se pueden agregar más vértices (máx. 100).");
            return;
        }
        Integer newVertex = addRandomVertex();
        if (newVertex != null) {
            edgeInfoLabel.setText("Vértice " + newVertex + " fue agregado.");
            updateUI();
        }
    }

    @FXML
    private void handleAddEdgesAndWeights() {
        Edge newEdge = addRandomEdge();
        if (newEdge != null) {
            edgeInfoLabel.setText("Arista agregada: " + newEdge.u + " - " + newEdge.v + " (Peso: " + newEdge.weight + ")");
        } else {
            edgeInfoLabel.setText("No se pudo agregar arista. Se necesitan al menos 2 vértices.");
        }
        updateUI();
    }

    @FXML
    private void handleRemoveVertex() {
        if (vertices.isEmpty()) {
            edgeInfoLabel.setText("No se puede eliminar. El grafo está vacío.");
            return;
        }
        int vertexToRemove = vertices.get(random.nextInt(vertices.size()));
        vertices.remove(Integer.valueOf(vertexToRemove));

        // Eliminar todas las aristas conectadas al vértice
        for (int i = 0; i < MAX_VERTICES; i++) {
            adjacencyMatrix[vertexToRemove][i] = 0;
            adjacencyMatrix[i][vertexToRemove] = 0;
        }
        edgeInfoLabel.setText("Vértice " + vertexToRemove + " y sus aristas fueron eliminados.");
        updateUI();
    }

    @FXML
    private void handleRemoveEdgesAndWeights() {
        List<Edge> currentEdges = findEdges();
        if (currentEdges.isEmpty()) {
            edgeInfoLabel.setText("No hay aristas para eliminar.");
            return;
        }
        Edge edgeToRemove = currentEdges.get(random.nextInt(currentEdges.size()));
        adjacencyMatrix[edgeToRemove.u][edgeToRemove.v] = 0;
        adjacencyMatrix[edgeToRemove.v][edgeToRemove.u] = 0;

        edgeInfoLabel.setText("Arista " + edgeToRemove.u + " - " + edgeToRemove.v + " fue eliminada.");
        updateUI();
    }

    @FXML
    private void handleClear() {
        vertices.clear();
        for (int[] row : adjacencyMatrix) {
            Arrays.fill(row, 0);
        }
        edgeInfoLabel.setText("Grafo limpiado.");
        updateUI();
    }


    //Lógica Principal del Grafo

    private Integer addRandomVertex() {
        if (vertices.size() >= MAX_VERTICES) return null;
        int newVertex;
        do {
            newVertex = random.nextInt(MAX_VERTICES); // Valores 0-99
        } while (vertices.contains(newVertex));
        vertices.add(newVertex);
        Collections.sort(vertices);
        return newVertex;
    }

    private Edge addRandomEdge() {
        if (vertices.size() < 2) return null;

        int u, v;
        int attempts = 0;
        // Intenta encontrar dos vértices que no estén ya conectados
        do {
            u = vertices.get(random.nextInt(vertices.size()));
            v = vertices.get(random.nextInt(vertices.size()));
            attempts++;
            // Evita un bucle infinito si el grafo está completo
            if (attempts > vertices.size() * vertices.size()) {
                return null;
            }
        } while (u == v || adjacencyMatrix[u][v] != 0);

        int weight = 1 + random.nextInt(50); // Peso 1-50
        adjacencyMatrix[u][v] = weight;
        adjacencyMatrix[v][u] = weight; // Para un grafo no dirigido

        return new Edge(u, v, weight);
    }

    private List<Edge> findEdges() {
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = i + 1; j < vertices.size(); j++) {
                int u = vertices.get(i);
                int v = vertices.get(j);
                if (adjacencyMatrix[u][v] > 0) {
                    edges.add(new Edge(u, v, adjacencyMatrix[u][v]));
                }
            }
        }
        return edges;
    }

    private void updateUI() {
        updateTextArea();
        drawGraph();
    }

    private void updateTextArea() {
        StringBuilder sb = new StringBuilder();
        sb.append("ADJACENCY MATRIX GRAPH CONTENT...\n");
        for (int i = 0; i < vertices.size(); i++) {
            sb.append("The vertex in the position: ").append(i).append(" is: ").append(vertices.get(i)).append("\n");
        }
        sb.append("\n----------------------------------\n\n");

        List<Edge> edges = findEdges();
        if (edges.isEmpty()) {
            sb.append("There are no edges between the vertexes.");
        } else {
            for (Edge edge : edges) {
                sb.append("There is edge between the vertexes: ").append(edge.u)
                        .append(".....").append(edge.v)
                        .append(". Weight: ").append(edge.weight).append("\n");
            }
        }
        contentTextArea.setText(sb.toString());
    }

    private void drawGraph() {
        graphPane.getChildren().clear();
        if (vertices.isEmpty()) return;

        double width = graphPane.getWidth();
        double height = graphPane.getHeight();
        double centerX = width / 2;
        double centerY = height / 2;
        double radius = Math.min(width, height) / 2 - 30;

        Map<Integer, Point> vertexPositions = new HashMap<>();

        // Calcular posiciones en un círculo
        for (int i = 0; i < vertices.size(); i++) {
            double angle = 2 * Math.PI * i / vertices.size();
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            vertexPositions.put(vertices.get(i), new Point(x, y));
        }

        // Dibujar aristas
        List<Edge> edges = findEdges();
        for (Edge edge : edges) {
            Point p1 = vertexPositions.get(edge.u);
            Point p2 = vertexPositions.get(edge.v);
            Line line = new Line(p1.x, p1.y, p2.x, p2.y);
            line.setStroke(Color.BLACK);

            // Resaltar la última arista modificada
            if(edgeInfoLabel.getText().contains(edge.u + " - " + edge.v)){
                line.setStroke(Color.RED);
                line.setStrokeWidth(2.0);
            }
            graphPane.getChildren().add(line);
        }

        // Dibujar vértices (encima de las aristas)
        for (Map.Entry<Integer, Point> entry : vertexPositions.entrySet()) {
            int vertexValue = entry.getKey();
            Point p = entry.getValue();

            Circle circle = new Circle(p.x, p.y, 20, Color.LIGHTSKYBLUE);
            circle.setStroke(Color.BLACK);

            Text text = new Text(String.valueOf(vertexValue));
            text.setBoundsType(TextBoundsType.VISUAL);

            StackPane stack = new StackPane(circle, text);
            stack.setLayoutX(p.x - 20);
            stack.setLayoutY(p.y - 20);
            stack.setAlignment(Pos.CENTER);

            graphPane.getChildren().add(stack);
        }
    }

    // --- Clases Auxiliares ---
    private static class Point {
        double x, y;
        Point(double x, double y) { this.x = x; this.y = y; }
    }

    private static class Edge {
        int u, v, weight;
        Edge(int u, int v, int weight) { this.u = u; this.v = v; this.weight = weight; }
    }
}