import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Graph {

    /** Minutes added to a journey for changing from one line to another. */
    private static final double CHANGE_PENALTY_MINUTES = 2.0;

    private final List<Station> stations = new ArrayList<>(); // holds every station where a station's index is its
                                                              // position
    private final Map<String, Integer> stationIndex = new HashMap<>(); // lowercase name -> index, for O(1) lookup

    private double[] distance; // best known time to each station
    private boolean[] visited; // stations whose shortest time is final
    private int[] previousStation; // predecessor of each station on the best route
    private String[] previousLine; // line colour used to arrive at each station

    /** A station on the network, holding its own adjacency list. */
    public static class Station {
        private final String id;
        private final LinkedHashSet<Edge> edges = new LinkedHashSet<>();

        private Station(String id) {
            this.id = id;
        }

        public String getStationID() {
            return id;
        }
    }

    /** A tram connection from one station to an adjacent one. */

    private class Edge {
        private final int toIndex; // station at the far end
        private final double time; // average journey time in minutes
        private final String lineColour;

        private Edge(int toIndex, double time, String lineColour) {
            this.toIndex = toIndex;
            this.time = time;
            this.lineColour = lineColour;
        }

    }

    /**
     * One entry in Dijkstra's queue: a station, and the best cumulative time found
     * to reach it.
     */
    private static class QueueEntry implements Comparable<QueueEntry> {
        private final int stationIndex;
        private final double timeSoFar;

        private QueueEntry(int stationIndex, double timeSoFar) {
            this.stationIndex = stationIndex;
            this.timeSoFar = timeSoFar;
        }

        @Override
        public int compareTo(QueueEntry other) {
            return Double.compare(this.timeSoFar, other.timeSoFar);
        }
    }

    // ------------------------------------------------Helper
    // Methods------------------------------------------------//

    public boolean stationExists(String station) {
        return indexOf(station) != -1;
    }

    /**
     * Index of a station by name, case-insensitive, or -1 if the network has no
     * such station.
     */
    private int indexOf(String station) {
        if (station == null) {
            return -1; // returning -1 if the station is not in the array
        }
        Integer index = stationIndex.get(station.trim().toLowerCase(Locale.ROOT));
        return (index == null) ? -1 : index;
    }

    /**
     * The station's name as spelled in the data file, so output matches the
     * network, not the typing.
     */
    private String canonicalName(String station) {
        int i = indexOf(station);
        return (i == -1) ? station : stations.get(i).id;
    }

    public int stationCount() {
        return stations.size();
    }

    public void printGraph() {
        System.out.println("Network Graph: ");
        for (Station station : stations) {
            System.out.print(station.id + ": "); // adding the id of the start station to the string
            boolean firstEdge = true;

            for (Edge e : station.edges) { // Traversing through the linked list of edges
                if (!firstEdge) {
                    System.out.print(", ");
                }
                firstEdge = false;
                System.out.print(stations.get(e.toIndex).id + " (" + e.lineColour + ", " + e.time + " mins)");
            }
            System.out.println();
        }
    }

    // method to print the shortest length between the start Station and the end
    // Station
    // method to print path, given the returned list of strings from the shortest
    // path method

    public void printShortestPath(String start, String end) {
        int startIndex = indexOf(start);
        int endIndex = indexOf(end);

        if (startIndex == -1 || endIndex == -1) {
            System.out.println("Unknown station: " + (startIndex == -1 ? start : end));
            return;
        }

        shortestLength(start, end);

        if (distance[endIndex] == Double.POSITIVE_INFINITY) {
            System.out.println("No route exists between " + canonicalName(start) + " and " + canonicalName(end));
            return;
        }

        List<Integer> route = indexPath(endIndex);
        if (route.size() == 1) {
            System.out.println("Start and destination are the same station.");
            return;
        }

        List<Leg> legs = legsFor(route);
        int changes = legs.size() - 1;

        System.out.println();
        System.out.println(canonicalName(start) + "  ->  " + canonicalName(end));
        System.out.printf("Journey time: %.1f mins   |   Changes: %d%n", distance[endIndex], changes);
        System.out.println();

        for (Leg leg : legs) {
            String board = leg.stationNames.get(0);
            String alight = leg.stationNames.get(leg.stationNames.size() - 1);
            int stops = leg.stationNames.size() - 1;
            System.out.printf("  %-10s %s  ->  %s   (%d %s)%n",
                    leg.lineColour, board, alight, stops, stops == 1 ? "stop" : "stops");
            if (leg.stationNames.size() > 2) {
                System.out.println("             via " + String.join(", ",
                        leg.stationNames.subList(1, leg.stationNames.size() - 1)));
            }
        }

        List<String> allNames = new ArrayList<>();
        for (int index : route)
            allNames.add(stations.get(index).id);
        System.out.println();
        System.out.println("Full route: " + String.join(" -> ", allNames));
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
        String name = station.trim();
        String key = name.toLowerCase(Locale.ROOT);
        if (stationIndex.containsKey(key)) {
            return false; // already on the map
        }

        stationIndex.put(key, stations.size());
        stations.add(new Station(name));
        return true;
    }

    public boolean addLine(String fromStation, String toStation, double timeBetween, String Colour) {

        addStation(fromStation); // doesn't do anything if station is already present
        addStation(toStation);

        int i = indexOf(fromStation); // finding the index of the start station
        int j = indexOf(toStation); // finding the index of the end Station

        if (i == j)
            return false; // if both stations are the same then no reason to add the line

        for (Edge e : stations.get(i).edges) { // Check if edge already exists
            if (e.toIndex == j)
                return false;
        }
        // Makes graph undirected by recording the connection each way
        stations.get(i).edges.add(new Edge(j, timeBetween, Colour));
        stations.get(i).edges.add(new Edge(i, timeBetween, Colour));
        return true;

    }

    // method to find the shortest length between two stations
    public double shortestLength(String start, String end) {

        // finding the indicies of the start and the end
        int startIndex = indexOf(start);
        int endIndex = indexOf(end);

        if ((startIndex == -1) || (endIndex == -1)) {
            throw new IllegalArgumentException("Illegal start or destination station");
        }
        // initialise data structures for Dijkstra's
        int numOfStations = stations.size();
        distance = new double[numOfStations];
        visited = new boolean[numOfStations];
        previousStation = new int[numOfStations];
        previousLine = new String[numOfStations];

        // initialise data for Dijkstra's
        for (int i = 0; i < numOfStations; i++) {
            distance[i] = Double.POSITIVE_INFINITY;
            visited[i] = false;
            previousStation[i] = -1;
        }
        // Distance to start station is 0
        distance[startIndex] = 0;

        PriorityQueue<QueueEntry> priorityQueue = new PriorityQueue<>();
        priorityQueue.add(new QueueEntry(startIndex, 0));

        while (!priorityQueue.isEmpty()) {
            int currentIndex = priorityQueue.poll().stationIndex;

            // skip if visited
            if (visited[currentIndex]) {
                continue;
            }
            // mark as visited
            visited[currentIndex] = true;

            // break if reached end station
            if (currentIndex == endIndex) {
                break;
            }

            for (Edge edge : stations.get(currentIndex).edges) {
                int neighbourIndex = edge.toIndex;
                if (visited[neighbourIndex]) {
                    continue;
                }
                double newDistance = distance[currentIndex] + edge.time;

                // changing line costs time, so charge for it
                String arrivedOnLine = previousLine[currentIndex];

                if (arrivedOnLine != null && !arrivedOnLine.equals(edge.lineColour)) {
                    newDistance += CHANGE_PENALTY_MINUTES;
                }

                // checks if changing line makes it a quicker journey
                if (newDistance < distance[neighbourIndex]) {

                    distance[neighbourIndex] = newDistance;
                    previousStation[neighbourIndex] = currentIndex;
                    previousLine[neighbourIndex] = edge.lineColour;
                    priorityQueue.add(new QueueEntry(neighbourIndex, newDistance));
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

        // running dijkstra's to get shortest length
        shortestLength(start, end);

        // creating/initialising a list to be the path
        List<String> path = new ArrayList<>();

        // if the route doesn't exist
        if (distance[endIndex] == Double.POSITIVE_INFINITY)
            return path;

        for (int index : indexPath(endIndex)) {
            path.add(stations.get(index).id);
        }
        return path;
    }

    /** Station indices along the best route, in travel order. */
    private List<Integer> indexPath(int endIndex) {
        List<Integer> route = new ArrayList<>();
        for (int current = endIndex; current != -1; current = previousStation[current]) {
            route.add(current);
        }
        Collections.reverse(route);
        return route;
    }

    /**
     * Splits a route into stretches travelled on a single line without changing.
     */
    private List<Leg> legsFor(List<Integer> route) {
        List<Leg> legs = new ArrayList<>();
        for (int pos = 1; pos < route.size(); pos++) {
            String line = previousLine[route.get(pos)];
            Leg current = legs.isEmpty() ? null : legs.get(legs.size() - 1);
            if (current == null || !line.equals(current.lineColour)) {
                current = new Leg(line, stations.get(route.get(pos - 1)).id);
                legs.add(current);
            }
            current.stationNames.add(stations.get(route.get(pos)).id);
        }
        return legs;
    }

    /**
     * A stretch of the journey on one line: boarding station first, alighting
     * station last.
     */
    private static class Leg {
        private final String lineColour;
        private final List<String> stationNames = new ArrayList<>();

        private Leg(String lineColour, String boardingStation) {
            this.lineColour = lineColour;
            this.stationNames.add(boardingStation);
        }
    }
}
