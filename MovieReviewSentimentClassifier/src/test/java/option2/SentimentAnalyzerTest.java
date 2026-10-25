package option2;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SentimentAnalyzerTest {

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

  private String output() {
    return outContent.toString();
  }

  private void runReport() {
    sentimentAnalyzer.main(new String[]{tempDir.toString(), "--report"});
  }

  private String readReport() throws IOException {
    return Files.readString(tempDir.resolve("report.csv"));
  }

  // ── argument validation (part-2-specific) ─────────────────────────────────

  @Test
  void testUnknownFlagPrintsError() throws IOException {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("review1.txt", "good");
    sentimentAnalyzer.main(new String[]{tempDir.toString(), "--badFlag"});
    assertTrue(output().toLowerCase().contains("error"));
  }

  @Test
  void testThreeArgumentsPrintsError() throws IOException {
    writeLexicon("word,score\ngood,0.8\n");
    sentimentAnalyzer.main(new String[]{tempDir.toString(), "--report", "extra"});
    assertTrue(output().toLowerCase().contains("error"));
  }

  // ── startup messages ──────────────────────────────────────────────────────

  @Test
  void testReportModePrintsLexiconLoadCount() throws IOException {
    writeLexicon("word,score\ngood,0.8\nbad,-0.7\n");
    writeReview("r1.txt", "good");

    runReport();

    assertTrue(output().contains("2 words loaded") || output().contains("2 word"));
  }

  @Test
  void testReportModePrintsFileCount() throws IOException {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("r1.txt", "good");
    writeReview("r2.txt", "good");

    runReport();

    assertTrue(output().contains("2 file") || output().contains("2 review"));
  }

  @Test
  void testReportModePrintsBatchModeMessage() throws IOException {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("r1.txt", "good");

    runReport();

    assertTrue(output().toLowerCase().contains("batch mode"));
  }

  // ── report.csv produced ───────────────────────────────────────────────────

  @Test
  void testReportCsvFileIsCreated() throws IOException {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("r1.txt", "good");

    runReport();

    assertTrue(Files.exists(tempDir.resolve("report.csv")));
  }

  @Test
  void testReportCsvCreatedInsideInputDirectory() throws IOException {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("r1.txt", "good");

    runReport();

    // report.csv must live inside the reviews folder, not elsewhere
    assertTrue(Files.exists(tempDir.resolve("report.csv")));
  }

  @Test
  void testReportCsvHeaderRow() throws IOException {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("r1.txt", "good");

    runReport();

    String header = readReport().lines().findFirst().orElse("");
    assertEquals("Filename,SentimentScore,Label", header);
  }

  @Test
  void testReportCsvContainsAllFilenames() throws IOException {
    writeLexicon("word,score\ngood,0.8\nbad,-0.8\n");
    writeReview("alpha.txt", "good");
    writeReview("beta.txt",  "bad");

    runReport();

    String report = readReport();
    assertTrue(report.contains("alpha.txt"));
    assertTrue(report.contains("beta.txt"));
  }

  @Test
  void testReportCsvRowCountMatchesReviewCount() throws IOException {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("r1.txt", "good");
    writeReview("r2.txt", "good");
    writeReview("r3.txt", "good");

    runReport();

    long dataRows = readReport().lines()
        .filter(l -> !l.startsWith("Filename") && !l.isBlank())
        .count();
    assertEquals(3, dataRows);
  }

  @Test
  void testReportCsvIsSortedDescending() throws IOException {
    writeLexicon("word,score\ngood,0.8\nbad,-0.8\n");
    writeReview("pos.txt", "good good good");
    writeReview("neg.txt", "bad bad bad");

    runReport();

    double prev = Double.MAX_VALUE;
    for (String line : readReport().lines()
        .filter(l -> !l.startsWith("Filename") && !l.isBlank())
        .toList()) {
      double score = Double.parseDouble(line.split(",")[1]);
      assertTrue(score <= prev, "Report rows must be sorted descending");
      prev = score;
    }
  }

  // ── console statistics ────────────────────────────────────────────────────

  @Test
  void testConsolePrintsStatisticsBlock() throws IOException {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("r1.txt", "good");

    runReport();

    assertTrue(output().contains("Statistics"));
  }

  @Test
  void testConsolePrintsTotalCount() throws IOException {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("r1.txt", "good");
    writeReview("r2.txt", "good");

    runReport();

    assertTrue(output().contains("Total analyzed"));
    assertTrue(output().contains("2"));
  }

  @Test
  void testConsolePrintsPositivePercentage() throws IOException {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("r1.txt", "good");

    runReport();

    assertTrue(output().contains("POSITIVE"));
    assertTrue(output().contains("100.0%"));
  }

  @Test
  void testConsolePrintsNegativePercentage() throws IOException {
    writeLexicon("word,score\nbad,-0.8\n");
    writeReview("r1.txt", "bad");

    runReport();

    assertTrue(output().contains("NEGATIVE"));
    assertTrue(output().contains("100.0%"));
  }

  @Test
  void testConsolePrintsMostPositive() throws IOException {
    writeLexicon("word,score\ngood,0.8\nbad,-0.8\n");
    writeReview("great.txt", "good good good");
    writeReview("awful.txt", "bad");

    runReport();

    String out = output();
    assertTrue(out.toLowerCase().contains("most positive"));
    assertTrue(out.contains("great.txt"));
  }

  @Test
  void testConsolePrintsMostNegative() throws IOException {
    writeLexicon("word,score\ngood,0.8\nbad,-0.8\n");
    writeReview("great.txt", "good");
    writeReview("awful.txt", "bad bad bad");

    runReport();

    String out = output();
    assertTrue(out.toLowerCase().contains("most negative"));
    assertTrue(out.contains("awful.txt"));
  }

  @Test
  void testConsoleMentionsReportCsvWritten() throws IOException {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("r1.txt", "good");

    runReport();

    assertTrue(output().contains("report.csv"));
  }

  // ── non-duplication: same score as interactive mode ───────────────────────

  @Test
  void testBatchScoreMatchesInteractiveModeScore() throws IOException {
    writeLexicon("word,score\ngood,0.8\nexcellent,1.0\n");
    writeReview("r1.txt", "good excellent");

    runReport();

    java.util.Map<String, Double> lexicon = java.util.Map.of("good", 0.8, "excellent", 1.0);
    SentimentScoreGeneratorPart1 expected = new SentimentScoreGeneratorPart1(lexicon, "good excellent");

    String scoreStr = readReport().lines()
        .filter(l -> l.contains("r1.txt"))
        .findFirst().orElse("").split(",")[1];

    assertEquals(expected.getScore(), Double.parseDouble(scoreStr), 0.005);
  }

  // ── part 1 mode still intact when --report absent ─────────────────────────

  @Test
  void testWithoutReportFlagDoesNotCreateReportCsv() throws IOException {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("r1.txt", "good");

    // Feed 'q' to immediately exit interactive mode
    java.io.InputStream originalIn = System.in;
    System.setIn(new java.io.ByteArrayInputStream("q\n".getBytes()));
    sentimentAnalyzer.main(new String[]{tempDir.toString()});
    System.setIn(originalIn);

    assertFalse(Files.exists(tempDir.resolve("report.csv")),
        "report.csv must not be created in interactive mode");
  }

  @Test
  void testWithoutReportFlagPrintsGoodbye() throws IOException {
    writeLexicon("word,score\ngood,0.8\n");
    writeReview("r1.txt", "good");

    java.io.InputStream originalIn = System.in;
    System.setIn(new java.io.ByteArrayInputStream("q\n".getBytes()));
    sentimentAnalyzer.main(new String[]{tempDir.toString()});
    System.setIn(originalIn);

    assertTrue(output().contains("Goodbye"));
  }
}
