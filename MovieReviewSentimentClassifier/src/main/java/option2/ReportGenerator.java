package option2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Processes all review files in batch mode and generates:
 * - A report.csv file (sorted by SentimentScore descending) saved into the input directory
 * - A statistics summary printed to the console
 * Sentiment scoring is delegated entirely to SentimentScoreGenerator to avoid duplication.
 */
public class ReportGenerator {

  private static final String REPORT_FILENAME = "report.csv";

  private final String inputDir;
  private final Map<String, Double> sentimentLexicon;
  private final ReviewFileManager reviewManager;

  /**
   * Constructs a ReportGenerator.
   *
   * @param inputDir         The directory containing .txt review files.
   * @param sentimentLexicon The loaded lexicon from CSVParser.
   * @param reviewManager    The ReviewFileManager used to access review files.
   */
  public ReportGenerator(String inputDir, Map<String, Double> sentimentLexicon,
      ReviewFileManager reviewManager) {
    this.inputDir = inputDir;
    this.sentimentLexicon = sentimentLexicon;
    this.reviewManager = reviewManager;
  }

  /**
   * Runs the batch analysis: scores all reviews, writes report.csv, and prints statistics.
   *
   * @throws InvalidArgumentsException if a review file cannot be read.
   * @throws IOException               if the report file cannot be written.
   */
  public void generate() throws InvalidArgumentsException, IOException {
    int total = reviewManager.getFileCount();
    System.out.println("Batch mode: analyzing " + total + " review(s)...\n");

    // --- Score all reviews ---
    List<ReviewResult> results = new ArrayList<>();
    for (int i = 1; i <= total; i++) {
      String fileName = reviewManager.getReviewFiles().get(i);
      String reviewText = reviewManager.readReviewFile(i);

      // Reuse the same SentimentScoreGenerator used in interactive mode — no duplication.
      SentimentScoreGenerator generator = new SentimentScoreGenerator(sentimentLexicon, reviewText);
      results.add(new ReviewResult(fileName, generator.getScore(), generator.getClassification()));
    }

    // --- Sort descending by score ---
    results.sort(Comparator.comparingDouble(ReviewResult::getScore).reversed());

    // --- Print results table ---
    System.out.println("Results:");
    for (ReviewResult r : results) {
      System.out.printf("  %-25s -> %5.2f %s%n", r.getFileName(), r.getScore(), r.getLabel());
    }
    System.out.println();

    // --- Compute and print statistics ---
    printStatistics(results);

    // --- Write report.csv ---
    writeReportCSV(results);
  }

  /**
   * Prints the statistics block to the console.
   *
   * @param results The sorted list of scored reviews.
   */
  private void printStatistics(List<ReviewResult> results) {
    int total = results.size();
    long positiveCount = results.stream().filter(r -> r.getLabel().equals("POSITIVE")).count();
    long neutralCount  = results.stream().filter(r -> r.getLabel().equals("NEUTRAL")).count();
    long negativeCount = results.stream().filter(r -> r.getLabel().equals("NEGATIVE")).count();

    // Most positive = first (list is sorted descending), most negative = last
    ReviewResult mostPositive = results.get(0);
    ReviewResult mostNegative = results.get(results.size() - 1);

    System.out.println("Statistics:");
    System.out.println("  Total analyzed : " + total);
    System.out.printf( "  POSITIVE       : %.1f%%%n", (positiveCount * 100.0) / total);
    System.out.printf( "  NEUTRAL        : %.1f%%%n", (neutralCount  * 100.0) / total);
    System.out.printf( "  NEGATIVE       : %.1f%%%n", (negativeCount * 100.0) / total);
    System.out.printf( "  Most positive  : %s (%.2f)%n", mostPositive.getFileName(), mostPositive.getScore());
    System.out.printf( "  Most negative  : %s (%.2f)%n", mostNegative.getFileName(), mostNegative.getScore());
    System.out.println();
  }

  /**
   * Writes report.csv into the input directory using OutputFileWriter.
   *
   * @param results The sorted list of scored reviews.
   * @throws IOException if the file cannot be written.
   */
  private void writeReportCSV(List<ReviewResult> results) throws IOException {
    StringBuilder sb = new StringBuilder();
    sb.append("Filename,SentimentScore,Label\n");
    for (ReviewResult r : results) {
      sb.append(r.getFileName())
        .append(",")
        .append(String.format("%.2f", r.getScore()))
        .append(",")
        .append(r.getLabel())
        .append("\n");
    }

    OutputFileWriter writer = new OutputFileWriter(this.inputDir, REPORT_FILENAME);
    writer.fileWrite(sb.toString());
    System.out.println(REPORT_FILENAME + " written to " + this.inputDir + REPORT_FILENAME);
  }

  // -------------------------------------------------------------------------
  // Private value-object for a single review's result
  // -------------------------------------------------------------------------

  /**
   * Immutable holder for one review's scoring result.
   */
  private static class ReviewResult {
    private final String fileName;
    private final double score;
    private final String label;

    ReviewResult(String fileName, double score, String label) {
      this.fileName = fileName;
      this.score = score;
      this.label = label;
    }

    public String getFileName() { return fileName; }
    public double getScore()    { return score; }
    public String getLabel()    { return label; }
  }
}
