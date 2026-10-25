package option2;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SentimentAnalyzerPart1Test {

  @TempDir
  Path tempDir;

  // Captured streams
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final java.io.InputStream originalIn = System.in;

  @BeforeEach
  void redirectStreams() {
    System.setOut(new PrintStream(outContent));
  }

  @AfterEach
  void restoreStreams() {
    System.setOut(originalOut);
    System.setIn(originalIn);
  }

  // ── helpers ──────────────────────────────────────────────────────────────

  /** Writes the sentiment lexicon into tempDir. */
  private void writeLexicon(String content) throws IOException {
    Files.writeString(tempDir.resolve("sentiment_lexicon.csv"), content);
  }

  /** Writes a review .txt file into tempDir. */
  private void writeReview(String filename, String content) throws IOException {
    Files.writeString(tempDir.resolve(filename), content);
  }

  /** Feeds a multi-line script to System.in. */
  private void feedInput(String... lines) {
    String joined = String.join(System.lineSeparator(), lines) + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(joined.getBytes()));
  }

  /** Returns everything printed to System.out so far. */
  private String output() {
    return outContent.toString();
  }

  /** Runs main() with the temp directory as the sole argument. */
  private void runMain() {
    sentimentAnalyzerPart1.main(new String[]{tempDir.toString()});
  }

  // ── argument validation ───────────────────────────────────────────────────

  @Test
  void testNoArgumentsPrintsError() {
    sentimentAnalyzerPart1.main(new String[]{});
    assertTrue(output().toLowerCase().contains("error"));
  }

  @Test
  void testTooManyArgumentsPrintsError() {
    sentimentAnalyzerPart1.main(new String[]{"dir1", "dir2"});
    assertTrue(output().toLowerCase().contains("error"));
  }

  @Test
  void testNonExistentDirectoryPrintsError() {
    sentimentAnalyzerPart1.main(new String[]{"/nonexistent/reviews"});
    assertTrue(output().toLowerCase().contains("error"));
  }

  @Test
  void testMissingLexiconFilePrintsError() throws IOException {
    // Valid directory but no sentiment_lexicon.csv
    Path emptyDir = tempDir.resolve("empty");
    Files.createDirectory(emptyDir);
    sentimentAnalyzerPart1.main(new String[]{emptyDir.toString()});
    assertTrue(output().toLowerCase().contains("error"));
  }

  // ── startup messages ──────────────────────────────────────────────────────

  @Test
  void testStartupPrintsLexiconLoadCount() throws IOException {
    writeLexicon("word,score\ngood,0.8\nbad,-0.7\n");
    writeReview("review1.txt", "This is good.");
    feedInput("q");

    runMain();

    String out = output();
    // Should report how many words were loaded from the lexicon
    assertTrue(out.contains("2 words loaded") || out.contains("2 word"));
  }

  @Test
  void testStartupPrintsReviewFileCount() throws IOException {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("review1.txt", "Good film.");
    writeReview("review2.txt", "Bad film.");
    feedInput("q");

    runMain();

    assertTrue(output().contains("2 file"));
  }

  @Test
  void testStartupListsReviewFilenames() throws IOException {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("review_drama.txt", "Quite dramatic.");
    feedInput("q");

    runMain();

    assertTrue(output().contains("review_drama.txt"));
  }

  // ── quit immediately ──────────────────────────────────────────────────────

  @Test
  void testQuitImmediatelyPrintsGoodbye() throws IOException {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("review1.txt", "Good.");
    feedInput("q");

    runMain();

    assertTrue(output().contains("Goodbye"));
  }

  // ── selecting a review ────────────────────────────────────────────────────

  @Test
  void testSelectValidReviewDisplaysContent() throws IOException {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("review1.txt", "This movie was good.");
    // select review 1, then answer 'n' to "analyze another?"
    feedInput("1", "n");

    runMain();

    assertTrue(output().contains("This movie was good."));
  }

  @Test
  void testSelectValidReviewDisplaysSentimentScore() throws IOException {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("review1.txt", "good");
    feedInput("1", "n");

    runMain();

    assertTrue(output().contains("Sentiment Score"));
  }

  @Test
  void testPositiveReviewClassifiedCorrectly() throws IOException {
    writeLexicon("word,score\ngood,0.8\nexcellent,1.0\n");
    writeReview("review1.txt", "good excellent");
    feedInput("1", "n");

    runMain();

    assertTrue(output().contains("POSITIVE"));
  }

  @Test
  void testNegativeReviewClassifiedCorrectly() throws IOException {
    writeLexicon("word,score\nbad,-0.8\nterrible,-1.0\n");
    writeReview("review1.txt", "bad terrible");
    feedInput("1", "n");

    runMain();

    assertTrue(output().contains("NEGATIVE"));
  }

  @Test
  void testNeutralReviewClassifiedCorrectly() throws IOException {
    writeLexicon("word,score\nokay,0.0\n");
    writeReview("review1.txt", "okay");
    feedInput("1", "n");

    runMain();

    assertTrue(output().contains("NEUTRAL"));
  }

  @Test
  void testReviewFilenameAppearsInOutput() throws IOException {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("my_review.txt", "good");
    feedInput("1", "n");

    runMain();

    assertTrue(output().contains("my_review.txt"));
  }

  // ── invalid selection handling ────────────────────────────────────────────

  @Test
  void testOutOfRangeNumberPrintsErrorAndRePrompts() throws IOException {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("review1.txt", "good");
    // enter 99 (out of range), then quit
    feedInput("99", "q");

    runMain();

    assertTrue(output().toLowerCase().contains("error"));
    assertTrue(output().contains("Goodbye"));
  }

  @Test
  void testNonNumericInputPrintsErrorAndRePrompts() throws IOException {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("review1.txt", "good");
    // enter garbage, then quit
    feedInput("abc", "q");

    runMain();

    assertTrue(output().toLowerCase().contains("error"));
    assertTrue(output().contains("Goodbye"));
  }

  @Test
  void testZeroIsInvalidSelection() throws IOException {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("review1.txt", "good");
    feedInput("0", "q");

    runMain();

    assertTrue(output().toLowerCase().contains("error"));
  }

  @Test
  void testNegativeNumberIsInvalidSelection() throws IOException {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("review1.txt", "good");
    feedInput("-1", "q");

    runMain();

    assertTrue(output().toLowerCase().contains("error"));
  }

  // ── "analyze another?" prompt ─────────────────────────────────────────────

  @Test
  void testAnswerYesThenQuitReDisplaysList() throws IOException {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("review1.txt", "good");
    // select 1, say yes, then quit
    feedInput("1", "y", "q");

    runMain();

    // The review list should appear at least twice (startup + after "y")
    String out = output();
    int firstOccurrence = out.indexOf("review1.txt");
    int secondOccurrence = out.indexOf("review1.txt", firstOccurrence + 1);
    assertTrue(secondOccurrence > firstOccurrence,
        "Review list should be shown again after answering 'y'");
  }

  @Test
  void testAnswerNoExitsWithGoodbye() throws IOException {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("review1.txt", "good");
    feedInput("1", "n");

    runMain();

    assertTrue(output().contains("Goodbye"));
  }

  @Test
  void testAnswerQuitOnAnalyzeAnotherExits() throws IOException {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("review1.txt", "good");
    feedInput("1", "q");

    runMain();

    assertTrue(output().contains("Goodbye"));
  }

  // ── multiple reviews in directory ─────────────────────────────────────────

  @Test
  void testCanAnalyzeTwoDifferentReviews() throws IOException {
    writeLexicon("word,score\ngood,0.8\nbad,-0.7\n");
    writeReview("positive.txt", "good good good");
    writeReview("negative.txt", "bad bad bad");

    // We don't know which index each file gets (filesystem order varies),
    // so select both in sequence and assert both scores appear
    feedInput("1", "y", "2", "n");

    runMain();

    // Both reviews were displayed — output should contain two sentiment score lines
    long scoreLines = output().lines()
        .filter(l -> l.contains("Sentiment Score"))
        .count();
    assertEquals(2, scoreLines);
  }

  @Test
  void testEmptyDirectoryShowsNoFiles() throws IOException {
    // Only the lexicon, no review .txt files
    writeLexicon("word,score\ngood,0.8\n");
    feedInput("q");

    runMain();

    assertTrue(output().contains("0 file") || output().contains("No review"));
  }
}
