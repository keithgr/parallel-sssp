
import java.util.*;

/**
 * Uses single-threaded Dijkstra's algorithm to compute the SSSP of a graph
 *
 * The result has perfect accuracy
 *
 * @author Keith Grable
 * @version 2017-12-02
 */
class SerialDijkstra {

    /**
     * Executes Dijkstra's algorithm on a graph, assign
     *
     * @param src The source vertex from graph g
     */
    public static Map<Vertex, Edge> computeShortestPaths(Vertex src) {

        //create output hashmap
        Map<Vertex, Edge> solution = new HashMap<>();

        //create priority queue for edges
        Queue<Edge> q = new PriorityQueue<>();

        //create dummy edge
        q.add(new Edge(src));

        //the min distance of the last edge that was dequeued
        double progress;

        //main loop
        while (!q.isEmpty()) {

            //get next edge
            Edge nextEdge = q.remove();

            //update progress
            progress = nextEdge.minDist;

            //map next vertex to its last edge
            Vertex nextVert = nextEdge.end;
            solution.put(nextVert, nextEdge);

            //add all edges that lead to unsolved vertices
            for (Edge conEdge : nextVert.conEdges) {
                if (!solution.containsKey(conEdge.end)) {
                    conEdge.minDist = progress + conEdge.weight;
                    q.add(conEdge);
                }
            }
            
        }

        return solution;

    }

}
