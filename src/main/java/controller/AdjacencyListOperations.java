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
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class AdjacencyListOperations implements Initializable {

    // Componentes FXML
    @FXML
    private TextArea contentTextArea;
    @FXML
    private Pane graphPane;
    @FXML
    private Label edgeInfoLabel;

    // Estructuras de Datos del Grafo (Lista de Adyacencia)
    private final Map<Character, List<Edge>> adjacencyList = new HashMap<>();
    private final List<Character> vertices = new ArrayList<>();
    private final List<Character> availableChars = new LinkedList<>();
    private final Random random = new Random();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        resetAvailableChars();
        edgeInfoLabel.setText("Grafo vacío. Agrega vértices para comenzar.");
        updateUI();
    }

    private void resetAvailableChars() {
        availableChars.clear();
        for (char c = 'A'; c <= 'Z'; c++) {
            availableChars.add(c);
        }
    }

    // Manejadores de Eventos de Botones

    @FXML
    private void handleRandomize() {
        handleClear();
        int numVertices = 5 + random.nextInt(6); // Generar entre 5 y 10 vértices
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
        if (availableChars.isEmpty()) {
            edgeInfoLabel.setText("No se pueden agregar más vértices (A-Z completo).");
            return;
        }
        Character newVertex = addRandomVertex();
        if (newVertex != null) {
            edgeInfoLabel.setText("Vértice '" + newVertex + "' fue agregado.");
            updateUI();
        }
    }

    @FXML
    private void handleAddEdgesAndWeights() {
        FullEdge newEdge = addRandomEdge();
        if (newEdge != null) {
            edgeInfoLabel.setText("Arista agregada: " + newEdge.source + " ..... " + newEdge.dest + ". Weight: " + newEdge.weight);
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
        // Seleccionar y eliminar el vértice
        Character vertexToRemove = vertices.remove(random.nextInt(vertices.size()));
        adjacencyList.remove(vertexToRemove);
        availableChars.add(vertexToRemove); // Hacerlo disponible de nuevo
        Collections.sort(availableChars);

        // Eliminar todas las aristas que apuntaban a él desde otros vértices
        for (List<Edge> edges : adjacencyList.values()) {
            edges.removeIf(edge -> edge.destination == vertexToRemove);
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

        // Seleccionar y eliminar la arista
        FullEdge edgeToRemove = allEdges.get(random.nextInt(allEdges.size()));

        adjacencyList.get(edgeToRemove.source).removeIf(e -> e.destination == edgeToRemove.dest);
        adjacencyList.get(edgeToRemove.dest).removeIf(e -> e.destination == edgeToRemove.source);

        edgeInfoLabel.setText("Arista " + edgeToRemove.source + " - " + edgeToRemove.dest + " fue eliminada.");
        updateUI();
    }

    @FXML
    private void handleClear() {
        vertices.clear();
        adjacencyList.clear();
        resetAvailableChars();
        edgeInfoLabel.setText("Grafo limpiado.");
        updateUI();
    }

    // --- Lógica Principal del Grafo ---

    private Character addRandomVertex() {
        if (availableChars.isEmpty()) return null;

        Character newVertex = availableChars.remove(random.nextInt(availableChars.size()));
        vertices.add(newVertex);
        adjacencyList.put(newVertex, new ArrayList<>());
        Collections.sort(vertices);
        return newVertex;
    }

    private FullEdge addRandomEdge() {
        if (vertices.size() < 2) return null;

        Character u, v;
        int attempts = 0;
        do {
            u = vertices.get(random.nextInt(vertices.size()));
            v = vertices.get(random.nextInt(vertices.size()));
            attempts++;
            if(attempts > vertices.size() * vertices.size()) return null; // Prevenir bucle infinito
        } while (u.equals(v) || edgeExists(u, v));

        int weight = 1 + random.nextInt(50);

        // Agregar arista en ambas direcciones para grafo no dirigido
        adjacencyList.get(u).add(new Edge(v, weight));
        adjacencyList.get(v).add(new Edge(u, weight));

        return new FullEdge(u, v, weight);
    }

    private boolean edgeExists(Character u, Character v) {
        return adjacencyList.get(u).stream().anyMatch(edge -> edge.destination.equals(v));
    }

    private List<FullEdge> getAllEdges() {
        List<FullEdge> allEdges = new ArrayList<>();
        for (Character source : adjacencyList.keySet()) {
            for (Edge edge : adjacencyList.get(source)) {
                // Para evitar duplicados en grafo no dirigido (e.g., A-B y B-A)
                if (source < edge.destination) {
                    allEdges.add(new FullEdge(source, edge.destination, edge.weight));
                }
            }
        }
        return allEdges;
    }

    // --- Actualización de la Interfaz de Usuario (UI) ---

    private void updateUI() {
        updateTextArea();
        drawGraph();
    }

    private void updateTextArea() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vertices.size(); i++) {
            Character vertex = vertices.get(i);
            sb.append("Vextex in the position ").append(i).append(": ").append(vertex).append("\n");
            sb.append(".....EDGES AND WEIGHTS:\n");
            sb.append("Singly Linked List Content\n");

            List<Edge> edges = adjacencyList.get(vertex);
            if (edges.isEmpty()) {
                sb.append("  (No edges)\n");
            } else {
                edges.sort(Comparator.comparing(e -> e.destination)); // Ordenar para consistencia
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
        double radius = Math.min(width, height) / 2 - 30;

        Map<Character, Point> vertexPositions = new HashMap<>();

        for (int i = 0; i < vertices.size(); i++) {
            double angle = 2 * Math.PI * i / vertices.size();
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            vertexPositions.put(vertices.get(i), new Point(x, y));
        }

        // Dibujar aristas
        for (FullEdge edge : getAllEdges()) {
            Point p1 = vertexPositions.get(edge.source);
            Point p2 = vertexPositions.get(edge.dest);
            Line line = new Line(p1.x, p1.y, p2.x, p2.y);

            // Resaltar la arista mencionada en la etiqueta de información
            if (edgeInfoLabel.getText().contains(edge.source + " ..... " + edge.dest) ||
                    edgeInfoLabel.getText().contains(edge.dest + " ..... " + edge.source)) {
                line.setStroke(Color.RED);
                line.setStrokeWidth(2.0);
            } else {
                line.setStroke(Color.BLACK);
            }
            graphPane.getChildren().add(line);
        }

        // Dibujar vértices
        for (Map.Entry<Character, Point> entry : vertexPositions.entrySet()) {
            Character vertexValue = entry.getKey();
            Point p = entry.getValue();

            Circle circle = new Circle(p.x, p.y, 20, Color.CYAN);
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

    //Clases Auxiliares
    private record Point(double x, double y) {}
    private record Edge(Character destination, int weight) {}
    private record FullEdge(Character source, Character dest, int weight) {}
}