package option2;

import java.io.IOException;

/**
 * Entry point for the Sentiment Analyzer.
 *
 * Usage:
 *   sentimentAnalyzer directory           — interactive mode (Part 1)
 *   sentimentAnalyzer directory --report  — batch report mode (Part 2)
 *
 * DESIGN PATTERN: Factory Method
 * main() does not decide which mode to run — it delegates that decision to
 * AnalyzerModeFactory#create, which returns an AnalyzerMode.
 * main() then calls mode.run() without knowing whether it is running
 * interactive or batch mode. Adding a new mode requires no changes here.
 *
 * DESIGN PATTERN: Strategy
 * The sentiment-scoring algorithm is encapsulated behind the ScoringStrategy
 * interface. Both modes use SentimentScoreGenerator as the concrete
 * strategy. Swapping the algorithm requires no changes to main(), the modes,
 * or ReportGenerator.
 */
public class sentimentAnalyzer {

  /**
   * The entry point of the application.
   *
   * @param args the input arguments, as String[]
   */
  public static void main(String[] args) {
    try {
      CommandLineParser parser  = new CommandLineParser(args);
      CSVParser         csv     = new CSVParser(new String[]{parser.getCsvFile()});
      ReviewFileManager reviews = new ReviewFileManager(parser.getInputDir());

      if (!csv.getLog().isEmpty()) {
        System.out.println(csv.getLog().toString());
        return;
      }

      System.out.println("Loading lexicon from " + parser.getCsvFile()
          + "... " + csv.getData().size() + " words loaded.");

      // DESIGN PATTERN: Factory Method
      // The factory decides which AnalyzerMode to create; main only calls run().
      AnalyzerMode mode = AnalyzerModeFactory.create(parser, csv.getData(), reviews);
      mode.run();

    } catch (InvalidArgumentsException e) {
      System.out.println(e.getMessage());
    } catch (IOException e) {
      System.out.println("Error writing report: " + e.getMessage());
    }
  }
}
