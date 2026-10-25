package option2;

import java.util.Map;
import java.util.Scanner;

/**
 * DESIGN PATTERN: Factory Method (Concrete Product)
 *
 * One of two concrete AnalyzerMode implementations produced by
 * AnalyzerModeFactory. Encapsulates the full interactive loop from
 * Part 1: listing reviews, prompting for selection, displaying text and score,
 * and looping until the user quits.
 *
 * Isolating this logic in its own class means the interactive behavior can
 * change without touching batch mode or the main entry point.
 */
public class InteractiveMode implements AnalyzerMode {

  private final Map<String, Double> lexicon;
  private final ReviewFileManager reviewManager;

  /**
   * Constructs an InteractiveMode.
   *
   * @param lexicon       the loaded sentiment lexicon
   * @param reviewManager manages access to the review .txt files
   */
  public InteractiveMode(Map<String, Double> lexicon, ReviewFileManager reviewManager) {
    this.lexicon = lexicon;
    this.reviewManager = reviewManager;
  }

  /**
   * Runs the interactive review-selection loop.
   *
   * DESIGN PATTERN: Strategy
   * Internally uses SentimentScoreGenerator as the concrete
   * ScoringStrategy. If the scoring algorithm changes, only the
   * strategy implementation needs to be swapped — this method is unaffected.
   */
  @Override
  public void run() throws InvalidArgumentsException {
    
    System.out.println("Scanning reviews folder... "
    + reviewManager.getFileCount() + " file(s) found.\n");

    Scanner sc = new Scanner(System.in);
    reviewManager.printReviewList();

    while (true) {
      System.out.print("Which would you like to analyze? (q to quit): ");
      String input = sc.nextLine();

      if (input.equals("q")) {
        System.out.println("Goodbye.");
        break;
      }

      int index;
      try {
        index = Integer.parseInt(input);
        if (index < 1 || index > reviewManager.getFileCount()) {
          System.out.println("Error: please enter a number between 1 and "
              + reviewManager.getFileCount() + ", or q to quit.");
          continue;
        }
      } catch (NumberFormatException e) {
        System.out.println("Error: please enter a number between 1 and "
            + reviewManager.getFileCount() + ", or q to quit.");
        continue;
      }

      String fileName   = reviewManager.getReviewFiles().get(index);
      String reviewText = reviewManager.readReviewFile(index);

      System.out.println("--- " + fileName + " ---");
      System.out.print(reviewText);
      System.out.println();

      // DESIGN PATTERN: Strategy — SentimentScoreGenerator is the concrete strategy;
      // it could be replaced with any other ScoringStrategy without changing this code.
      SentimentScoreGenerator result = new SentimentScoreGenerator(lexicon, reviewText);
      System.out.println(result.toString());
      System.out.println("-------------------------------");

      System.out.print("Analyze another? (y/n): ");
      String another = sc.nextLine().toLowerCase();

      if (another.equals("y")) {
        System.out.println();
        reviewManager.printReviewList();
      } else if (another.equals("n") || another.equals("q")) {
        System.out.println("Goodbye.");
        break;
      } else {
        System.out.println("Error: please enter y or n.");
      }
    }
    sc.close();
  }
}
