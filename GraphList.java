import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedHashSet;

public class GraphList {
    GraphNode headNode;

    public class GraphNode{
        String id;
        LinkedHashSet<GraphNode> edges;
        GraphNode next;
        public GraphNode(String label){
            this.id = label;
            this.edges = new LinkedHashSet<>();
            }
}
    public GraphList(){}

    public void addNode(String label){
        GraphNode node = new GraphNode(label);
        node.next = headNode;
        headNode = node;
        }
    public class Edge{
        GraphNode destination;
        Double time;
        String colour;

        public Edge(GraphNode destination, String colour,Double time){
            this.destination = destination;
            this.time = time;
            this.colour = colour;
        }
    }




    public void addEdge(String fromStation, String endStation, String colour, Double time){
        GraphNode startNode = findNode(fromStation);
        if (startNode == null){
            addNode(fromStation);
            startNode = headNode;
        }

        GraphNode endNode = findNode(endStation);
        if (endNode == null){
            addNode(endStation);
            endNode = headNode;
        }
    }


    private GraphNode findNode(String label){
        GraphNode node = headNode;
            while(node != null){
                if (node.id == label) return node;
                node = node.next;
            }return null;
        }
    public void print(){
        GraphNode node = headNode;
        while(node != null){
            System.out.print("[ " + node.id + " : ");
            for(GraphNode neighbor: node.edges){
                System.out.print(neighbor.id + " ");
            }
            System.out.println("]");
            node = node.next;
        }
    }
    public void loadFromFile(String filename){
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;

            while ((line = br.readLine()) != null){
                String[] section = line.split(",");

            }
        }
    }
}
