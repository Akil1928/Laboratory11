package domain;

import domain.list.ListException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdjacencyMatrixGraphTest {

    @Test
    void test() {
        try {
            AdjacencyMatrixGraph graph = new AdjacencyMatrixGraph(50);
            for (char i = 'a'; i <= 'e'; i++) {
                graph.addVertex(i);
            }
            graph.addEdgeWeight('a', 'b', util.Utility.random(20) + 2);
            graph.addEdgeWeight('a', 'c', util.Utility.random(20) + 2);
            graph.addEdgeWeight('a', 'd', util.Utility.random(20) + 2);
            graph.addEdgeWeight('b', 'e', util.Utility.random(20) + 2);
            graph.addEdgeWeight('c', 'd', util.Utility.random(20) + 2);
            graph.addEdgeWeight('c', 'e', util.Utility.random(20) + 2);

            System.out.println(graph);
            System.out.println("\nVertex deleted: a");
            graph.removeVertex('a');
            System.out.println(graph);
            System.out.println("\nEdge deleted e----b");
            graph.removeEdge('e', 'b');
            System.out.println(graph);

        } catch (GraphException e) {
            throw new RuntimeException(e);
        } catch (ListException e) {
            throw new RuntimeException(e);

        }
    }
}