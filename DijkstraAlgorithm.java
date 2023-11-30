import java.util.PriorityQueue;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Set;
import java.util.Collection;

public class DijkstraAlgorithm {
    private HashMap<String, Country> countriesGraph;
    private Country source;
    private Country destination;
    private HashMap<String, String> countriesVisited;

    public DijkstraAlgorithm(HashMap<String, Country> countriesGraph, Country source, Country destination) {
        this.countriesGraph = countriesGraph;
        this.source = source;
        this.destination = destination;
    }

    //Use BFS to add to countryHeap
    public int runAlgorithm() {
        PriorityQueue<Country> path = new PriorityQueue<Country>();
        HashMap<Country, Integer> finalDistances = new HashMap<Country, Integer>();
        HashMap<String, String> visitedEdges = new HashMap<String, String>();
        countriesVisited = new HashMap<String, String>();

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

                    //Never visited this edge before
                    if (!visitedEdges.containsKey(cnEdgeStr) && !visitedEdges.containsKey(ncEdgeStr)) {
                        visitedEdges.put(cnEdgeStr, ncEdgeStr);
                        path.add(n);
                    }
                }
            }
        }

        //no path exists
        if (destination.getDistanceFromSource() == Integer.MAX_VALUE) {
            return -1;
        } else {
            return finalDistances.get(destination);
        }
    }

    public LinkedList<String> getPath() {
        //we can constantly insert at start
        LinkedList<String> toReturn = new LinkedList<String>();
        String toSearch = destination.getRepName();

        while (toSearch != source.getRepName()) {
            String prevCountry = countriesVisited.get(toSearch);
            int distance = countriesGraph.get(toSearch).getNeighbours().get(prevCountry);

            
            String toAdd = "";
            toAdd = "* " + prevCountry + " --> " + toSearch + " (" +
                    Integer.toString(distance) + " km.)\n";
            
            toReturn.addFirst(toAdd);
            
            toSearch = prevCountry;
        }

        return toReturn;
    }
}