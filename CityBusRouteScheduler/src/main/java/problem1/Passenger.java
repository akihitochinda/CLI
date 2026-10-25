package problem1;

import java.util.Objects;

/**
 * The class Passenger, which is a passenger on Route
 */
public class Passenger {
  private String name;
  private Integer boardingNumber;
  private Integer destinationNumber;

  /**
   * Instantiates a new Passenger.
   * @param name              the name, as a String
   * @param boardingNumber    the boarding number, as an Integer
   * @param destinationNumber the destination number, as an Integer
   */
  public Passenger(String name, Integer boardingNumber, Integer destinationNumber) {
    this.name = name;
    this.boardingNumber = boardingNumber;
    this.destinationNumber = destinationNumber;
  }

  /**
   * Gets name of the passenger
   * @return the name, as a String
   */
  public String getName() {
    return name;
  }

  /**
   * Gets boarding number.
   * @return the boarding number, as an Integer
   */
  public Integer getBoardingNumber() {
    return boardingNumber;
  }

  /**
   * Gets destination number.
   * @return the destination number, as an Integer
   */
  public Integer getDestinationNumber() {
    return destinationNumber;
  }

  @Override
  public String toString() {
    return "Passenger{" +
        "name='" + name + '\'' +
        ", boardingNumber=" + boardingNumber +
        ", destinationNumber=" + destinationNumber +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Passenger passenger = (Passenger) o;
    return Objects.equals(name, passenger.name) && Objects.equals(boardingNumber,
        passenger.boardingNumber) && Objects.equals(destinationNumber,
        passenger.destinationNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, boardingNumber, destinationNumber);
  }
}
