package problem1;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class Route, which is a single bus route that extend ArrayList of Stop objects.
 */
public class Route extends ArrayList<Stop> {
  private Integer routeNumber;
  private String routeName;
  private Integer maxPassengerCapacity;
  private List<Integer> expressStopNumbers;

  /**
   * Constructor for Route that takes a route number, route name, a list of stop names, and a
   * capacity as arguments.
   *
   * @param routeNumber          the route number
   * @param routeName            the route name
   * @param maxPassengerCapacity the max passenger capacity
   * @param expressStopNumbers   the express stop numbers
   * @param stopNames            the stop names
   */
  public Route(Integer routeNumber, String routeName, Integer maxPassengerCapacity, List<Integer> expressStopNumbers, List<String> stopNames) {
    this.routeNumber = routeNumber;
    this.routeName = routeName;
    this.maxPassengerCapacity = maxPassengerCapacity;
    this.expressStopNumbers = expressStopNumbers;
    this.validateRoute(stopNames);
    this.validateCapacity();
    this.addStops(stopNames);
  }

  /**
   * Method to add the stopName to the list of Strings
   * @param stopNames as list of Strings
   */
  private void addStops(List<String> stopNames) {
    int stopNumber = 1;
    for (String stopName : stopNames) {
      if (isExpressStop(stopNumber)) {
        this.add(new ExpressStop(stopNumber, stopName, new ArrayList<>()));
      } else {
        this.add(new Stop(stopNumber, stopName, new ArrayList<>())); //PassengerList is initially empty
      }
      stopNumber++;
    }
  }

  /**
   * Method to check if it is express stop
   *
   * @param stopNumber the stop number
   * @return the boolean
   */
  public boolean isExpressStop(Integer stopNumber) {
    return expressStopNumbers.contains(stopNumber); // ← hidden inside Route
  }

  /**
   * Gets route number.
   * @return the route number, an Integer
   */
  public Integer getRouteNumber() {
    return routeNumber;
  }

  /**
   * Gets route name.
   * @return the route name
   */
  public String getRouteName() {
    return routeName;
  }

  /**
   * Gets max Passenger Capacity
   * @return the max passenger capacity, an Integer
   */
  public Integer getMaxPassengerCapacity() {
    return maxPassengerCapacity;
  }

  /**
   * Method to validate the route with at least 2 stops
   * @param stopNames, stop name, as list of Strings
   */
  private void validateRoute(List<String> stopNames) {
      if (stopNames == null || stopNames.size() < 2) {
        throw new IllegalArgumentException("A route must have at least 2 stops.");
      }
    // at least one expressStop for each Route
    if (expressStopNumbers == null || expressStopNumbers.isEmpty()) {
      throw new IllegalArgumentException("A route must have at least one express stop.");
    }
  }

  /**
   * Check the validation of the capacity to be at least one.
   */
  private void validateCapacity() {
    if (maxPassengerCapacity < 1) {
      throw new IllegalArgumentException("Capacity must be at least 1.");
    }
  }


  /**
   * Has capacity boolean.
   * @param boardingStop    the boarding stop, an integer
   * @param destinationStop the destination stop, an integer
   * @return the boolean
   */
  public boolean hasCapacity(int boardingStop, int destinationStop) {
    for (int i = boardingStop; i < destinationStop; i++) {
      if (passengersOnBoardBetween(i, i + 1) >= maxPassengerCapacity) {
        return false;
      }
    }
    return true;
  }

  /**
   * Find a stop number
   * @param stopNumber the stop number, an integer
   * @return the stop
   */
  public Stop findStop(Integer stopNumber) {
    for (Stop stop : this) {
      if (stop.getStopNumber().equals(stopNumber)) {
        return stop;
      }
    }
    return null;
  }

  /**
   * Method to check the number of people boarding between the stop
   * @param boardingStop the boarding stop, an integer
   * @param destinationStop the destination stop, an integer
   * @return the number of people
   */
  public int passengersOnBoardBetween(int boardingStop, int destinationStop) {
    int count = 0;
    for (Stop stop : this) {
      for (Passenger passenger : stop.getPassengerList()) {
        if (passenger.getBoardingNumber() <= boardingStop &&
            passenger.getDestinationNumber() >= destinationStop) {
          count++;
        }
      }
    }
    return count;
  }

  @Override
  public String toString() {
    return "Route{" +
        "routeNumber=" + routeNumber +
        ", routeName='" + routeName + '\'' +
        ", maxPassengerCapacity=" + maxPassengerCapacity +
        ", expressStopNumbers=" + expressStopNumbers +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    Route stops = (Route) o;
    return Objects.equals(routeNumber, stops.routeNumber) && Objects.equals(
        routeName, stops.routeName) && Objects.equals(maxPassengerCapacity,
        stops.maxPassengerCapacity) && Objects.equals(expressStopNumbers,
        stops.expressStopNumbers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), routeNumber, routeName, maxPassengerCapacity,
        expressStopNumbers);
  }
}
