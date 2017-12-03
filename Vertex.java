
import java.util.*;

/**
 * A small class to represent the intersection of multiple roads
 *
 * @author Keith Grable
 * @version 2017-12-02
 */
class Vertex {
    
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
    List<Edge> conEdges = new ArrayList<>();
    
    /** 
     * Constructs a vertex from text data 
     * 
     * @param lineNum The line number of the text file where this data is found
     * @param line A line of text data
     */
    Vertex(int lineNum, String line){
        
        //fill in basic data
        String[] data = line.split("[ ]+");
        
        id = lineNum;
        name = data[0];
        lat = Double.parseDouble(data[1]);
        lon = Double.parseDouble(data[2]);
    }
    
    /**
     * Constructs a vertex with given latitude and longitude
     * 
     * @param la Latitude
     * @param lo Longitude
     */
    Vertex(double la, double lo){
        lat = la;
        lon = lo;
    }
    
    /**
     * @return A string for a vertex in METAL format
     */
    @Override
    public String toString(){
        return name + " " + lat + " " + lon;
    }
    
}
