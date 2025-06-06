package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

import java.net.URL;
import java.util.*;

public class LinkedGraphOperationsController implements Initializable {

    // Componentes FXML
    @FXML
    private TextArea contentTextArea;
    @FXML
    private Pane graphPane;
    @FXML
    private Label edgeInfoLabel;

    //Lista de Vértices Disponibles (Personajes Históricos)
    private static final List<String> ALL_HISTORICAL_FIGURES = Arrays.asList(
            "A.Magno", "Platón", "Freud", "C.Colón", "Edison", "Pitágoras",
            "Gandhi", "Mozart", "Mahoma", "Armstrong", "Da Vinci", "Newton",
            "Einstein", "Shakespeare", "Cleopatra", "Napoleón", "Mandela"
    );

    // Estructuras de Datos del Grafo
    private final Map<String, List<Edge>> adjacencyList = new HashMap<>();
    private final List<String> vertices = new ArrayList<>(); // Vértices actualmente en el grafo
    private final List<String> availableVertices = new LinkedList<>(); // Vértices que se pueden agregar
    private final Random random = new Random();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        resetAvailableVertices();
        edgeInfoLabel.setText("Grafo vacío. Agrega vértices para comenzar.");
        updateUI();
    }

    private void resetAvailableVertices() {
        availableVertices.clear();
        availableVertices.addAll(ALL_HISTORICAL_FIGURES);
    }

    // Manejadores de Eventos de Botones (deben estar enlazados en el FXML)

    @FXML
    private void handleRandomize() {
        handleClear();
        int numVertices = 7 + random.nextInt(5); // Generar entre 7 y 11 vértices
        for (int i = 0; i < numVertices; i++) {
            addRandomVertex();
        }
        int numEdges = numVertices + random.nextInt(numVertices);
        for (int i = 0; i < numEdges; i++) {
            addRandomEdge();
        }
        edgeInfoLabel.setText("Se ha generado un nuevo grafo aleatorio.");
        updateUI();
    }

    @FXML
    private void handleAddVertex() {
        if (availableVertices.isEmpty()) {
            edgeInfoLabel.setText("No hay más personajes para agregar.");
            return;
        }
        String newVertex = addRandomVertex();
        if (newVertex != null) {
            edgeInfoLabel.setText("Vértice '" + newVertex + "' fue agregado.");
            updateUI();
        }
    }

    @FXML
    private void handleAddEdgesAndWeights() {
        FullEdge newEdge = addRandomEdge();
        if (newEdge != null) {
            edgeInfoLabel.setText("Edge between vertexes: " + newEdge.source + " ..... " + newEdge.dest + ". Weight: " + newEdge.weight);
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
        String vertexToRemove = vertices.remove(random.nextInt(vertices.size()));
        adjacencyList.remove(vertexToRemove);
        availableVertices.add(vertexToRemove);
        Collections.sort(availableVertices);

        for (List<Edge> edges : adjacencyList.values()) {
            edges.removeIf(edge -> edge.destination.equals(vertexToRemove));
        }
        edgeInfoLabel.setText("Vértice '" + vertexToRemove + "' y sus aristas fueron eliminados.");
        updateUI();
    }

    @FXML
    private void handleRemoveEdgesAndWeights() {
        List<FullEdge> allEdges = getAllEdges();
        if (allEdges.isEmpty()) {
            edgeInfoLabel.setText("No hay aristas para eliminar.");
            return;
        }
        FullEdge edgeToRemove = allEdges.get(random.nextInt(allEdges.size()));
        adjacencyList.get(edgeToRemove.source).removeIf(e -> e.destination.equals(edgeToRemove.dest));
        adjacencyList.get(edgeToRemove.dest).removeIf(e -> e.destination.equals(edgeToRemove.source));
        edgeInfoLabel.setText("Arista " + edgeToRemove.source + " - " + edgeToRemove.dest + " fue eliminada.");
        updateUI();
    }

    @FXML
    private void handleClear() {
        vertices.clear();
        adjacencyList.clear();
        resetAvailableVertices();
        edgeInfoLabel.setText("Grafo limpiado.");
        updateUI();
    }

    //Lógica Principal del Grafo

    private String addRandomVertex() {
        if (availableVertices.isEmpty()) return null;

        String newVertex = availableVertices.remove(random.nextInt(availableVertices.size()));
        vertices.add(newVertex);
        adjacencyList.put(newVertex, new ArrayList<>());
        Collections.sort(vertices);
        return newVertex;
    }

    private FullEdge addRandomEdge() {
        if (vertices.size() < 2) return null;

        String u, v;
        int attempts = 0;
        do {
            u = vertices.get(random.nextInt(vertices.size()));
            v = vertices.get(random.nextInt(vertices.size()));
            attempts++;
            if (attempts > vertices.size() * vertices.size()) return null;
        } while (u.equals(v) || edgeExists(u, v));

        int weight = 1000 + random.nextInt(1001); // Peso entre 1000 y 2000

        adjacencyList.get(u).add(new Edge(v, weight));
        adjacencyList.get(v).add(new Edge(u, weight));

        return new FullEdge(u, v, weight);
    }

    private boolean edgeExists(String u, String v) {
        return adjacencyList.get(u).stream().anyMatch(edge -> edge.destination.equals(v));
    }

    private List<FullEdge> getAllEdges() {
        List<FullEdge> allEdges = new ArrayList<>();
        for (String source : adjacencyList.keySet()) {
            for (Edge edge : adjacencyList.get(source)) {
                if (source.compareTo(edge.destination) < 0) {
                    allEdges.add(new FullEdge(source, edge.destination, edge.weight));
                }
            }
        }
        return allEdges;
    }

    // Actualización de la Interfaz de Usuario (UI)

    private void updateUI() {
        updateTextArea();
        drawGraph();
    }

    private void updateTextArea() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vertices.size(); i++) {
            String vertex = vertices.get(i);
            sb.append("The vertex in the position ").append(i + 1).append(" is: ").append(vertex).append("\n");
            sb.append(".......EDGES AND WEIGHTS:\n");
            sb.append("Singly Linked List Content\n");

            List<Edge> edges = adjacencyList.get(vertex);
            if (edges.isEmpty()) {
                sb.append("  (No edges)\n");
            } else {
                edges.sort(Comparator.comparing(e -> e.destination));
                for (Edge edge : edges) {
                    sb.append("Edge=").append(edge.destination).append(", Weight=").append(edge.weight).append("\n");
                }
            }
            sb.append("\n");
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
        double radius = Math.min(width, height) / 2 - 40;

        Map<String, Point> vertexPositions = new HashMap<>();

        for (int i = 0; i < vertices.size(); i++) {
            double angle = 2 * Math.PI * i / vertices.size();
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            vertexPositions.put(vertices.get(i), new Point(x, y));
        }

        for (FullEdge edge : getAllEdges()) {
            Point p1 = vertexPositions.get(edge.source);
            Point p2 = vertexPositions.get(edge.dest);
            Line line = new Line(p1.x, p1.y, p2.x, p2.y);

            if (edgeInfoLabel.getText().contains(edge.source + " ..... " + edge.dest) ||
                    edgeInfoLabel.getText().contains(edge.dest + " ..... " + edge.source)) {
                line.setStroke(Color.RED);
                line.setStrokeWidth(2.0);
            } else {
                line.setStroke(Color.BLACK);
            }
            graphPane.getChildren().add(line);
        }

        for (Map.Entry<String, Point> entry : vertexPositions.entrySet()) {
            String vertexValue = entry.getKey();
            Point p = entry.getValue();

            Circle circle = new Circle(p.x, p.y, 25, Color.CYAN);
            circle.setStroke(Color.BLACK);

            Text text = new Text(vertexValue);
            text.setFont(Font.font(10));
            text.setBoundsType(TextBoundsType.VISUAL);

            StackPane stack = new StackPane(circle, text);
            stack.setLayoutX(p.x - 25);
            stack.setLayoutY(p.y - 25);
            stack.setAlignment(Pos.CENTER);

            graphPane.getChildren().add(stack);
        }
    }

    // Clases Auxiliares (Records)
    private record Point(double x, double y) {}
    private record Edge(String destination, int weight) {}
    private record FullEdge(String source, String dest, int weight) {}
}