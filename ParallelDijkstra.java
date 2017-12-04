
import java.util.*;
import java.util.concurrent.*;
import static java.lang.Math.*;

/**
 * Uses a multi-threaded variant of Dijkstra's algorithm to compute the SSSP of
 * a graph
 *
 * The result does not have perfect accuracy
 *
 * @author Keith Grable
 * @version 2017-12-02
 */
class ParallelDijkstra {

    /**
     * An array of all processes
     */
    static Process[] processes;

    /**
     * Executes Dijkstra's algorithm on a graph, given a graph and the name of
     * the source vertex
     *
     * @param g The graph to be computed on
     * @param sourceName The label for the source vertex
     * @param elas The maximum amount of progress that one thread may have ahead
     * of an adjacent thread
     *
     * @return A map of of each vertex to its last edge for its shortest path
     */
    static ConcurrentMap<Vertex, Edge> computeShortestPaths(
            Graph g, String sourceName,
            double elas, int threadCount) throws InterruptedException {
        return computeShortestPaths(g, g.nameToVertex.get(sourceName),
                elas, threadCount);
    }

    /**
     * Executes Dijkstra's algorithm on a graph, given a source vertex
     *
     * @param src The source vertex from graph g
     *
     * @return A map of of each vertex to its last edge for its shortest path
     */
    static ConcurrentMap<Vertex, Edge> computeShortestPaths(Graph g, Vertex src,
            double elas, int threadCount) throws InterruptedException {

        //create output hashmap
        ConcurrentMap<Vertex, Edge> solution = new ConcurrentHashMap<>();

        //in order to partition the data
        //we must sort the vertices by relative angle
        //from the source vertex
        Comparator<Vertex> byAngle = (Vertex a, Vertex b) -> {
            double angleA = Vertex.relativeAngleFromSrc(src, a);
            double angleB = Vertex.relativeAngleFromSrc(src, b);

            return (angleA > angleB ? 1 : -1);
        };

        //sort vertices by comparator
        Arrays.sort(g.vertices, byAngle);

        //compute number of vertices that are connected to source
        int vertCount = g.countVertices(src);

        //update vertex ids
        //determine which processes the vertices will be designated to
        for (int i = 0; i < g.vertices.length; i++) {
            g.vertices[i].id = i;
            g.vertices[i].pDeg = i * threadCount / g.vertices.length;
        }

        //create threads
        processes = new Process[threadCount];
        for (int p = 0; p < threadCount; p++) {
            processes[p] = new Process(g, src, solution, vertCount, p, elas);
        }

        //link adjacent threads to each other
        for (int p = 0; p < threadCount; p++) {
            processes[p].left
                    = processes[(p == 0 ? processes.length - 1 : p - 1)];
            processes[p].right
                    = processes[(p == processes.length - 1 ? 0 : p + 1)];
        }

        //map src vertex
        solution.put(src, new Edge(src));

        //add edges around the src
        for (Edge conEdge : src.conEdges) {
            conEdge.minDist = conEdge.weight;
            processes[conEdge.end.pDeg].q.add(conEdge);
        }

        //run threads
        for (int p = 0; p < threadCount; p++) {
            processes[p].start();
        }

        //join threads
        for (int p = 0; p < threadCount; p++) {
            processes[p].join();
        }

        return solution;
    }

    /**
     * A process will compute a wedge of the graph, centered at the source,
     * containing |V| / threadCount vertices
     */
    private static class Process extends Thread {

        private Graph g;
        private Vertex src;
        private ConcurrentMap<Vertex, Edge> solution;
        private final Queue<Edge> q;
        private int vertCount;

        private int id;
        private double elasticity;

        private double progress = 0.0;
        private Process left, right;

        private Process(Graph g, Vertex s, ConcurrentMap<Vertex, Edge> sol,
                int vCount, int i, double elas) {

            //init basic fields
            this.g = g;
            src = s;
            solution = sol;
            vertCount = vCount;

            id = i;
            elasticity = elas;

            //create priority queue for edges
            q = new PriorityBlockingQueue<>();
        }

        @Override
        public void run() {

            //the next edge
            Edge nextEdge;

            //main loop
            while (solution.keySet().size() < vertCount) {

                    //get next edge
                    if (!q.isEmpty() && q.peek().minDist < min(left.progress, right.progress) + elasticity) {
                        nextEdge = q.remove();
                    } else {
                        //ignore progress for suspended threads
                        progress = 999_999_999.0;
                        continue;
                    }

                    //update progress
                    progress = nextEdge.minDist;

                    //next vertex is the end of last edge
                    Vertex nextVert = nextEdge.end;
                    
                    //skip if vertex has already been solved
                    if (solution.containsKey(nextVert)) {
                        continue;
                    }

                    //map vertex to solution
                    solution.put(nextVert, nextEdge);

                    //add all edges that lead to unsolved vertices
                    for (Edge conEdge : nextVert.conEdges) {
                        if (!solution.containsKey(conEdge.end)) {
                            conEdge.minDist = progress + conEdge.weight;
                            processes[conEdge.end.pDeg].q.add(conEdge);
                        }
                    }

            }//end main loop

        }//end run()       

    }//end Process

    /**
     * This method checks if all processes are suspended (progress ==
     * MAX_DOUBLE)
     *
     * @return Status of processes
     */
    private static boolean allProcessesDone() {
        boolean allDone = true;
        for (int p = 0; p < processes.length; p++) {
            if (processes[p].progress != Double.MAX_VALUE) {
                allDone = false;
            }
        }
        return allDone;
    }

}//end ParallelDijkstra
