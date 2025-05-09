
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Graph {
    
    private Station[] stations;   // Array for the stations on the map
    private int numOfStations;    // Count for the number of stations 
    private int maxNumber;
    private PriorityQueue<Edge> dijkstraPQ;  // Priority queue for Dijkstra's algorithm
    private double[] distance;    // Distance array for Dijkstra's algorithm
    private boolean[] visited;    // Set for finalized stations in Dijkstra's algorithm
    private int[] previous;       // For tracking the path
    
    public class Station {
        private String id;          // ID for the station
        private LinkedHashSet<Edge> edges;  // Adjacency list
        
        private Station(String id) {
            this.id = id;
            this.edges = new LinkedHashSet<>();
        }
        
        public String getId() {
            return id;
        }
    }

    private class Edge implements Comparable<Edge> {
        private int fromIndex;    // Index of source
        private int toIndex;      // Index of destination
        private double time;      // Time between stations
        private String lineColour; // Line color
        
        private Edge(int fromIndex, int toIndex, double time, String lineColour) {
            this.fromIndex = fromIndex;
            this.toIndex = toIndex;
            this.time = time;
            this.lineColour = lineColour;
        }
        
        // For priority queue in Dijkstra's algorithm
        @Override
        public int compareTo(Edge other) {
            return Double.compare(this.time, other.time);
        }
    }

    public Graph(int maximum) {
        // Initialization
        maxNumber = maximum;
        stations = new Station[maxNumber];
        numOfStations = 0;
        dijkstraPQ = new PriorityQueue<>();
    }
    
    //------------------------------------------------Helper Methods------------------------------------------------//

    public boolean stationExists(String stationId) {
        return indexOf(stationId) != -1;
    }

    private int indexOf(String stationId) {
        for (int i = 0; i < numOfStations; i++) {
            if (stations[i] != null && stations[i].id.equals(stationId)) {
                return i; // Returning the index of the station with the same ID
            }
        }
        return -1; // Returning -1 if the station is not in the array
    }

    public void printGraph() {
        System.out.println("Metro Network Graph:");
        for (int i = 0; i < numOfStations; i++) {
            if (stations[i] != null) {
                System.out.print(stations[i].id + ": ");
                boolean firstEdge = true;
                
                for (Edge e : stations[i].edges) {
                    if (!firstEdge) {
                        System.out.print(", ");
                    } else {
                        firstEdge = false;
                    }
                    System.out.print(stations[e.toIndex].id + "(" + e.lineColour + ", " + e.time + "min)");
                }
                System.out.println();
            }
        }
    }

    // Method to print the shortest path between start and end stations
    public void printShortestPath(String start, String end) {
        List<String> path = findShortestPath(start, end);
        if (path.isEmpty()) {
            System.out.println("No path exists between " + start + " and " + end);
            return;
        }
        
        double totalTime = shortestLength(start, end);
        
        System.out.println("Shortest path from " + start + " to " + end + " (" + totalTime + " minutes):");
        for (int i = 0; i < path.size(); i++) {
            System.out.print(path.get(i));
            if (i < path.size() - 1) {
                System.out.print(" -> ");
            }
        }
        System.out.println();
    }

    public void loadFromFile(String filename) {
        try {
            List<String[]> dataRows = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String line;
                line = reader.readLine(); // Skip the header
                while ((line = reader.readLine()) != null) {
                    String[] section = line.split(",");
                    dataRows.add(section);
                }   
            }
            
            System.out.println("Loading " + dataRows.size() + " connections from " + filename);
            
            for (String[] row : dataRows) {
                String from = row[0].trim();
                String to = row[1].trim();
                String colour = row[2].trim();
                double time = Double.parseDouble(row[3].trim());
                
                // Add stations if they don't exist
                addStation(from);
                addStation(to);
                
                // Add the connection between stations
                addLine(from, to, time, colour);
            }
            
            System.out.println("Successfully loaded " + numOfStations + " stations and " + dataRows.size() + " connections.");
            
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error processing data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    //------------------------------------------------End of Helper Methods------------------------------------------------//

    public boolean addStation(String station) {
        if (stationExists(station)) return false;

        stations[numOfStations] = new Station(station);  // Adding new station object with the id as the station
        numOfStations++;
        return true; 
    }

    public boolean addLine(String fromStation, String toStation, double timeBetween, String colour) {
        int i = indexOf(fromStation);          // Finding the index of the start station
        int j = indexOf(toStation);            // Finding the index of the end station

        if (i == j) return false;              // If both stations are the same then no reason to add the line 

        if (i == -1) {                         // Case if start station doesn't exist on the map
            addStation(fromStation);      
            i = indexOf(fromStation);
        }
        
        if (j == -1) {
            addStation(toStation);
            j = indexOf(toStation);
        }

        // Check if edge already exists
        for (Edge e : stations[i].edges) {
            if (e.toIndex == j) return false;
        }

        // Add edges in both directions (undirected graph)
        stations[i].edges.add(new Edge(i, j, timeBetween, colour));
        stations[j].edges.add(new Edge(j, i, timeBetween, colour));
        return true;
    }
    
    // Method to find the shortest path length between two stations
    public double shortestLength(String start, String end) {
        // Finding the indices of the start and the end 
        int startIndex = indexOf(start);
        int endIndex = indexOf(end);

        if (startIndex == -1 || endIndex == -1) {
            throw new IllegalArgumentException("Invalid start or destination station");
        }

        // Initialize data structures for Dijkstra's algorithm
        distance = new double[numOfStations];
        visited = new boolean[numOfStations];
        previous = new int[numOfStations];
        
        // Initialize distances
        for (int i = 0; i < numOfStations; i++) {
            distance[i] = Double.POSITIVE_INFINITY;
            visited[i] = false;
            previous[i] = -1;
        }
        
        // Distance to start is 0
        distance[startIndex] = 0;
        
        // Priority queue for Dijkstra
        PriorityQueue<Edge> pq = new PriorityQueue<>();
        pq.add(new Edge(startIndex, startIndex, 0, ""));
        
        while (!pq.isEmpty()) {
            Edge current = pq.poll();
            int currentIndex = current.toIndex;
            
            // Skip if already visited
            if (visited[currentIndex]) continue;
            
            // Mark as visited
            visited[currentIndex] = true;
            
            // If reached destination, we're done
            if (currentIndex == endIndex) break;
            
            // Explore neighbors
            for (Edge edge : stations[currentIndex].edges) {
                int neighborIndex = edge.toIndex;
                
                if (!visited[neighborIndex]) {
                    double newDist = distance[currentIndex] + edge.time;
                    
                    if (newDist < distance[neighborIndex]) {
                        distance[neighborIndex] = newDist;
                        previous[neighborIndex] = currentIndex;
                        pq.add(new Edge(currentIndex, neighborIndex, newDist, edge.lineColour));
                    }
                }
            }
        }
        
        return distance[endIndex];
    }

    // Method to find the shortest path between two stations
    public List<String> findShortestPath(String start, String end) {
        int startIndex = indexOf(start);
        int endIndex = indexOf(end);
        
        if (startIndex == -1 || endIndex == -1) {
            return new ArrayList<>();
        }
        
        // Run Dijkstra's algorithm
        shortestLength(start, end);
        
        // Reconstruct path
        List<String> path = new ArrayList<>();
        
        // No path exists
        if (distance[endIndex] == Double.POSITIVE_INFINITY) {
            return path;
        }
        
        // Build path from end to start
        for (int at = endIndex; at != -1; at = previous[at]) {
            path.add(stations[at].id);
        }
        
        // Reverse to get start-to-end path
        Collections.reverse(path);
        return path;
    }
    
    // Method to find which lines are used on the shortest path
    public Map<String, String> getPathWithLines(String start, String end) {
        int startIndex = indexOf(start);
        int endIndex = indexOf(end);
        
        if (startIndex == -1 || endIndex == -1) {
            return new LinkedHashMap<>();
        }
        
        // Run Dijkstra's algorithm
        shortestLength(start, end);
        
        // No path exists
        if (distance[endIndex] == Double.POSITIVE_INFINITY) {
            return new LinkedHashMap<>();
        }
        
        // Build path with lines
        LinkedHashMap<String, String> pathWithLines = new LinkedHashMap<>();
        
        // Start from end and work backwards
        int currentIndex = endIndex;
        while (currentIndex != startIndex) {
            int prevIndex = previous[currentIndex];
            
            // Find the edge between prevIndex and currentIndex
            String lineColor = null;
            for (Edge edge : stations[prevIndex].edges) {
                if (edge.toIndex == currentIndex) {
                    lineColor = edge.lineColour;
                    break;
                }
            }
            
            pathWithLines.put(stations[currentIndex].id, lineColor);
            currentIndex = prevIndex;
        }
        
        // Add start station
        pathWithLines.put(stations[startIndex].id, "START");
        
        // Reverse to get start-to-end
        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        List<Map.Entry<String, String>> entries = new ArrayList<>(pathWithLines.entrySet());
        Collections.reverse(entries);
        
        for (Map.Entry<String, String> entry : entries) {
            result.put(entry.getKey(), entry.getValue());
        }
        
        return result;
    }
    
    
}

