import java.util.Scanner;
import java.util.*;
public class Driver {
    public static void main(String[] args){
        GraphList graph = new GraphList();
        graph.loadFromFile("Metrolink_times_linecolour.csv");
        //graph.print();
        try(Scanner scanner = new Scanner(System.in)){
            
        System.out.println("Welcome to the Metrolink Journey Planner!");
        System.out.println("Enter a start station");
        String start = scanner.nextLine().trim();
        System.out.println("Enter an end station");
        String end = scanner.nextLine().trim();

        if (!graph.stationExists(start)) System.out.println("Invalid start Station. Please try again.");
        if (!graph.stationExists(end)) System.out.println("Invalid end Station. Please try again.");
        if (!graph.stationExists(end)&&!graph.stationExists(start)) System.out.println("Both stations are invalid. Please try again.");
        //graph.getNeighbours(start);
        //graph.neighbourList(start);
        //graph.neighbourList(start);
        //int[] adjacent= graph.getNeighbours(start);
        //for(GraphNode adjacents : adjacent)System.out.println(adjacents.id);
        List<String> journey = graph.Dijkstas(start,end, null);
        if (!journey.isEmpty()){
            System.out.println("Shortest path:");
            for (String station : journey){
                System.out.println("->" + station);
            }
        }
        }

        
    }
}
