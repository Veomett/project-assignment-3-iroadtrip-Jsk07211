import java.util.ArrayList;
import java.util.HashMap;

public class Country implements Comparable<Country>{
    private String repName;                 //representative name
    private HashMap<String, Integer> neighbours;    //countryCode of neighbours and distance
    private int distanceFromSource;             //all distances in files are whole numbers

    //Constructor for countries with >2 aliases
    public Country(String repName) {
        this.repName = repName;
    }

    @Override
    public int compareTo(Country c) {
        return Integer.compare(this.distanceFromSource, c.distanceFromSource);
    }
}