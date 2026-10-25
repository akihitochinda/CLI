package option2;

import java.io.File;
import java.util.Objects;

/**
 * Processes and validates command line arguments. This class doesn't *do* anything with the values
 * provided by the user beyond initial validation. It is another class' responsibility to determine
 * what to do with the user input.
 * Accepted invocations:
 *   sentimentAnalyzer directory           — interactive mode (Part 1)
 *   sentimentAnalyzer directory --report  — batch report mode (Part 2)
 */
public class CommandLineParser {

  private String inputDir;
  private String csvFile;
  private boolean reportMode;

  private static final String LEXICON_FILENAME = "sentiment_lexicon.csv";
  private static final String REPORT_FLAG      = "--report";

  /**
   * Constructor for the CommandLineParser Class.
   *
   * @param args The arguments provided by the user.
   * @throws InvalidArgumentsException if the arguments supplied are invalid.
   */
  public CommandLineParser(String[] args) throws InvalidArgumentsException {
    if (args.length < 1 || args.length > 2) {
      throw new InvalidArgumentsException(
          "Error: Usage: sentimentAnalyzer <directory> [--report]");
    }

    this.inputDir   = args[0];
    this.reportMode = false;

    // Parse optional second argument
    if (args.length == 2) {
      if (!args[1].equals(REPORT_FLAG)) {
        throw new InvalidArgumentsException(
            "Error: Unknown flag '" + args[1] + "'. Only --report is supported.");
      }
      this.reportMode = true;
    }

    // Normalize trailing separator (e.g., reviews/)
    if (!this.inputDir.endsWith(File.separator)) {
      this.inputDir += File.separator;
    }

    this.csvFile = this.inputDir + LEXICON_FILENAME;
    this.validatePaths();
  }

  /**
   * Validates that the input directory and lexicon CSV file exist.
   *
   * @throws InvalidArgumentsException if paths are invalid.
   */
  private void validatePaths() throws InvalidArgumentsException {
    File folder = new File(this.inputDir);
    if (!folder.exists()) {
      throw new InvalidArgumentsException(
          "Error: Input directory does not exist: " + this.inputDir);
    }
    if (!folder.isDirectory()) {
      throw new InvalidArgumentsException(
          "Error: Input path is not a directory: " + this.inputDir);
    }

    File file = new File(this.csvFile);
    if (!file.exists()) {
      throw new InvalidArgumentsException(
          "Error: CSV file does not exist: " + this.csvFile);
    }
  }

  /** @return The normalized input directory path (always ends with a file separator). */
  public String getInputDir() {
    return this.inputDir;
  }

  /** @return The full path to the sentiment lexicon CSV. */
  public String getCsvFile() {
    return this.csvFile;
  }

  /** @return true if --report was supplied; false for interactive mode. */
  public boolean isReportMode() {
    return this.reportMode;
  }

  @Override
  public String toString() {
    return "CommandLineParser{" +
        "inputDir='"   + inputDir   + '\'' +
        ", csvFile='"  + csvFile    + '\'' +
        ", reportMode=" + reportMode +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    CommandLineParser that = (CommandLineParser) o;
    return reportMode == that.reportMode
        && Objects.equals(inputDir, that.inputDir)
        && Objects.equals(csvFile,  that.csvFile);
  }

  @Override
  public int hashCode() {
    return Objects.hash(inputDir, csvFile, reportMode);
  }
}
