
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
     * Executes Dijkstra's algorithm on a graph, given a graph and the name of
     * the source vertex
     *
     * @param g The graph to be computed on
     * @param sourceName The label for the source vertex\
     *
     * @return A map of of each vertex to its last edge for its shortest path
     */
    static Map<Vertex, Edge> computeShortestPaths(Graph g, String sourceName) {
        return computeShortestPaths(g.nameToVertex.get(sourceName));
    }

    /**
     * Executes Dijkstra's algorithm on a graph, given a source vertex
     *
     * @param src The source vertex from graph g
     *
     * @return A map of of each vertex to its last edge for its shortest path
     */
    static Map<Vertex, Edge> computeShortestPaths(Vertex src) {

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
