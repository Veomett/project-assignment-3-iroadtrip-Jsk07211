/**
 * 
 * This is a static class used to read and clean up data from text files and update the respective hashmaps
 * 
 * Methods for:
 *  void createStateNameEntries()
 *      - Adds to nameDict: Keys are country aliases, values are the representative country name
 *      - Adds to countriesGraph: Keys are the representative country names, values are empty hashmaps
 * 
 *  void createBorderNameEntries()
 *      - Adds to nameDict: Keys are country names (with or without country codes)
 *      - Adds to countriesGraph: For the value hashmap, adds keys (neighbour names) with values (Integer.MAX_VALUE)
 * 
 *  void getStateNameDistances()
 *      - Adds to countriesGraph: For the value hashmap, adds keys (neighbour names) with values (actual distances between capitals)
 * 
 *  void createEntriesForNameExceptions()
 *      - Similar to createStateNameEntries(), but for countries with non-obvious aliases
 * 
 *  void cleanData() 
 *      - Remove all neighbours in countriesGraph's values that have Integer.MAX_VALUE as distance between capitals
 * 
 */

import java.util.HashMap;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Set;
import java.util.LinkedList;
import java.util.Collection;
import java.io.*;

public class TextCleaner {
    public static void createStateNameEntries(FileReader stateNames, HashMap<String, String> nameDict, HashMap<String, HashMap<String, Integer>> countriesGraph) {
        BufferedReader reader;

        try{
            reader = new BufferedReader(stateNames);
            String line = reader.readLine();    //skip first line

            for (line = reader.readLine(); line != null; line = reader.readLine()) {
                //state_name.tsv == tab separated values
                String[] fieldVals = line.split("\t", 0);

                //if end date of state != 2020, don't add
                if (!fieldVals[4].contains("2020")) {
                    continue;
                }

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

                //added to country graph with no neighbours
                if (!countriesGraph.containsKey(repVal)) {
                    HashMap<String, Integer> neighbours = new HashMap<String, Integer>();
                    countriesGraph.put(repVal, neighbours);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createBorderNameEntries(FileReader borders, HashMap<String, String> nameDict, HashMap<String, HashMap<String, Integer>> countriesGraph) {
        //checks for strings with ()
        Pattern hasAlias = Pattern.compile("^([a-zA-Z\\-' ]+ \\([a-zA-Z ]+\\)) ([0-9,.]+) km$");
        //checks for strings without ()
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

                //add country names to hashmap if needed
                String countryKey = countryKeys.get(0);

                //get representative name
                if (nameDict.containsKey(countryKey)) {
                    countryKey = nameDict.get(countryKey);
                }

                for (String country : countryKeys) {
                    if (!nameDict.containsKey(country)) {
                        nameDict.put(country, countryKey);
                    }
                }

                if (!countriesGraph.containsKey(countryKey)) {
                    HashMap<String, Integer> neighbours = new HashMap<String, Integer>();
                    countriesGraph.put(countryKey, neighbours);
                }

                for (int i = 1; i < fieldVals.length; i++) {
                    if (fieldVals[i].strip().length() == 0) {
                        break;
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

                    //add country names to hashmap if needed
                    String neighbourKey = cAdd.get(0);

                    if (nameDict.containsKey(neighbourKey)) {
                        neighbourKey = nameDict.get(neighbourKey);
                    }

                    for (String country : cAdd) {
                        if (!nameDict.containsKey(country)) {
                            nameDict.put(country.toUpperCase(), neighbourKey);
                        }
                    }

                    if (!countriesGraph.containsKey(neighbourKey)) {
                        HashMap<String, Integer> neighbours = new HashMap<String, Integer>();
                        countriesGraph.put(neighbourKey, neighbours);
                    }

                    HashMap<String, Integer> country = countriesGraph.get(countryKey);
                    HashMap<String, Integer> neighbour = countriesGraph.get(neighbourKey);

                    //Add border indicator to neighbour hashmap of both countries
                    //Add all neighbours 
                    country.put(neighbourKey, Integer.MAX_VALUE);
                    neighbour.put(countryKey, Integer.MAX_VALUE);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void getStateNameDistances(FileReader capdist, HashMap<String, String> nameDict, HashMap<String, HashMap<String, Integer>> countriesGraph) {
        BufferedReader reader;

        try{
            reader = new BufferedReader(capdist);
            String line = reader.readLine();    //skip first line

            for (line = reader.readLine(); line != null; line = reader.readLine()) {
                //capdist.csv == comma separated values
                String[] fieldVals = line.split(",", 0);

                //uses country codes from state_name.tsv
                String countryKey = nameDict.get(fieldVals[1].toUpperCase());
                String neighbourKey = nameDict.get(fieldVals[3].toUpperCase());
                int distanceFromNeighbour = Integer.parseInt(fieldVals[4]);

                //If country A exists as a country and country B exists as its neighbour
                if (countriesGraph.containsKey(countryKey) && countriesGraph.get(countryKey).containsKey(neighbourKey)) {
                    countriesGraph.get(countryKey).replace(neighbourKey, distanceFromNeighbour);
                    countriesGraph.get(neighbourKey).replace(countryKey, distanceFromNeighbour);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //creates keys for states with difficult to detect aliases
    //create keys for country codes not in state_name.tsv
    public static void createEntriesForNameExceptions(HashMap<String, String> nameDict, HashMap<String, HashMap<String, Integer>> countriesGraph) {
        //First entry of alias list is the representative country code
        String[] DRCAliases = {"Congo, Democratic Republic of", "Congo, Democratic Republic of the", "Democratic Republic of the Congo", "DRC"};
        String[] PRKAliases = {"North Korea", "Korea, North", "Korea, People's Republic of", "PRK"};
        String[] ROKAliases = {"South Korea", "Korea, South", "Korea, Republic of", "KOR"};
        String[] USAAliases = {"United States of America", "United States", "US", "USA"};
        String[] DENAliases = {"Denmark", "Greenland", "DEN"};
        String[] BHMAliases = {"Bahamas", "Bahamas, The", "BHM"};
        String[] UKGAliases = {"United Kingdom", "UK", "UKG"};
        String[] DRVAliases = {"Vietnam, Democratic Republic of", "Vietnam", "DRV"};
        String[] GFRAliases = {"German Federal Republic", "Germany", "GFR"};

        ArrayList<String[]> exceptionsList = new ArrayList<String[]>();
        exceptionsList.add(DRCAliases);
        exceptionsList.add(PRKAliases);
        exceptionsList.add(ROKAliases);
        exceptionsList.add(USAAliases);
        exceptionsList.add(DENAliases);
        exceptionsList.add(BHMAliases);
        exceptionsList.add(UKGAliases);
        exceptionsList.add(DRVAliases);
        exceptionsList.add(GFRAliases);

        for (String[] aliasList : exceptionsList) {
            String repName = aliasList[0].toUpperCase();
            HashMap<String, Integer> neighbours = new HashMap<String, Integer>();
            countriesGraph.put(repName, neighbours);

            for (String val : aliasList) {
                nameDict.put(val.toUpperCase(), repName);
            }
        }
    }
    
    //Handle () aliases as well
    public static ArrayList<String> generateAliasEntries(String toTokenise) {
        ArrayList<String> toAdd = new ArrayList<String>();

        String[] alias = toTokenise.split("\\(|/", 0);

        for (String s : alias) {
            String sClean = s
                            .replaceAll("\\)", "")
                            .strip();
            toAdd.add(sClean.toUpperCase());
        }
        return toAdd;
    }

    public static void cleanData(HashMap<String, HashMap<String, Integer>> countriesGraph) {
        Set countryNames = countriesGraph.keySet();

        //gets name of country
        for (Object countryName : countryNames) {
            HashMap<String, Integer> validNeighbours = new HashMap<String, Integer>();
            HashMap<String, Integer> neighbours = countriesGraph.get(((String)countryName));

            if ((((String)countryName)).equals("DENMARK")) {
                countriesGraph.replace((String)countryName, validNeighbours);
            }

            Set neighbourNames = neighbours.keySet();

            //gets name of neighbours
            for (Object neighbourName : neighbourNames) {
                Integer distanceFromNeighbour = neighbours.get((String)neighbourName);

                //if default value was replaced after capdist.csv
                //ignore Denmark since it connects Canada to China
                if (distanceFromNeighbour != Integer.MAX_VALUE && !neighbourName.equals("DENMARK")) {
                    validNeighbours.put((String)neighbourName, distanceFromNeighbour);
                }
            }

            //have graph point at new hashmap with valid neighbours
            countriesGraph.replace((String)countryName, validNeighbours);
        }
    }

    public static void viewHashMap(HashMap<String, HashMap<String, Integer>> countriesGraph) {
        Set countryNames = countriesGraph.keySet();
        
        //gets name of country
        for (Object countryName : countryNames) {
            System.out.println("Country " + countryName + ":");
            HashMap<String, Integer> neighbours = countriesGraph.get(((String)countryName));
            Set neighbourNames = neighbours.keySet();

            //gets name of neighbours
            for (Object neighbourName : neighbourNames) {
                System.out.println("\tneighbourName[" + (String)neighbourName + "]: " + Integer.toString(neighbours.get((String)neighbourName)));
            }
        }
    }   
}