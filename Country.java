import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;

public class Country implements Comparable<Country>{
    private String repName;                         //representative name
    private HashMap<String, Double> neighbours;    //countryName of neighbours and distance
    private Double distanceFromSource;   

    //Constructor for countries with >2 aliases
    public Country(String repName) {
        this.repName = repName;
        this.distanceFromSource = Double.POSITIVE_INFINITY;
        this.neighbours = new HashMap<String, Double>();
    }

    public void addNeighbour(String key, Double value) {
        if (!neighbours.containsKey(key)){
            neighbours.put(key, value);
        }
    }

    public String getRepName() {
        return this.repName;
    }

    @Override
    public int compareTo(Country c) {
        return Double.compare(this.distanceFromSource, c.distanceFromSource);
    }

    @Override
    public String toString() {
        Set keys = neighbours.keySet();
        Iterator i = keys.iterator();
        String toReturn = "\nNeighbours: ";

        while (i.hasNext()) {
            Object key = i.next();
            toReturn += "neighbours[" + key + "]: " + neighbours.get(key) + "; ";
        }
        toReturn += "\n";
        return toReturn;
    }
}