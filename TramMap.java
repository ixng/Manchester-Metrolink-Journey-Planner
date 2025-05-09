import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class TramMap {
    private int numOfStations;
    private Station headStation;
    LinkedHashMap<Integer,Station> stations = new LinkedHashMap<>();
    private HashMap<Integer,String> node = new HashMap<>();
    public class Station{
        
        String id;
        Station next;
        Map<String, Map<String,Double>> colouredConnections = new LinkedHashMap<>();

    public Station(String station){
        this.id = station;
        this.next = null;
        }

    public void addAdjacentStation(String adjacent,String colour, double time){  //adding edge and its distance in time
        if(!colouredConnections.containsKey(adjacent))colouredConnections.put(adjacent,new LinkedHashMap<>());

        colouredConnections.get(adjacent).put(colour,time);
        System.out.println("Added Connection from " + id + " to " + adjacent + "(Colour: " + colour + " Time: " + time);
    }
    
    public Set<String> getConnectionColours(String station){
        if(colouredConnections.containsKey(station))return colouredConnections.get(station).keySet();

        return Collections.emptySet();
    }
    public Set<String> getConnectedStations(){
        return colouredConnections.keySet();
    }

    public Double getConnectionstime(String station, String colour){
        if((colouredConnections.containsKey(station)) && (colouredConnections.get(station).containsKey(colour)))
        return colouredConnections.get(station).get(colour);
        
        return null;
    }
    
    }
    public TramMap(){
        numOfStations = 0;
    }
    public boolean stationExists(String station){
        boolean exists = true;
        if(stations.containsKey(indexOf(station)) == false)exists = false;
            
        return exists;
    }
    public int indexOf(String str){

        for(int i = 0; i < numOfStations; i++){
            if((node.containsKey(i) != false) && (node.get(i) == str))return i; //returning the index of the building with the same id
        }
        
        return -1; //returning -1 if the station is not in the array
    }
    public Station findStation(String Station){
        Station current = headStation;
        while(current != null){
            if(current.id.equals(Station))return current;
            current = current.next;
        }
        return null;
    }
    public final boolean addStation(String station){

        //if(indexOf(station) != -1 )throw new IllegalArgumentException("Station Already Exists");
        //numOfStations++;
        Station newStation = new Station(station);
        newStation.next = headStation;
        headStation = newStation;
        //System.out.println(nodes.id);
        node.put(numOfStations,station);
        stations.put(numOfStations,newStation);
        numOfStations++;
        
        //stations.put(numOfStations,nodes.id);   //Adding new station object with the id as the Station
        //headStation = node[numOfStations];
        
        return true; 
    }

    public final boolean addLine(String fromStation, String toStation, Double timeBetween, String colour){
        //int i = indexOf(fromStation);
        //int j = indexOf(toStation);
        //if(!stations.containsKey(i))addStation(fromStation);
        //if(!stations.containsKey(j))addStation(toStation);
        if((!stationExists(fromStation)) || (!stationExists(toStation)))return false;  //doesnt add line if station exists
        Station fromStationObj = findStation(fromStation);
        Station toStationObj = findStation(toStation);

        if((fromStationObj == null) || (toStationObj == null))return false;

        fromStationObj.addAdjacentStation(toStation, colour, timeBetween);
        toStationObj.addAdjacentStation(fromStation, colour, timeBetween);
        //stations.get(numOfStations).addStationLines(node[numOfStations],colour);
        //stations.get(numOfStations-1).addStationLines(node[numOfStations-1],colour);
        //stations.get(numOfStations).addAdjacentStation(node[numOfStations],timeBetween);
        //stations.get(numOfStations-1).addAdjacentStation(node[numOfStations-1],timeBetween);
        System.out.println("Added line: " + fromStation + " -> " + toStation + "Colour: " + colour + " Time: " + timeBetween);
        return true;
    }
    public void loadFromFile(String filename){
        
        try{    

            //HashSet<String> uniqueStations = new HashSet<>();
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
            System.out.println("Adding connections:");    
            for(String[] row : matrix){

                    String from = row[0].trim();
                    String to = row[1].trim();
                    String colour = row[2].trim();
                    Double time = Double.parseDouble(row[3].trim());
                    if(!stationExists(from))addStation(from);
                    if(!stationExists(to))addStation(to);
                    addLine(from, to, time, colour);
                    System.out.println("Total Stations: " + numOfStations);
                    System.out.println("Station Mapping: ");
                    for(int i = 0; i < numOfStations; i++)System.out.println(i +": " + node.get(i));
            
        }   
        }
        catch (IOException END){
            System.err.println("Error reading file:" + END.getMessage());
        }
    }


}

