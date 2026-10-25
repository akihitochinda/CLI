package option2;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ReportGeneratorTest {

  @TempDir
  Path tempDir;

  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;

  @BeforeEach
  void redirectOut() {
    System.setOut(new PrintStream(outContent));
  }

  @AfterEach
  void restoreOut() {
    System.setOut(originalOut);
  }

  // ── helpers ───────────────────────────────────────────────────────────────

  private void writeLexicon(String csv) throws IOException {
    Files.writeString(tempDir.resolve("sentiment_lexicon.csv"), csv);
  }

  private void writeReview(String name, String body) throws IOException {
    Files.writeString(tempDir.resolve(name), body);
  }

  private void runGenerator() throws InvalidArgumentsException, IOException {
    String dir = tempDir.toString();
    CSVParser csv = new CSVParser(new String[]{dir + java.io.File.separator + "sentiment_lexicon.csv"});
    ReviewFileManager reviews = new ReviewFileManager(dir + java.io.File.separator);
    ReportGenerator gen = new ReportGenerator(dir + java.io.File.separator, csv.getData(), reviews);
    gen.generate();
  }

  private String output() {
    return outContent.toString();
  }

  private String readReport() throws IOException {
    return Files.readString(tempDir.resolve("report.csv"));
  }

  // ── report.csv creation ───────────────────────────────────────────────────

  @Test
  void testReportCsvIsCreated() throws Exception {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("review1.txt", "good");

    runGenerator();

    assertTrue(Files.exists(tempDir.resolve("report.csv")));
  }

  @Test
  void testReportCsvHasCorrectHeader() throws Exception {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("review1.txt", "good");

    runGenerator();

    String firstLine = readReport().lines().findFirst().orElse("");
    assertEquals("Filename,SentimentScore,Label", firstLine);
  }

  @Test
  void testReportCsvContainsAllReviewFilenames() throws Exception {
    writeLexicon("word,score\ngood,0.8\nbad,-0.7\n");
    writeReview("alpha.txt", "good good");
    writeReview("beta.txt",  "bad bad");

    runGenerator();

    String report = readReport();
    assertTrue(report.contains("alpha.txt"));
    assertTrue(report.contains("beta.txt"));
  }

  @Test
  void testReportCsvLabelsAreCorrect() throws Exception {
    writeLexicon("word,score\ngood,0.8\nbad,-0.8\nokay,0.0\n");
    writeReview("pos.txt",     "good good");
    writeReview("neg.txt",     "bad bad");
    writeReview("neutral.txt", "okay");

    runGenerator();

    String report = readReport();
    assertTrue(report.contains("POSITIVE"));
    assertTrue(report.contains("NEGATIVE"));
    assertTrue(report.contains("NEUTRAL"));
  }

  @Test
  void testReportCsvIsSortedDescendingByScore() throws Exception {
    writeLexicon("word,score\ngood,0.8\nbad,-0.8\n");
    writeReview("pos.txt", "good good good");
    writeReview("neg.txt", "bad bad bad");

    runGenerator();

    List<String> lines = readReport().lines()
        .filter(l -> !l.startsWith("Filename"))   // skip header
        .filter(l -> !l.isBlank())
        .toList();

    // Extract the scores from each data row (second column)
    double prev = Double.MAX_VALUE;
    for (String line : lines) {
      double score = Double.parseDouble(line.split(",")[1]);
      assertTrue(score <= prev, "Rows must be sorted descending by score");
      prev = score;
    }
  }

  @Test
  void testReportCsvScoreFormattedToTwoDecimalPlaces() throws Exception {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("review1.txt", "good");

    runGenerator();

    // Every data row's second column should match a decimal pattern with exactly 2 dp
    readReport().lines()
        .filter(l -> !l.startsWith("Filename") && !l.isBlank())
        .forEach(line -> {
          String scoreStr = line.split(",")[1];
          assertTrue(scoreStr.matches("-?\\d+\\.\\d{2}"),
              "Score should have exactly 2 decimal places, got: " + scoreStr);
        });
  }

  @Test
  void testReportCsvCorrectRowCount() throws Exception {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("r1.txt", "good");
    writeReview("r2.txt", "good good");
    writeReview("r3.txt", "good good good");

    runGenerator();

    long dataRows = readReport().lines()
        .filter(l -> !l.startsWith("Filename") && !l.isBlank())
        .count();
    assertEquals(3, dataRows);
  }

  // ── console output: results table ────────────────────────────────────────

  @Test
  void testConsolePrintsBatchModeHeader() throws Exception {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("review1.txt", "good");

    runGenerator();

    assertTrue(output().toLowerCase().contains("batch mode"));
  }

  @Test
  void testConsolePrintsReviewCount() throws Exception {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("r1.txt", "good");
    writeReview("r2.txt", "good");

    runGenerator();

    assertTrue(output().contains("2"));
  }

  @Test
  void testConsolePrintsResultsLabel() throws Exception {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("review1.txt", "good");

    runGenerator();

    assertTrue(output().contains("Results"));
  }

  @Test
  void testConsolePrintsEachReviewFilename() throws Exception {
    writeLexicon("word,score\ngood,0.8\nbad,-0.7\n");
    writeReview("happy.txt", "good");
    writeReview("sad.txt",   "bad");

    runGenerator();

    assertTrue(output().contains("happy.txt"));
    assertTrue(output().contains("sad.txt"));
  }

  @Test
  void testConsolePrintsSentimentLabelsInResults() throws Exception {
    writeLexicon("word,score\ngood,0.9\nbad,-0.9\n");
    writeReview("pos.txt", "good");
    writeReview("neg.txt", "bad");

    runGenerator();

    assertTrue(output().contains("POSITIVE"));
    assertTrue(output().contains("NEGATIVE"));
  }

  // ── console output: statistics block ─────────────────────────────────────

  @Test
  void testConsolePrintsStatisticsLabel() throws Exception {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("review1.txt", "good");

    runGenerator();

    assertTrue(output().contains("Statistics"));
  }

  @Test
  void testConsolePrintsTotalAnalyzed() throws Exception {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("r1.txt", "good");
    writeReview("r2.txt", "good");

    runGenerator();

    // "Total analyzed : 2"
    assertTrue(output().contains("Total analyzed"));
    assertTrue(output().contains("2"));
  }

  @Test
  void testConsolePrintsAllThreePercentageLabels() throws Exception {
    writeLexicon("word,score\ngood,0.8\nbad,-0.8\nokay,0.0\n");
    writeReview("pos.txt",     "good");
    writeReview("neg.txt",     "bad");
    writeReview("neutral.txt", "okay");

    runGenerator();

    String out = output();
    assertTrue(out.contains("POSITIVE"));
    assertTrue(out.contains("NEGATIVE"));
    assertTrue(out.contains("NEUTRAL"));
  }

  @Test
  void testConsolePrintsMostPositiveFilename() throws Exception {
    writeLexicon("word,score\ngood,0.8\nbad,-0.8\n");
    writeReview("best.txt",  "good good good");
    writeReview("worst.txt", "bad");

    runGenerator();

    assertTrue(output().contains("Most positive") || output().contains("most positive"));
    assertTrue(output().contains("best.txt"));
  }

  @Test
  void testConsolePrintsMostNegativeFilename() throws Exception {
    writeLexicon("word,score\ngood,0.8\nbad,-0.8\n");
    writeReview("best.txt",  "good");
    writeReview("worst.txt", "bad bad bad");

    runGenerator();

    assertTrue(output().contains("Most negative") || output().contains("most negative"));
    assertTrue(output().contains("worst.txt"));
  }

  @Test
  void testStatisticsPercentagesAddToOneHundred() throws Exception {
    writeLexicon("word,score\ngood,0.8\nbad,-0.8\nokay,0.0\n");
    writeReview("pos.txt",     "good");
    writeReview("neg.txt",     "bad");
    writeReview("neutral.txt", "okay");

    runGenerator();

    // Extract the three percentage values from lines containing "%"
    double total = output().lines()
        .filter(l -> l.contains("%"))
        .mapToDouble(l -> {
          String pct = l.replaceAll("[^0-9.]", " ").trim().split("\\s+")[0];
          return Double.parseDouble(pct);
        })
        .sum();

    assertEquals(100.0, total, 0.5); // allow small rounding
  }

  @Test
  void testAllPositiveReviewsShowZeroNeutralAndNegative() throws Exception {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("r1.txt", "good good");
    writeReview("r2.txt", "good");

    runGenerator();

    String out = output();
    // Both non-positive percentages should be 0.0%
    assertTrue(out.contains("0.0%"));
  }

  // ── report.csv written confirmation ──────────────────────────────────────

  @Test
  void testConsoleMentionsReportCsvWritten() throws Exception {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("review1.txt", "good");

    runGenerator();

    assertTrue(output().contains("report.csv"));
  }

  // ── scoring consistency with interactive mode ─────────────────────────────

  @Test
  void testBatchScoreMatchesDirectSentimentScoreGenerator() throws Exception {
    writeLexicon("word,score\ngood,0.8\nexcellent,1.0\n");
    writeReview("review1.txt", "good excellent");

    runGenerator();

    Map<String, Double> lexicon = Map.of("good", 0.8, "excellent", 1.0);
    SentimentScoreGeneratorPart1 expected =
        new SentimentScoreGeneratorPart1(lexicon, "good excellent");

    String scoreInReport = readReport().lines()
        .filter(l -> l.contains("review1.txt"))
        .findFirst().orElse("").split(",")[1];

    double reportScore = Double.parseDouble(scoreInReport);
    assertEquals(expected.getScore(), reportScore, 0.005);
  }

  // ── edge cases ────────────────────────────────────────────────────────────

  @Test
  void testSingleReviewMostPositiveAndMostNegativeAreSameFile() throws Exception {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("only.txt", "good");

    runGenerator();

    // Both extremes should point to the only file
    long occurrences = output().lines()
        .filter(l -> l.contains("only.txt"))
        .count();
    // filename appears in results table AND in most-positive AND most-negative lines
    assertTrue(occurrences >= 2);
  }

  @Test
  void testReviewWithNoLexiconMatchesScoresZero() throws Exception {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("unknown.txt", "zzz yyy xxx");  // no tokens in lexicon

    runGenerator();

    String scoreStr = readReport().lines()
        .filter(l -> l.contains("unknown.txt"))
        .findFirst().orElse("").split(",")[1];

    assertEquals(0.00, Double.parseDouble(scoreStr), 0.001);
  }
}
