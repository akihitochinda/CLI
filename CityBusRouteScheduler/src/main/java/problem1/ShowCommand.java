package problem1;


/**
 * The type Show command.
 */
public class ShowCommand implements Command {
  private BusNetwork busNetwork;
  private String input;

  /**
   * Instantiates a new Show command.
   *
   * @param busNetwork the bus network
   * @param input      the input
   */
  public ShowCommand(BusNetwork busNetwork, String input) {
    this.busNetwork = busNetwork;
    this.input = input;
  }

  @Override
  public void execute() {
    try {
      Integer routeNumber = extractRouteNumber(input);
      // create route object
      Route route = busNetwork.findRouteByNumber(routeNumber);
      if (!isValidRoute(route, routeNumber))
        return;
      // show routes - the first line
      StringBuilder sb = new StringBuilder();
      sb.append(String.format("Route %d – %s (Capacity: %d)\n",
          route.getRouteNumber(),
          route.getRouteName(),
          route.getMaxPassengerCapacity()));
      // display each single stop
      for (Stop stop : route) {
        int onBoard = route.passengersOnBoardBetween(
            stop.getStopNumber(), stop.getStopNumber() + 1);
        // check if the stop is express
        String expressTag;
        if (stop.isExpress()) {
          expressTag = " [E]";
        } else {
          expressTag = "";
        }
        sb.append(String.format("  Stop %d [%s]%s %d passengers aboard\n",
            stop.getStopNumber(),
            stop.getStopName(),
            expressTag, // [E] tag after the stop name
            onBoard));
      }
      System.out.println(sb);
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
}
