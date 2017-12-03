
import java.util.*;
import static java.lang.Math.*;

/**
 * Small class to represent a weighted connection between two vertices
 *
 * @author Keith Grable
 * @version 2017-12-02
 */
class Edge implements Comparable<Edge> {

    /**
     * Radius of the Earth (mi)
     */
    static final double RADIUS = 3_959.0;
    
    /**
     * The name of the edge
     */
    String name;

    /**
     * The two vertices that are joined by this edge
     */
    Vertex start, end;

    /**
     * The cost of this edge
     */
    double weight;

    /**
     * The shortest distance from the main source vertex to the end vertex
     */
    double minDist = Double.MAX_VALUE;

    /**
     * Mirrored duplicate of this edge
     */
    Edge dupe;
    
    /**
     * Constructs an edge from text data and connects them to their
     * corresponding vertices
     *
     * This depends on the construction of all vertices
     *
     * @param verts The array of vertices to connect
     * @param line A line of text data
     */
    Edge(Vertex[] verts, String line) {

        //fill in basic data
        String[] data = line.split("[ ]+");

        name = data[2];
        start = verts[Integer.parseInt(data[0])];
        end = verts[Integer.parseInt(data[1])];

        //compute the weight of the edge based on latitude/longitude
        List<Vertex> shapingPoints = new ArrayList<>();
        shapingPoints.add(start);
        for (int p = 4; p < data.length; p += 2) {
            shapingPoints.add(
                    new Vertex(
                            Double.parseDouble(data[p - 1]),
                            Double.parseDouble(data[p])
                    )
            );
        }
        shapingPoints.add(end);

        weight = routeDistance(shapingPoints);

        //create mirrored duplicate edge
        dupe = new Edge(name, end, start, weight);

    }

    /**
     * Constructs an edge from the given fields
     *
     * @param nm The name of the vertex
     * @param st The start vertex
     * @param en The end vertex
     * @param wt The weight of the edge
     */
    Edge(String nm, Vertex st, Vertex en, double wt) {
        name = nm;
        start = st;
        end = en;
        weight = wt;
    }

    /**
     * Constructs a dummy edge for the initialization of the algorithm
     */
    Edge(Vertex src){
        end = src;
        minDist = 0.0;
    }
    
    /**
     * Computes the total distance along a direct route of given vertices
     *
     * @param sp The shaping points of the route
     * 
     * @return The distance that one must travel by traveliing around
     * the globe 
     */
    private static double routeDistance(List<Vertex> sp) {

        double dist = 0.0;
        for (int p = 1; p < sp.size(); p++) {
            double lat1 = toRadians(sp.get(p - 1).lat), 
                    lon1 = toRadians(sp.get(p - 1).lon),
                    lat2 = toRadians(sp.get(p).lat), 
                    lon2 = toRadians(sp.get(p).lon);
            dist += acos(
                    sin(lat1) * sin(lat2) 
                            + cos(lat1) * cos(lat2) * cos(lon2 - lon1)
            ) * RADIUS;
        }

        return dist;

    }

    /**
     * Edges with lower minimum distances are given priority
     */
    @Override
    public int compareTo(Edge other) {
        return (this.minDist > other.minDist ? 1 : -1);
    }

}
