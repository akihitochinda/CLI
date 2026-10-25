package problem;

import java.util.Scanner;

/**
 * The type Main.
 */
public class Main {

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {

    Scanner sc = new Scanner(System.in);
    SocialPostManager manager = new SocialPostManager();

    while (true) {
      System.out.println("What would you like to do?");
      String input = sc.nextLine();

      if (input.equals("done")) {
        System.out.println("Goodbye!");
        break;
      }

      Command command = parseCommand(input, manager, sc);
      if (command != null) {
        command.execute();
      } else {
        System.out.println("Invalid command. Try again.");
      }
    }
    sc.close();
  }

  private static Command parseCommand(String input, SocialPostManager manager, Scanner sc) {
    String[] commands = input.split(" ");
    try {
      return switch (commands[0]) {
        case "create" -> new CreateCommand(manager, sc);
        case "feature" -> new AddFeatureCommand(manager, commands[1]);
        case "strategy" -> new StrategyCommand(manager, commands[1]);
        case "save" -> new SaveTemplateCommand(manager, commands[1]);
        case "clone" -> new CloneCommand(manager, commands[1]);
        case "publish" -> new PublishCommand(manager);
        case "like" -> new LikeCommand(manager);
        case "share" -> new ShareCommand(manager);
        default -> null;
      };
    } catch (Exception e) {
      return null;
    }
  }
}
