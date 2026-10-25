package problem2;

import java.util.Scanner;

/**
 * The type Main.
 */
public class Main {

  /**
   * The entry point of application. "done" to shut down the system.
   * If the user enters something unexpected or invalid, give them the opportunity to try again
   * — the program should not crash or terminate.
   *
   * @param args the input arguments, as String[]
   */
  public static void main(String[] args) {

    // Create the client (User typing commands)
    Scanner sc = new Scanner(System.in);

    // Create receivers (bookings)
    BookingManager manager = new BookingManager();

    // Create the invoker (user input / CLI loop)
    while (true) {
      System.out.println("What would you like to do?");
      String input = sc.nextLine();

      if (input.equals("done")) {
        System.out.println("Stay fit!");
        break;
      }

      Command command = parseCommand(input, manager, sc);
      if (command != null) {
        command.execute();
      } else {
        System.out.println("Invalid command. Try again.");
      }
    }
    sc.close(); // use one shared Scanner for the whole CLI and close it once at the end of the program
  }

  // Create commands
  private static Command parseCommand(String input, BookingManager manager, Scanner sc) {
    String[] commands = input.split(" ");

    try {
      return switch (commands[0]) {
        case "browse" -> new BrowseCommand(manager);
        case "book" -> new BookCommand(manager, Integer.parseInt(commands[1]), sc);
        case "show" -> new ShowCommand(manager, Integer.parseInt(commands[1]));
        case "clone" -> new CloneCommand(manager, Integer.parseInt(commands[1]), sc);
        case "cancel" -> new CancelCommand(manager, Integer.parseInt(commands[1]), sc);
        case "schedule" -> new ScheduleCommand(manager, sc);
        case "pricing" -> new PricingCommand(manager, commands[1]);
        default -> null;
      };
    } catch (Exception e) {
      return null;
    }
  }
}