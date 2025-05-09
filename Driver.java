import java.util.Scanner;


public class Driver {
    public static void main(String[] args){
        Graph graph = new Graph(1000);
        graph.loadFromFile("Metrolink_times_linecolour.csv");
        graph.printGraph();
        try(Scanner scanner = new Scanner(System.in)){
            
        System.out.println("Welcome to the Metrolink Journey Planner!");
        System.out.println("Enter a start station");
        String start = scanner.nextLine().trim();
        System.out.println("Enter an end station");
        String end = scanner.nextLine().trim();
        if (graph.stationExists(start) == false) System.out.println("Invalid start Station. Please try again.");
        if (graph.stationExists(end) == false) System.out.println("Invalid end Station. Please try again.");
        if ((graph.stationExists(end) == false)&&(graph.stationExists(start) == false)) System.out.println("Both stations are invalid. Please try again.");
        graph.printShortestPath(start, end);
        //graph.getNeighbours(start);

        //graph.neighbourList(start);
        //graph.neighbourList(start);
        //int[] adjacent= graph.getNeighbours(start);
        //for(GraphNode adjacents : adjacent)System.out.println(adjacents.id);
        
        //graph.DijkstrasAlgorithm(graph.findNode(start));
        //graph.printGraph();
        
        }

        
    }






    
}
