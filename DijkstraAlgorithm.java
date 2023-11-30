import java.util.PriorityQueue;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Deque;
import java.util.Set;
import java.util.Collection;

public class DijkstraAlgorithm {
    private HashMap<String, Country> countriesGraph;
    private Country source;
    private Country destination;

    public DijkstraAlgorithm(HashMap<String, Country> countriesGraph, Country source, Country destination) {
        this.countriesGraph = countriesGraph;
        this.source = source;
        this.destination = destination;
    }

    //Use BFS to add to countryHeap
    public HashMap<Country, Integer> runAlgorithm() {
        PriorityQueue<Country> path = new PriorityQueue<Country>();
        HashMap<Country, Integer> finalDistances = new HashMap<Country, Integer>();
        HashMap<String, String> visitedEdges = new HashMap<String, String>();

        source.setDistanceFromSource(0);
        path.add(source);

        while (!path.isEmpty()) {
            Country c = path.poll();

            //System.out.println("Removing: " + c.getRepName() + " of distance " + c.getDistanceFromSource());

            if (!finalDistances.containsKey(c)) {
                finalDistances.put(c, c.getDistanceFromSource());
            } else {
                //finalised, won't get any smaller value
                continue;
            }

            Collection neighbours = c.getNeighbours().keySet();

            for (Object neighbour : neighbours) {
                Country n = countriesGraph.get((String)neighbour);
                int distanceFromHead = n.getNeighbours().get(c.getRepName());
                int distanceFromSource = distanceFromHead + c.getDistanceFromSource();
                n.setDistanceFromSource(distanceFromSource);

                //String concatenation as key value pair
                String cnEdgeStr = c.getRepName() + n.getRepName();
                String ncEdgeStr = n.getRepName() + c.getRepName();
                if (!visitedEdges.containsKey(cnEdgeStr) && !visitedEdges.containsValue(ncEdgeStr)) {
                    visitedEdges.put(cnEdgeStr, ncEdgeStr);
                    path.add(n);
                }
            }
        }

        //no path exists
        if (destination.getDistanceFromSource() == Integer.MAX_VALUE) {
            return null;
        } else {
            return finalDistances;
        }
    }
}