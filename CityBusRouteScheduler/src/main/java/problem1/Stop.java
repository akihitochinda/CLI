package problem1;

import java.util.List;
import java.util.Objects;

/**
 * Class stop which is a bus stop.
 */
public class Stop {
  private Integer stopNumber;
  private String stopName;
  private List<Passenger> passengerList;

  /**
   * Instantiates a new Stop.
   * @param stopNumber    the stop number, as a String
   * @param stopName      the stop name, as a String
   * @param passengerList the passenger list, as a list of passengers
   */
  public Stop(Integer stopNumber, String stopName, List<Passenger> passengerList) {
    this.stopNumber = stopNumber;
    this.stopName = stopName;
    this.passengerList = passengerList;
  }

  /**
   * Gets stop number.
   * @return the stop number, an Integer
   */
  public Integer getStopNumber() {
    return stopNumber;
  }

  /**
   * Gets stop name.
   * @return the stop name, as a String
   */
  public String getStopName() {
    return stopName;
  }

  /**
   * Gets passenger list.
   * @return the passenger list, as a list of passenger
   */
  public List<Passenger> getPassengerList() {
    return passengerList;
  }

  /**
   * Method to add passenger to list
   * @param passenger the passenger
   */
  public void addPassenger(Passenger passenger) {
    passengerList.add(passenger);
  }

  /**
   * Passenger size int, method to check the passenger list size.
   * @return the number of passenger
   */
  public int passengerSize() {
    return passengerList.size();
  }

  /**
   * Check if it is express stop
   * @return the boolean
   */
  public boolean isExpress() {
    return false;
  }

  @Override
  public String toString() {
    return "Stop{" +
        "stopNumber=" + stopNumber +
        ", stopName='" + stopName + '\'' +
        ", passengerList=" + passengerList +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Stop stop = (Stop) o;
    return Objects.equals(stopNumber, stop.stopNumber) && Objects.equals(stopName,
        stop.stopName) && Objects.equals(passengerList, stop.passengerList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(stopNumber, stopName, passengerList);
  }
}
