import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Graph {

    private Station[] stations; // array for the stations on the map
    private int numOfStations; // count for the number of stations
    private int maxNumber;
    private double[] distance;
    private boolean[] visited; // set for finalised stations in dj algo
    private int[] previousStation; // for tracking the path

    public class Station {
        private String id; // id for the station
        private LinkedHashSet<Edge> edges; // adjacency list

        private Station(String id) {
            this.id = id;
            this.edges = new LinkedHashSet<>();
        }

        public String getStationID() {
            return id;
        }
    }

    private class Edge implements Comparable<Edge> {
        // This class implements a Comparable so that it can be added to the priority
        // queue
        private int fromIndex; // index of Start
        private int toIndex; // index of destination
        private double time; // time between stations
        private String lineColour; // line colour

        private Edge(int fromIndex, int toIndex, double time, String lineColour) {
            this.fromIndex = fromIndex;
            this.toIndex = toIndex;
            this.time = time;
            this.lineColour = lineColour;
        }

        @Override
        public int compareTo(Edge edge) {
            return Double.compare(this.time, edge.time);
        }

    }

    public Graph(int maximum) {
        // Initialisation
        maxNumber = maximum;
        stations = new Station[maxNumber];
        numOfStations = 0;

    }
    // ------------------------------------------------Helper
    // Methods------------------------------------------------//

    public boolean stationExists(String station) {
        return indexOf(station) != -1;
    }

    private int indexOf(String station) {
        for (int i = 0; i < numOfStations; i++) {
            if ((stations[i] != null) && (stations[i].id.equalsIgnoreCase(station)))
                return i; // returning the index of the building with the same id
        }

        return -1; // returning -1 if the station is not in the array
    }

    public void printGraph() {
        System.out.println("Network Graph: ");
        for (int i = 0; i < numOfStations; i++) {
            if (stations[i] != null) {
                System.out.print(stations[i].id + ": "); // adding the id of the start station to the string
                boolean firstEdge = true;
                for (Edge e : stations[i].edges) { // Traversing through the linked list of edges
                    if (!firstEdge) {
                        System.out.print(", ");
                    } else {
                        firstEdge = false;
                    }
                    ;
                    System.out.print(stations[e.toIndex].id + " ( " + e.lineColour + " , " + e.time + " mins )");
                    // adding the colour of the edge to the string
                }
                System.out.println();
            }

        }

    }

    // method to print the shortest length between the start Station and the end
    // Station
    // method to print path, given the returned list of strings from the shortest
    // path method

    public void printShortestPath(String start, String end) {
        List<String> path = findShortestPath(start, end);
        if (path.isEmpty()) {
            System.out.println("No path exists between " + start + " and " + end);
            return;

        }
        double totalTime = distance[indexOf(end)];

        System.out.println("Shortest path from " + start + " to " + end + " ( " + totalTime + " mins ): ");

        for (int i = 0; i < path.size(); i++) {
            System.out.print(path.get(i));
            if (i < path.size() - 1) {
                System.out.print(" -> ");
            }
        }
        System.out.println();
    }
    // method to print path, given the returned list of strings from the shortest
    // path method

    public void loadFromFile(String filename) {

        try {
            List<String[]> dataRows = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String line;
                line = reader.readLine(); // skips the header
                while ((line = reader.readLine()) != null) {
                    String[] section = line.split(",");
                    dataRows.add(section);
                }
            }
            // System.out.println("Number of Edges read: " + dataRows.size() + "| From: " +
            // filename); //syntax

            for (String[] row : dataRows) {

                String from = row[0].trim();
                String to = row[1].trim();
                String colour = row[2].trim();
                double time = Double.parseDouble(row[3].trim());
                // add stations
                addStation(from);
                addStation(to);
                // add connection between lines
                addLine(from, to, time, colour);

            }
            // System.out.println("Number of loaded Stations " + numOfStations + "
            // stations"); //syntax
            // System.out.println("Number of loaded Connections " + dataRows.size() + "
            // connections"); //syntax
        } catch (IOException END) {
            System.err.println("Error reading file:" + END.getMessage());
        }
    }
    // ------------------------------------------------End of Helper
    // Methods------------------------------------------------//

    public boolean addStation(String station) {

        if (stationExists(station))
            return false;

        stations[numOfStations] = new Station(station); // Adding new station object with the id as the Station
        numOfStations++;
        return true;
    }

    public boolean addLine(String fromStation, String toStation, double timeBetween, String Colour) {
        int i = indexOf(fromStation); // finding the index of the start station
        int j = indexOf(toStation); // finding the index of the end Station

        if (i == j)
            return false; // if both stations are the same then no reason to add the line

        if (i == -1) { // case if start station doesnt exist on the map
            addStation(fromStation);
            i = indexOf(fromStation);
        }
        if (j == -1) {
            addStation(toStation);
            j = indexOf(toStation);
        }

        for (Edge e : stations[i].edges) { // Check if edge already exists
            if (e.toIndex == j)
                return false;
        }
        // Makes graph undirected
        stations[i].edges.add(new Edge(i, j, timeBetween, Colour));
        stations[j].edges.add(new Edge(j, i, timeBetween, Colour));
        return true;

    }

    // method to find the shortest length between two stations
    public double shortestLength(String start, String end) {

        // finding the indicies of the start and the end
        int startIndex = indexOf(start);
        int endIndex = indexOf(end);

        if ((startIndex == -1) || (endIndex == -1))
            throw new IllegalArgumentException("Illegal start or destination station");
        // initialise data structures for Dijkstra's
        distance = new double[numOfStations];
        visited = new boolean[numOfStations];
        previousStation = new int[numOfStations];

        // track Line transfers

        // initialise data for Dijkstra's
        for (int i = 0; i < numOfStations; i++) {
            distance[i] = Double.POSITIVE_INFINITY;
            visited[i] = false;
            previousStation[i] = -1;
        }
        // Distance to start station is 0
        distance[startIndex] = 0;

        PriorityQueue<Edge> priorityQueue = new PriorityQueue<>();
        priorityQueue.add(new Edge(startIndex, startIndex, 0, ""));

        while (!priorityQueue.isEmpty()) {
            Edge currentStation = priorityQueue.poll();
            int currentIndex = currentStation.toIndex;

            // skip if visited
            if (visited[currentIndex])
                continue;

            // mark as visited
            visited[currentIndex] = true;

            // break if reached end station
            if (currentIndex == endIndex)
                break;

            for (Edge edge : stations[currentIndex].edges) {
                int neighborIndex = edge.toIndex;

                if (!visited[neighborIndex]) {
                    double newDistance = distance[currentIndex] + edge.time;
                    if (!currentStation.lineColour.equals("") && !edge.lineColour.equals(currentStation.lineColour))
                        newDistance += 2d;
                    if (newDistance < distance[neighborIndex]) {
                        distance[neighborIndex] = newDistance;
                        previousStation[neighborIndex] = currentIndex;
                        priorityQueue.add(new Edge(currentIndex, neighborIndex, newDistance, edge.lineColour));

                    }
                }
            }
        }
        return distance[endIndex];
    }

    public List<String> findShortestPath(String start, String end) {

        // getting indicies of the start and end stations
        int startIndex = indexOf(start);
        int endIndex = indexOf(end);

        if ((startIndex == -1) || (endIndex == -1))
            return new ArrayList<>();

        // running dijkstra's to get shortest lenght
        shortestLength(start, end);

        // creating|initialising a list to be the path
        List<String> path = new ArrayList<>();
        if (distance[endIndex] == Double.POSITIVE_INFINITY)
            return path;

        for (int current = endIndex; current != -1; current = previousStation[current]) {
            path.add(stations[current].id);
        }
        Collections.reverse(path);
        return path;
    }

}
