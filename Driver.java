import java.util.Scanner;

public class Driver {

    private static final String DEFAULT_DATA_FILE = "Metrolink_times_linecolour.csv";

    public static void main(String[] args) {
        String dataFile = (args.length > 0) ? args[0] : DEFAULT_DATA_FILE;

        Graph graph = new Graph();
        graph.loadFromFile(dataFile);

        if (graph.stationCount() == 0) {
            System.out.println("No network data loaded from " + dataFile);
            System.out.println("Check the file path and try again.");
            return;
        }
        try (Scanner scanner = new Scanner(System.in)) {

            System.out.println("Welcome to the Metrolink Journey Planner!");
            System.out.println("Enter a start station");
            String start = scanner.nextLine().trim();
            System.out.println("Enter an end station");
            String end = scanner.nextLine().trim();

            boolean startValid = graph.stationExists(start);
            boolean endValid = graph.stationExists(end);

            if (!startValid || !endValid) {
                if (!startValid && !endValid)
                    System.out.println("Both stations are invalid. Please try again.");
                else if (!startValid)
                    System.out.println("Invalid start station: " + start);
                else
                    System.out.println("Invalid end station: " + end);
                return;
            }

            System.out.println("Would you like the shortest route between the stations? Yes/No");
            String response = scanner.nextLine().trim();
            if (response.equalsIgnoreCase("yes") || response.equalsIgnoreCase("y")) {
                graph.printShortestPath(start, end);
                return;
            }
            if (response.equalsIgnoreCase("no") || response.equalsIgnoreCase("n")) {
                System.out.println("GOODBYE");
                return;
            } else {
                System.out.println("Input valid response next time. GOODBYE");
                return;
            }

        }

    }

}
