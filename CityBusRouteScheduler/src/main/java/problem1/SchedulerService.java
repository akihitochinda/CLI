package problem1;

import java.util.Scanner;

/**
 * The type Scheduler service.
 */
public class SchedulerService {

  /**
   * Instantiates a new Scheduler service.
   */
  public SchedulerService() {
  }

  /**
   * Method to handle different command words SchedulerService.
   * This class implements the service as a public method that takes a BusNetwork as its argument.
   *
   * @param busNetwork the bus network, as BusNetwork
   */
  public void command(BusNetwork busNetwork) {

    // Create the client (User typing commands)
    Scanner scanner = new Scanner(System.in);

    // Create receivers (busNetwork)
    // BusNetwork busNetwork = new BusNetwork("Seattle", routes);

    // Create the invoker (user input / CLI loop)
    while (true) {
      System.out.println("What would you like to do?");
      String input = scanner.nextLine(); // take the next argument like board 12
      String command = input.split(" ")[0]; // Extract the first word in the user input, split using space

      // Create commands
      switch (command) {
        case "board":
          new BoardCommand(busNetwork, scanner, input).execute(); // input board 12 will be separated at concreteCommand class
          break;
        case "show":
          new ShowCommand(busNetwork, input).execute();
          break;
        case "routes":
          new RoutesCommand(busNetwork).execute();
          break;
        case "done":
          System.out.println("Safe travels!");
          scanner.close(); // use one shared Scanner for the whole CLI and close it once at the end of the program
          return;
        default:
          System.out.println("Invalid command. Try again.");
          break;
      }
    }
  }
}
