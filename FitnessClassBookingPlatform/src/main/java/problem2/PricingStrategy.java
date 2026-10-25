package problem2;

/**
 * The interface Pricing strategy.
 * 
 * Strategy pattern: PricingStrategy is the strategy interface, and each concrete strategy implements this interface.
 * The context class will use a PricingStrategy to calculate the price for a gym class booking, allowing for different pricing algorithms to be used interchangeably and swapped at runtime.
 */
public interface PricingStrategy {

  /**
   * Calculate price double.
   *
   * @param gymClass      the gym class, as GymClass
   * @param memberName    the member name, as String
   * @param priorBookings the prior bookings, as int
   * @return the double
   */
  double calculatePrice(GymClass gymClass, String memberName, int priorBookings);

  /**
   * Gets name.
   *
   * @return the name
   */
  String getName();
}
