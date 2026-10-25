package problem;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CommandLineParserTest {
  private  CommandLineParser testParser;

  @BeforeEach
  void setUp() throws InvalidArgumentsException {
    testParser =  new CommandLineParser(new String[]{
        "--email",
        "--email-template", "src/main/java/problem/email-template.txt",
        "--letter",
        "--letter-template", "src/main/java/problem/letter-template.txt",
        "--output-dir",     "src/main/java/problem",
        "--csv-file", "src/main/java/problem/insurance-company-memebers.csv",
    });
  }


  @Test
  void getGenerateEmail() {
    assertTrue( testParser.getGenerateEmail() != null);
  }

  @Test
  void getGenerateLetter() {
    assertTrue( testParser.getGenerateLetter() != null);
  }

  @Test
  void getEmailTemplate() {
    assertEquals( "src/main/java/problem/email-template.txt", testParser.getEmailTemplate());
  }

  @Test
  void getLetterTemplate() {
    assertEquals("src/main/java/problem/letter-template.txt", testParser.getLetterTemplate());
  }

  @Test
  void getOutputDir() {
    assertEquals("src/main/java/problem", testParser.getOutputDir());
  }

  @Test
  void getCsvFile() {
    assertEquals("src/main/java/problem/insurance-company-memebers.csv", testParser.getCsvFile());
  }

  @Test
  void testEmailWithoutEmailTemplate() {
    assertThrows(InvalidArgumentsException.class,
        () -> new CommandLineParser(new String[]{
            "--email",
            "--letter",
            "--letter-template", "src/main/java/problem/letter-template.txt",
            "--output-dir",     "src/main/java/problem",
            "--csv-file", "src/main/java/problem/insurance-company-memebers.csv",
        }));
  }

  @Test
  void testLetterWithoutLetterTemplate() {
    assertThrows(InvalidArgumentsException.class,
        () -> new CommandLineParser(new String[]{
            "--email",
            "--email-template", "src/main/java/problem/email-template.txt",
            "--letter",
            "--output-dir",     "src/main/java/problem",
            "--csv-file", "src/main/java/problem/insurance-company-memebers.csv",
        }));
  }

  @Test
  void testNoCSVFile() {
    assertThrows(InvalidArgumentsException.class,
        () -> new CommandLineParser(new String[]{
            "--email",
            "--email-template", "src/main/java/problem/email-template.txt",
            "--letter",
            "--letter-template", "src/main/java/problem/letter-template.txt",
            "--output-dir",     "src/main/java/problem",
        }));
  }

  @Test
  void testNoOutputDir() {
    assertThrows(InvalidArgumentsException.class,
        () -> new CommandLineParser(new String[]{
            "--email",
            "--email-template", "src/main/java/problem/email-template.txt",
            "--letter",
            "--letter-template", "src/main/java/problem/letter-template.txt",
            "--csv-file", "src/main/java/problem/insurance-company-memebers.csv",
        }));
  }

  @Test
  void testNoEmailOrLetterTemplate() {
    assertThrows(InvalidArgumentsException.class,
        () -> new CommandLineParser(new String[]{
            "--email-template", "src/main/java/problem/email-template.txt",
            "--letter-template", "src/main/java/problem/letter-template.txt",
            "--output-dir",     "src/main/java/problem",
            "--csv-file", "src/main/java/problem/insurance-company-memebers.csv",
        }));
  }

  @Test
  void testArrayOutOfIndex() {
    assertThrows(InvalidArgumentsException.class,
        () -> new CommandLineParser(new String[]{
            "--email",
            "--email-template",
            "--output-dir",     "src/main/java/problem",
            "--csv-file", "src/main/java/problem/insurance-company-memebers.csv",
        }));
  }


  @Test
  void testToString() {
    String expectedString = "CommandLineParser{" +
        "generateEmail=" + testParser.getGenerateEmail() +
        ", generateLetter=" + testParser.getGenerateLetter() +
        ", emailTemplate='" + testParser.getEmailTemplate() + '\'' +
        ", letterTemplate='" + testParser.getLetterTemplate() + '\'' +
        ", outputDir='" + testParser.getOutputDir() + '\'' +
        ", csvFile='" + testParser.getCsvFile() + '\'' +
        '}';
    assertEquals(expectedString, testParser.toString());

  }

  @Test
  void testEquals_SameObject() {
    assertEquals(testParser, testParser);
  }

  @Test
  void testEquals_ObjectNull() {
    assertFalse(testParser.equals(null));
  }

  @Test
  void testEquals_DifferentDataType() {
    assertFalse(testParser.equals("String"));
  }


  @Test
  void testHashCode() {
    int expectedHashCode = Objects.hash(testParser.getGenerateEmail(), testParser.getGenerateLetter(),
        testParser.getEmailTemplate(), testParser.getLetterTemplate(), testParser.getOutputDir(),
        testParser.getCsvFile());
    assertEquals(expectedHashCode, testParser.hashCode());
  }
}