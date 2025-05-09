import java.util.Scanner;


public class Driver {
    public static void main(String[] args){
        Graph graph = new Graph(1000);
        graph.loadFromFile("Metrolink_times_linecolour.csv");
        // graph.printGraph(); //syntax
        try(Scanner scanner = new Scanner(System.in)){
            
        System.out.println("Welcome to the Metrolink Journey Planner!");
        System.out.println("Enter a start station");
        String start = scanner.nextLine().trim();
        System.out.println("Enter an end station");
        String end = scanner.nextLine().trim();
        if (graph.stationExists(start) == false) System.out.println("Invalid start Station. Please try again.");
        if (graph.stationExists(end) == false) System.out.println("Invalid end Station. Please try again.");
        if ((graph.stationExists(end) == false)&&(graph.stationExists(start) == false)) System.out.println("Both stations are invalid. Please try again.");
        System.out.println("Would you like the shortest route between the stations? Yes/No");
        String response = scanner.nextLine().trim();
        if(response.equals("yes")){
            graph.printShortestPath(start, end);
            return;
        }
        if(response.equals("no")){
            System.out.println("GOODBYE");
            return;
        }
        else{
            System.out.println("Input valid response next time. GOODBYE");
            return;
        }
        
        }

        
    }






    
}
