
import java.io.FileNotFoundException;
import java.util.*;


/**
 *
 * @author Keith Grable
 * @version 2017-12-02
 */
public class Driver {
    
    public static void main(String[] args) throws FileNotFoundException {
        
        Graph g = new Graph(args[0]);
        
        Vertex source = g.nameToVertex.get("A-15@NY/QC&I-87@NY/Can");
        Vertex dest = g.nameToVertex.get("NY27@OceAve");
        
        Map<Vertex, Edge> m = SerialDijkstra.computeShortestPaths(g.nameToVertex.get("A-15@NY/QC&I-87@NY/Can"));
        

        Vertex v =dest;
        while(v != null){
            System.out.println(v);
            Edge e = m.get(v);
            v = e.start;
        }
        
        v =dest;
        int count = 233;
        while(v != null){
            Edge e = m.get(v);
            System.out.println( count + " " + (count + 1) + " " + e.name);
            v = e.start;
            count--;
        }
        
    }
    
}
