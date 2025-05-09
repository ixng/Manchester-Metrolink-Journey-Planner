import java.util.*;


public class Node {
    private final String station;
    private Float distance = Float.MAX_VALUE;
    private List<Node> shortestPath = new LinkedList<>();
    private Map<Node,Float> adjacentNodes = new HashMap<>();


    public void addAdjacentNode(Node station, Float weight){
        adjacentNodes.put(station, weight);
    }
    @Override
    public int compareTo(Node station){
        return Float.compare(this.distance, station.getDistance());
    }
}
