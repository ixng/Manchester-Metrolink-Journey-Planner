import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Graph {
    
    private Station[] stations;   //array for the stations on the map
    private int numOfStations;  //count for the number of stations 
    private int maxNumber;
    PriorityQueue<Edge> dijkstraPQ;  //Priority queue for Dijkstras algo
    private double[] distance;
    private boolean[] visited;      //set for finalised stations in dj algo
    private int[] previousStation;  //for tracking the path    
    public LinkedHashMap<Station,HashMap<Station,String>> colours = new LinkedHashMap<>();
    public Station parent;
    
    public class Station{
        Station next;
        private String id;          //id for the station
        private LinkedHashSet<Edge> edges;  //adjacency list
        //private Maps<Station,Integer> adjacentStations = new LinkedHashMap<>();
        //public Station parent;
        //public LinkedHashMap<Station,Set<String>> colours = new LinkedHashMap<>(); //colours list (for each station it has this colour)
        private Station(String id){
            this.id = id;
            this.edges = new LinkedHashSet<>();
            }
        public String getStationID(){
            return id;
        }
    }

    private class Edge implements Comparable<Edge>{
        //This class implements a Comparable so that it can be added to the priority queue
        private int fromIndex;
        private int toIndex; //index of destination
        private double time;  // time between stations
        private String lineColour;
        private Edge(int fromIndex,int toIndex, double time, String lineColour){
            this.fromIndex = fromIndex;
            this.toIndex = toIndex;
            this.time = time;
            this.lineColour = lineColour;
        }
        @Override
        public int compareTo(Edge edge){
            return Double.compare(this.time,edge.time);
        }
            
        }

        

    

    public Graph(int maximum){
        //Initialisation
        maxNumber = maximum;
        stations = new Station[maxNumber];
        numOfStations = 0;
        dijkstraPQ = new PriorityQueue<>();
        
    }
    //------------------------------------------------Helper Methods------------------------------------------------//

    public boolean stationExists(String station){
        return indexOf(station) != -1;
    }

    private int indexOf(String station){
        for(int i = 0; i < numOfStations; i++){
            if((stations[i] != null) && (stations[i].id == station))return i; //returning the index of the building with the same id
        }
        
        return -1; //returning -1 if the station is not in the array
    }

    public void printGraph(){
        System.out.println("Network Graph: ");
        for(int i = 0; i < numOfStations; i++){
            if(stations[i] != null){
                System.out.print(stations[i].id + ": ");  //adding the id of the start station to the string
                boolean firstEdge = true;
                    for(Edge e : stations[i].edges){       //Traversing through the linked list of edges
                        if(!firstEdge){System.out.print(", ");}
                        else{firstEdge = false;};
                        System.out.print(stations[e.toIndex].id + " ( " + e.lineColour + " , " + e.time + " mins )");
                          //adding the colour of the edge to the string
                        }
                        System.out.println();
                    }
                    
                    
                }
                
            }
        
    

    //method to print the shortest length between the start Station and the end Station
    //method to print path, given the returned list of strings from the shortest path method
    
    public final void shortestPathPrint(String start, String end){
        List<String> path = findShortestPath(start,end);
        if(path.isEmpty()){
            System.out.println("No path exists between " + start + " and " + end);
            return;
    }
        double totalTime = shortestLength(start, end);

        System.out.println("Shortest path from " + start + " to " + end + " ( " + totalTime + " mins): ");
        for(int i = 0; i < path.size(); i++){
            System.out.print(path.get(i));
            if( i < path.size() -1)System.out.print(" -> ");
        }
        System.out.println();
    }
    //method to print path, given the returned list of strings from the shortest path method
    

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
            System.out.println("Number of Edges read: " + dataRows.size() + "| From: " + filename);  //syntax
            
            for(String[] row : dataRows){
                
                String from = row[0].trim();
                String to = row[1].trim();
                String colour = row[2].trim();
                Double time = Double.parseDouble(row[3].trim());
                //add stations
                addStation(from);
                addStation(to);
                //add connection between lines
                addLine(from, to, time, colour);
                
                
            }
            System.out.println("Number of loaded Stations " + numOfStations + " stations");
            System.out.println("Number of loaded Connections " + dataRows.size() + " connections");
        }catch (IOException END){
            System.err.println("Error reading file:" + END.getMessage());
        }
    }
    //------------------------------------------------End of Helper Methods------------------------------------------------//

    public boolean addStation(String station){

        if(stationExists(station))return false;

        stations[numOfStations] = new Station(station);  //Adding new station object with the id as the Station
        numOfStations++;
        return true; 
    }

    public boolean addLine(String fromStation, String toStation, double timeBetween, String Colour){
        int i = indexOf(fromStation);          //finding the index of the start station
        int j = indexOf(toStation);            //finding the index of the end Station

        if(i == j)return false;                //if both stations are the same then no reason to add the line 

        if(i == -1){                          //case if start station doesnt exist on the map
            addStation(fromStation);      
            i = indexOf(fromStation);
        }
        if(j == -1){
            addStation(toStation);
            j = indexOf(toStation);
        }

        for(Edge e : stations[i].edges){     //Check if edge already exists
            if(e.toIndex == j)return false;
        }
        //Makes graph undirected 
        stations[i].edges.add(new Edge(i, j,timeBetween, Colour));
        stations[j].edges.add(new Edge(j, i, timeBetween, Colour));
        return true;

    }
    //method to find the shortest length between two stations
    public double shortestLength(String start, String end){

        //finding the indicies of the start and the end 
        int startIndex = indexOf(start);
        int endIndex = indexOf(end);

        if((startIndex == -1) || (endIndex == -1))
            throw new IllegalArgumentException("Illegal start or destination");

        //initialise data for Dijkstra's

    }


    public final LinkedHashMap<String, String> shortestPath(String start,String end){

        //getting indicies of the start and end stations
        int from = indexOf(start);
        int to = indexOf(end);

        if((from == -1) || (to == -1) || (from == to))return null;

        shortestLength(start, end);

        //creating initialising 2 lists
        LinkedHashMap<String,String> tempList = new LinkedHashMap<String,String>();
        LinkedHashMap<String,String> pathList = new LinkedHashMap<String,String>();

        Station endStation = stations[to];
        Station traverse = endStation;

        while(traverse != null){
            for(Map.Entry<Station,Set<String>> colour : colours.entrySet())
            Set<String> colourSet = colour.getValue();
            tempList.put(traverse.id,colourSet);
            traverse = traverse.parent;
        }
        List<Map.Entry<String, String>> entries = new ArrayList<>(tempList.entrySet());
        Collections.reverse(entries);
        for(Map.Entry<String,String> entry : entries){
            pathList.put(entry.getKey(), entry.getValue());
        }

        return pathList;
        

        
    }

}
