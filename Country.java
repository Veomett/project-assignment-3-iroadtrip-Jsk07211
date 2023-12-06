import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;

public class Country implements Comparable<Country>{
    private String repName;                         //representative name
    private HashMap<String, Integer> neighbours;    //countryName of neighbours and distance
    private int distanceFromSource;   
    private Country prevCountry;

    //Constructor for countries with >2 aliases
    public Country(String repName) {
        this.repName = repName;
        this.distanceFromSource = Integer.MAX_VALUE;
        this.neighbours = new HashMap<String, Integer>();
        this.prevCountry = null;
    }

    public void addNeighbour(String key) {
        if (!neighbours.containsKey(key)){
            //will be updated later
            neighbours.put(key, null);
        }
    }

    public void setNeighbourDistance(String neighbour, int distance) {
        neighbours.replace(neighbour, distance);
    }

    public void removeNullEntries() {
        HashMap<String, Integer> placeholder = new HashMap<String, Integer>();
        Set neighbourKeys = neighbours.keySet();

        for (Object key : neighbourKeys) {
            if (neighbours.get(key) != null) {
                placeholder.put(((String)key), neighbours.get(key));
            }
        }
        neighbours = placeholder;
    }

    public String getRepName() {
        return this.repName;
    }

    public HashMap<String, Integer> getNeighbours() {
        return this.neighbours;
    }

    public int getDistanceFromSource() {
        return this.distanceFromSource;
    }

    public Country getPrevCountry() {
        return this.prevCountry;
    }

    public void setDistanceFromSource(int newDist) {
        this.distanceFromSource = newDist;
    }

    public void setPrevCountry(Country prevCountry) {
        this.prevCountry = prevCountry;
    }

    @Override
    public int compareTo(Country c) {
        return Integer.compare(this.distanceFromSource, c.distanceFromSource);
    }

    @Override
    public String toString() {
        Set keys = neighbours.keySet();

        String toReturn = "\nNeighbours: ";

        for (Object key : keys) {
            toReturn += "neighbours[" + key + "]: " + neighbours.get(key) + "; ";
        }

        toReturn += "\n";
        return toReturn;
    }
}