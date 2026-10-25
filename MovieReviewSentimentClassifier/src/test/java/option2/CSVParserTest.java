package option2;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CSVParserTest {

  @TempDir
  Path tempDir;

  private Path csvFile;

  @BeforeEach
  void setUp() throws IOException {
    csvFile = tempDir.resolve("sentiment_lexicon.csv");
  }

  private void writeCSV(String content) throws IOException {
    Files.writeString(csvFile, content);
  }

  // ---- Normal parsing ----

  @Test
  void testBasicTwoColumnParsing() throws IOException {
    writeCSV("word,score\ngood,0.8\nbad,-0.7\n");
    CSVParser parser = new CSVParser(new String[]{csvFile.toString()});
    Map<String, Double> data = parser.getData();

    assertEquals(2, data.size());
    assertEquals(0.8,  data.get("good"), 1e-9);
    assertEquals(-0.7, data.get("bad"),  1e-9);
    assertTrue(parser.getLog().isEmpty());
  }

  @Test
  void testHeaderLineIsSkipped() throws IOException {
    writeCSV("word,score\nhappy,0.9\n");
    CSVParser parser = new CSVParser(new String[]{csvFile.toString()});
    // "word" is the header, should not appear as a key
    assertFalse(parser.getData().containsKey("word"));
    assertFalse(parser.getData().containsKey("score"));
  }

  @Test
  void testWordsStoredAsLowercase() throws IOException {
    writeCSV("word,score\nGOOD,0.5\nBad,-0.4\n");
    CSVParser parser = new CSVParser(new String[]{csvFile.toString()});
    assertTrue(parser.getData().containsKey("good"));
    assertTrue(parser.getData().containsKey("bad"));
    assertFalse(parser.getData().containsKey("GOOD"));
  }

  @Test
  void testMultipleWords() throws IOException {
    writeCSV("word,score\ngreat,0.9\nterrible,-0.9\nokay,0.0\n");
    CSVParser parser = new CSVParser(new String[]{csvFile.toString()});
    assertEquals(3, parser.getData().size());
  }

  @Test
  void testHeaderOnlyFileProducesEmptyData() throws IOException {
    writeCSV("word,score\n");
    CSVParser parser = new CSVParser(new String[]{csvFile.toString()});
    assertTrue(parser.getData().isEmpty());
    assertTrue(parser.getLog().isEmpty());
  }

  @Test
  void testScoreZero() throws IOException {
    writeCSV("word,score\nneutral,0.0\n");
    CSVParser parser = new CSVParser(new String[]{csvFile.toString()});
    assertEquals(0.0, parser.getData().get("neutral"), 1e-9);
  }

  @Test
  void testScoreAtBoundaries() throws IOException {
    writeCSV("word,score\nbest,1.0\nworst,-1.0\n");
    CSVParser parser = new CSVParser(new String[]{csvFile.toString()});
    assertEquals(1.0,  parser.getData().get("best"),  1e-9);
    assertEquals(-1.0, parser.getData().get("worst"), 1e-9);
  }

  // ---- Invalid argument handling ----

  @Test
  void testTooFewArgumentsLogsError() {
    CSVParser parser = new CSVParser(new String[]{});
    assertTrue(parser.getData().isEmpty());
    assertFalse(parser.getLog().isEmpty());
  }

  @Test
  void testTooManyArgumentsLogsError() {
    CSVParser parser = new CSVParser(new String[]{"file1.csv", "file2.csv"});
    assertTrue(parser.getData().isEmpty());
    assertFalse(parser.getLog().isEmpty());
  }

  // ---- File error handling ----

  @Test
  void testFileNotFoundLogsError() {
    CSVParser parser = new CSVParser(new String[]{"/nonexistent/path/lexicon.csv"});
    assertTrue(parser.getData().isEmpty());
    assertFalse(parser.getLog().isEmpty());
  }

  // ---- Malformed lines ----

  @Test
  void testMalformedLineWithBadNumberLogsError() throws IOException {
    writeCSV("word,score\ngood,not_a_number\n");
    CSVParser parser = new CSVParser(new String[]{csvFile.toString()});
    // Bad line is skipped and an error is logged
    assertFalse(parser.getLog().isEmpty());
    assertFalse(parser.getData().containsKey("good"));
  }

  @Test
  void testLineWithOnlyOneColumnIsSkipped() throws IOException {
    // A line without a comma should be silently skipped (parts.length != 2)
    writeCSV("word,score\njustoneword\ngood,0.8\n");
    CSVParser parser = new CSVParser(new String[]{csvFile.toString()});
    assertEquals(1, parser.getData().size());
    assertTrue(parser.getData().containsKey("good"));
  }

  @Test
  void testLineWithMoreThanTwoColumnsIsSkipped() throws IOException {
    writeCSV("word,score\ngood,0.8,extra\nbad,-0.7\n");
    CSVParser parser = new CSVParser(new String[]{csvFile.toString()});
    // Only the valid 2-column line should be added
    assertEquals(1, parser.getData().size());
    assertTrue(parser.getData().containsKey("bad"));
  }

  // ---- equals / hashCode / toString ----

  @Test
  void testEqualParsersWithSameFile() throws IOException {
    writeCSV("word,score\ngood,0.8\n");
    CSVParser p1 = new CSVParser(new String[]{csvFile.toString()});
    CSVParser p2 = new CSVParser(new String[]{csvFile.toString()});
    assertEquals(p1, p2);
    assertEquals(p1.hashCode(), p2.hashCode());
  }

  @Test
  void testNotEqualWithDifferentData() throws IOException {
    writeCSV("word,score\ngood,0.8\n");
    CSVParser p1 = new CSVParser(new String[]{csvFile.toString()});

    Path csvFile2 = tempDir.resolve("other.csv");
    Files.writeString(csvFile2, "word,score\nbad,-0.7\n");
    CSVParser p2 = new CSVParser(new String[]{csvFile2.toString()});

    assertNotEquals(p1, p2);
  }

  @Test
  void testEqualsSameObject() throws IOException {
    writeCSV("word,score\ngood,0.8\n");
    CSVParser p = new CSVParser(new String[]{csvFile.toString()});
    assertEquals(p, p);
  }

  @Test
  void testNotEqualsNull() throws IOException {
    writeCSV("word,score\ngood,0.8\n");
    CSVParser p = new CSVParser(new String[]{csvFile.toString()});
    assertNotEquals(null, p);
  }

  @Test
  void testNotEqualsDifferentType() throws IOException {
    writeCSV("word,score\ngood,0.8\n");
    CSVParser p = new CSVParser(new String[]{csvFile.toString()});
    assertNotEquals("a string", p);
  }

  @Test
  void testToStringContainsData() throws IOException {
    writeCSV("word,score\ngood,0.8\n");
    CSVParser p = new CSVParser(new String[]{csvFile.toString()});
    String str = p.toString();
    assertTrue(str.contains("data") || str.contains("good"));
  }
}
