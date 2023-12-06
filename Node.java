public class Node implements Comparable<Node> {
    private String country;
    private int distance;
    private String prevCountry;

    public Node(String country, int distance) {
        this.country = country;
        this.distance = distance;
        this.prevCountry = null;
    }

    public String getCountry() {
        return this.country;
    }

    public int getDistance() {
        return this.distance;
    }

    public String getPrevCountry() {
        return this.prevCountry;
    }

    public void setPrevCountry(String prevCountry) {
        this.prevCountry = prevCountry;
    }


    @Override
    public int compareTo(Node e) {
        return this.distance - e.getDistance();
    }
}