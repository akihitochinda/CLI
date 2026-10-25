package option2;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class BatchModeTest {

  @TempDir
  Path tempDir;

  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;

  private Map<String, Double> lexicon;
  private ReviewFileManager reviewManager;
  private String inputDir;

  @BeforeEach
  void setUp() throws IOException {
    System.setOut(new PrintStream(outContent));
    inputDir = tempDir.toString() + java.io.File.separator;
    Files.writeString(tempDir.resolve("sentiment_lexicon.csv"),
        "word,score\ngood,0.8\nbad,-0.8\nokay,0.0\n");
    CSVParser csv = new CSVParser(new String[]{inputDir + "sentiment_lexicon.csv"});
    lexicon = csv.getData();
  }

  @AfterEach
  void tearDown() {
    System.setOut(originalOut);
  }

  // ── helpers ───────────────────────────────────────────────────────────────

  private void writeReview(String name, String body) throws IOException {
    Files.writeString(tempDir.resolve(name), body);
  }

  private BatchMode buildMode() {
    reviewManager = new ReviewFileManager(inputDir);
    return new BatchMode(inputDir, lexicon, reviewManager);
  }

  private String output() {
    return outContent.toString();
  }

  private String readReport() throws IOException {
    return Files.readString(tempDir.resolve("report.csv"));
  }

  // ── AnalyzerMode contract ─────────────────────────────────────────────────

  @Test
  void testBatchModeImplementsAnalyzerMode() {
    reviewManager = new ReviewFileManager(inputDir);
    AnalyzerMode mode = new BatchMode(inputDir, lexicon, reviewManager);
    assertInstanceOf(AnalyzerMode.class, mode);
  }

  @Test
  void testRunViaInterfaceReferenceCreatesReport() throws Exception {
    writeReview("review1.txt", "good");
    // Call through the interface — Factory Method contract
    AnalyzerMode mode = buildMode();
    mode.run();
    assertTrue(Files.exists(tempDir.resolve("report.csv")));
  }

  // ── report.csv produced ───────────────────────────────────────────────────

  @Test
  void testRunCreatesReportCsv() throws Exception {
    writeReview("review1.txt", "good");
    buildMode().run();
    assertTrue(Files.exists(tempDir.resolve("report.csv")));
  }

  @Test
  void testReportCsvHasCorrectHeader() throws Exception {
    writeReview("review1.txt", "good");
    buildMode().run();
    assertEquals("Filename,SentimentScore,Label",
        readReport().lines().findFirst().orElse(""));
  }

  @Test
  void testReportCsvContainsReviewFilename() throws Exception {
    writeReview("my_review.txt", "good");
    buildMode().run();
    assertTrue(readReport().contains("my_review.txt"));
  }

  @Test
  void testReportCsvContainsCorrectLabel() throws Exception {
    writeReview("pos.txt", "good good good");
    buildMode().run();
    assertTrue(readReport().contains("POSITIVE"));
  }

  @Test
  void testReportCsvIsSortedDescending() throws Exception {
    writeReview("pos.txt", "good good good");
    writeReview("neg.txt", "bad bad bad");
    buildMode().run();

    double prev = Double.MAX_VALUE;
    for (String line : readReport().lines()
        .filter(l -> !l.startsWith("Filename") && !l.isBlank())
        .toList()) {
      double score = Double.parseDouble(line.split(",")[1]);
      assertTrue(score <= prev, "Rows must be sorted descending");
      prev = score;
    }
  }

  @Test
  void testReportCsvRowCountMatchesReviewCount() throws Exception {
    writeReview("r1.txt", "good");
    writeReview("r2.txt", "bad");
    writeReview("r3.txt", "okay");
    buildMode().run();

    long dataRows = readReport().lines()
        .filter(l -> !l.startsWith("Filename") && !l.isBlank())
        .count();
    assertEquals(3, dataRows);
  }

  // ── console output ────────────────────────────────────────────────────────

  @Test
  void testRunPrintsBatchModeMessage() throws Exception {
    writeReview("review1.txt", "good");
    buildMode().run();
    assertTrue(output().toLowerCase().contains("batch mode"));
  }

  @Test
  void testRunPrintsStatisticsBlock() throws Exception {
    writeReview("review1.txt", "good");
    buildMode().run();
    assertTrue(output().contains("Statistics"));
  }

  @Test
  void testRunPrintsTotalAnalyzed() throws Exception {
    writeReview("r1.txt", "good");
    writeReview("r2.txt", "bad");
    buildMode().run();
    assertTrue(output().contains("Total analyzed"));
    assertTrue(output().contains("2"));
  }

  @Test
  void testRunPrintsMostPositive() throws Exception {
    writeReview("great.txt", "good good good");
    writeReview("awful.txt", "bad");
    buildMode().run();
    assertTrue(output().toLowerCase().contains("most positive"));
    assertTrue(output().contains("great.txt"));
  }

  @Test
  void testRunPrintsMostNegative() throws Exception {
    writeReview("great.txt", "good");
    writeReview("awful.txt", "bad bad bad");
    buildMode().run();
    assertTrue(output().toLowerCase().contains("most negative"));
    assertTrue(output().contains("awful.txt"));
  }

  @Test
  void testRunMentionsReportCsvWritten() throws Exception {
    writeReview("review1.txt", "good");
    buildMode().run();
    assertTrue(output().contains("report.csv"));
  }

  // ── non-interactive: no System.in dependency ──────────────────────────────

  @Test
  void testRunDoesNotReadFromSystemIn() throws Exception {
    writeReview("review1.txt", "good");
    // Replace System.in with an empty stream — any read would cause NoSuchElementException
    java.io.InputStream original = System.in;
    System.setIn(new java.io.ByteArrayInputStream(new byte[0]));
    try {
      assertDoesNotThrow(() -> buildMode().run());
    } finally {
      System.setIn(original);
    }
  }

  // ── Strategy pattern: score matches SentimentScoreGenerator ──────────────

  @Test
  void testScoreInReportMatchesSentimentScoreGenerator() throws Exception {
    writeReview("review1.txt", "good");
    buildMode().run();

    SentimentScoreGenerator expected = new SentimentScoreGenerator(lexicon, "good");
    String scoreStr = readReport().lines()
        .filter(l -> l.contains("review1.txt"))
        .findFirst().orElse("").split(",")[1];

    assertEquals(expected.getScore(), Double.parseDouble(scoreStr), 0.005);
  }

  @Test
  void testMultiTokenScoreInReportMatchesSentimentScoreGenerator() throws Exception {
    writeReview("review1.txt", "good good bad");
    buildMode().run();

    // (0.8 + 0.8 + (-0.8)) / 3 = 0.2667
    SentimentScoreGenerator expected =
        new SentimentScoreGenerator(lexicon, "good good bad");
    String scoreStr = readReport().lines()
        .filter(l -> l.contains("review1.txt"))
        .findFirst().orElse("").split(",")[1];

    assertEquals(expected.getScore(), Double.parseDouble(scoreStr), 0.005);
  }
}
