package problem1;

import java.util.List;

/**
 * The class Express stop.
 */
public class ExpressStop extends Stop {

  /**
   * Instantiates a new Express stop.
   * @param stopNumber    the stop number, as an Integer
   * @param stopName      the stop name, as a String
   * @param passengerList the passenger list, as a list of passenger
   */
  public ExpressStop(Integer stopNumber, String stopName, List<Passenger> passengerList) {
    super(stopNumber, stopName, passengerList);
  }

  @Override
  public boolean isExpress() {
    return true;
  }

}
