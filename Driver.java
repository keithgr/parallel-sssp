
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
        Vertex source = g.nameToVertex.get("CA1@GoatRockRd");
        Vertex dest = g.nameToVertex.get("NY276/QC221");

        long start, end;
        
        //start = System.nanoTime();
        //Map<Vertex, Edge> m = SerialDijkstra.computeShortestPaths(g, source);
        //end = System.nanoTime();
        
        start = System.nanoTime();
        ConcurrentMap<Vertex, Edge> m = ParallelDijkstra.computeShortestPaths(g, source, 50.0, 3);
        end = System.nanoTime();
        
        
        double dist = m.get(dest).minDist;
        
        System.out.printf("%,d\t%,6.9f\n", end - start, dist);
        
/*
        //System.out.println(dist);
        System.out.println("TMG 1.0 collapsed");
        
        int count = 0;
        Vertex v = dest;
        while (v != null) {
            //System.out.println(v);
            Edge e = m.get(v);
            v = e.start;
            count++;
        }

        System.out.println(count + " " + (count - 1));

        v = dest;
        
        while (v != null) {
            System.out.println(v);
            Edge e = m.get(v);
            v = e.start;
            //count++;
        }
        
        v = dest;
        count -= 2;
        while (v != null) {
            Edge e = m.get(v);
            System.out.println(count + " " + (count + 1) + " " + e.name);
            v = e.start;
            count--;
        }
*/
    }

}
