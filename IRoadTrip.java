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

    public IRoadTrip (String [] args) throws IOException {

        try {
            FileReader borders = new FileReader(args[0]);
            FileReader capdist = new FileReader(args[1]);
            FileReader stateNames = new FileReader(args[2]);

            nameDict = new HashMap<String, String>();
            countriesGraph = new HashMap<String, Country>();
            
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
        Country source = countriesGraph.get(country1);
        Country destination = countriesGraph.get(country2);

        DijkstraAlgorithm dA = new DijkstraAlgorithm(countriesGraph, source, destination);
        int distance = dA.runAlgorithm();

        return distance;
    }

    public List<String> findPath (String country1, String country2) {
        Country source = countriesGraph.get(country1);
        Country destination = countriesGraph.get(country2);

        DijkstraAlgorithm dA = new DijkstraAlgorithm(countriesGraph, source, destination);
        int distance = dA.runAlgorithm();

        if (distance < 0) {
            return null;
        }

        List<String> toReturn = dA.getPath();

        return toReturn;
    }

    public void acceptUserInput() {
        Scanner scan = new Scanner(System.in);
        String userInput = "";

        while (true) {
            System.out.print("Choice: ");
            userInput = scan.nextLine();

            if (userInput.toUpperCase().equals("EXIT")) {
                break;
            }

            while (!userInput.equals("1") && !userInput.equals("2")) {
                System.out.println("Invalid input. Choose an option between 1-2: ");
                userInput = scan.nextLine();
            }

            System.out.println("Please enter first country: ");
            String input1 = scan.nextLine();

            while (!nameDict.containsKey(input1)) {
                System.out.println("Invalid country name. Please enter a valid country name.");
                input1 = scan.nextLine();
            }

            String country1 = nameDict.get(input1);

            System.out.println("Please enter second country: ");
            String input2 = scan.nextLine();

            while (!nameDict.containsKey(input2)) {
                System.out.println("Invalid country name. Please enter a valid country name.");
                input2 = scan.nextLine();
            }

            String country2 = nameDict.get(input2);

            if (userInput.equals("1")) {
                int distance = getDistance(country1, country2);

                System.out.println(distance);
            } else {    //userInput == 2
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
    }


    public void printMenu() {
        System.out.println("Please choose from the following options: ");
        System.out.println("1: Get distance between two countries");
        System.out.println("2: Get path between two countries");
        System.out.println("EXIT: Exit Program");
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 3) {

            IRoadTrip a3 = new IRoadTrip(args);

            a3.printMenu();
            a3.acceptUserInput();

        } else {
            System.out.println("Invalid input");
        }
    }
}