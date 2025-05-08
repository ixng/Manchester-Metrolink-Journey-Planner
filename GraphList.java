import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
public class GraphList {
    private GraphNode headNode;
    private GraphNode[] verticies;
    private int numOfVertices;
    private int maxNum;
    private String[] coloursOfVertex;
    private List<String> colours = new ArrayList<>();
    private GraphNode parent; //Pointer to parent Node
    public class GraphNode implements Comparable<GraphNode>{
        
        String id;
        LinkedHashSet<Edge> edges;
        GraphNode next;
        Float distance = Float.MAX_VALUE;
        List<GraphNode> shortestPath = new LinkedList<>();
        Map<GraphNode, Float> adjacentNodes = new HashMap<>();
        
        public GraphNode(String station){
            this.id = station;
            this.edges = new LinkedHashSet<>();
            
            }
        
        public void addAdjacentNode(GraphNode node, Float distance){
                adjacentNodes.put(node, distance);
            }
        @Override
        public int compareTo(GraphNode endOfEdge){
                return Float.compare(endOfEdge.distance, this.distance);
            }
    }
    public GraphList(){
        //intialistaion
        int maximum = 10000000;
        maxNum = maximum;
        verticies = new GraphNode[maxNum];
        numOfVertices = 0;
        coloursOfVertex = new String[maxNum];
    }
    
    public void addNode(String station){
        GraphNode node = new GraphNode(station);
        node.next = headNode;
        headNode = node;
    }
    public void addColour(String colour){
        colours.add(colour);

    }
    public class Edge implements Comparator<Edge>,  Comparable<Edge>{
        GraphNode destination;
        Float time;
        String colour;
        int toIndex;
        private Edge(GraphNode destination, String colour,Float time, int toIndex){
            this.destination = destination;
            this.time = time;
            this.colour = colour;
            this.toIndex = toIndex;
        }

        @Override
        public int compare(Edge e1, Edge e2){
            if(e1.time < e2.time)return -1;
            if(e2.time < e1.time)return 1;
            return 0;
        }

        @Override
        public int compareTo(Edge edge){
            if(this.time < edge.time)return -1;
            if(this.time > edge.time)return 1;
            return 0;
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
            if(stationExists(toStation) == true)
        }
        
    }

    private GraphNode findNode(String station){
        GraphNode node = headNode;
            while(node != null){
                if (node.id.equals(station)) return node;
                node = node.next;
            }return null;
    }
    private String findColour(String colour){
        
        for(String lines : colours){
            if(lines.equals(colour))return colour;
         }
         return null;   
    }
    public boolean stationExists(String station){
        boolean exists = true;
        if(findNode(station) == null){exists = false;};
            
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
        
        try{
            List<String[]> dataRows = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new FileReader(filename))){
            String line;
            line = reader.readLine(); //skips the header
            while ((line = reader.readLine()) != null){
                String[] section = line.split(",");
                dataRows.add(section);
            
        }   
    }
            
            
            String [][] matrix = dataRows.toArray(new String[0][]);
            

            for(String[] row : matrix){
                
                
                
                
                String from = row[0].trim();
                String to = row[1].trim();
                String colour = row[2].trim();
                Float time = Float.parseFloat(row[3].trim());
                if(stationExists(from) == false || stationExists(to) == false){
                    numOfVertices++;}
                
                
                
                System.out.println("Adding station: " + from); 
                System.out.println("Adding station: " + to); 
                addColour(colour);
                addNode(from);
                addNode(to);
                addEdge(from, to, colour, time);
                addEdge(to, from, colour, time); //allows graph to be undirected 
                verticies[numOfVertices] = findNode(from);
                coloursOfVertex[numOfVertices] = findColour(colour);
                System.out.println(coloursOfVertex[numOfVertices]); //syntax
                System.out.println(verticies[numOfVertices].id); //syntax
                
                System.out.println(numOfVertices);//syntax
                
            }
        }catch (IOException END){
            System.err.println("Error reading file:" + END.getMessage());
        }
    }
    
    private void evaluateDistanceAndPath(GraphNode adjacentNode, Float time, GraphNode startNode){
        Float newdistance = startNode.distance + time;
        if ( newdistance < adjacentNode.distance){
            adjacentNode.distance = newdistance;
            List<GraphNode> shortestPath = new ArrayList<>();

            if (startNode.shortestPath.equals(null)){
                shortestPath.addAll(startNode.shortestPath);
            }

            shortestPath.add(startNode);
            adjacentNode.shortestPath = shortestPath;
        }
    }

    //public GraphNode[] getNeighbours(String station){
        //GraphNode node = findNode(station);
        //GraphNode[] neighbours = node.toArray(new GraphNode[0]);
        //return neighbours;
        //}
        
        //return neighbours;

    public void calculateShortestPath(GraphNode start){
        start.distance = 0f;
        Set<GraphNode> settledNodes = new HashSet<>();
        Queue<GraphNode> unsettledNodes = new PriorityQueue<>(Collections.singleton(start));
        
        while (!unsettledNodes.isEmpty()){
            GraphNode currentNode = unsettledNodes.poll();
            for (Edge edge : currentNode.edges){
                GraphNode adjacentNode = edge.destination;
                Float time = edge.time;

                if(!settledNodes.contains(adjacentNode)){
                    evaluateDistanceAndPath(adjacentNode, time, start);
                    unsettledNodes.add(adjacentNode);
                }
            }
            settledNodes.add(currentNode);
            }
        }

    
    public void printSolution(int dist[]){
        System.out.println("Node \t\t\t Distance from Start Station");
        for(int i = 0; i<numOfVertices; i++){
            System.out.println(verticies[i].id + "\t\t\t " + dist[i]);
        }
    }
    
    private int indexOf(GraphNode station){

        for(int i = 0; i < numOfVertices; i++){
            if(verticies[i] == station ){
                return i; //returns the index of the vertex with the same id
            }
        }
        return -1;  // returning -1 means the station is not in the array
    }
    private int toIndex(String station, Collection<Edge> edges){
        findEdge(station, edges);
    }
    
    
    
    public void DijkstrasAlgorithm(GraphNode start){
            int[] pathCost = new int[numOfVertices];
            boolean[] finalisedSet = new boolean[numOfVertices];
            int numFinalised = 0;
            GraphNode startStation = start;
            int station = indexOf(start);
            if(station == -1)return;
            for(int i = 0; i<numOfVertices; i ++){
                finalisedSet[i]= false;
                pathCost[i]= Integer.MAX_VALUE;
            }
            
            PriorityQueue<Edge> foo = new PriorityQueue<Edge>();
            for(String colour : coloursOfVertex){
                
                foo.add(new Edge(startStation, colour,0f));}
            pathCost[station] = 0;
            headNode = null;
            
            while(numFinalised < numOfVertices){
                if(foo.isEmpty())break;
                //Processing the current vertex
                Edge r = foo.poll();
                int index = r.destination;
                Edge v = verticies[index];
                if(finalisedSet[index])continue;
                finalisedSet[index] = true;
                numFinalised++;

                //Process its one-hop neigbours
                Iterator<Edge> itr = v.edges.iterator();
                while(itr.hasNext()){
                    Edge temp = itr.next();
                    int j = temp.toIndex;
                    if(!finalisedSet[j]){
                        int costEstimate = 
                    }
                }
            }
        }   
    
    }

         
            
         

