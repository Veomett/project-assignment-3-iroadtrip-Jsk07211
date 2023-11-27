import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.io.*;

public class IRoadTrip {
    private HashMap<String, String> stateNameDict;
    private HashMap<String, Country> countriesGraph;
    public IRoadTrip (String [] args) throws IOException {
        FileReader borders = new FileReader(args[0]);
        FileReader capdist = new FileReader(args[1]);
        FileReader stateNames = new FileReader(args[2]);

        stateNameDict = new HashMap<String, String>();

        createStateNameDict(stateNames);
    }

    private void createStateNameDict(FileReader stateNames) {
        BufferedReader reader;

        try{
            createEntriesForStateNameExceptions();

            reader = new BufferedReader(stateNames);
            String line = reader.readLine();    //skip first line

            line = reader.readLine();
            while (line != null) {
                //state_name.tsv == tab separated values
                String[] fieldVals = line.split("\t", 0);

                ArrayList<String> toAdd = generateAliasList(fieldVals[2]);
                //if state is not in dictionary, add it with country code as value
                for (String stateName : toAdd) {
                    if (!stateNameDict.containsKey(stateName)) {
                        stateNameDict.put(stateName, fieldVals[1]);
                        System.out.printf("stateNameDict[%s]: %s\n", stateName, stateNameDict.get(stateName));
                    }
                }

                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //creates keys for states with difficult to detect aliases
    private void createEntriesForStateNameExceptions() {
        //First entry of alias list is the representative country code
        String[] DRCAliases = {"DRC", "Congo, Democratic Republic of", "Congo, Democratic Republic of the", "Democratic Republic of the Congo"};
        String[] PRKAliases = {"PRK", "North Korea", "Korea, North"};
        String[] ROKAliases = {"ROK", "South Korea", "Korea, South"};
        String[] MYAAliases = {"MYA", "Myanmar, Burma"};
        String[] DRVAliases = {"DRV", "VNM", "RVN", "Vietnam", "Annam", "Cochin China", "Tonkin"};
        String[] TANAliases = {"TAZ", "ZAN", "Tanzania", "Tanganyika", "Zanzibar"};

        ArrayList<String[]> exceptionsList = new ArrayList<String[]>();
        exceptionsList.add(DRCAliases);
        exceptionsList.add(PRKAliases);
        exceptionsList.add(ROKAliases);
        exceptionsList.add(MYAAliases);
        exceptionsList.add(DRVAliases);
        exceptionsList.add(TANAliases);

        for (String[] aliasList : exceptionsList) {
            for (String val : aliasList) {
                stateNameDict.put(val, aliasList[0]);
            }
        }
    }

    //Handle () aliases as well
    public ArrayList<String> generateAliasList(String toTokenise) {
        ArrayList<String> toReturn = new ArrayList<String>();

        String[] alias = toTokenise.split("\\(|/", 0);

        for (String s : alias) {
            String sClean = s
                            .replaceAll("\\)", "")
                            .strip();
            toReturn.add(sClean);
        }
        return toReturn;
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

