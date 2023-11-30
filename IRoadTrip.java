import java.io.IOException;
import java.util.Scanner;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Set;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Collection;
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
            cleanData();
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

                System.out.println(repVal);

                if (!countriesGraph.containsKey(repVal)) {
                    Country c = new Country(repVal);
                    countriesGraph.put(repVal, c);
                }
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
                        String countryStr = countryKeys.get(0);
                        String neighbourStr = cAdd.get(0);
                        Country c = countriesGraph.get(countryStr);
                        c.addNeighbour(neighbourStr);

                        if (!countriesGraph.get(neighbourStr).getNeighbours().containsKey(countryStr)) {
                            Country n = countriesGraph.get(neighbourStr);
                            n.addNeighbour(countryStr);
                        }
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
                int distanceFromSource = Integer.parseInt(fieldVals[4]);

                Country c = countriesGraph.get(countryKey);

                if (c != null && neighbourKey != null && c.getNeighbours().containsKey(neighbourKey)) {
                    Country n = countriesGraph.get(neighbourKey);
                    c.setNeighbourDistance(neighbourKey, distanceFromSource);
                    n.setNeighbourDistance(countryKey, distanceFromSource);
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

    public void cleanData() {
        Collection values = countriesGraph.values();

        for (Object country : values) {
            ((Country)country).removeNullEntries();
        }
    }

    public void viewHashMap() {
        Set keys = countriesGraph.keySet();

        for (Object key : keys) {
            System.out.println("countriesGraph[" + key + "]: " + countriesGraph.get(key));
        }
    }

    public int getDistance (String country1, String country2) {
        Country source = countriesGraph.get(country1);
        Country destination = countriesGraph.get(country2);

        DijkstraAlgorithm dA = new DijkstraAlgorithm(countriesGraph, source, destination);
        HashMap<Country, Integer> results = dA.runAlgorithm();

        if (results == null) {
            return -1;
        }

        int distance = results.get(destination);
        return distance;
    }

    /*
    public List<String> findPath (String country1, String country2) {
        Country source = countriesGraph.get(country1);
        Country destination = countriesGraph.get(country2);
    }
    */

    /*
    public void acceptUserInput() {
        // Replace with your code
        System.out.println("IRoadTrip - skeleton");
    }
   */

    public void printMenu() {
        System.out.println("Please choose from the following options: ");
        System.out.println("1: Get distance between two countries");
        System.out.println("2: Get path between two countries");
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 3) {

            IRoadTrip a3 = new IRoadTrip(args);
            Scanner scan = new Scanner(System.in);

            a3.printMenu();
            String userInput = scan.nextLine();

            while (!userInput.equals("1") && !userInput.equals("2")) {
                System.out.println("Invalid input. Choose an option between 1-2: ");
                userInput = scan.nextLine();
            }

            System.out.println("Please enter first country: ");
            String country1 = scan.nextLine();
            System.out.println("Please enter second country: ");
            String country2 = scan.nextLine();

            if (userInput.equals("1")) {
                int distance = a3.getDistance(country1, country2);

                System.out.println(distance);
            } /*else {    //userInput == 2
                List<String> a3.findPath(country1, country2);
            }*/
        } else {
            System.out.println("Invalid input");
        }
    }
}

