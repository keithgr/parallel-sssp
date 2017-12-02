
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
     * Constructs a graph from a METAL text file
     *
     * @param fileName The file name
     */
    Graph(String fileName) throws FileNotFoundException {

        //init scanner
        Scanner in = new Scanner(new File(fileName));

        //skip header line
        in.nextLine();
        
        //get |V| and |E|
        int vertCount = in.nextInt();
        int edgeCount = in.nextInt();
        
        //array of vertices is needed to create edges
        Vertex[] vertices = new Vertex[vertCount];
        
        //construct and store all vertices
        for(int v = 0; v < vertCount; v++){
            vertices[v] = new Vertex(v, in.nextLine());
        }
        
        //construct all edges
        //connect each one to its start vertex
        for(int e = 0; e < edgeCount; e++){
            Edge temp = new Edge(vertices, in.nextLine());
            temp.start.conEdges.add(temp);
        }
        
    }
    
}