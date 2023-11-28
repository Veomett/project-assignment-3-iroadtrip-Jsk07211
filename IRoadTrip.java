import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.*;

public class IRoadTrip {
    private HashMap<String, String> nameDict;
    private HashMap<String, Country> countriesGraph;
    public IRoadTrip (String [] args) throws IOException {
        FileReader borders = new FileReader(args[0]);
        FileReader capdist = new FileReader(args[1]);
        FileReader stateNames = new FileReader(args[2]);

        nameDict = new HashMap<String, String>();
        populateNameDict(borders, capdist, stateNames);
    }

    private void populateNameDict(FileReader borders, FileReader capdist, FileReader stateNames) {
        createEntriesForNameExceptions();
        createStateNameEntries(stateNames);
        createBorderNameEntries(borders);
    }

    private void createStateNameEntries(FileReader stateNames) {
        BufferedReader reader;

        try{
            reader = new BufferedReader(stateNames);
            String line = reader.readLine();    //skip first line

            line = reader.readLine();
            while (line != null) {
                //state_name.tsv == tab separated values
                String[] fieldVals = line.split("\t", 0);

                generateAliasEntries(fieldVals[2]);

                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createBorderNameEntries(FileReader borders) {
        Pattern hasAlias = Pattern.compile("^([a-zA-Z\\-' ]+ \\([a-zA-Z ]+\\)) ([0-9,.]+) km$");
        Pattern noAlias = Pattern.compile("^([a-zA-Z\\-' ]+) ([0-9,.]+) km$");
        Matcher matchAlias;
        Matcher matchNoAlias;

        BufferedReader reader;

        try{
            reader = new BufferedReader(borders);

            String line = reader.readLine();

            while (line != null) {
                String[] fieldVals = line.split("=|;", 0);

                for (int i = 1; i < fieldVals.length; i++) {
                    System.out.println(fieldVals[i]);

                    if (fieldVals[i].strip().length() == 0) {
                        continue;
                    }

                    matchAlias = hasAlias.matcher(fieldVals[i].strip());
                    matchNoAlias = noAlias.matcher(fieldVals[i].strip());

                    if (matchAlias.find()) {
                        System.out.println(matchAlias.group(1));
                        System.out.println(matchAlias.group(2));
                        System.out.println("Hello");
                    } else if (matchNoAlias.find()) {
                        System.out.println(matchNoAlias.group(1));
                        System.out.println(matchNoAlias.group(2));
                    } else {
                        System.out.println("No match");
                    }
                }

                line = reader.readLine();
                //break; //**remove later**
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //creates keys for states with difficult to detect aliases
    public void createEntriesForNameExceptions() {
        //First entry of alias list is the representative country code
        String[] DRCAliases = {"Congo, Democratic Republic of", "Congo, Democratic Republic of the", "Democratic Republic of the Congo", "DRC"};
        String[] PRKAliases = {"North Korea", "Korea, North", "PRK"};
        String[] ROKAliases = {"South Korea", "Korea, South", "ROK"};
        String[] MYAAliases = {"Myanmar", "Burma", "MYA"};
        String[] DRVAliases = {"Vietnam", "Annam", "Cochin China", "Tonkin", "DRV", "VNM", "RVN",};
        String[] TANAliases = {"Tanzania", "Tanganyika", "Zanzibar", "TAZ", "ZAN"};
        String[] USAAliases = {"United States of America", "United States", "USA"};
        String[] DENAliases = {"Denmark", "Greenland", "DEN"};
        String[] CocosAliases = {"Cocos Islands", "Keeling Islands"};

        ArrayList<String[]> exceptionsList = new ArrayList<String[]>();
        exceptionsList.add(DRCAliases);
        exceptionsList.add(PRKAliases);
        exceptionsList.add(ROKAliases);
        exceptionsList.add(MYAAliases);
        exceptionsList.add(DRVAliases);
        exceptionsList.add(TANAliases);
        exceptionsList.add(USAAliases);
        exceptionsList.add(DENAliases);
        exceptionsList.add(CocosAliases);

        for (String[] aliasList : exceptionsList) {
            for (String val : aliasList) {
                nameDict.put(val, aliasList[0]);
            }
        }
    }

    //Handle () aliases as well
    private void generateAliasEntries(String toTokenise) {
        ArrayList<String> toAdd = new ArrayList<String>();

        String[] alias = toTokenise.split("\\(|/", 0);

        for (String s : alias) {
            String sClean = s
                            .replaceAll("\\)", "")
                            .strip();
            toAdd.add(sClean);
        }
        
        String repVal = toAdd.get(0);

        //if state is not in dictionary, add it with representative country name as value
        for (String stateName : toAdd) {
            if (!nameDict.containsKey(stateName)) {
                nameDict.put(stateName, repVal);
                //System.out.printf("nameDict[%s]: %s\n", stateName, nameDict.get(stateName));
                }
            }
    }

    /*
    private ArrayList<String> dijkstraAlgo() {
    }

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

