import java.util.ArrayList;
import java.util.HashMap;

public class Country implements Comparable<Country{
    private ArrayList<String> countryNames;
    private String countryCode;
    private HashMap<Country> neighbours;
    private int distanceFromSource;     //all distances in files are whole numbers

    //use default constructor for now

    @Override
    public int compareTo(Country c) {
        return Integer.compare(this.distanceFromSource, c.distanceFromSource);
    }
}