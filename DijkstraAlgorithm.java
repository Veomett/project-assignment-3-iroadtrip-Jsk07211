import java.util.PriorityQueue;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Deque;
import java.util.Set;
import java.util.Iterator;

public class DijkstraAlgorithm {
    private HashMap<String, Country> countriesGraph;
    private Country source;
    private Country destination;

    public DijkstraAlgorithm(HashMap<String, Country> countriesGraph, String sourceStr, String destinationStr) {
        this.countriesGraph = countriesGraph;
        this.source = countriesGraph.get(sourceStr);
        this.destination = countriesGraph.get(destinationStr);
    }

    //Use BFS to add to countryHeap
    public int createCountryHeap() {
        PriorityQueue<Country> countryHeap = new PriorityQueue<Country>();
        Deque<Set> toAdd = new LinkedList<Set>();
        Iterator iterate;

        
        countryHeap.add(source);

        toAdd.add(source.getNeighbours().keySet());

        while (!toAdd.isEmpty()) {
            Set neighbour = toAdd.poll();

            if (!neighbour.isEmpty()) {
                iterate = neighbour.iterator();

                while (iterate.hasNext()) {
                    Country c = countriesGraph.get(iterate.next());

                    if (!countryHeap.contains(c)) {
                        countryHeap.add(c);
                    } else {
                        //no need to add neighbours because we've already added it
                        continue;
                    }

                    if (!c.getNeighbours().isEmpty()) {
                        toAdd.add(c.getNeighbours().keySet());
                    }
                }
            } 
        }
        if (!countryHeap.contains(destination)) {
            return -1;
        }
        return 0;
    }
}