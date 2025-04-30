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
    
}
