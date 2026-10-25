package option2;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AnalyzerModeFactoryTest {

  @TempDir
  Path tempDir;

  private Map<String, Double> lexicon;
  private ReviewFileManager reviewManager;
  private String inputDir;

  @BeforeEach
  void setUp() throws IOException {
    inputDir = tempDir.toString() + java.io.File.separator;
    Files.writeString(tempDir.resolve("sentiment_lexicon.csv"),
        "word,score\ngood,0.8\nbad,-0.7\n");
    Files.writeString(tempDir.resolve("review1.txt"), "good");

    CSVParser csv = new CSVParser(new String[]{inputDir + "sentiment_lexicon.csv"});
    lexicon = csv.getData();
    reviewManager = new ReviewFileManager(inputDir);
  }

  // helpers to build a CommandLineParser for each mode
  private CommandLineParser interactiveParser() throws InvalidArgumentsException {
    return new CommandLineParser(new String[]{tempDir.toString()});
  }

  private CommandLineParser reportParser() throws InvalidArgumentsException {
    return new CommandLineParser(new String[]{tempDir.toString(), "--report"});
  }

  // ── Factory Method: correct product type ─────────────────────────────────

  @Test
  void testFactoryReturnsInteractiveModeWhenNoFlag() throws InvalidArgumentsException {
    AnalyzerMode mode = AnalyzerModeFactory.create(interactiveParser(), lexicon, reviewManager);
    assertInstanceOf(InteractiveMode.class, mode);
  }

  @Test
  void testFactoryReturnsBatchModeWhenReportFlag() throws InvalidArgumentsException {
    AnalyzerMode mode = AnalyzerModeFactory.create(reportParser(), lexicon, reviewManager);
    assertInstanceOf(BatchMode.class, mode);
  }

  // ── Factory Method: both products implement AnalyzerMode ─────────────────

  @Test
  void testInteractiveModeImplementsAnalyzerMode() throws InvalidArgumentsException {
    AnalyzerMode mode = AnalyzerModeFactory.create(interactiveParser(), lexicon, reviewManager);
    assertInstanceOf(AnalyzerMode.class, mode);
  }

  @Test
  void testBatchModeImplementsAnalyzerMode() throws InvalidArgumentsException {
    AnalyzerMode mode = AnalyzerModeFactory.create(reportParser(), lexicon, reviewManager);
    assertInstanceOf(AnalyzerMode.class, mode);
  }

  // ── Factory produces distinct objects on each call ────────────────────────

  @Test
  void testFactoryProducesNewInstanceEachCall() throws InvalidArgumentsException {
    AnalyzerMode m1 = AnalyzerModeFactory.create(reportParser(), lexicon, reviewManager);
    AnalyzerMode m2 = AnalyzerModeFactory.create(reportParser(), lexicon, reviewManager);
    assertNotSame(m1, m2);
  }

  // ── Observable behavior: batch mode creates report.csv ──────────────────

  @Test
  void testBatchModeRunCreatesReportCsv() throws Exception {
    AnalyzerMode mode = AnalyzerModeFactory.create(reportParser(), lexicon, reviewManager);
    mode.run();
    assertTrue(Files.exists(tempDir.resolve("report.csv")),
        "BatchMode.run() must produce report.csv in the input directory");
  }

  // ── Observable behavior: interactive mode does not create report.csv ─────

  @Test
  void testInteractiveModeRunDoesNotCreateReportCsv() throws Exception {
    // Feed 'q' immediately so the interactive loop exits
    java.io.InputStream original = System.in;
    System.setIn(new java.io.ByteArrayInputStream("q\n".getBytes()));
    try {
      AnalyzerMode mode = AnalyzerModeFactory.create(interactiveParser(), lexicon, reviewManager);
      mode.run();
    } finally {
      System.setIn(original);
    }
    assertFalse(Files.exists(tempDir.resolve("report.csv")));
  }
}
