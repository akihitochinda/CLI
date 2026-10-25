package problem2;

/**
 * The type Peak pricing.
 * Peak/off-peak — classes scheduled between 07:00–09:00 and 17:00–19:00 cost more than off-peak classes.
 */
public class PeakPricing implements PricingStrategy {

  private static final double PEAK_SURCHARGE_RATIO = 1.2; // 20% more for peak times
  private static final int MORNING_PEAK_START = 7;
  private static final int MORNING_PEAK_END = 9;
  private static final int EVENING_PEAK_START = 17;
  private static final int EVENING_PEAK_END = 19;

    @Override
    public double calculatePrice(GymClass gymClass, String memberName, int priorBookings) {
        double base = gymClass.getBasePrice();
        if (isPeak(gymClass.getDateTime())) {
            return base * PEAK_SURCHARGE_RATIO;
        }
        return base;
    }

    private boolean isPeak(String dateTime) {
        // simple parse: "Mon 08:00"
        int hour = Integer.parseInt(dateTime.split(" ")[1].split(":")[0]);
        return (hour >= MORNING_PEAK_START && hour <= MORNING_PEAK_END)
            || (hour >= EVENING_PEAK_START && hour <= EVENING_PEAK_END);
    }

    @Override
    public String getName() {
        return "peak";
    }
}
