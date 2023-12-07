/**
 * 
 *  void setFiles()
 *      - Read files passed as arguments and cleans up data accordingly
 * 
 *  int getDistance()
 *      - Gets distance between two countries
 * 
 *  List<String> findPath()
 *      - Gets path between source country and destination country
 */

import java.io.IOException;
import java.util.Scanner;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.List;
import java.util.LinkedList;
import java.util.Collection;
import java.io.*;

public class IRoadTrip {
    protected HashMap<String, String> nameDict;
    protected HashMap<String, HashMap<String, Integer>> countriesGraph;
    private String bordersfd;
    private String capdistfd;
    private String stateNamesfd;

    public IRoadTrip (String [] args) throws IOException {
        this.bordersfd = args[0];
        this.capdistfd = args[1];
        this.stateNamesfd = args[2];

        setFiles();
    }

    public void setFiles() {
        try {
            FileReader borders = new FileReader(bordersfd);
            FileReader capdist = new FileReader(capdistfd);
            FileReader stateNames = new FileReader(stateNamesfd);

            nameDict = new HashMap<String, String>();
            countriesGraph = new HashMap<String, HashMap<String, Integer>>();
            
            TextCleaner.createEntriesForNameExceptions(nameDict, countriesGraph);
            TextCleaner.createStateNameEntries(stateNames, nameDict, countriesGraph);
            TextCleaner.createBorderNameEntries(borders, nameDict, countriesGraph);
            TextCleaner.getStateNameDistances(capdist, nameDict, countriesGraph);
            TextCleaner.cleanData(countriesGraph);
        } catch (FileNotFoundException e) {
            System.out.println(e + "\nHalting execution...");
            System.exit(-1);
        }
    }

    public int getDistance (String country1, String country2) {
        String source = nameDict.get(country1.toUpperCase());
        String destination = nameDict.get(country2.toUpperCase());

        HashMap<String, Integer> neighbours = countriesGraph.get(source);

        if (neighbours == null || !neighbours.containsKey(destination)) {
            return -1;
        } else {
            return neighbours.get(destination);
        }
    }

    public List<String> findPath (String country1, String country2) {
        PriorityQueue<Node> path = new PriorityQueue<Node>();
        HashMap<String, Integer> finalDistances = new HashMap<String, Integer>();
        LinkedList<String> visitedEdges = new LinkedList<String>();
        HashMap<String, String> countryPairs = new HashMap<String, String>();
        
        //initial min heap
        Node source = new Node(country1, 0);
        path.add(source);

        while (!path.isEmpty()) {
            Node current = path.poll();
            String currentName = current.getCountry();

            if (!finalDistances.containsKey(currentName)) {
                finalDistances.put(currentName, current.getDistance());
                countryPairs.put(currentName, current.getPrevCountry());
            } else {
                continue;
            }
            
            //gets set of neighbour keys
            Set neighbours = countriesGraph.get(currentName).keySet();

            for (Object neighbour : neighbours) {
                String neighbourName = (String)neighbour;

                //should always be >= 0 since we are looking at adjacent countries
                int distanceFromNeighbour = getDistance(currentName, neighbourName);

                //since current node already contains distance from source
                int distanceFromSource = distanceFromNeighbour + current.getDistance();

                if (finalDistances.get(neighbourName) == null || distanceFromSource < finalDistances.get(neighbourName)) {
                    Node neighbourCountry = new Node(neighbourName, distanceFromSource);
                    neighbourCountry.setPrevCountry(currentName);
                    
                    //Used to check if edge has been visited before, unique edge code by string concatenation
                    String cnEdgeStr = currentName + neighbourName;
                    String ncEdgeStr = neighbourName + currentName;

                    if (!visitedEdges.contains(cnEdgeStr) && !visitedEdges.contains(ncEdgeStr)) {
                        visitedEdges.addFirst(cnEdgeStr);
                        path.add(neighbourCountry);
                    }
                }
            } 
        }

        if (!finalDistances.containsKey(country2)) {
            return null;
        }

        //we can constantly insert at start
        LinkedList<String> toReturn = new LinkedList<String>();
        String toSearch = country2;

        while (toSearch != country1) {
            String prevCountry = countryPairs.get(toSearch);
            int dist = countriesGraph.get(toSearch).get(prevCountry);
            
            String toAdd = "";
            toAdd = "* " + prevCountry + " --> " + toSearch + " (" +
                    Integer.toString(dist) + " km.)";
            
            toReturn.addFirst(toAdd);
            
            toSearch = prevCountry;
        }
        return toReturn;

    }

    public void acceptUserInput() {
        Scanner scan = new Scanner(System.in);

        while (true) {
            System.out.print("Enter the name of the first country (type EXIT to quit): ");
            String input1 = scan.nextLine().toUpperCase();

            if (input1.equals("EXIT")) {
                System.out.println("Thank you for using our services!");
                System.exit(0);
            }

            while (!nameDict.containsKey(input1)) {
                System.out.println("Invalid country name. Please enter a valid country name.");
                System.out.print("Enter the name of the first country (type EXIT to quit): ");
                input1 = scan.nextLine().toUpperCase();

                if (input1.equals("EXIT")) {
                    System.out.println("Thank you for using our services!");
                    System.exit(0);
                }
            }

            System.out.print("Enter the name of the second country (type EXIT to quit): ");
            String input2 = scan.nextLine().toUpperCase();

            if (input2.equals("EXIT")) {
                System.out.println("Thank you for using our services!");
                System.exit(0);
            }

            while (!nameDict.containsKey(input2)) {
                System.out.println("Invalid country name. Please enter a valid country name.");
                System.out.print("Enter the name of the second country (type EXIT to quit): ");
                input2 = scan.nextLine().toUpperCase();

                if (input2.toUpperCase().equals("EXIT")) {
                    System.out.println("Thank you for using our services!");
                    System.exit(0);
                }
            }

            String country1 = nameDict.get(input1);
            String country2 = nameDict.get(input2);

            List<String> path = findPath(country1, country2);

            if (path == null) {
                System.out.println("No path exists");
            } else {
                for (String text : path) {
                    System.out.println(text);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 3) {

            IRoadTrip a3 = new IRoadTrip(args);
            a3.acceptUserInput();

        } else {
            System.out.println("Invalid input");
        }
    }
}