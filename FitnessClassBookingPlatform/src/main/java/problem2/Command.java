package problem2;

import java.util.Scanner;
import java.util.List;

/**
 * The interface Command.
 * 
 * Commad pattern: encapsulates a request as an object, allowing for parameterization and queuing of requests.
 * Each command implements the Command interface and defines its own execute() method, making it easy to add new commands without changing existing code.
 */
// Command interface - defines the contract for all commands
interface Command {

  /**
   * Execute.
   */
  void execute();
}

// Concrete Commands - implement the Command interface

/**
 * The type Browse command.
 */
// Browse Command
class BrowseCommand implements Command {

  private final BookingManager manager;

  /**
   * Instantiates a new Browse command.
   *
   * @param manager the manager, as BookingManager
   */
  public BrowseCommand(BookingManager manager) {
    this.manager = manager;
  }

  @Override
  public void execute() {
    manager.browse();
  }
}

/**
 * The type Book command.
 */
// Book Command
class BookCommand implements Command {

  private final BookingManager manager;
  private final int classId;
  private final Scanner sc;

  /**
   * Instantiates a new Book command.
   *
   * @param manager the manager, as BookingManager
   * @param classId the class id, as int
   * @param sc      the sc, as Scanner
   */
  public BookCommand(BookingManager manager, int classId, Scanner sc) {
    this.manager = manager;
    this.classId = classId;
    this.sc = sc;
  }

  @Override
  public void execute() {
    System.out.print("Your name? ");
    String name = sc.nextLine();
    manager.book(classId, name);
  }
}

/**
 * The type Show command.
 */
// Show Command
class ShowCommand implements Command {
  
  private final BookingManager manager;
  private final int classId;

  /**
   * Instantiates a new Show command.
   *
   * @param manager the manager, BookingManager
   * @param classId the class id, as int
   */
  public ShowCommand(BookingManager manager, int classId) {
    this.manager = manager;
    this.classId = classId;
  }

  @Override
  public void execute() {
    manager.show(classId);
  }
}

/**
 * The type Clone command.
 */
// Clone Command (gym manager action)
class CloneCommand implements Command {
  
  private final BookingManager manager;
  private final int classId;
  private final Scanner sc;

  /**
   * Instantiates a new Clone command.
   *
   * @param manager the manager, as BookingManager
   * @param classId the class id, as int
   * @param sc      the sc, as Scanner
   */
  public CloneCommand(BookingManager manager, int classId, Scanner sc) {
    this.manager = manager;
    this.classId = classId;
    this.sc = sc;
  }

  @Override
  public void execute() {
    System.out.print("New date/time? ");
    String newDateTime = sc.nextLine();
    manager.cloneClass(classId, newDateTime);
  }
}

/**
 * The type Cancel command.
 */
// Cancel Command
class CancelCommand implements Command {
  
  private final BookingManager manager;
  private final int classId;
  private final Scanner sc;

  /**
   * Instantiates a new Cancel command.
   *
   * @param manager the manager, as BookingManager
   * @param classId the class id, as int
   * @param sc      the sc, as Scanner
   */
  public CancelCommand(BookingManager manager, int classId, Scanner sc) {
    this.manager = manager;
    this.classId = classId;
    this.sc = sc;
  }

  @Override
  public void execute() {
    System.out.print("Your name? ");
    String name = sc.nextLine();
    manager.cancel(classId, name);
  }
}

/**
 * The type Schedule command.
 */
// Schedule Command (gym manager action)
class ScheduleCommand implements Command {

  private final BookingManager manager;
  private final Scanner sc;

  /**
   * Instantiates a new Schedule command.
   *
   * @param manager the manager, as BookingManager
   * @param sc      the sc, as Scanner
   */
  public ScheduleCommand(BookingManager manager, Scanner sc) {
    this.manager = manager;
    this.sc = sc;
  }

  @Override
  public void execute() {
    System.out.print("Class name? ");
    String name = sc.nextLine();
    System.out.print("Instructor? ");
    String instructor = sc.nextLine();
    System.out.print("Studio room? ");
    String studio = sc.nextLine();
    System.out.print("Difficulty level? ");
    String difficulty = sc.nextLine();
    System.out.print("Required equipment (comma separated)? ");
    String equipmentInput = sc.nextLine();
    String[] equipment = equipmentInput.split("\\s*,\\s*"); //  ignore spaces around comma-separated values
    System.out.print("Date/time? ");
    String dateTime = sc.nextLine();
    System.out.print("Capacity? ");
    int capacity = Integer.parseInt(sc.nextLine());
    System.out.print("Price? ");
    double price = Double.parseDouble(sc.nextLine());
    manager.scheduleNewClass(name, instructor, studio, difficulty, List.of(equipment), dateTime,
        capacity, price);
  }
}

/**
 * The type Pricing command.
 */
// Pricing Command (gym manager action)
class PricingCommand implements Command {

  private final BookingManager manager;
  private final String strategy;

  /**
   * Instantiates a new Pricing command.
   *
   * @param manager  the manager, as BookingManager
   * @param strategy the strategy, as String
   */
  public PricingCommand(BookingManager manager, String strategy) {
    this.manager = manager;
    this.strategy = strategy;
  }

  @Override
  public void execute() {
    switch (strategy) {
      case "flat":
        manager.setPricingStrategy(new FlatRatePricing());
        break;
      case "peak":
        manager.setPricingStrategy(new PeakPricing());
        break;
      case "discount":
        manager.setPricingStrategy(new LoyaltyPricing());
        break;
      default:
        System.out.println("Unknown pricing strategy.");
        return;
    }
    System.out.println("Pricing strategy set to " + strategy);
  }
}
