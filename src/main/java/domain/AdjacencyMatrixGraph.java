package domain;

import domain.list.ListException;
import domain.queue.LinkedQueue;
import domain.queue.QueueException;
import domain.stack.LinkedStack;
import domain.stack.StackException;

public class AdjacencyMatrixGraph implements Graph {
    private Vertex[] vertexList; //arreglo de objetos tupo vértice
    private Object[][] adjacencyMatrix; //arreglo bidimensional
    private int n; //max de elementos
    private int counter; //contador de vertices

    //para los recorridos dfs, bfs
    private LinkedStack stack;
    private LinkedQueue queue;

    //Constructor
    public AdjacencyMatrixGraph(int n) {
        if (n <= 0) System.exit(1); //sale con status==1 (error)
        this.n = n;
        this.counter = 0;
        this.vertexList = new Vertex[n];
        this.adjacencyMatrix = new Object[n][n];
        this.stack = new LinkedStack();
        this.queue = new LinkedQueue();
        initMatrix(); //inicializa matriz de objetos con cero
    }

    private void initMatrix() {
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                this.adjacencyMatrix[i][j] = 0; //init con ceros
    }

    @Override
    public int size() throws ListException {
        return counter;
    }

    @Override
    public void clear() {
        this.vertexList = new Vertex[n];
        this.adjacencyMatrix = new Object[n][n];
        this.counter = 0; //inicializo contador de vértices
        this.initMatrix();
    }

    @Override
    public boolean isEmpty() {
        return counter == 0;
    }

    @Override
    public boolean containsVertex(Object element) throws GraphException, ListException {
        if (isEmpty())
            throw new GraphException("Adjacency Matrix is Empty");

        return indexOf(element) != -1;
    }

    @Override
    public boolean containsEdge(Object a, Object b) throws GraphException, ListException {
        if (isEmpty()) {
            return false;
        }
        if (!containsVertex(a) || !containsVertex(b)) {
            return false;
        }
        int indexA = indexOf(a);
        int indexB = indexOf(b);

        return !adjacencyMatrix[indexA][indexB].equals(0);
    }

    @Override
    public void addVertex(Object element) throws GraphException, ListException {
        if (counter >= vertexList.length)
            throw new GraphException("Adjacency Matrix Graph is Full");
        vertexList[counter++] = new Vertex(element);
    }

    @Override
    public void addEdge(Object a, Object b) throws GraphException, ListException {
        if (!containsVertex(a) || !containsVertex(b)) {
            throw new GraphException("Cannot add edge beetween vertexes[" + a + "] and [" + b + "]");
        }
        adjacencyMatrix[indexOf(a)][indexOf(b)] = 1;
        //grafo no dirigido
        adjacencyMatrix[indexOf(b)][indexOf(a)] = 1;
    }

    private int indexOf(Object a) {
        for (int i = 0; i < counter; i++) {  // Corregido: cambié <= por <
            if (util.Utility.compare(vertexList[i].data, a) == 0) {
                return i;
            }
        }
        return -1;//significa que la data de los vertices no coincide con el elemento
    }

    @Override
    public void addWeight(Object a, Object b, Object weight) throws GraphException, ListException {
        if (!containsEdge(a, b)) {
            throw new GraphException("Cannot add edge beetween vertexes[" + a + "] and [" + b + "]");
        }
        adjacencyMatrix[indexOf(a)][indexOf(b)] = weight;
        //grafo no dirigido
        adjacencyMatrix[indexOf(b)][indexOf(a)] = weight;
    }

    @Override
    public void addEdgeWeight(Object a, Object b, Object weight) throws GraphException, ListException {
        if (!containsVertex(a) || !containsVertex(b)) {
            throw new GraphException("Cannot add edge beetween vertexes[" + a + "] and [" + b + "]");
        }
        adjacencyMatrix[indexOf(a)][indexOf(b)] = weight;
        //grafo no dirigido
        adjacencyMatrix[indexOf(b)][indexOf(a)] = weight;
    }

    @Override
    public void removeVertex(Object element) throws GraphException, ListException {
        if (isEmpty()) {
            throw new GraphException("Adjacency Matrix Graph is Empty");
        }
        int index = indexOf(element);
        if (index != -1) {
            for (int i = index; i < counter - 1; i++) {
                vertexList[i] = vertexList[i + 1]; //desplaza los vertices a la izquierda

                //elimino el vertice de la matriz de adyacencia
                for (int j = 0; j < counter; j++) {
                    adjacencyMatrix[i][j] = adjacencyMatrix[i + 1][j];
                    adjacencyMatrix[j][i] = adjacencyMatrix[j][i + 1];
                }
            }

            // Este bucle anidado usaba la misma variable 'i' que ya estaba definida
            for (int k = 0; k < counter; k++) {
                for (int j = index; j < counter-1; j++) {
                    adjacencyMatrix[k][j] = adjacencyMatrix[k][j + 1]; //desplaza las columnas a la izquierda
                }
                //limpio la ultima fila y columna
            }
            counter--;
        }
    }

    @Override
    public void removeEdge(Object a, Object b) throws GraphException, ListException {
        // Implementación pendiente
    }

    // Recorrido en profundidad
    @Override
    public String dfs() throws GraphException, StackException, ListException {
        setVisited(false);//marca todos los vertices como no vistados
        // inicia en el vertice 0
        String info = vertexList[0].data + ", ";
        vertexList[0].setVisited(true); // lo marca
        stack.clear();
        stack.push(0); //lo apila
        while (!stack.isEmpty()) {
            // obtiene un vertice adyacente no visitado,
            //el que esta en el tope de la pila
            int index = adjacentVertexNotVisited((int) stack.top());
            if (index == -1) // no lo encontro
                stack.pop();
            else {
                vertexList[index].setVisited(true); // lo marca
                info += vertexList[index].data + ", "; //lo muestra
                stack.push(index); //inserta la posicion
            }
        }
        return info;
    }

    //Recorrido en amplitud
    @Override
    public String bfs() throws GraphException, QueueException, ListException {
        setVisited(false);//marca todos los vertices como no visitados
        // inicia en el vertice 0
        String info = vertexList[0].data + ", ";
        vertexList[0].setVisited(true); // lo marca
        queue.clear();
        queue.enQueue(0); // encola el elemento
        int v2;
        while (!queue.isEmpty()) {
            int v1 = (int) queue.deQueue(); // remueve el vertice de la cola
            // hasta que no tenga vecinos sin visitar
            while ((v2 = adjacentVertexNotVisited(v1)) != -1) {
                // obtiene uno
                vertexList[v2].setVisited(true); // lo marca
                info += vertexList[v2].data + ", "; //lo muestra
                queue.enQueue(v2); // lo encola
            }
        }
        return info;
    }

    //setteamos el atributo visitado del vertice respectivo
    private void setVisited(boolean value) {
        for (int i = 0; i < counter; i++) {
            vertexList[i].setVisited(value); //value==true o false
        }//for
    }

    private int adjacentVertexNotVisited(int index) {
        for (int i = 0; i < counter; i++) {
            if (!adjacencyMatrix[index][i].equals(0)
                    && !vertexList[i].isVisited())
                return i;//retorna la posicion del vertice adyacente no visitado
        }//for i
        return -1;
    }

    @Override
    public String toString() {
        String result = "Adjacency Matrix Graph Content...";
        //se muestran todos los vértices del grafo
        for (int i = 0; i < counter; i++) {
            result += "\nThe vextex in the position: " + i + " is: " + vertexList[i].data;
        }
        for (int i = 0; i < counter; i++) {
            for (int j = 0; j < counter; j++) {
                if (util.Utility.compare(adjacencyMatrix[i][j], 0) != 0) {
                    //si existe una arista
                    result += "\nThere is an edge between the vertexes:" + vertexList[i].data + "...."
                            + vertexList[j].data;

                    //si existe peso que lo muestre
                    if (util.Utility.compare(adjacencyMatrix[i][j], 1) != 0) {
                        //Si matriz[fila][columna] != 1 existe un peso agregado
                        result += "_WEIGHT" + adjacencyMatrix[i][j];
                    }
                }
            }
        }
        return result;
    }
}