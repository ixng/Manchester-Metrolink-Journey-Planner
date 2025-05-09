import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class TramMap {
    private int numOfStations;
    private Station headStation;
    LinkedHashMap<Integer,Station> stations = new LinkedHashMap<>();
    String[] node;
    public class Station{
        
        String id;
        Station next;
        Map<String, Double> adjacentStations = new LinkedHashMap<>();
        Map<String,String> stationsLines = new LinkedHashMap<>();
    public Station(String station){
        this.id = station;
        this.next = null;
        }

    public void addAdjacentStation(String adjacent, double time){  //adding edge and its distance in time
        adjacentStations.put(adjacent,time);
        
    }
    public void addStationLines(String station, String colour){  //adds the lines that the station has
        stationsLines.put(station,colour);
    }
    

    }
    public TramMap(){
        numOfStations = 0;
        node = new String[1000000000];
    }
    public boolean stationExists(String station){
        boolean exists = true;
        if(stations.containsKey(indexOf(station)) == false){exists = false;};
            
        return exists;
    }
    public int indexOf(String str){

        for(int i = 0; i < numOfStations; i++){
            if((node[i] != null) && (node[i] == str))return i; //returning the index of the building with the same id
        }
        
        return -1; //returning -1 if the station is not in the array
    }
    public Station findStation(String Station){
        Station node = headStation;
        while(node != null){
            if(node.id.equals(Station))return node;
        }
        return null;
    }
    public final boolean addStation(String station){

        if(indexOf(station) != -1 )throw new IllegalArgumentException("Station Already Exists");
        //numOfStations++;
        Station nodes = new Station(station);
        //System.out.println(nodes.id);
        node[numOfStations] = nodes.id;
        nodes.next = headStation;
        headStation = nodes;
        //stations.put(numOfStations,nodes.id);   //Adding new station object with the id as the Station
        //headStation = node[numOfStations];
        
        return true; 
    }

    public final boolean addLine(String fromStation, String toStation, Double timeBetween, String colour){
        int i = indexOf(fromStation);
        int j = indexOf(toStation);
        if(!stations.containsKey(i))addStation(fromStation);
        if(!stations.containsKey(j))addStation(toStation);
        stations.get(numOfStations).addStationLines(node[numOfStations],colour);
        stations.get(numOfStations-1).addStationLines(node[numOfStations-1],colour);
        stations.get(numOfStations).addAdjacentStation(node[numOfStations],timeBetween);
        stations.get(numOfStations-1).addAdjacentStation(node[numOfStations-1],timeBetween);
        return true;
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
                if(stationExists(from) == false || stationExists(to) == false){
                    numOfStations++;}
                
                addStation(from);
                stations.put(numOfStations,findStation(from));
                numOfStations++;
                addStation(to);
                stations.put(numOfStations,findStation(to));    
                addLine(from, to, time, colour);
                System.out.println("Adding station: " + from + " " + indexOf(from)); 
                System.out.println("Adding station: " + to + " " + indexOf(to)); 
                 
                
            }
        }catch (IOException END){
            System.err.println("Error reading file:" + END.getMessage());
        }
    }
    
}
