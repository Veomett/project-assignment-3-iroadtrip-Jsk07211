import java.io.IOException;
import java.util.Scanner;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Set;
import java.util.Iterator;
import java.io.*;

public class IRoadTrip {
    private HashMap<String, String> nameDict;
    private HashMap<String, Country> countriesGraph;

    public IRoadTrip (String [] args) throws IOException {

        try {
            FileReader borders = new FileReader(args[0]);
            FileReader capdist = new FileReader(args[1]);
            FileReader stateNames = new FileReader(args[2]);

            nameDict = new HashMap<String, String>();
            countriesGraph = new HashMap<String, Country>();
            
            createEntriesForNameExceptions();
            createStateNameEntries(stateNames);
            createBorderNameEntries(borders);
            getStateNameDistances(capdist);
            viewHashMap();
        } catch (FileNotFoundException e) {
            System.out.println(e + "\nHalting execution...");
            System.exit(-1);
        }
    }

    private void createStateNameEntries(FileReader stateNames) {
        BufferedReader reader;

        try{
            reader = new BufferedReader(stateNames);
            String line = reader.readLine();    //skip first line

            for (line = reader.readLine(); line != null; line = reader.readLine()) {
                //state_name.tsv == tab separated values
                String[] fieldVals = line.split("\t", 0);

                //no need to capture key value since no distances are found in this file
                //also generate an alias for the code
                ArrayList<String> cAdd = generateAliasEntries(fieldVals[2] + "/" + fieldVals[1]);

                String repVal = cAdd.get(0);


                //if state is not in dictionary, add it with representative country name as value
                for (String stateName : cAdd) {
                    if (!nameDict.containsKey(stateName)) {
                        nameDict.put(stateName, repVal);
                    }
                }

                if (!countriesGraph.containsKey(repVal)) {
                    Country c = new Country(repVal);
                    countriesGraph.put(repVal, c);
                }

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

            for (line = reader.readLine(); line != null; line = reader.readLine()) {
                String[] fieldVals = line.split("=|;", 0);
                ArrayList<String> countryKeys = generateAliasEntries(fieldVals[0]);

                for (int i = 1; i < fieldVals.length; i++) {
                    if (fieldVals[i].strip().length() == 0) {
                        continue;
                    }

                    matchAlias = hasAlias.matcher(fieldVals[i].strip());
                    matchNoAlias = noAlias.matcher(fieldVals[i].strip());

                    String toAdd = "";

                    if (matchAlias.find()) {
                        toAdd = matchAlias.group(1);
                    } 
                    
                    if (matchNoAlias.find()) {
                        toAdd = matchNoAlias.group(1);
                    } 

                    ArrayList<String> cAdd = generateAliasEntries(toAdd);

                    if (countriesGraph.containsKey(cAdd.get(0)) && countriesGraph.containsKey(countryKeys.get(0))) {
                        Country c = countriesGraph.get(countryKeys.get(0));
                        c.addNeighbour(cAdd.get(0));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getStateNameDistances(FileReader capdist) {
        BufferedReader reader;

        try{
            reader = new BufferedReader(capdist);
            String line = reader.readLine();    //skip first line

            for (line = reader.readLine(); line != null; line = reader.readLine()) {
                //capdist.csv == comma separated values
                String[] fieldVals = line.split(",", 0);

                //uses country codes from state_name.tsv
                String countryKey = nameDict.get(fieldVals[1]);
                String neighbourKey = nameDict.get(fieldVals[3]);
                Double distanceFromSource = Double.parseDouble(fieldVals[4]);

                Country c = countriesGraph.get(countryKey);

                if (c != null && neighbourKey != null && c.getNeighbours().containsKey(neighbourKey)) {
                    c.setNeighbourDistance(neighbourKey, distanceFromSource);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //creates keys for states with difficult to detect aliases
    //create keys for country codes not in state_name.tsv
    public void createEntriesForNameExceptions() {
        //First entry of alias list is the representative country code
        String[] DRCAliases = {"Congo, Democratic Republic of", "Congo, Democratic Republic of the", "Democratic Republic of the Congo", "DRC"};
        String[] PRKAliases = {"North Korea", "Korea, North", "PRK"};
        String[] ROKAliases = {"South Korea", "Korea, South", "Korea", "ROK", "KOR"};
        String[] MYAAliases = {"Myanmar", "Burma", "MYA"};
        String[] DRVAliases = {"Vietnam", "Annam", "Cochin China", "Tonkin", "DRV", "VNM", "RVN",};
        String[] TANAliases = {"Tanzania", "Tanganyika", "Zanzibar", "TAZ", "ZAN"};
        String[] USAAliases = {"United States of America", "United States", "USA"};
        String[] DENAliases = {"Denmark", "Greenland", "DEN"};
        String[] BHMAliases = {"Bahamas", "Bahamas, The", "BHM"};

        ArrayList<String[]> exceptionsList = new ArrayList<String[]>();
        exceptionsList.add(DRCAliases);
        exceptionsList.add(PRKAliases);
        exceptionsList.add(ROKAliases);
        exceptionsList.add(MYAAliases);
        exceptionsList.add(DRVAliases);
        exceptionsList.add(TANAliases);
        exceptionsList.add(USAAliases);
        exceptionsList.add(DENAliases);
        exceptionsList.add(BHMAliases);

        for (String[] aliasList : exceptionsList) {
            Country c = new Country(aliasList[0]);
            countriesGraph.put(aliasList[0], c);

            for (String val : aliasList) {
                nameDict.put(val, aliasList[0]);
            }
        }
    }

    //Handle () aliases as well
    private ArrayList<String> generateAliasEntries(String toTokenise) {
        ArrayList<String> toAdd = new ArrayList<String>();

        String[] alias = toTokenise.split("\\(|/", 0);

        for (String s : alias) {
            String sClean = s
                            .replaceAll("\\)", "")
                            .strip();
            toAdd.add(sClean);
        }
        return toAdd;
    }

    public void viewHashMap() {
        Set keys = countriesGraph.keySet();
        Iterator i = keys.iterator();

        while (i.hasNext()) {
            Object key = i.next();
            System.out.println("countriesGraph[" + key + "]: " + countriesGraph.get(key));
        }
    }

    public int getDistance (String country1, String country2) {
        DijkstraAlgorithm dA = new DijkstraAlgorithm(countriesGraph, country1, country2);
        return dA.createCountryHeap();
    }

    /*
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

            IRoadTrip a3 = new IRoadTrip(args);
            Scanner scan = new Scanner(System.in);

            System.out.println("Please enter first country: ");
            String country1 = scan.nextLine();
            System.out.println("Please enter second country: ");
            String country2 = scan.nextLine();

            if (a3.getDistance(country1, country2) < 0) {
                System.out.println("No path exists");
            } else {
                System.out.println("It works!");
            }

        } else {
            System.out.println("Invalid input");
        }
    }
}

