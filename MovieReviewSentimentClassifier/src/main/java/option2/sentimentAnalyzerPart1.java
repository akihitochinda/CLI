package option2;

import java.util.Scanner;

/**
 * The type Main.
 * It takes one argument: a path to a folder containing .txt review files.
 * On startup, the program loads the sentiment lexicon from the same folder
 * (sentinel_lexicon.csv must be present)
 */
public class sentimentAnalyzerPart1 {

  /**
   * The entry point of application.
   *
   * @param args the input arguments, as String[]
   */
  public static void main(String[] args) {
    try {
      CommandLineParserPart1 parser = new CommandLineParserPart1(args);
      CSVParser processor = new CSVParser(new String[]{parser.getCsvFile()});
      ReviewFileManager reviewManager = new ReviewFileManager(parser.getInputDir());
      
      if (processor.getLog().isEmpty()) {
        System.out.println("Loading lexicon from " + parser.getCsvFile() + "... " + processor.getData().size() + " words loaded.");
        System.out.println("Scanning reviews folder... " + reviewManager.getFileCount() + " file(s) found.\n");

        Scanner sc = new Scanner(System.in);

        // Scans the folder and lists all .txt files by number
        reviewManager.printReviewList();

        while (true) {
          // Prompts the user to select a file by entering its number, or q to quit
          System.out.print("Which would you like to analyze? (q to quit): ");
          String input = sc.nextLine();
          
          if (input.equals("q")) {
            System.out.println("Goodbye.");
            break;
          }
          
          // If the user enters an invalid selection (e.g., a number outside the range, or non-numeric
          // input), the program should print a clear error and re-prompt
          int index;
          try {
            index = Integer.parseInt(input);
            if (index < 1 || index > reviewManager.getFileCount()) {
              System.out.println("Error: please enter a number between 1 and " + reviewManager.getFileCount() + ", or q to quit.");
              continue;
            }
          } catch (NumberFormatException e) {
            System.out.println("Error: please enter a number between 1 and " + reviewManager.getFileCount() + ", or q to quit.");
            continue;
          }

          // Displays the full review text
          String fileName = reviewManager.getReviewFiles().get(index);
          String reviewText = reviewManager.readReviewFile(index);
            
          System.out.println("--- " + fileName + " ---");
          System.out.print(reviewText);
          System.out.println();

          // Displays its computed sentiment score (rounded to 2 decimal places), and its label.
          SentimentScoreGeneratorPart1 result = new SentimentScoreGeneratorPart1(processor.getData(), reviewText);
          System.out.println(result.toString());
          System.out.println("-------------------------------");

          // Asks whether the user wants to analyze another review
          System.out.print("Analyze another? (y/n): ");
          String anotherInput = sc.nextLine().toLowerCase();

          // If yes, re-displays the list. If no or q, exits cleanly
          if (anotherInput.equals("y")) {
            System.out.println();
            reviewManager.printReviewList();
          } else if (anotherInput.equals("n") || anotherInput.equals("q")) {
            System.out.println("Goodbye.");
            break;
          } else {
            System.out.println("Error: please enter y or n.");
          }
        }
        sc.close();
      } else {
        System.out.println(processor.getLog().toString());
      }
    } catch (InvalidArgumentsException e) {
      System.out.println(e.getMessage());
    }
  }
}