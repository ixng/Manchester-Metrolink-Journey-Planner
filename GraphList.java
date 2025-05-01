import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashSet;

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
        if (startNode == null && endNode == null){
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
                if (node.id == station) return node;
                node = node.next;
            }return null;
        }

    public boolean stationExists(String station){
        boolean exists = true;
        if(findNode(station) == null)
        exists = false;
        return exists;
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
                //System.out.println("Adding station: " + from); syntax
                //System.out.println("Adding station: " + to); syntax
                
                addNode(from);
                addNode(to);
                addEdge(from, to, colour, time);
                addEdge(to, from, colour, time); //allows graph to be undirected 

            }
        }catch (IOException END){
            System.err.println("Error reading file:" + END.getMessage());
        }
    }
}
