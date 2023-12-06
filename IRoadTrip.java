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
import java.util.List;
import java.util.LinkedList;
import java.util.Collection;
import java.io.*;

public class IRoadTrip {
    protected HashMap<String, String> nameDict;
    protected HashMap<String, Country> countriesGraph;
    private HashMap<String, String> countriesVisited;
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
            countriesGraph = new HashMap<String, Country>();
            
            TextCleaner.createEntriesForNameExceptions(nameDict, countriesGraph);
            TextCleaner.createStateNameEntries(stateNames, nameDict, countriesGraph);
            TextCleaner.createBorderNameEntries(borders, nameDict, countriesGraph);
            TextCleaner.getStateNameDistances(capdist, nameDict, countriesGraph);
            TextCleaner.cleanData(countriesGraph);

            borders = new FileReader(bordersfd);
            TextCleaner.getValidCountriesWithoutCodes(borders, nameDict);
            //TextCleaner.viewHashMap(nameDict);
        } catch (FileNotFoundException e) {
            System.out.println(e + "\nHalting execution...");
            System.exit(-1);
        }
    }

    public int getDistance (String country1, String country2) {
        PriorityQueue<Country> path = new PriorityQueue<Country>();
        HashMap<Country, Integer> finalDistances = new HashMap<Country, Integer>();
        HashMap<String, Country[]> visitedEdges = new HashMap<String, Country[]>();
        countriesVisited = new HashMap<String, String>();

        Country source = countriesGraph.get(country1);
        Country destination = countriesGraph.get(country2);

        source.setDistanceFromSource(0);
        source.setPrevCountry(source);
        path.add(source);

        while (!path.isEmpty()) {
            Country c = path.poll();

            if (!finalDistances.containsKey(c)) {
                finalDistances.put(c, c.getDistanceFromSource());
                countriesVisited.put(c.getRepName(), c.getPrevCountry().getRepName());
            } else {
                continue;
            }

            Collection neighbours = c.getNeighbours().keySet();

            for (Object neighbour : neighbours) {
                Country n = countriesGraph.get((String)neighbour);
                int distanceFromHead = n.getNeighbours().get(c.getRepName());
                int distanceFromSource = distanceFromHead + c.getDistanceFromSource();

                //Only add to min heap if this route is faster
                if (distanceFromSource < n.getDistanceFromSource()) {
                    n.setPrevCountry(c);
                    n.setDistanceFromSource(distanceFromSource);

                    //String concatenation as key value pair
                    String cnEdgeStr = c.getRepName() + n.getRepName();
                    String ncEdgeStr = n.getRepName() + c.getRepName();

                    Country[] countryPair = {c, n};

                    //Never visited this edge before
                    if (!visitedEdges.containsKey(cnEdgeStr) && !visitedEdges.containsKey(ncEdgeStr)) {
                        visitedEdges.put(cnEdgeStr, countryPair);
                        path.add(n);
                    }
                }
            }
        }
        
        int travelDist = 0;

        //no path exists
        if (destination.getDistanceFromSource() == Integer.MAX_VALUE) {
            travelDist = -1;
        } else {
            travelDist = finalDistances.get(destination);
        }

        //Restore countriesGraph back to default
        Collection<Country[]> visitedKeys = visitedEdges.values();

        for (Country[] visited : visitedKeys) {
            TextCleaner.restoreDefault(visited);
        }

        return travelDist;
    }

    public List<String> findPath (String country1, String country2) {
        Country source = countriesGraph.get(country1);
        Country destination = countriesGraph.get(country2);

        //if not a country with distances
        if (!countriesGraph.containsKey(country1) || !countriesGraph.containsKey(country2)) {
            return null;
        }

        int distance = getDistance(country1, country2);

        if (distance < 0) {
            return null;
        }

        //we can constantly insert at start
        LinkedList<String> toReturn = new LinkedList<String>();
        String toSearch = destination.getRepName();

        while (toSearch != source.getRepName()) {
            String prevCountry = countriesVisited.get(toSearch);
            int dist = countriesGraph.get(toSearch).getNeighbours().get(prevCountry);

            
            //countriesGraph.get(toSearch).setDistanceFromSource(Integer.MAX_VALUE);
            
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
            setFiles();
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