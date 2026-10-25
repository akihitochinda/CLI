package option2;

import java.util.Map;

/**
 * DESIGN PATTERN: Factory Method (Creator)
 *
 * Centralizes the decision of which AnalyzerMode to instantiate.
 * sentimentAnalyzer.main calls create() and receives an
 * AnalyzerMode without knowing whether it is interactive or batch.
 *
 * This is appropriate because the selection logic — checking the --report flag
 * — is a single, well-defined decision point. Moving it here means:
 *   1. main stays clean: it only calls mode.run().
 *   2. Adding a third mode (e.g. --json) requires adding one new AnalyzerMode
 *      implementation and one branch here — no other file changes.
 */
public class AnalyzerModeFactory {

  /**
   * Produces the correct AnalyzerMode based on the parsed command-line arguments.
   *
   * DESIGN PATTERN: Factory Method
   * The factory method pattern is used here instead of a constructor because the
   * type of object to create depends on a runtime condition (isReportMode()).
   * The caller is decoupled from both concrete types.
   *
   * @param parser        the parsed and validated command-line arguments
   * @param lexicon       the loaded sentiment lexicon
   * @param reviewManager manages access to the review .txt files
   * @return an InteractiveMode or BatchMode as appropriate
   */
  public static AnalyzerMode create(CommandLineParser parser,
      Map<String, Double> lexicon,
      ReviewFileManager reviewManager) {

    if (parser.isReportMode()) {
      // DESIGN PATTERN: Factory Method — produces BatchMode (Concrete Product)
      return new BatchMode(parser.getInputDir(), lexicon, reviewManager);
    } else {
      // DESIGN PATTERN: Factory Method — produces InteractiveMode (Concrete Product)
      return new InteractiveMode(lexicon, reviewManager);
    }
  }
}
