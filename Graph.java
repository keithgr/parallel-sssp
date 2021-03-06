
import java.io.*;
import java.util.*;

/**
 * A class that sets up and contains the graph structure from the input file
 *
 * @author Keith Grable
 * @version 2017-12-02
 */
class Graph {

    /**
     * An array of all vertices
     */
    Vertex[] vertices;

    /**
     * A map from vertex name to its corresponding vertex
     */
    Map<String, Vertex> nameToVertex = new HashMap<>();

    /**
     * Constructs a graph from a METAL text file
     *
     * @param fileName The file name
     */
    Graph(String fileName) throws FileNotFoundException {

        //init scanner
        Scanner in = new Scanner(new File("./graphs/" + fileName));

        //skip header line
        in.nextLine();

        //get |V| and |E|
        int vertCount = in.nextInt();
        int edgeCount = in.nextInt();
        in.nextLine();

        //array of vertices is needed to create edges
        vertices = new Vertex[vertCount];

        //construct and store all vertices
        for (int v = 0; v < vertCount; v++) {
            vertices[v] = new Vertex(v, in.nextLine());
            nameToVertex.put(vertices[v].name, vertices[v]);
        }

        //construct all edges
        //connect each one to its start vertex
        for (int e = 0; e < edgeCount; e++) {
            Edge temp = new Edge(vertices, in.nextLine());
            temp.start.conEdges.add(temp);
            temp.end.conEdges.add(temp.dupe);
        }

    }

    /**
     * Iterates through all vertices connected to a source in order to count
     * them
     *
     * @param src The source vertex
     *
     * @return The number of connected vertices
     */
    int countVertices(Vertex src) {

        //contains visited vertices
        Set<Vertex> visited = new HashSet<>();

        //stack of vertices to be checked
        Stack<Vertex> newVertices = new Stack<>();

        //begin with source
        newVertices.push(src);

        //count vertices
        while (!newVertices.isEmpty()) {
            Vertex v = newVertices.pop();
            visited.add(v);
            for (Edge e : v.conEdges) {
                if(!visited.contains(e.end)){
                    newVertices.push(e.end);
                }
            }
        }
        
        return visited.size();
    }

}
