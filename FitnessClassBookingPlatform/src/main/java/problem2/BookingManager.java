package problem2;

import java.util.*;

// Receiver classes - these know how to perform the actual work

/**
 * The type Booking manager.
 */
// BookingManager receiver
public class BookingManager {

  private final Map<Integer, GymClass> classes = new HashMap<>();
  private final Map<String, Integer> bookingCounts = new HashMap<>();
  private PricingStrategy pricingStrategy = new FlatRatePricing(); // pricing strategy always starts with new flat rate price
  private final PaymentProcessor paymentProcessor = new FastPayAdapter();
  private int nextId = 1;

  /**
   * Instantiates a new Booking manager.
   */
  public BookingManager() {
    seedData();
  }

  /**
   * Sets pricing strategy.
   *  - The pricing strategy should be swappable at runtime without modifying booking logic.
   *  - Adding a new pricing strategy in the future should require no changes to existing classes.
   *
   * @param strategy the strategy, as PricingStrategy
   */
  public void setPricingStrategy(PricingStrategy strategy) {
    this.pricingStrategy = strategy;
  }

  private void seedData() {
    addClass(new GymClass("Yoga", "Sarah", "Studio A",
        "Beginner", List.of("Mat", "Block"),
        "Mon 09:00", 10, 15.0));
    addClass(new GymClass("HIIT", "Marcus", "Studio B",
        "Intermediate", List.of("None"),
        "Mon 18:00", 12, 18.0));
    addClass(new GymClass("Spin", "Leila", "Studio C",
        "Advanced", List.of("None"),
        "Tue 07:00", 15, 20.0));
  }

  private void addClass(GymClass c) {
    c.setId(nextId++);
    classes.put(c.getId(), c);
  }

  /**
   * "browse" to list all currently scheduled classes with their details and available spots;
   * The browse command should show each class on a single line with a summary
   * (ID, name, instructor, date/time, spots remaining)
   */
  public void browse() {
    System.out.println("ID Name Instructor Date/Time Spots");
    for (GymClass c : classes.values()) {
      System.out.printf("%d %s %s %s %d/%d%n",
          c.getId(),
          c.getName(),
          c.getInstructor(),
          c.getDateTime(),
          c.getSpotsRemaining(),
          c.getCapacity());
    }
  }

  /**
   * "book <class id>" to book a spot in a class;
   *  - When a user books a class, the system should calculate and display the price they will pay under the currently active strategy.
   *    The gym manager can switch the active pricing strategy at runtime using a new "pricing <strategy>" command.
   *  - After pricing is calculated, the system should attempt to process payment through FastPay.
   *    If the charge fails, the booking should not be confirmed.
   *
   * @param id         the id, as int
   * @param memberName the member name, as String
   */
  public void book(int id, String memberName) {
    GymClass gymClass = classes.get(id);
    if (!validateClassAndAvailability(gymClass)) {
      return;
    }

    int priorBookings = bookingCounts.getOrDefault(memberName, 0);
    double price = pricingStrategy.calculatePrice(gymClass, memberName, priorBookings);
    System.out.printf("Price: $%.2f%n", price);

    if (!processPayment(memberName, price)) {
      return;
    }

    completeBooking(gymClass, memberName);
  }

  private boolean validateClassAndAvailability(GymClass gymClass) {
    if (gymClass == null) {
      System.out.println("Invalid class ID.");
      return false;
    }
    if (gymClass.getSpotsRemaining() <= 0) {
      System.out.println("Sorry, that class is full.");
      return false;
    }
    return true;
  }

  private boolean processPayment(String memberName, double price) {
    if (!paymentProcessor.charge(memberName, price)) {
      System.out.println("Payment failed. Booking not confirmed.");
      return false;
    }
    return true;
  }

  private void completeBooking(GymClass gymClass, String memberName) {
    if (gymClass.book(memberName)) {
      bookingCounts.merge(memberName, 1, Integer::sum); // Increment booking count for loyalty pricing
      System.out.println(memberName + " has been booked into "
          + gymClass.getName() + " on " + gymClass.getDateTime());
    } else {
      System.out.println("Booking failed. You may already be registered for that class.");
    }
  }

  /**
   * "cancel <class id>" to cancel an existing booking;
   *
   * @param id         the id, as int
   * @param memberName the member name, as String
   */
  public void cancel(int id, String memberName) {
    GymClass c = classes.get(id);
    if (c != null && c.cancel(memberName)) {
      System.out.println("Booking cancelled.");
    } else {
      System.out.println("Cancellation failed.");
    }
  }

  /**
   * "show <class id>" to display full details of a specific class, including who is booked; The
   * show <class id> command should display all details, including the full list of members booked.
   *
   * @param id the id, as int
   */
  public void show(int id) {
    GymClass c = classes.get(id);
    if (c == null) {
      System.out.println("Invalid class ID.");
      return;
    }
    System.out.println(c.toString());
  }

  /**
   * Prototype pattern: The cloneClass method creates a new GymClass instance by cloning an existing one and changing only the date/time.
   * This allows for easy creation of similar classes without modifying the original, adhering to the Prototype design pattern.
   * 
   * "clone <class id>" to create a new class based on an existing one, adjusting only the date and time (gym manager action);
   * Cloning an existing class should produce a fully independent copy — changes to the clone must not affect the original.
   *
   * @param id          the id, as int
   * @param newDateTime the new date time, as String
   */
  public void cloneClass(int id, String newDateTime) {
    GymClass original = classes.get(id);
    if (original == null) {
      System.out.println("Invalid class ID.");
      return;
    }
    GymClass copy = original.cloneWithNewDate(newDateTime);
    addClass(copy);
    System.out.println("Class cloned successfully! New class ID: " + copy.getId());
  }

  /**
   * "schedule" to add a new class to the schedule (gym manager action);
   *
   * @param name       the name, as String
   * @param instructor the instructor, as String
   * @param studio     the studio, as String
   * @param difficulty the difficulty, as String
   * @param equipment  the equipment, as List<String>
   * @param dateTime   the date time, as String
   * @param capacity   the capacity, as int
   * @param basePrice  the base price, as double
   */
  public void scheduleNewClass(String name, String instructor, String studio,
                           String difficulty, List<String> equipment,
                           String dateTime, int capacity, double basePrice) {
    GymClass newClass = new GymClass(name, instructor, studio,
        difficulty, equipment, dateTime, capacity, basePrice);
    addClass(newClass);
    System.out.println("New class scheduled successfully! Class ID: " + newClass.getId());
  }
}
