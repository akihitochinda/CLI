package problem;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OutputFileWriterTest {

  private OutputFileWriter testOutputFileWriter;
  private String testOutputDir;
  private String testFileName;


  @BeforeEach
  void setUp() {
    testOutputDir = "src/main/java/problem";
    testFileName = "Email_1.txt";
    testOutputFileWriter = new OutputFileWriter(testOutputDir, testFileName);
  }



  @Test
  void fileWrite_NewContent() throws IOException {
    String newContent = "Hello World!";
    testOutputFileWriter.fileWrite(newContent);
    // read the string written in the file
    String wordsWritten = Files.readString(Paths.get(testOutputDir + "/" + testFileName));
    assertEquals(newContent, wordsWritten);
  }

  @Test
  void fileWrite_throwsException() throws IOException {
    String newContent = "Hello World!";
    OutputFileWriter notFoundOutputFileWriter = new OutputFileWriter("notFoundDirectory", testFileName);
    notFoundOutputFileWriter.fileWrite(newContent);
    assertFalse(Files.exists(Paths.get("notFoundDirectory", testFileName)));
  }

  @Test
  void fileCreation_correctPath() throws IOException {
    testOutputFileWriter.fileWrite("Hello World!");
    assertTrue(Files.exists(Paths.get(testOutputDir, testFileName)));
  }

  @Test
  void fileWrite_closeFile() throws IOException {
    // write "hello world" in the testOutputFileWriter file
    testOutputFileWriter.fileWrite("Hello World!");
    testOutputFileWriter.fileWrite("Hello World again!");
    String wordsWritten = Files.readString(Paths.get(testOutputDir,testFileName));
    assertEquals(wordsWritten, "Hello World again!");
  }


  @Test
  void testToString() {
    String expectedString = "OutputFileWriter{" +
        "outputDir='" + testOutputFileWriter.getOutputDir() + '\'' +
        ", fileName='" + testOutputFileWriter.getFileName() + '\'' +
        '}';
    assertEquals(expectedString, testOutputFileWriter.toString());
  }

  @Test
  void testEqualsSameObject() {
    assertTrue(testOutputFileWriter.equals(testOutputFileWriter));
  }

  @Test
  void testEquals_ObjectNull() {
    assertFalse(testOutputFileWriter.equals(null));
  }

  @Test
  void testEquals_DifferentDataType() {
    assertFalse(testOutputFileWriter.equals("String"));
  }

  @Test
  void testEquals_DifferentOutputDir() {
    testOutputDir = "src/main/java";
    assertFalse(testOutputDir.equals(testOutputFileWriter.getOutputDir()));
  }

  @Test
  void testHashCode() {
    int expectedHashCode = Objects.hash(testOutputFileWriter.getOutputDir(), testOutputFileWriter.getFileName());
    assertEquals(expectedHashCode, testOutputFileWriter.hashCode());
  }

  @Test
  void getOutputDir() {
    assertEquals("src/main/java/problem",  testOutputFileWriter.getOutputDir());
  }

  @Test
  void getFileName() {
    assertEquals("Email_1.txt", testOutputFileWriter.getFileName());

  }
}