import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Set;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Collection;
import java.io.*;

public class TextCleaner {
    public static void createStateNameEntries(FileReader stateNames, HashMap<String, String> nameDict, HashMap<String, Country> countriesGraph) {
        BufferedReader reader;

        try{
            reader = new BufferedReader(stateNames);
            String line = reader.readLine();    //skip first line

            for (line = reader.readLine(); line != null; line = reader.readLine()) {
                //state_name.tsv == tab separated values
                String[] fieldVals = line.split("\t", 0);

                //no need to capture key value since no distances are found in this file
                //also generate an alias for the code
                ArrayList<String> cAdd = generateAliasEntries(fieldVals[2] + "/" + fieldVals[1], countriesGraph);

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

    public static void createBorderNameEntries(FileReader borders, HashMap<String, String> nameDict, HashMap<String, Country> countriesGraph) {
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
                ArrayList<String> countryKeys = generateAliasEntries(fieldVals[0], countriesGraph);

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

                    ArrayList<String> cAdd = generateAliasEntries(toAdd, countriesGraph);

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
    
    public static void getStateNameDistances(FileReader capdist, HashMap<String, String> nameDict, HashMap<String, Country> countriesGraph) {
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
    public static void createEntriesForNameExceptions(HashMap<String, String> nameDict, HashMap<String, Country> countriesGraph) {
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
    public static ArrayList<String> generateAliasEntries(String toTokenise, HashMap<String, Country> countriesGraph) {
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

    public static void cleanData(HashMap<String, Country> countriesGraph) {
        Collection values = countriesGraph.values();

        for (Object country : values) {
            ((Country)country).removeNullEntries();
        }
    }

    public static void viewHashMap(HashMap<String, Country> countriesGraph) {
        Set keys = countriesGraph.keySet();

        for (Object key : keys) {
            System.out.println("countriesGraph[" + key + "]: " + countriesGraph.get(key));
        }
    }
}