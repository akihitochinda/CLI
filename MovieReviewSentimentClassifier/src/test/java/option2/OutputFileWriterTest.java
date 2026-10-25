package option2;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class OutputFileWriterTest {

  @TempDir
  Path tempDir;

  private OutputFileWriter writer;
  private String outputDir;   // tempDir path with trailing separator
  private static final String FILE_NAME = "output.txt";

  @BeforeEach
  void setUp() {
    // Mirrors CommandLineParser: inputDir always ends with File.separator
    outputDir = tempDir.toString() + java.io.File.separator;
    writer = new OutputFileWriter(outputDir, FILE_NAME);
  }

  // ── fileWrite: content correctness ───────────────────────────────────────

  @Test
  void testFileWriteCreatesFile() throws IOException {
    writer.fileWrite("Hello World!");
    // Class joins as outputDir + fileName — use the same construction
    assertTrue(Files.exists(Path.of(outputDir + FILE_NAME)));
  }

  @Test
  void testFileWriteStoresCorrectContent() throws IOException {
    String content = "Hello World!";
    writer.fileWrite(content);
    assertEquals(content, Files.readString(Path.of(outputDir + FILE_NAME)));
  }

  @Test
  void testFileWriteStoresMultilineContent() throws IOException {
    String content = "Line one\nLine two\nLine three";
    writer.fileWrite(content);
    assertEquals(content, Files.readString(Path.of(outputDir + FILE_NAME)));
  }

  @Test
  void testFileWriteStoresEmptyString() throws IOException {
    writer.fileWrite("");
    assertEquals("", Files.readString(Path.of(outputDir + FILE_NAME)));
  }

  @Test
  void testFileWriteOverwritesPreviousContent() throws IOException {
    writer.fileWrite("First write");
    writer.fileWrite("Second write");
    // Only the second content should remain — FileWriter does not append by default
    assertEquals("Second write", Files.readString(Path.of(outputDir + FILE_NAME)));
  }

  @Test
  void testFileWriteClosesFileAfterWrite() throws IOException {
    // If the file is properly closed, a second write must succeed without error
    writer.fileWrite("First");
    assertDoesNotThrow(() -> writer.fileWrite("Second"));
  }

  // ── fileWrite: CSV content (ReportGenerator integration) ─────────────────

  @Test
  void testFileWriteCsvContent() throws IOException {
    String csv = "Filename,SentimentScore,Label\nreview1.txt,0.80,POSITIVE\n";
    OutputFileWriter csvWriter = new OutputFileWriter(outputDir, "report.csv");
    csvWriter.fileWrite(csv);

    assertEquals(csv, Files.readString(Path.of(outputDir + "report.csv")));
  }

  @Test
  void testFileWriteCorrectPathUsed() throws IOException {
    writer.fileWrite("data");
    // File must appear at outputDir + fileName — the exact path the class uses
    assertTrue(Files.exists(Path.of(outputDir + FILE_NAME)));
  }

  // ── fileWrite: invalid directory ──────────────────────────────────────────

  @Test
  void testFileWriteToNonExistentDirectoryDoesNotCreateFile() throws IOException {
    OutputFileWriter badWriter = new OutputFileWriter("/nonexistent/path/", FILE_NAME);
    badWriter.fileWrite("content");
    assertFalse(Files.exists(Path.of("/nonexistent/path/" + FILE_NAME)));
  }

  @Test
  void testFileWriteToNonExistentDirectoryDoesNotThrow() {
    // The class catches IOException internally — must not bubble up to callers
    OutputFileWriter badWriter = new OutputFileWriter("/nonexistent/path/", FILE_NAME);
    assertDoesNotThrow(() -> badWriter.fileWrite("content"));
  }

  // ── getOutputDir ──────────────────────────────────────────────────────────

  @Test
  void testGetOutputDirReturnsConstructorValue() {
    assertEquals(outputDir, writer.getOutputDir());
  }

  @Test
  void testGetOutputDirIsUnchangedAfterWrite() throws IOException {
    writer.fileWrite("something");
    assertEquals(outputDir, writer.getOutputDir());
  }

  // ── getFileName ───────────────────────────────────────────────────────────

  @Test
  void testGetFileNameReturnsConstructorValue() {
    assertEquals(FILE_NAME, writer.getFileName());
  }

  @Test
  void testGetFileNameIsUnchangedAfterWrite() throws IOException {
    writer.fileWrite("something");
    assertEquals(FILE_NAME, writer.getFileName());
  }

  // ── equals ────────────────────────────────────────────────────────────────

  @Test
  void testEqualsSameObject() {
    assertEquals(writer, writer);
  }

  @Test
  void testEqualsSymmetricWithSameFields() {
    OutputFileWriter other = new OutputFileWriter(outputDir, FILE_NAME);
    assertEquals(writer, other);
    assertEquals(other, writer);
  }

  @Test
  void testNotEqualsNull() {
    assertNotEquals(null, writer);
  }

  @Test
  void testNotEqualsDifferentType() {
    assertNotEquals("a string", writer);
  }

  @Test
  void testNotEqualsDifferentOutputDir() {
    OutputFileWriter other = new OutputFileWriter("/some/other/dir/", FILE_NAME);
    assertNotEquals(writer, other);
  }

  @Test
  void testNotEqualsDifferentFileName() {
    OutputFileWriter other = new OutputFileWriter(outputDir, "different.txt");
    assertNotEquals(writer, other);
  }

  @Test
  void testNotEqualsDifferentDirAndFileName() {
    OutputFileWriter other = new OutputFileWriter("/other/dir/", "other.txt");
    assertNotEquals(writer, other);
  }

  // ── hashCode ──────────────────────────────────────────────────────────────

  @Test
  void testHashCodeConsistentWithEquals() {
    OutputFileWriter other = new OutputFileWriter(outputDir, FILE_NAME);
    assertEquals(writer.hashCode(), other.hashCode());
  }

  @Test
  void testHashCodeMatchesObjectsHash() {
    int expected = Objects.hash(writer.getOutputDir(), writer.getFileName());
    assertEquals(expected, writer.hashCode());
  }

  @Test
  void testHashCodeStableAcrossCalls() {
    assertEquals(writer.hashCode(), writer.hashCode());
  }

  // ── toString ──────────────────────────────────────────────────────────────

  @Test
  void testToStringContainsOutputDir() {
    assertTrue(writer.toString().contains("outputDir"));
  }

  @Test
  void testToStringContainsFileName() {
    assertTrue(writer.toString().contains("fileName"));
  }

  @Test
  void testToStringMatchesExpectedFormat() {
    String expected = "OutputFileWriter{" +
        "outputDir='" + writer.getOutputDir() + '\'' +
        ", fileName='" + writer.getFileName() + '\'' +
        '}';
    assertEquals(expected, writer.toString());
  }
}
