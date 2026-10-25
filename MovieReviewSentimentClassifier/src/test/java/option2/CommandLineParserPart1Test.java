package option2;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CommandLineParserPart1Test {

  @TempDir
  Path tempDir;

  private Path lexiconFile;

  @BeforeEach
  void setUp() throws IOException {
    // CommandLineParser_part1 expects a "sentiment_lexicon.csv" inside the given directory
    lexiconFile = tempDir.resolve("sentiment_lexicon.csv");
    Files.writeString(lexiconFile, "word,score\ngood,0.8\nbad,-0.7\n");
  }

  // ---- Constructor / validation ----

  @Test
  void testValidDirectoryConstructsSuccessfully() {
    assertDoesNotThrow(() -> new CommandLineParserPart1(new String[]{tempDir.toString()}));
  }

  @Test
  void testTooFewArgumentsThrows() {
    assertThrows(InvalidArgumentsException.class,
        () -> new CommandLineParserPart1(new String[]{}));
  }

  @Test
  void testTooManyArgumentsThrows() {
    assertThrows(InvalidArgumentsException.class,
        () -> new CommandLineParserPart1(new String[]{tempDir.toString(), "extra"}));
  }

  @Test
  void testNonExistentDirectoryThrows() {
    assertThrows(InvalidArgumentsException.class,
        () -> new CommandLineParserPart1(new String[]{"/nonexistent/path/reviews"}));
  }

  @Test
  void testPathToFileNotDirectoryThrows() throws IOException {
    Path plainFile = tempDir.resolve("notADir.txt");
    Files.writeString(plainFile, "hello");
    assertThrows(InvalidArgumentsException.class,
        () -> new CommandLineParserPart1(new String[]{plainFile.toString()}));
  }

  @Test
  void testMissingLexiconFileThrows() throws IOException {
    // Valid directory but no sentiment_lexicon.csv inside
    Path emptyDir = tempDir.resolve("empty");
    Files.createDirectory(emptyDir);
    assertThrows(InvalidArgumentsException.class,
        () -> new CommandLineParserPart1(new String[]{emptyDir.toString()}));
  }

  // ---- getInputDir ----

  @Test
  void testGetInputDirEndsWithSeparator() throws InvalidArgumentsException {
    CommandLineParserPart1 parser = new CommandLineParserPart1(new String[]{tempDir.toString()});
    assertTrue(parser.getInputDir().endsWith(java.io.File.separator));
  }

  @Test
  void testGetInputDirWithTrailingSeparatorAlreadyPresent() throws InvalidArgumentsException {
    // Passing path that already ends with separator should not double it
    String pathWithSep = tempDir.toString() + java.io.File.separator;
    CommandLineParserPart1 parser = new CommandLineParserPart1(new String[]{pathWithSep});
    assertFalse(parser.getInputDir().endsWith(
        java.io.File.separator + java.io.File.separator));
  }

  // ---- getCsvFile ----

  @Test
  void testGetCsvFilePointsToLexicon() throws InvalidArgumentsException {
    CommandLineParserPart1 parser = new CommandLineParserPart1(new String[]{tempDir.toString()});
    assertTrue(parser.getCsvFile().endsWith("sentiment_lexicon.csv"));
  }

  @Test
  void testGetCsvFileIsInsideInputDir() throws InvalidArgumentsException {
    CommandLineParserPart1 parser = new CommandLineParserPart1(new String[]{tempDir.toString()});
    assertTrue(parser.getCsvFile().startsWith(parser.getInputDir()));
  }

  // ---- equals / hashCode / toString ----

  @Test
  void testEqualsParsersWithSameDir() throws InvalidArgumentsException {
    CommandLineParserPart1 p1 = new CommandLineParserPart1(new String[]{tempDir.toString()});
    CommandLineParserPart1 p2 = new CommandLineParserPart1(new String[]{tempDir.toString()});
    assertEquals(p1, p2);
  }

  @Test
  void testEqualsSameObject() throws InvalidArgumentsException {
    CommandLineParserPart1 p = new CommandLineParserPart1(new String[]{tempDir.toString()});
    assertEquals(p, p);
  }

  @Test
  void testNotEqualsNull() throws InvalidArgumentsException {
    CommandLineParserPart1 p = new CommandLineParserPart1(new String[]{tempDir.toString()});
    assertNotEquals(null, p);
  }

  @Test
  void testNotEqualsDifferentType() throws InvalidArgumentsException {
    CommandLineParserPart1 p = new CommandLineParserPart1(new String[]{tempDir.toString()});
    assertNotEquals("a string", p);
  }

  @Test
  void testHashCodeConsistentWithEquals() throws InvalidArgumentsException {
    CommandLineParserPart1 p1 = new CommandLineParserPart1(new String[]{tempDir.toString()});
    CommandLineParserPart1 p2 = new CommandLineParserPart1(new String[]{tempDir.toString()});
    assertEquals(p1.hashCode(), p2.hashCode());
  }

  @Test
  void testToStringContainsInputDir() throws InvalidArgumentsException {
    CommandLineParserPart1 p = new CommandLineParserPart1(new String[]{tempDir.toString()});
    assertTrue(p.toString().contains("inputDir"));
    assertTrue(p.toString().contains("csvFile"));
  }

  @Test
  void testHashCodeMatchesObjectsHash() throws InvalidArgumentsException {
    CommandLineParserPart1 p = new CommandLineParserPart1(new String[]{tempDir.toString()});
    int expected = Objects.hash(p.getInputDir(), p.getCsvFile());
    assertEquals(expected, p.hashCode());
  }
}
