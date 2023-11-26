import java.io.IOException;
import java.util.HashMap;

public class IRoadTrip {
    private HashMap<Country> countriesGraph;
    public IRoadTrip (String [] args) {
        String[] validArgs = {"borders.txt", "capdist.csv", "state_name.tsv"};

        for (String arg : args) {
            System.out.println(arg);
        }
    }

    private void fileToHashMap() {
        //Handle countries with multiple names
        //First entry of alias list is the representative country code
        String[] DRCAliases = {"DRC", "Congo, Democratic Republic of", "Congo, Democratic Republic of the", "Democratic Republic of the Congo"};
        String[] PRKAliases = {"PRK", "North Korea", "Korea, North"};
        String[] ROKAliases = {"ROK", "South Korea", "Korea, South"};
        String[] MYAAliases = {"MYA", "Myanmar, Burma"};
        String[] DRVAliases = {"DRV", "VNM", "RVN", "Vietnam"}
        String[] TANAliases = {"TAZ", "ZAN", "Tanzania", "Tanganyika", "Zanzibar"};
    }

    /*
    public int getDistance (String country1, String country2) {
        // Replace with your code
        return -1;
    }


    public List<String> findPath (String country1, String country2) {
        // Replace with your code
        return null;
    }


    public void acceptUserInput() {
        // Replace with your code
        System.out.println("IRoadTrip - skeleton");
    }
    */

   public HashMap<String> getCountriesGraph() {
        return this.countriesGraph;
   }

    public static void main(String[] args) throws IOException {
        IRoadTrip a3 = new IRoadTrip(args);
    }
}

