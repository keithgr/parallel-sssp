
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.*;

/**
 *
 * @author Keith Grable
 * @version 2017-12-02
 */
public class Driver {

    public static void main(String[] args)
            throws FileNotFoundException, InterruptedException {

        Graph g = new Graph(args[0]);

        //"NY80@CR25"
        //"A-15@NY/QC&I-87@NY/Can"
        //"NY27@OceAve"
        Vertex source = g.nameToVertex.get("A-15@NY/QC&I-87@NY/Can");
        Vertex dest = g.nameToVertex.get("NY27@OceAve");

        //Map<Vertex, Edge> m = SerialDijkstra.computeShortestPaths(g, source);
        ConcurrentMap<Vertex, Edge> m = ParallelDijkstra.computeShortestPaths(g, source, 10.0, 2);

        double dist = m.get(dest).minDist;

        System.out.println(dist);

        /*
        
         Vertex v = dest;
         while (v != null) {
         System.out.println(v);
         Edge e = m.get(v);
         v = e.start;
         }

         v = dest;
         int count = 233;
         while (v != null) {
         Edge e = m.get(v);
         System.out.println(count + " " + (count + 1) + " " + e.name);
         v = e.start;
         count--;
         }
         */
    }

}
