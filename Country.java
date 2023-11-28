import java.util.ArrayList;
import java.util.HashMap;

public class Country implements Comparable<Country>{
    private String repName;                         //representative name
    private HashMap<String, String> neighbours;    //countryName of neighbours and distance
    private double distanceFromSource;   

    //Constructor for countries with >2 aliases
    public Country(String repName) {
        this.repName = repName;
        this.distanceFromSource = Integer.MAX_VALUE;
        this.neighbours = new HashMap<String, String>();
    }

    @Override
    public int compareTo(Country c) {
        return Double.compare(this.distanceFromSource, c.distanceFromSource);
    }

    //Identified by its representative name
    @Override
    public boolean equals(Object o) {
        if (this.repName == o) {
            return true;
        }
        return false;
    }
}