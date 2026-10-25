package option2;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CommandLineParserTest {

  @TempDir
  Path tempDir;

  @BeforeEach
  void setUp() throws IOException {
    Files.writeString(tempDir.resolve("sentiment_lexicon.csv"),
        "word,score\ngood,0.8\nbad,-0.7\n");
  }

  // ── Constructor / argument-count validation ───────────────────────────────

  @Test
  void testValidDirectoryOneArgConstructsSuccessfully() {
    assertDoesNotThrow(() -> new CommandLineParser(new String[]{tempDir.toString()}));
  }

  @Test
  void testValidDirectoryWithReportFlagConstructsSuccessfully() {
    assertDoesNotThrow(() ->
        new CommandLineParser(new String[]{tempDir.toString(), "--report"}));
  }

  @Test
  void testNoArgumentsThrows() {
    assertThrows(InvalidArgumentsException.class,
        () -> new CommandLineParser(new String[]{}));
  }

  @Test
  void testThreeArgumentsThrows() {
    assertThrows(InvalidArgumentsException.class,
        () -> new CommandLineParser(
            new String[]{tempDir.toString(), "--report", "extra"}));
  }

  @Test
  void testUnknownFlagThrows() {
    assertThrows(InvalidArgumentsException.class,
        () -> new CommandLineParser(new String[]{tempDir.toString(), "--unknown"}));
  }

  @Test
  void testNonExistentDirectoryThrows() {
    assertThrows(InvalidArgumentsException.class,
        () -> new CommandLineParser(new String[]{"/nonexistent/path/reviews"}));
  }

  @Test
  void testPathToFileNotDirectoryThrows() throws IOException {
    Path plainFile = tempDir.resolve("notADir.txt");
    Files.writeString(plainFile, "hello");
    assertThrows(InvalidArgumentsException.class,
        () -> new CommandLineParser(new String[]{plainFile.toString()}));
  }

  @Test
  void testMissingLexiconThrows() throws IOException {
    Path emptyDir = tempDir.resolve("empty");
    Files.createDirectory(emptyDir);
    assertThrows(InvalidArgumentsException.class,
        () -> new CommandLineParser(new String[]{emptyDir.toString()}));
  }

  // ── isReportMode ─────────────────────────────────────────────────────────

  @Test
  void testIsReportModeFalseWithOneArg() throws InvalidArgumentsException {
    CommandLineParser p = new CommandLineParser(new String[]{tempDir.toString()});
    assertFalse(p.isReportMode());
  }

  @Test
  void testIsReportModeTrueWithReportFlag() throws InvalidArgumentsException {
    CommandLineParser p = new CommandLineParser(
        new String[]{tempDir.toString(), "--report"});
    assertTrue(p.isReportMode());
  }

  // ── getInputDir ───────────────────────────────────────────────────────────

  @Test
  void testGetInputDirEndsWithSeparator() throws InvalidArgumentsException {
    CommandLineParser p = new CommandLineParser(new String[]{tempDir.toString()});
    assertTrue(p.getInputDir().endsWith(java.io.File.separator));
  }

  @Test
  void testGetInputDirDoesNotDoubleTrailingSeparator() throws InvalidArgumentsException {
    String withSep = tempDir.toString() + java.io.File.separator;
    CommandLineParser p = new CommandLineParser(new String[]{withSep});
    assertFalse(p.getInputDir().endsWith(
        java.io.File.separator + java.io.File.separator));
  }

  @Test
  void testGetInputDirEndsWithSeparatorInReportMode() throws InvalidArgumentsException {
    CommandLineParser p = new CommandLineParser(
        new String[]{tempDir.toString(), "--report"});
    assertTrue(p.getInputDir().endsWith(java.io.File.separator));
  }

  // ── getCsvFile ────────────────────────────────────────────────────────────

  @Test
  void testGetCsvFilePointsToLexicon() throws InvalidArgumentsException {
    CommandLineParser p = new CommandLineParser(new String[]{tempDir.toString()});
    assertTrue(p.getCsvFile().endsWith("sentiment_lexicon.csv"));
  }

  @Test
  void testGetCsvFileIsInsideInputDir() throws InvalidArgumentsException {
    CommandLineParser p = new CommandLineParser(new String[]{tempDir.toString()});
    assertTrue(p.getCsvFile().startsWith(p.getInputDir()));
  }

  @Test
  void testGetCsvFilePointsToLexiconInReportMode() throws InvalidArgumentsException {
    CommandLineParser p = new CommandLineParser(
        new String[]{tempDir.toString(), "--report"});
    assertTrue(p.getCsvFile().endsWith("sentiment_lexicon.csv"));
  }

  // ── equals / hashCode / toString ─────────────────────────────────────────

  @Test
  void testEqualsParsersWithSameDirAndNoFlag() throws InvalidArgumentsException {
    CommandLineParser p1 = new CommandLineParser(new String[]{tempDir.toString()});
    CommandLineParser p2 = new CommandLineParser(new String[]{tempDir.toString()});
    assertEquals(p1, p2);
  }

  @Test
  void testEqualsParsersWithSameDirAndReportFlag() throws InvalidArgumentsException {
    CommandLineParser p1 = new CommandLineParser(new String[]{tempDir.toString(), "--report"});
    CommandLineParser p2 = new CommandLineParser(new String[]{tempDir.toString(), "--report"});
    assertEquals(p1, p2);
  }

  @Test
  void testNotEqualsInteractiveVsReportMode() throws InvalidArgumentsException {
    CommandLineParser p1 = new CommandLineParser(new String[]{tempDir.toString()});
    CommandLineParser p2 = new CommandLineParser(new String[]{tempDir.toString(), "--report"});
    assertNotEquals(p1, p2);
  }

  @Test
  void testEqualsSameObject() throws InvalidArgumentsException {
    CommandLineParser p = new CommandLineParser(new String[]{tempDir.toString()});
    assertEquals(p, p);
  }

  @Test
  void testNotEqualsNull() throws InvalidArgumentsException {
    CommandLineParser p = new CommandLineParser(new String[]{tempDir.toString()});
    assertNotEquals(null, p);
  }

  @Test
  void testNotEqualsDifferentType() throws InvalidArgumentsException {
    CommandLineParser p = new CommandLineParser(new String[]{tempDir.toString()});
    assertNotEquals("a string", p);
  }

  @Test
  void testHashCodeConsistentWithEquals() throws InvalidArgumentsException {
    CommandLineParser p1 = new CommandLineParser(new String[]{tempDir.toString()});
    CommandLineParser p2 = new CommandLineParser(new String[]{tempDir.toString()});
    assertEquals(p1.hashCode(), p2.hashCode());
  }

  @Test
  void testHashCodeDiffersAcrossModes() throws InvalidArgumentsException {
    CommandLineParser p1 = new CommandLineParser(new String[]{tempDir.toString()});
    CommandLineParser p2 = new CommandLineParser(new String[]{tempDir.toString(), "--report"});
    assertNotEquals(p1.hashCode(), p2.hashCode());
  }

  @Test
  void testHashCodeMatchesObjectsHash() throws InvalidArgumentsException {
    CommandLineParser p = new CommandLineParser(new String[]{tempDir.toString()});
    int expected = Objects.hash(p.getInputDir(), p.getCsvFile(), p.isReportMode());
    assertEquals(expected, p.hashCode());
  }

  @Test
  void testToStringContainsExpectedFields() throws InvalidArgumentsException {
    CommandLineParser p = new CommandLineParser(new String[]{tempDir.toString(), "--report"});
    String s = p.toString();
    assertTrue(s.contains("inputDir"));
    assertTrue(s.contains("csvFile"));
    assertTrue(s.contains("reportMode"));
  }
}
