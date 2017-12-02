
import java.util.*;

/**
 * A small class to represent the intersection of multiple roads
 *
 * @author Keith Grable
 * @version 2017-12-02
 */
class Vertex {

    /** An array containing all vertices from the input file */
    static Vertex[] vertices;

    /**
     * A unique integer to identify this vertex
     */
    int id;

    /**
     * The name of the vertex
     */
    String name;

    /**
     * Latitude and longitude
     */
    double lat, lon;

    /**
     * A list of the connected edges
     */
    List<Edge> conEdges;

}
