
import static java.lang.Math.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    static ConcurrentMap<Vertex, Edge> computeShortestPaths(Graph g, String sourceName,
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

        //update vertex ids
        //determine which processes the vertices will be designated to
        for (int i = 0; i < g.vertices.length; i++) {
            g.vertices[i].id = i;
            g.vertices[i].pDeg = i * threadCount / g.vertices.length;
        }

        //create threads
        processes = new Process[threadCount];
        for (int p = 0; p < threadCount; p++) {
            processes[p] = new Process(src, solution, elas);
            processes[p].id = p;
        }

        //link adjacent threads to each other
        for (int p = 0; p < threadCount; p++) {
            processes[p].left
                    = processes[(p == 0 ? processes.length - 1 : p - 1)];
            processes[p].right
                    = processes[(p == processes.length - 1 ? 0 : p + 1)];
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

        private int id;
        private Vertex src;
        private ConcurrentMap<Vertex, Edge> solution;
        private Queue<Edge> q;
        private double elasticity;

        private double progress = 0.0;
        private Process left, right;

        private Process(Vertex s, ConcurrentMap<Vertex, Edge> sol, double elas) {
            src = s;
            solution = sol;
            elasticity = elas;

            //create priority queue for edges
            q = new PriorityBlockingQueue<>();

            //create dummy edge
            q.add(new Edge(src));
        }

        @Override
        public void run() {

            DONE:
            while (true) {

                //main loop
                while (!q.isEmpty()) {

                    //get next edge
                    Edge nextEdge = q.remove();

                    //update progress
                    progress = nextEdge.minDist;

                    //if this process is too far ahead of a neighbor
                    //then wait
                    while (min(left.progress, right.progress) + elasticity
                            < this.progress) {
                        try {
                            Process.sleep(3);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ParallelDijkstra.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    //map next vertex to its last edge
                    Vertex nextVert = nextEdge.end;

                    //concurrency of map 
                    //ensures that only one process accesses the solutions
                    //map at one time
                    solution.putIfAbsent(nextVert, nextEdge);

                    //add all edges that lead to unsolved vertices
                    //edge must be added to appropriate priority queue
                    for (Edge conEdge : nextVert.conEdges) {
                        if (!solution.containsKey(conEdge.end)) {
                            conEdge.minDist = progress + conEdge.weight;
                            Vertex conEnd = conEdge.end;
                            processes[conEnd.pDeg].q.add(conEdge);
                        }
                    }

                }//end main loop

                //if process temporarily has no edges to compute
                //then max out progress so that it does not interfere with
                //neighbor processes
                progress = Double.MAX_VALUE;

                while (q.isEmpty()) {
                    if (allProcessesDone()) {
                        break DONE;
                    }
                }
            }

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
            if (processes[p].progress < Double.MAX_VALUE - 1.0) {
                allDone = false;
            }
        }
        return allDone;
    }

}//end ParallelDijkstra
