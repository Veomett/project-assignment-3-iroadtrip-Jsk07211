import java.io.IOException;
import java.util.HashMap;
//ask prof how to import multimap wth
//import java.com.google.common.collect.ArrayListMultimap;
import java.util.StringTokenizer;
import java.io.*;

public class IRoadTrip {
    private HashMap<String, String[]> stateNameDict;
    private HashMap<String, Country> countriesGraph;
    public IRoadTrip (String [] args) throws IOException {
        FileReader borders = new FileReader(args[0]);
        FileReader capdist = new FileReader(args[1]);
        FileReader stateName = new FileReader(args[2]);
    }

    private void createStateNameDict() {
        //Handle countries with multiple names
        //First entry of alias list is the representative country code
        String[] DRCAliases = {"DRC", "Congo, Democratic Republic of", "Congo, Democratic Republic of the", "Democratic Republic of the Congo"};
        String[] PRKAliases = {"PRK", "North Korea", "Korea, North"};
        String[] ROKAliases = {"ROK", "South Korea", "Korea, South"};
        String[] MYAAliases = {"MYA", "Myanmar, Burma"};
        String[] DRVAliases = {"DRV", "VNM", "RVN", "Vietnam"};
        String[] TANAliases = {"TAZ", "ZAN", "Tanzania", "Tanganyika", "Zanzibar"};

        String[] countryCodeExceptionList = {"DRC", "PRK", "ROK", "MYA", "DRV", "TAZ"};

    }

    public void generateAliasList() {
        StringTokenizer st = new StringTokenizer("Turkey (Turkiye)");

        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            System.out.println(token);

            if (token.contains("(")) {
                token = token.substring(1, token.length() - 1);
            }
            System.out.println(token);
        }
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

   public HashMap<String> getCountriesGraph() {
        return this.countriesGraph;
   }
   */

    public static void main(String[] args) throws IOException {
        if (args.length == 3) {
            try {
                IRoadTrip a3 = new IRoadTrip(args);
            } catch (FileNotFoundException e) {
                System.out.println(e + "\nHalting execution...");
                System.exit(-1);
            }
        } else {
            System.out.println("Invalid input");
        }
    }
}

