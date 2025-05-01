import java.util.Scanner;

public class Driver {
    public static void main(String[] args){
        GraphList graph = new GraphList();
        graph.loadFromFile("Metrolink_times_linecolour.csv");
        graph.print();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Metrolink Journey Planner!");
        System.out.println("Enter a start station");
        String start = scanner.nextLine().trim();
        System.out.println("Enter an end station");
        String end = scanner.nextLine().trim();

        if (graph.stationExists(start) != true && graph.stationExists(end) != true)
        System.out.println("Invalid Station. Please try again.");
    }
}
