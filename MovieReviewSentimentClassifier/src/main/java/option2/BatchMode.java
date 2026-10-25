package option2;

import java.io.IOException;
import java.util.Map;

/**
 * DESIGN PATTERN: Factory Method (Concrete Product)
 *
 * The second concrete AnalyzerMode produced by AnalyzerModeFactory.
 * Delegates all batch logic to ReportGenerator: scoring every review,
 * writing report.csv, and printing the statistics block.
 *
 * Keeping batch behavior here means it can evolve independently of interactive
 * mode — and sentimentAnalyzer.main stays unaware of either.
 */
public class BatchMode implements AnalyzerMode {

  private final ReportGenerator reportGenerator;

  /**
   * Constructs a BatchMode.
   *
   * @param inputDir      the directory containing review files (used for output path)
   * @param lexicon       the loaded sentiment lexicon
   * @param reviewManager manages access to the review .txt files
   */
  public BatchMode(String inputDir, Map<String, Double> lexicon,
      ReviewFileManager reviewManager) {
    // DESIGN PATTERN: Strategy — ReportGenerator receives the lexicon and uses
    // SentimentScoreGenerator (the concrete ScoringStrategy) internally.
    // Swapping the strategy requires no change here or in main.
    this.reportGenerator = new ReportGenerator(inputDir, lexicon, reviewManager);
  }

  /**
   * Runs the batch analysis: scores all reviews, prints statistics, writes report.csv.
   */
  @Override
  public void run() throws InvalidArgumentsException, IOException {
    reportGenerator.generate();
  }
}
