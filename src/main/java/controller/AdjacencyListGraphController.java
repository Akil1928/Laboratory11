package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.*;

public class AdjacencyListGraphController implements Initializable {

    @FXML private Button randomizeBtn;
    @FXML private Button containsVertexBtn;
    @FXML private Button dfsBtn;
    @FXML private Button containsEdgeBtn;
    @FXML private Button toStringBtn;
    @FXML private Button bfsBtn;
    @FXML private TextField fromVertexField;
    @FXML private TextField toVertexField;
    @FXML private TextField weightField;
    @FXML private Button addEdgeBtn;
    @FXML private ScrollPane graphContentScrollPane;
    @FXML private TextArea graphContentArea;
    @FXML private Pane graphVisualizationPane;
    @FXML private Label statusLabel;
    @FXML private Label resultLabel;

    // Grafo representado como lista de adyacencia
    private Map<String, List<Edge>> adjacencyList;
    private Set<String> vertices;
    private Random random;

    // Clase interna para representar aristas
    private static class Edge {
        String destination;
        int weight;

        Edge(String destination, int weight) {
            this.destination = destination;
            this.weight = weight;
        }

        @Override
        public String toString() {
            return destination + " (Weight: " + weight + ")";
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        adjacencyList = new HashMap<>();
        vertices = new HashSet<>();
        random = new Random();

        // Inicializar con algunos vértices y aristas por defecto
        initializeDefaultGraph();
        updateGraphDisplay();
        updateGraphVisualization();
    }

    private void initializeDefaultGraph() {
        // Crear vértices A-J como en la imagen
        String[] defaultVertices = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        for (String vertex : defaultVertices) {
            addVertex(vertex);
        }

        // Agregar algunas aristas por defecto
        addEdgeToGraph("A", "B", 4);
        addEdgeToGraph("A", "C", 2);
        addEdgeToGraph("B", "D", 6);
        addEdgeToGraph("C", "E", 3);
        addEdgeToGraph("D", "F", 8);
        addEdgeToGraph("E", "G", 5);
        addEdgeToGraph("F", "H", 7);
        addEdgeToGraph("G", "I", 9);
        addEdgeToGraph("H", "J", 1);
    }

    private void addVertex(String vertex) {
        vertices.add(vertex);
        adjacencyList.putIfAbsent(vertex, new ArrayList<>());
    }

    private void addEdgeToGraph(String from, String to, int weight) {
        addVertex(from);
        addVertex(to);

        // Grafo no dirigido - agregar en ambas direcciones
        adjacencyList.get(from).add(new Edge(to, weight));
        adjacencyList.get(to).add(new Edge(from, weight));
    }

    @FXML
    private void randomizeGraph() {
        adjacencyList.clear();
        vertices.clear();

        // Crear 10 vértices aleatorios
        String[] possibleVertices = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O"};
        int numVertices = random.nextInt(6) + 5; // Entre 5 y 10 vértices

        for (int i = 0; i < numVertices; i++) {
            addVertex(possibleVertices[i]);
        }

        // Crear aristas aleatorias
        List<String> vertexList = new ArrayList<>(vertices);
        int numEdges = random.nextInt(10) + 5; // Entre 5 y 15 aristas

        for (int i = 0; i < numEdges; i++) {
            String from = vertexList.get(random.nextInt(vertexList.size()));
            String to = vertexList.get(random.nextInt(vertexList.size()));
            if (!from.equals(to)) {
                int weight = random.nextInt(50) + 1; // Peso entre 1 y 50
                addEdgeToGraph(from, to, weight);
            }
        }

        updateGraphDisplay();
        updateGraphVisualization();
        statusLabel.setText("Graph randomized successfully");
        resultLabel.setText("");
    }

    @FXML
    private void containsVertex() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Contains Vertex");
        dialog.setHeaderText("Check if vertex exists");
        dialog.setContentText("Enter vertex:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String vertex = result.get().toUpperCase();
            boolean contains = vertices.contains(vertex);
            resultLabel.setText("Vertex '" + vertex + "' " + (contains ? "exists" : "does not exist"));
            statusLabel.setText("Contains vertex check completed");
        }
    }

    @FXML
    private void containsEdge() {
        if (fromVertexField.getText().isEmpty() || toVertexField.getText().isEmpty()) {
            showAlert("Please enter both vertices to check edge");
            return;
        }

        String from = fromVertexField.getText().toUpperCase();
        String to = toVertexField.getText().toUpperCase();

        boolean contains = false;
        if (adjacencyList.containsKey(from)) {
            for (Edge edge : adjacencyList.get(from)) {
                if (edge.destination.equals(to)) {
                    contains = true;
                    break;
                }
            }
        }

        resultLabel.setText("Edge '" + from + "' -> '" + to + "' " + (contains ? "exists" : "does not exist"));
        statusLabel.setText("Contains edge check completed");
    }

    @FXML
    private void addEdge() {
        if (fromVertexField.getText().isEmpty() || toVertexField.getText().isEmpty() || weightField.getText().isEmpty()) {
            showAlert("Please fill all fields to add edge");
            return;
        }

        try {
            String from = fromVertexField.getText().toUpperCase();
            String to = toVertexField.getText().toUpperCase();
            int weight = Integer.parseInt(weightField.getText());

            if (weight < 1 || weight > 50) {
                showAlert("Weight must be between 1 and 50");
                return;
            }

            addEdgeToGraph(from, to, weight);
            updateGraphDisplay();
            updateGraphVisualization();

            resultLabel.setText("Edge added: " + from + " -> " + to + " (Weight: " + weight + ")");
            statusLabel.setText("Edge added successfully");

            // Limpiar campos
            fromVertexField.clear();
            toVertexField.clear();
            weightField.clear();

        } catch (NumberFormatException e) {
            showAlert("Please enter a valid number for weight");
        }
    }

    @FXML
    private void performDFS() {
        if (vertices.isEmpty()) {
            showAlert("Graph is empty");
            return;
        }

        String startVertex = vertices.iterator().next();
        List<String> dfsResult = dfs(startVertex);

        resultLabel.setText("DFS Tour: " + String.join(" -> ", dfsResult));
        statusLabel.setText("DFS traversal completed");
    }

    @FXML
    private void performBFS() {
        if (vertices.isEmpty()) {
            showAlert("Graph is empty");
            return;
        }

        String startVertex = vertices.iterator().next();
        List<String> bfsResult = bfs(startVertex);

        resultLabel.setText("BFS Tour: " + String.join(" -> ", bfsResult));
        statusLabel.setText("BFS traversal completed");
    }

    @FXML
    private void showToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Adjacency List Representation:\n");

        for (String vertex : vertices) {
            sb.append(vertex).append(": ");
            List<Edge> edges = adjacencyList.get(vertex);
            if (edges.isEmpty()) {
                sb.append("[]");
            } else {
                sb.append("[");
                for (int i = 0; i < edges.size(); i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(edges.get(i));
                }
                sb.append("]");
            }
            sb.append("\n");
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Graph String Representation");
        alert.setHeaderText("Adjacency List");
        alert.setContentText(sb.toString());
        alert.showAndWait();

        statusLabel.setText("String representation displayed");
    }

    private List<String> dfs(String startVertex) {
        List<String> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        dfsHelper(startVertex, visited, result);
        return result;
    }

    private void dfsHelper(String vertex, Set<String> visited, List<String> result) {
        visited.add(vertex);
        result.add(vertex);

        for (Edge edge : adjacencyList.get(vertex)) {
            if (!visited.contains(edge.destination)) {
                dfsHelper(edge.destination, visited, result);
            }
        }
    }

    private List<String> bfs(String startVertex) {
        List<String> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();

        queue.offer(startVertex);
        visited.add(startVertex);

        while (!queue.isEmpty()) {
            String vertex = queue.poll();
            result.add(vertex);

            for (Edge edge : adjacencyList.get(vertex)) {
                if (!visited.contains(edge.destination)) {
                    visited.add(edge.destination);
                    queue.offer(edge.destination);
                }
            }
        }

        return result;
    }

    private void updateGraphDisplay() {
        StringBuilder sb = new StringBuilder();

        for (String vertex : vertices) {
            sb.append("Vertex in the position ").append(vertex).append("\n");
            sb.append("......EDGES AND WEIGHTS:\n");
            sb.append("Singly Linked List Content\n\n");

            List<Edge> edges = adjacencyList.get(vertex);
            for (Edge edge : edges) {
                sb.append("Edge=").append(edge.destination)
                        .append(", Weight=").append(edge.weight).append("\n");
            }
            sb.append("\n");
        }

        graphContentArea.setText(sb.toString());
    }

    private void updateGraphVisualization() {
        graphVisualizationPane.getChildren().clear();

        if (vertices.isEmpty()) return;

        // Posiciones circulares para los vértices
        Map<String, double[]> positions = new HashMap<>();
        List<String> vertexList = new ArrayList<>(vertices);
        double centerX = graphVisualizationPane.getPrefWidth() / 2;
        double centerY = graphVisualizationPane.getPrefHeight() / 2;
        double radius = Math.min(centerX, centerY) - 50;

        for (int i = 0; i < vertexList.size(); i++) {
            double angle = 2 * Math.PI * i / vertexList.size();
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            positions.put(vertexList.get(i), new double[]{x, y});
        }

        // Dibujar aristas
        Set<String> drawnEdges = new HashSet<>();
        for (String vertex : vertices) {
            double[] pos1 = positions.get(vertex);
            for (Edge edge : adjacencyList.get(vertex)) {
                String edgeKey = vertex.compareTo(edge.destination) < 0 ?
                        vertex + "-" + edge.destination : edge.destination + "-" + vertex;

                if (!drawnEdges.contains(edgeKey)) {
                    double[] pos2 = positions.get(edge.destination);

                    Line line = new Line(pos1[0], pos1[1], pos2[0], pos2[1]);
                    line.setStroke(Color.BLACK);
                    line.setStrokeWidth(2);
                    graphVisualizationPane.getChildren().add(line);

                    // Etiqueta de peso
                    double midX = (pos1[0] + pos2[0]) / 2;
                    double midY = (pos1[1] + pos2[1]) / 2;
                    Text weightLabel = new Text(midX, midY, String.valueOf(edge.weight));
                    weightLabel.setFill(Color.RED);
                    weightLabel.setFont(Font.font(12));
                    graphVisualizationPane.getChildren().add(weightLabel);

                    drawnEdges.add(edgeKey);
                }
            }
        }

        // Dibujar vértices
        for (String vertex : vertices) {
            double[] pos = positions.get(vertex);

            Circle circle = new Circle(pos[0], pos[1], 20);
            circle.setFill(Color.LIGHTBLUE);
            circle.setStroke(Color.DARKBLUE);
            circle.setStrokeWidth(2);
            graphVisualizationPane.getChildren().add(circle);

            Text label = new Text(pos[0] - 5, pos[1] + 5, vertex);
            label.setFont(Font.font(14));
            label.setFill(Color.BLACK);
            graphVisualizationPane.getChildren().add(label);
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
