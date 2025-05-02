import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
public class GraphList {
    GraphNode headNode;

    public class GraphNode{
        String id;
        LinkedHashSet<Edge> edges;
        GraphNode next;
        public GraphNode(String station){
            this.id = station;
            this.edges = new LinkedHashSet<>();
            
            }
    }
    public GraphList(){}

    public void addNode(String station){
        GraphNode node = new GraphNode(station);
        node.next = headNode;
        headNode = node;
    }

    public class Edge{
        GraphNode destination;
        Float time;
        String colour;

        public Edge(GraphNode destination, String colour,Float time){
            this.destination = destination;
            this.time = time;
            this.colour = colour;
        }
    }

    public void addEdge(String fromStation, String toStation, String colour, Float time){
        GraphNode startNode = findNode(fromStation);
        GraphNode endNode = findNode(toStation);
        if (startNode != null && endNode != null){
            addNode(fromStation);
            startNode = headNode;
            startNode.edges.add(new Edge(startNode, colour, time));
            addNode(toStation);
            endNode = headNode;
            endNode.edges.add(new Edge(endNode, colour, time));
        }

        
        
        
    }
    
    private GraphNode findNode(String station){
        GraphNode node = headNode;
            while(node != null){
                if (node.id.equals(station)) return node;
                node = node.next;
            }return null;
        }

    
    public void print(){
        GraphNode station = headNode;
        while(station!= null){
            System.out.print("[ " + station.id + " , "+ station.next.id + " , ");
            for(Edge info: station.edges){
                System.out.print( info.colour + " , " + info.time);
            }
            System.out.println(" ]");
            station = station.next;
        }
    }
    public void loadFromFile(String filename){
        try(BufferedReader reader = new BufferedReader(new FileReader(filename))){
            String line;
            line = reader.readLine();//skips the header
            while ((line = reader.readLine()) != null){
                String[] section = line.split(",");
                

                String from = section[0].trim();
                String to = section[1].trim();
                String colour = section[2].trim();
                Float time = Float.parseFloat(section[3].trim());
                System.out.println("Adding station: " + from); //syntax
                System.out.println("Adding station: " + to); //syntax
                
                addNode(from);
                addNode(to);
                addEdge(from, to, colour, time);
                addEdge(to, from, colour, time); //allows graph to be undirected 

            }
        }catch (IOException END){
            System.err.println("Error reading file:" + END.getMessage());
        }
    }
    
    public boolean stationExists(String station){
        boolean exists = true;
        if(findNode(station).equals(null))exists = false;
        
        return exists;
    }

    //public GraphNode[] getNeighbours(String station){
        //GraphNode node = findNode(station);
        //GraphNode[] neighbours = node.toArray(new GraphNode[0]);
        //return neighbours;
        //}
        
        //return neighbours;
    
    
    
    public Edge findEdge(String from, String to){
        GraphNode fromNode = findNode(from);
        if(fromNode == null)return null;

        for(Edge edge: fromNode.edges){
            if (edge.destination.id.equals(to))return edge;
            }
        return null;
    }
    //public void neighbourList(String station){
       // GraphNode[] list = getNeighbours(station);
       // for (int i = 1; i<10;i++){
       // System.out.println("Neighbour:" + list[i]);}
        
    //}
    
    public List<String> Dijkstas(String startStation, String endStation, Float time){
        Map<GraphNode,Float> distance = new HashMap<>();
        Map<GraphNode,GraphNode> previous = new HashMap<>();
        Set<GraphNode> visited = new HashSet<>();
        PriorityQueue<GraphNode> queue = new PriorityQueue<>(Comparator.comparing(distance::get));
        
        GraphNode startNode = findNode(startStation);
        GraphNode endNode = findNode(endStation);

        GraphNode node = headNode;
        while(node != null){
            distance.put(node, Float.MAX_VALUE);
            node = node.next;
        }
        distance.put(startNode, 0f);
        queue.add(startNode);

        while(!queue.isEmpty()){
            GraphNode currentNode = queue.poll();
            
            if(!visited.add(currentNode))continue;
            if(currentNode.equals(endNode))break;

            for(Edge edge : currentNode.edges){
                GraphNode neighbour = edge.destination;
                float totalDist = distance.get(currentNode) + edge.time;
                if(totalDist < distance.get(neighbour)){
                    distance.put(neighbour, totalDist);
                    previous.put(neighbour, currentNode);
                    queue.add(neighbour);
                }
            }
        }
        List<String> path = new LinkedList<>();
        for (GraphNode at = endNode; at != null; at = previous.get(at)){
            path.add(0, at.id);
        }
        if (path.isEmpty() || !path.get(0).equals(startStation)){
            System.out.println("No path found");
            return Collections.emptyList();
        }
        return path;
    }
}
