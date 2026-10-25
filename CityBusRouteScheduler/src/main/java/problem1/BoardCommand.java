package problem1;

import java.util.Scanner;

/**
 * The type Board command.
 */
public class BoardCommand implements Command {
  private BusNetwork busNetwork;
  private Scanner scanner;
  private String input;

  /**
   * Instantiates a new Board command.
   *
   * @param busNetwork the bus network
   * @param scanner    the scanner
   * @param input      the input
   */
  public BoardCommand(BusNetwork busNetwork, Scanner scanner, String input) {
    this.busNetwork = busNetwork;
    this.scanner = scanner;
    this.input = input;
  }

  @Override
  public void execute() {
    try {
      Integer routeNumber = extractRouteNumber(input);
      // create route object and do Null check
      Route route = busNetwork.findRouteByNumber(routeNumber);
      if (!isValidRoute(route, routeNumber))
        return;
      // ask for passenger details and store the passenger name details
      System.out.println("Passenger name?");
      String name = scanner.nextLine();
      // ask whether this is an express trip
      System.out.println("Is this an express trip? (yes/no)");
      String expressAnswer = scanner.nextLine().trim().toLowerCase();
      Boolean expressTrip = parseExpressAnswer(expressAnswer);
      if (expressTrip == null) {
        System.out.println("Sorry, please answer yes or no.");
        return;
      }
      // ask for boarding stop information and store the info
      System.out.println("Boarding at which stop? (1-10)");
      int boardingStop = Integer.parseInt(scanner.nextLine().trim());
      // ask for destination stop information and store the info
      System.out.println("Destination stop? (1-10)");
      int destinationStop = Integer.parseInt(scanner.nextLine().trim());
      // validations
      if (!isValidStop(route, boardingStop))
        return;
      if (!isValidStop(route, destinationStop))
        return;
      if (!isValidExpressTrip(route, boardingStop, destinationStop, expressTrip))
        return;
      if (!isValidDirection(boardingStop, destinationStop))
        return;
      if (!isValidCapacity(route, boardingStop, destinationStop))
        return;
      // try to add passenger
      Passenger passenger = new Passenger(name, boardingStop, destinationStop);
      route.findStop(boardingStop).addPassenger(passenger);
      System.out.println(name + " has boarded Route " + routeNumber +
          " from stop " + boardingStop + ", heading to stop " + destinationStop + ".");
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Extracts the route number from the input string.
   *
   * @param input the input string
   * @return the route number
   */
  private Integer extractRouteNumber(String input) {
    // split the input into two, and then converts the second number into integer and extract it
    String[] parts  = input.split(" ");
    Integer routeNumber = Integer.parseInt(parts[1]);
    return routeNumber;
  }

  /**
   * Checks if the route is valid.
   * 
   * @param route       the route
   * @param routeNumber the route number
   * @return true if the route is valid, false otherwise
   */
  private boolean isValidRoute(Route route, Integer routeNumber) {
    if (route == null) {
      System.out.println("Sorry, Route " + routeNumber + " does not exist.");
      return false;
    }
    return true;
  }

  /**
   * Checks if the stop number is valid for the given route.
   *
   * @param route      the route
   * @param stopNumber the stop number
   * @return true if the stop number is valid, false otherwise
   */
  private boolean isValidStop(Route route, int stopNumber) {
    if (route.findStop(stopNumber) == null) {
      System.out.println("Sorry, stop " + stopNumber + " does not exist on this route.");
      return false;
    }
    return true;
  }

  /**
   * Checks if the destination stop comes after the boarding stop.
   * 
   * @param boardingStop   the boarding stop
   * @param destinationStop the destination stop
   * @return true if the destination stop comes after the boarding stop, false otherwise
   */
  private boolean isValidDirection(int boardingStop, int destinationStop) {
    if (destinationStop <= boardingStop) {
      System.out.println("Sorry, destination must come after boarding stop.");
      return false;
    }
    return true;
  }

  /**
   * Checks if there is capacity on the bus between the boarding and destination stops.
   * 
   * @param route          the route
   * @param boardingStop   the boarding stop
   * @param destinationStop the destination stop
   * @return true if there is capacity, false otherwise
   */
  private boolean isValidCapacity(Route route, int boardingStop, int destinationStop) {
    if (!route.hasCapacity(boardingStop, destinationStop)) {
      System.out.println("Sorry, the bus is full between those stops.");
      return false;
    }
    return true;
  }

  /**
   * Parses the user's answer to the express trip question.
   *
   * @param answer the user's answer
   * @return true if the user answered yes, false if the user answered no, null if the answer is invalid
   */
  private Boolean parseExpressAnswer(String answer) {
    if ("yes".equals(answer) || "y".equals(answer)) {
      return Boolean.TRUE;
    }
    if ("no".equals(answer) || "n".equals(answer)) {
      return Boolean.FALSE;
    }
    return null;
  }

  /**
   * Checks if the express trip is valid for the given route and stops.
   *
   * @param route          the route
   * @param boardingStop   the boarding stop
   * @param destinationStop the destination stop
   * @param expressTrip    whether the trip is express
   * @return true if the express trip is valid, false otherwise
   */
  private boolean isValidExpressTrip(Route route, int boardingStop, int destinationStop, boolean expressTrip) {
    Stop boarding = route.findStop(boardingStop);
    Stop destination = route.findStop(destinationStop);

    if (expressTrip) {
      if (!boarding.isExpress()) {
        System.out.println("Sorry, boarding stop " + boardingStop + " is not an express stop.");
        return false;
      }
      if (!destination.isExpress()) {
        System.out.println("Sorry, destination stop " + destinationStop + " is not an express stop.");
        return false;
      }
    } else {
      if (boarding.isExpress()) {
        System.out.println("Sorry, boarding stop " + boardingStop + " is an express stop and requires an express trip.");
        return false;
      }
      if (destination.isExpress()) {
        System.out.println("Sorry, destination stop " + destinationStop + " is an express stop and requires an express trip.");
        return false;
      }
    }
    return true;
  }
}
