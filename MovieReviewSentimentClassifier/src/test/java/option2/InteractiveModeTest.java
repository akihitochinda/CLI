package option2;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class InteractiveModeTest {

  @TempDir
  Path tempDir;

  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final InputStream originalIn = System.in;

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
    System.setIn(originalIn);
  }

  // ── helpers ───────────────────────────────────────────────────────────────

  private void writeReview(String name, String body) throws IOException {
    Files.writeString(tempDir.resolve(name), body);
  }

  private void feedInput(String... lines) {
    String joined = String.join(System.lineSeparator(), lines) + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(joined.getBytes()));
  }

  private InteractiveMode buildMode() {
    reviewManager = new ReviewFileManager(inputDir);
    return new InteractiveMode(lexicon, reviewManager);
  }

  private String output() {
    return outContent.toString();
  }

  // ── startup: review list ──────────────────────────────────────────────────

  @Test
  void testRunPrintsAvailableReviewList() throws Exception {
    writeReview("review1.txt", "good");
    feedInput("q");
    buildMode().run();
    assertTrue(output().contains("review1.txt"));
  }

  @Test
  void testRunListsMultipleReviews() throws Exception {
    writeReview("alpha.txt", "good");
    writeReview("beta.txt",  "bad");
    feedInput("q");
    buildMode().run();
    String out = output();
    assertTrue(out.contains("alpha.txt"));
    assertTrue(out.contains("beta.txt"));
  }

  // ── quit immediately ──────────────────────────────────────────────────────

  @Test
  void testQuitImmediatelyPrintsGoodbye() throws Exception {
    writeReview("review1.txt", "good");
    feedInput("q");
    buildMode().run();
    assertTrue(output().contains("Goodbye"));
  }

  // ── valid selection ───────────────────────────────────────────────────────

  @Test
  void testSelectReviewDisplaysItsText() throws Exception {
    writeReview("review1.txt", "good movie");
    feedInput("1", "n");
    buildMode().run();
    assertTrue(output().contains("good movie"));
  }

  @Test
  void testSelectReviewDisplaysFilename() throws Exception {
    writeReview("my_review.txt", "good");
    feedInput("1", "n");
    buildMode().run();
    assertTrue(output().contains("my_review.txt"));
  }

  @Test
  void testSelectReviewDisplaysSentimentScore() throws Exception {
    writeReview("review1.txt", "good");
    feedInput("1", "n");
    buildMode().run();
    assertTrue(output().contains("Sentiment Score"));
  }

  @Test
  void testPositiveReviewShowsPositiveLabel() throws Exception {
    writeReview("review1.txt", "good good good");
    feedInput("1", "n");
    buildMode().run();
    assertTrue(output().contains("POSITIVE"));
  }

  @Test
  void testNegativeReviewShowsNegativeLabel() throws Exception {
    writeReview("review1.txt", "bad bad bad");
    feedInput("1", "n");
    buildMode().run();
    assertTrue(output().contains("NEGATIVE"));
  }

  @Test
  void testNeutralReviewShowsNeutralLabel() throws Exception {
    writeReview("review1.txt", "okay");
    feedInput("1", "n");
    buildMode().run();
    assertTrue(output().contains("NEUTRAL"));
  }

  // ── invalid selection handling ────────────────────────────────────────────

  @Test
  void testOutOfRangeIndexPrintsError() throws Exception {
    writeReview("review1.txt", "good");
    feedInput("99", "q");
    buildMode().run();
    assertTrue(output().toLowerCase().contains("error"));
  }

  @Test
  void testNonNumericInputPrintsError() throws Exception {
    writeReview("review1.txt", "good");
    feedInput("abc", "q");
    buildMode().run();
    assertTrue(output().toLowerCase().contains("error"));
  }

  @Test
  void testZeroInputPrintsError() throws Exception {
    writeReview("review1.txt", "good");
    feedInput("0", "q");
    buildMode().run();
    assertTrue(output().toLowerCase().contains("error"));
  }

  @Test
  void testNegativeIndexPrintsError() throws Exception {
    writeReview("review1.txt", "good");
    feedInput("-1", "q");
    buildMode().run();
    assertTrue(output().toLowerCase().contains("error"));
  }

  @Test
  void testInvalidInputRepromptsAndAcceptsValidNext() throws Exception {
    writeReview("review1.txt", "good");
    // bad input first, then valid selection, then quit
    feedInput("abc", "1", "n");
    buildMode().run();
    assertTrue(output().contains("Sentiment Score"));
  }

  // ── "analyze another?" prompt ─────────────────────────────────────────────

  @Test
  void testAnswerNExitsWithGoodbye() throws Exception {
    writeReview("review1.txt", "good");
    feedInput("1", "n");
    buildMode().run();
    assertTrue(output().contains("Goodbye"));
  }

  @Test
  void testAnswerQOnAnalyzeAnotherExitsWithGoodbye() throws Exception {
    writeReview("review1.txt", "good");
    feedInput("1", "q");
    buildMode().run();
    assertTrue(output().contains("Goodbye"));
  }

  @Test
  void testAnswerYReDisplaysReviewList() throws Exception {
    writeReview("review1.txt", "good");
    feedInput("1", "y", "q");
    buildMode().run();
    // review1.txt should appear at least twice: initial list + after "y"
    String out = output();
    int first  = out.indexOf("review1.txt");
    int second = out.indexOf("review1.txt", first + 1);
    assertTrue(second > first, "Review list should be shown again after answering 'y'");
  }

  @Test
  void testInvalidAnswerToAnalyzeAnotherPrintsError() throws Exception {
    writeReview("review1.txt", "good");
    feedInput("1", "x", "1", "n");
    buildMode().run();
    assertTrue(output().toLowerCase().contains("error"));
  }

  // ── multiple reviews analyzed in sequence ────────────────────────────────

  @Test
  void testAnalyzeTwoReviewsInSequenceShowsTwoScores() throws Exception {
    writeReview("pos.txt", "good good");
    writeReview("neg.txt", "bad bad");
    feedInput("1", "y", "2", "n");
    buildMode().run();
    long scoreLines = output().lines()
        .filter(l -> l.contains("Sentiment Score"))
        .count();
    assertEquals(2, scoreLines);
  }

  // ── does not create report.csv ────────────────────────────────────────────

  @Test
  void testInteractiveModeDoesNotWriteReportCsv() throws Exception {
    writeReview("review1.txt", "good");
    feedInput("q");
    buildMode().run();
    assertFalse(Files.exists(tempDir.resolve("report.csv")));
  }

  // ── Strategy pattern: scoring via SentimentScoreGenerator ────────────────

  @Test
  void testScoreDisplayedMatchesSentimentScoreGenerator() throws Exception {
    writeReview("review1.txt", "good");
    feedInput("1", "n");
    buildMode().run();

    // The score shown must match what SentimentScoreGenerator computes directly
    SentimentScoreGenerator expected = new SentimentScoreGenerator(lexicon, "good");
    String expectedStr = String.format("%.2f", expected.getScore());
    assertTrue(output().contains(expectedStr));
  }
}
