import java.util.Scanner;

public class Driver {
    public static void main(String[] args) {
        Graph graph = new Graph(1000);
        graph.loadFromFile("Metrolink_times_linecolour.csv");
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
