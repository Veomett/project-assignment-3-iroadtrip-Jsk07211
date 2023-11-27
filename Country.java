import java.util.ArrayList;
import java.util.HashMap;

public class Country implements Comparable<Country>{
    private ArrayList<String> countryNames;
    private String countryCode;                 //representative code
    private HashMap<String, Integer> neighbours;    //countryCode of neighbours and distance
    private int distanceFromSource;             //all distances in files are whole numbers

    //Constructor for countries with >2 aliases
    public Country(ArrayList<String> countryNames, String countryCode) {
        this.countryNames = countryNames;
        this.countryCode = countryCode;
    }

    @Override
    public int compareTo(Country c) {
        return Integer.compare(this.distanceFromSource, c.distanceFromSource);
    }
}