package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class SinglyLinkedListGraphController {

    @FXML
    private Button randomizeButton;
    @FXML
    private Button containsVertexButton;
    @FXML
    private Button dfsTourButton;
    @FXML
    private Button containsEdgeButton;
    @FXML
    private Button toStringButton;
    @FXML
    private Button bfsTourButton;
    @FXML
    private Label edgeBetweenVerticesLabel;
    @FXML
    private TextArea graphContentTextArea;
    @FXML
    private Group graphDisplayGroup; // Container for drawing the graph

    // Placeholder for your actual graph implementation
    private SinglyLinkedListGraph graph;

    // Map to store Circle objects associated with vertex names for drawing
    private Map<String, Circle> vertexCircles = new HashMap<>();
    private Map<String, Text> vertexLabels = new HashMap<>();


    // You'll need to define your SinglyLinkedListGraph class and its methods
    // For this example, I'll use a placeholder inner class.
    // In a real application, this would be in its own file.
    public class SinglyLinkedListGraph {
        // Placeholder for graph data. In a real scenario, this would be a more complex structure
        // e.g., Map<String, VertexNode> where VertexNode contains its edges.
        private Map<String, Map<String, Integer>> adjacencyList = new HashMap<>();
        private List<String> historicalCharacters = List.of(
                "J.César", "Mahoma", "Isabel", "Marx", "Darwin",
                "Newton", "M.Ali", "M.L.King", "Edison", "C.Colón"
        );
        private Random random = new Random();

        public SinglyLinkedListGraph() {
            // Initialize with the 10 characters
            historicalCharacters.forEach(this::addVertex);
        }

        public void addVertex(String name) {
            adjacencyList.putIfAbsent(name, new HashMap<>());
        }

        public void addEdge(String source, String destination, int weight) {
            if (adjacencyList.containsKey(source) && adjacencyList.containsKey(destination)) {
                adjacencyList.get(source).put(destination, weight);
                // For an undirected graph, add the reverse edge as well
                adjacencyList.get(destination).put(source, weight);
            }
        }

        public boolean containsVertex(String name) {
            return adjacencyList.containsKey(name);
        }

        public boolean containsEdge(String source, String destination) {
            return adjacencyList.containsKey(source) && adjacencyList.get(source).containsKey(destination);
        }

        public void randomizeGraph() {
            adjacencyList.clear();
            historicalCharacters.forEach(this::addVertex); // Re-add vertices

            // Create a random number of edges between 1000 and 2000
            int numEdges = random.nextInt(1001) + 1000; // 1000 to 2000 inclusive

            for (int i = 0; i < numEdges; i++) {
                String vertex1 = historicalCharacters.get(random.nextInt(historicalCharacters.size()));
                String vertex2 = historicalCharacters.get(random.nextInt(historicalCharacters.size()));
                if (!vertex1.equals(vertex2) && !containsEdge(vertex1, vertex2)) {
                    int weight = random.nextInt(1001) + 1000; // Random weight between 1000 and 2000
                    addEdge(vertex1, vertex2, weight);
                }
            }
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("SINGLY LINKED LIST GRAPH CONTENT...\n");
            int position = 1;
            for (String vertex : historicalCharacters) { // Maintain order for display
                if (adjacencyList.containsKey(vertex)) {
                    sb.append("The vertex in the position ").append(position++).append(": ").append(vertex).append("\n");
                    sb.append("....... EDGES AND WEIGHTS:\n");
                    if (adjacencyList.get(vertex).isEmpty()) {
                        sb.append("          (No edges)\n");
                    } else {
                        for (Map.Entry<String, Integer> entry : adjacencyList.get(vertex).entrySet()) {
                            sb.append("          Edge=").append(entry.getKey()).append(". Weight=").append(entry.getValue()).append("\n");
                        }
                    }
                    sb.append("\n");
                }
            }
            return sb.toString();
        }

        public String dfsTour(String startVertex) {
            if (!containsVertex(startVertex)) {
                return "DFS Tour: Start vertex not found.";
            }
            StringBuilder sb = new StringBuilder("DFS Tour (starting from " + startVertex + "): ");
            Map<String, Boolean> visited = new HashMap<>();
            adjacencyList.keySet().forEach(v -> visited.put(v, false));
            dfsRecursive(startVertex, visited, sb);
            return sb.toString();
        }

        private void dfsRecursive(String vertex, Map<String, Boolean> visited, StringBuilder sb) {
            visited.put(vertex, true);
            sb.append(vertex).append(" -> ");
            for (String neighbor : adjacencyList.get(vertex).keySet()) {
                if (!visited.get(neighbor)) {
                    dfsRecursive(neighbor, visited, sb);
                }
            }
        }

        public String bfsTour(String startVertex) {
            if (!containsVertex(startVertex)) {
                return "BFS Tour: Start vertex not found.";
            }
            StringBuilder sb = new StringBuilder("BFS Tour (starting from " + startVertex + "): ");
            Map<String, Boolean> visited = new HashMap<>();
            adjacencyList.keySet().forEach(v -> visited.put(v, false));
            java.util.Queue<String> queue = new java.util.LinkedList<>();

            visited.put(startVertex, true);
            queue.add(startVertex);

            while (!queue.isEmpty()) {
                String current = queue.poll();
                sb.append(current).append(" -> ");
                for (String neighbor : adjacencyList.get(current).keySet()) {
                    if (!visited.get(neighbor)) {
                        visited.put(neighbor, true);
                        queue.add(neighbor);
                    }
                }
            }
            return sb.toString();
        }

        public List<String> getVertices() {
            return historicalCharacters; // Return in a consistent order
        }

        public Map<String, Map<String, Integer>> getAdjacencyList() {
            return adjacencyList;
        }
    }


    @FXML
    public void initialize() {
        // Initialize the graph (e.g., create an empty graph or a default one)
        graph = new SinglyLinkedListGraph();
        graph.randomizeGraph(); // Start with a random graph as in the example image
        updateGraphDisplay();
        graphContentTextArea.setText(graph.toString());
    }

    @FXML
    private void handleRandomize() {
        graph.randomizeGraph();
        updateGraphDisplay();
        graphContentTextArea.setText(graph.toString());
        edgeBetweenVerticesLabel.setText("Edge between vertexes: "); // Clear previous edge info
    }

    @FXML
    private void handleContainsVertex() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Contains Vertex");
        dialog.setHeaderText("Check if a vertex exists in the graph.");
        dialog.setContentText("Enter vertex name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(vertexName -> {
            boolean exists = graph.containsVertex(vertexName);
            graphContentTextArea.setText("Checking for vertex: " + vertexName + "\nResult: " + (exists ? "Vertex found!" : "Vertex not found."));
        });
        edgeBetweenVerticesLabel.setText("Edge between vertexes: ");
    }

    @FXML
    private void handleContainsEdge() {
        TextInputDialog dialog1 = new TextInputDialog();
        dialog1.setTitle("Contains Edge - Source");
        dialog1.setHeaderText("Check if an edge exists in the graph.");
        dialog1.setContentText("Enter source vertex name:");

        Optional<String> result1 = dialog1.showAndWait();
        result1.ifPresent(sourceVertex -> {
            TextInputDialog dialog2 = new TextInputDialog();
            dialog2.setTitle("Contains Edge - Destination");
            dialog2.setHeaderText("Check if an edge exists in the graph.");
            dialog2.setContentText("Enter destination vertex name:");

            Optional<String> result2 = dialog2.showAndWait();
            result2.ifPresent(destinationVertex -> {
                boolean exists = graph.containsEdge(sourceVertex, destinationVertex);
                String message = "Checking for edge: " + sourceVertex + " -> " + destinationVertex + "\nResult: " + (exists ? "Edge found!" : "Edge not found.");
                graphContentTextArea.setText(message);

                if (exists) {
                    int weight = graph.getAdjacencyList().get(sourceVertex).get(destinationVertex);
                    edgeBetweenVerticesLabel.setText("Edge between vertexes: " + sourceVertex + " - " + destinationVertex + ". Weight: " + weight);
                } else {
                    edgeBetweenVerticesLabel.setText("Edge between vertexes: ");
                }
            });
        });
    }

    @FXML
    private void handleToString() {
        graphContentTextArea.setText(graph.toString());
        edgeBetweenVerticesLabel.setText("Edge between vertexes: ");
    }

    @FXML
    private void handleDFSTour() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("DFS Tour");
        dialog.setHeaderText("Perform a Depth-First Search tour.");
        dialog.setContentText("Enter starting vertex name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(startVertex -> {
            graphContentTextArea.setText(graph.dfsTour(startVertex));
        });
        edgeBetweenVerticesLabel.setText("Edge between vertexes: ");
    }

    @FXML
    private void handleBFSTour() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("BFS Tour");
        dialog.setHeaderText("Perform a Breadth-First Search tour.");
        dialog.setContentText("Enter starting vertex name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(startVertex -> {
            graphContentTextArea.setText(graph.bfsTour(startVertex));
        });
        edgeBetweenVerticesLabel.setText("Edge between vertexes: ");
    }

    /**
     * Updates the visual representation of the graph on the right pane.
     * This method clears existing drawings and redraws the graph based on the
     * current state of the 'graph' object.
     */
    private void updateGraphDisplay() {
        graphDisplayGroup.getChildren().clear(); // Clear existing circles and lines
        vertexCircles.clear();
        vertexLabels.clear();

        List<String> vertices = graph.getVertices();
        double centerX = graphDisplayGroup.getBoundsInLocal().getWidth() / 2;
        double centerY = graphDisplayGroup.getBoundsInLocal().getHeight() / 2;
        double radius = Math.min(centerX, centerY) * 0.7; // Radius for the circle layout of vertices

        // Draw vertices
        for (int i = 0; i < vertices.size(); i++) {
            String vertexName = vertices.get(i);
            double angle = 2 * Math.PI * i / vertices.size();
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);

            Circle circle = new Circle(x, y, 25, Color.LIGHTBLUE); // Radius 25
            circle.setStroke(Color.DARKCYAN);
            circle.setStrokeWidth(2);
            vertexCircles.put(vertexName, circle);

            Text label = new Text(x - (vertexName.length() * 3), y + 5, vertexName); // Adjust text position
            label.setFont(new Font("System Bold", 12));
            label.setFill(Color.DARKRED);
            vertexLabels.put(vertexName, label);

            graphDisplayGroup.getChildren().addAll(circle, label);
        }

        // Draw edges
        Map<String, Map<String, Integer>> adjacencyList = graph.getAdjacencyList();
        for (String source : adjacencyList.keySet()) {
            for (Map.Entry<String, Integer> edge : adjacencyList.get(source).entrySet()) {
                String destination = edge.getKey();
                // Ensure both source and destination circles exist (should be true if vertices were drawn)
                if (vertexCircles.containsKey(source) && vertexCircles.containsKey(destination)) {
                    Circle sourceCircle = vertexCircles.get(source);
                    Circle destCircle = vertexCircles.get(destination);

                    Line line = new Line(sourceCircle.getCenterX(), sourceCircle.getCenterY(),
                            destCircle.getCenterX(), destCircle.getCenterY());
                    line.setStroke(Color.GREEN);
                    line.setStrokeWidth(2);

                    // Add lines first so circles appear on top
                    graphDisplayGroup.getChildren().add(0, line); // Add to the beginning of the list
                }
            }
        }
    }
}