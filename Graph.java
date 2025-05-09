import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Graph {
    
    private Station[] stations;   //array for the stations on the map
    private int numOfStations;  //count for the number of stations 
    private int maxNumber;
    PriorityQueue<Edge> dijkstraPQ;  //Priority queue for Dijkstras algo
    int[] dj; //array for dj algo
    Boolean[] finalSet;    //set for finalised stations in dj algo


    private class Station{
        private String id;          //id for the station
        private LinkedHashSet<Edge> edges;  //adjacency list
        //private Maps<Station,Integer> adjacentStations = new LinkedHashMap<>();
        private Station parent;
        private LinkedHashMap<Station,String> colours; //colours list (for each station it has this colour)
        private Station(String id){
            this.id = id;
            this.edges = new LinkedHashSet<>();
            

        }
    }

    private class Edge implements Comparator<Edge>, Comparable<Edge>{
        //This class implements Comparator and Comparable so that it can be added to the priority queue
        private int toIndex; //index of destination
        private double time;  // time between stations
        private String lineColour;
        private Edge(int toIndex, double time, String lineColour){
            this.toIndex = toIndex;
            this.time = time;
            this.lineColour = lineColour;
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

    public Graph(int maximum){
        //Initialisation
        maxNumber = maximum;
        stations = new Station[maxNumber];
        numOfStations = 0;
        dijkstraPQ = new PriorityQueue<Edge>();
        dj = new int[maximum];
        finalSet = new Boolean[maximum];
    }
    //------------------------------------------------Helper Methods------------------------------------------------//



    private int indexOf(String str){

        for(int i = 0; i < numOfStations; i++){
            if((stations[i] != null) && (stations[i].id == str))return i; //returning the index of the building with the same id
        }
        
        return -1; //returning -1 if the station is not in the array
    }

    public void printGraph(){
        for(int i = 0; i < numOfStations; i++){
            String printStr = "";
            if(stations[i] != null){
                printStr += stations[i].id;  //adding the id of the start station to the string
                if(!stations[i].edges.isEmpty()){
                    printStr += " : ";
                    for(Edge e : stations[i].edges){       //Traversing through the linked list of edges
                        if((stations[e.toIndex] != null) && (stations[e.toIndex].colours != null) ){
                            printStr += stations[e.toIndex].id;   //adding the id of the destination station to the string
                            printStr += ", ";
                            printStr += stations[e.toIndex].colours;  //adding the colour of the edge to the string
                        }
                    }
                    printStr = printStr.substring(0,printStr.length());
                    
                }
                System.out.println(printStr);
            }
        }
    }

    //method to print the shortest length between the start Station and the end Station
    public final void shortestLengthPrint(String start, String end, int x){
        System.out.println(end + " is " + String.valueOf(x) + " away from " + start);
    }
    //method to print path, given the returned list of strings from the shortest path method
    public final void shortestPathPrint(List<String> ls){

        if(ls.size() == 0)return; //checks if the list is empty

        String printStr = "";
        for(String s : ls){
            printStr += s + " -> ";
        }
        printStr = printStr.substring(0,printStr.length());

        System.out.println(printStr);



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
                Double time = Double.parseDouble(row[3].trim());
                
                
                
                
                System.out.println("Adding station: " + from); 
                System.out.println("Adding station: " + to); 
                colours.put(from,colour);
                colours.put(to,colour);
                addStation(from);
                addStation(to);
                addLine(from, to, time, colour);
                 
                
            }
        }catch (IOException END){
            System.err.println("Error reading file:" + END.getMessage());
        }
    }
    //------------------------------------------------End of Helper Methods------------------------------------------------//

    public final boolean addStation(String station){

        if(indexOf(station) != -1 )throw new IllegalArgumentException("Station Already Exists");

        stations[numOfStations] = new Station(station);  //Adding new station object with the id as the Station
        numOfStations++;
        return true; 
    }

    public final boolean addLine(String fromStation, String toStation, Double timeBetween, String Colour){
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

        LinkedHashSet<Edge> tempList = stations[i].edges;
        Iterator<Edge> itr;
        for(itr = tempList.iterator(); itr.hasNext();){
            Edge e = itr.next();
            if(e.toIndex == j)return false;
        }

        stations[i].edges.add(new Edge(j,timeBetween, Colour));
        stations[j].edges.add(new Edge(i, timeBetween, Colour));
        return true;

    }
    //method to find the shortest length between two stations
    public final int shortestLength(String start, String end){

        //finding the indicies of the start and the end 
        int from = indexOf(start);
        int to = indexOf(end);

        if((from == -1) || (to == -1))throw new IllegalArgumentException("Illegal start or destination");

        return dj[to];

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
            for(String colour : traverse.colours)
            tempList.put(traverse.id,colour);
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
