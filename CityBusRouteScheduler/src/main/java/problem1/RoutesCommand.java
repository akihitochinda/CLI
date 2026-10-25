package problem1;


/**
 * The type Routes command.
 */
public class RoutesCommand implements Command {
  private BusNetwork busNetwork;

  /**
   * Instantiates a new Routes command.
   *
   * @param busNetwork the bus network
   */
  public RoutesCommand(BusNetwork busNetwork) {
    this.busNetwork = busNetwork;
  }

  @Override
  public void execute() {
    StringBuilder sb = new StringBuilder();
    sb.append("Available routes:\n");
    for (Route route : busNetwork.getRouteList()) {
      sb.append(String.format("  Route %d – %s (%d stops, capacity: %d)\n",
          route.getRouteNumber(),
          route.getRouteName(),
          route.size(),
          route.getMaxPassengerCapacity()));
    }
    System.out.println(sb.toString());
  }
}
