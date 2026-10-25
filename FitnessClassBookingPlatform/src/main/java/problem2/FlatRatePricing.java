package problem2;

/**
 * The type Flat rate pricing.
 * Flat rate — every member pays the same fixed price.
 */
public class FlatRatePricing implements PricingStrategy {

  @Override
  public double calculatePrice(GymClass gymClass, String memberName, int priorBookings) {
      return gymClass.getBasePrice();
  }
  
  @Override
  public String getName() {
      return "flat";
  }
}
