package problem2;

/**
 * The type Loyalty pricing.
 * Member discount — members who have booked more than 5 classes in total receive a
 * percentage discount on any new booking.
 */
public class LoyaltyPricing implements PricingStrategy {

  private static final double DISCOUNT_PERCENTAGE = 0.10; // 10% discount
  private static final double DISCOUNT_MULTIPLIER = 1.0 - DISCOUNT_PERCENTAGE;
  private static final int DISCOUNT_THRESHOLD = 5;

  @Override
  public double calculatePrice(GymClass gymClass, String memberName, int priorBookings) {
    double base = gymClass.getBasePrice();
    if (priorBookings > DISCOUNT_THRESHOLD) {
      return base * DISCOUNT_MULTIPLIER;
    }
    return base;
  }

    @Override
    public String getName() {
        return "loyalty";
    }
}
