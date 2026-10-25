package problem2;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookingManagerIntegrationTest {

  private final PrintStream originalOut = System.out;
  private final java.io.InputStream originalIn = System.in;
  private ByteArrayOutputStream outContent;

  @BeforeEach
  void setUp() {
    outContent = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outContent));
  }

  @AfterEach
  void tearDown() {
    System.setOut(originalOut);
    System.setIn(originalIn);
  }

  @Test
  void mainShouldProcessPricingAndBookingFlow() {
    String userInput = String.join(System.lineSeparator(),
        "pricing peak",
        "book 1",
        "Alice",
        "show 1",
        "done") + System.lineSeparator();

    System.setIn(new ByteArrayInputStream(userInput.getBytes()));

    Main.main(new String[0]);

    String output = outContent.toString();
    assertTrue(output.contains("Pricing strategy set to peak"), "Should confirm pricing strategy change.");
    assertTrue(output.contains("Price: $18.00"), "Peak pricing should apply to the 09:00 class.");
    assertTrue(output.contains("Alice has been booked into Yoga on Mon 09:00"), "Booking message should appear.");
    assertTrue(output.contains("Stay fit!"), "Main should end with the goodbye message.");
  }

  @Test
  void bookCommandShouldIntegrateWithBookingManager() {
    BookingManager manager = new BookingManager();
    Scanner sc = new Scanner("Bob" + System.lineSeparator());
    new BookCommand(manager, 2, sc).execute();

    String output = outContent.toString();
    assertTrue(output.contains("Your name?"));
    assertTrue(output.contains("Price: $18.00"), "HIIT should be priced at $18.00 with the default flat pricing on Mon 18:00.");
    assertTrue(output.contains("Bob has been booked into HIIT on Mon 18:00"));

    outContent.reset();
    manager.show(2);
    String showOutput = outContent.toString();
    assertTrue(showOutput.contains("HIIT"));
    assertTrue(showOutput.contains("Bob"), "Show should list the booked member.");
  }

  @Test
  void fastPayAdapterShouldFetchTransactionLog() {
    PaymentProcessor processor = new FastPayAdapter();

    String log = processor.fetchTransactionLog("Alice");

    assertEquals("FastPay transactions for Alice", log);
  }

  @Test
  void browseCommandShouldDisplayAllClasses() {
    BookingManager manager = new BookingManager();
    new BrowseCommand(manager).execute();

    String output = outContent.toString();
    assertTrue(output.contains("ID Name Instructor Date/Time Spots"));
    assertTrue(output.contains("Yoga"));
    assertTrue(output.contains("HIIT"));
    assertTrue(output.contains("Spin"));
  }

  @Test
  void cloneCommandShouldCreateNewClassWithNewDateTime() {
    BookingManager manager = new BookingManager();
    Scanner sc = new Scanner("Wed 10:00" + System.lineSeparator());
    new CloneCommand(manager, 1, sc).execute();

    String output = outContent.toString();
    assertTrue(output.contains("Class cloned successfully! New class ID: 4"));

    outContent.reset();
    manager.show(4);
    String showOutput = outContent.toString();
    assertTrue(showOutput.contains("Wed 10:00"), "Cloned class should use the new date/time.");
    assertTrue(showOutput.contains("Yoga"), "Cloned class should preserve the original name.");
  }

  @Test
  void cancelCommandShouldRemoveExistingBooking() {
    BookingManager manager = new BookingManager();
    Scanner bookScanner = new Scanner("Charlie" + System.lineSeparator());
    new BookCommand(manager, 3, bookScanner).execute();

    outContent.reset();
    Scanner cancelScanner = new Scanner("Charlie" + System.lineSeparator());
    new CancelCommand(manager, 3, cancelScanner).execute();

    String output = outContent.toString();
    assertTrue(output.contains("Your name?"));
    assertTrue(output.contains("Booking cancelled."));
  }

  @Test
  void scheduleCommandShouldAddNewClassToManager() {
    BookingManager manager = new BookingManager();
    String scheduleInput = String.join(System.lineSeparator(),
        "Pilates",
        "Nina",
        "Studio D",
        "Beginner",
        "Mat,Block",
        "Thu 11:00",
        "8",
        "22.5") + System.lineSeparator();
    Scanner sc = new Scanner(scheduleInput);
    new ScheduleCommand(manager, sc).execute();

    String output = outContent.toString();
    assertTrue(output.contains("New class scheduled successfully! Class ID: 4"));

    outContent.reset();
    manager.show(4);
    String showOutput = outContent.toString();
    assertTrue(showOutput.contains("Pilates"));
    assertTrue(showOutput.contains("Thu 11:00"));
  }

  @Test
  void peakPricingShouldReturnBasePriceWhenNotPeak() {
    PeakPricing pricing = new PeakPricing();
    GymClass gymClass = new GymClass("Spin", "Leila", "Studio C", "Advanced",
        List.of("None"), "Mon 10:00", 15, 20.0);

    double price = pricing.calculatePrice(gymClass, "Bob", 0);

    assertEquals(20.0, price, 0.0001, "Non-peak times should return base price.");
  }

  @Test
  void peakPricingShouldReturnStrategyName() {
    PeakPricing pricing = new PeakPricing();

    assertEquals("peak", pricing.getName());
  }

  @Test
  void pricingCommandShouldSetFlatStrategy() {
    BookingManager manager = new BookingManager();
    new PricingCommand(manager, "flat").execute();

    String output = outContent.toString();
    assertTrue(output.contains("Pricing strategy set to flat"));

    outContent.reset();
    manager.book(1, "Alice");
    String bookOutput = outContent.toString();
    assertTrue(bookOutput.contains("Price: $15.00"), "Flat strategy should preserve the base price.");
  }

  @Test
  void flatPricingShouldReturnStrategyName() {
    FlatRatePricing pricing = new FlatRatePricing();

    assertEquals("flat", pricing.getName());
  }

  @Test
  void pricingCommandShouldSetDiscountStrategy() {
    BookingManager manager = new BookingManager();
    new PricingCommand(manager, "discount").execute();

    String output = outContent.toString();
    assertTrue(output.contains("Pricing strategy set to discount"));
  }

  @Test
  void loyaltyPricingShouldApplyDiscountAfterMoreThanFiveBookings() {
    BookingManager manager = new BookingManager();
    manager.setPricingStrategy(new LoyaltyPricing());

    manager.book(1, "Alice");
    manager.book(2, "Alice");
    manager.book(3, "Alice");

    manager.scheduleNewClass("Pilates", "Nina", "Studio D", "Beginner",
        List.of("Mat"), "Thu 11:00", 10, 20.0);
    manager.scheduleNewClass("Barre", "Leo", "Studio E", "Intermediate",
        List.of("None"), "Fri 12:00", 10, 20.0);
    manager.scheduleNewClass("Boxing", "Mia", "Studio F", "Advanced",
        List.of("Gloves"), "Sat 14:00", 10, 20.0);
    manager.scheduleNewClass("Stretch", "Joy", "Studio G", "Beginner",
        List.of("Mat"), "Sun 10:00", 10, 20.0);

    manager.book(4, "Alice");
    manager.book(5, "Alice");
    manager.book(6, "Alice");

    outContent.reset();
    manager.book(7, "Alice");

    String output = outContent.toString();
    assertTrue(output.contains("Price: $18.00"), "Loyalty pricing should apply a 10% discount after 6 prior bookings.");
    assertTrue(output.contains("Alice has been booked into Stretch on Sun 10:00"));
  }

  @Test
  void loyaltyPricingShouldReturnStrategyName() {
    LoyaltyPricing pricing = new LoyaltyPricing();

    assertEquals("loyalty", pricing.getName());
  }

  @Test
  void pricingCommandShouldHandleUnknownStrategy() {
    BookingManager manager = new BookingManager();
    new PricingCommand(manager, "gold").execute();

    String output = outContent.toString();
    assertTrue(output.contains("Unknown pricing strategy."), "Unknown pricing strategy should print an error.");
  }

  @Test
  void bookingManagerShouldRejectInvalidClassId() {
    BookingManager manager = new BookingManager();
    manager.book(999, "Alice");

    String output = outContent.toString();
    assertTrue(output.contains("Invalid class ID."), "Booking with an invalid class ID should be rejected.");
  }

  @Test
  void bookingManagerShouldRejectBookingWhenClassIsFull() {
    BookingManager manager = new BookingManager();
    for (int i = 0; i < 10; i++) {
      manager.book(1, "Member" + i);
    }

    outContent.reset();
    manager.book(1, "LateMember");

    String output = outContent.toString();
    assertTrue(output.contains("Sorry, that class is full."), "Booking should be rejected once the class reaches capacity.");
  }

  @Test
  void bookingManagerShouldRejectBlankMemberNamePayment() {
    BookingManager manager = new BookingManager();

    manager.book(1, "   ");

    String output = outContent.toString();
    assertTrue(output.contains("Price: $15.00"), "Booking should still calculate a price before payment.");
    assertTrue(output.contains("Payment failed. Booking not confirmed."), "Blank member names should fail payment.");
  }

  @Test
  void peakPricingShouldReturnPeakSurchargeForPeakTime() {
    PeakPricing pricing = new PeakPricing();
    GymClass gymClass = new GymClass("Yoga", "Sarah", "Studio A", "Beginner",
        List.of("Mat", "Block"), "Mon 08:00", 10, 15.0);

    double price = pricing.calculatePrice(gymClass, "Bob", 0);

    assertEquals(18.0, price, 0.0001, "Peak times should apply the 20% surcharge.");
  }

  @Test
  void mainShouldParseBrowseCloneCancelAndScheduleCommands() {
    String userInput = String.join(System.lineSeparator(),
        "browse",
        "book 2",
        "Charlie",
        "cancel 2",
        "Charlie",
        "clone 1",
        "Wed 10:00",
        "schedule",
        "Pilates",
        "Nina",
        "Studio D",
        "Beginner",
        "Mat,Block",
        "Thu 11:00",
        "8",
        "22.5",
        "done") + System.lineSeparator();

    System.setIn(new ByteArrayInputStream(userInput.getBytes()));
    Main.main(new String[0]);

    String output = outContent.toString();
    assertTrue(output.contains("ID Name Instructor Date/Time Spots"), "Browse command should print the class list header.");
    assertTrue(output.contains("Charlie has been booked into HIIT on Mon 18:00"), "Book command should confirm booking via Main.");
    assertTrue(output.contains("Booking cancelled."), "Cancel command should confirm cancellation.");
    assertTrue(output.contains("Class cloned successfully! New class ID: 4"), "Clone command should create a new class.");
    assertTrue(output.contains("New class scheduled successfully!"), "Schedule command should create a new class.");
  }

  @Test
  void gymClassShouldReturnStudioDifficultyAndEquipment() {
    GymClass gymClass = new GymClass("Yoga", "Sarah", "Studio A", "Beginner",
        List.of("Mat", "Block"), "Mon 09:00", 10, 15.0);

    assertEquals("Studio A", gymClass.getStudio());
    assertEquals("Beginner", gymClass.getDifficulty());
    assertEquals(List.of("Mat", "Block"), gymClass.getEquipment());
  }

  @Test
  void fastPayProcessorShouldValidateCharges() {
    FastPayProcessor processor = new FastPayProcessor();

    assertTrue(processor.initiateCharge("Alice", 100.0));
    assertFalse(processor.initiateCharge("   ", 100.0));
    assertFalse(processor.initiateCharge("Alice", 0.0));
    assertFalse(processor.initiateCharge("Alice", -50.0));
  }
}
