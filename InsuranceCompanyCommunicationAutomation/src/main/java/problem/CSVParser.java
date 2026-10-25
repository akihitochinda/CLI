package problem;

import java.util.Objects;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses a CSV file where each field is enclosed in double quotes and separated by commas.
 * Fields may contain commas within the double quotes.
 * The first row of the CSV is treated as a header row.
 */
public class CSVParser {

  private List<Map<String, String>> data;
  private ErrorLogger log;

  /**
   * Constructs a CSVParser and immediately parses the given file path.
   *
   * @param filePath the path to the CSV file to parse
   */
  public CSVParser(String filePath) {
    this.log = new ErrorLogger();
    this.data = new ArrayList<>();
    if (validateFilePath(filePath)) {
      List<String> lines = readFile(filePath);
      if (!lines.isEmpty()) {
        this.data = processInput(lines);
      }
    }
  }

  /**
   * Validates that the provided file path is not null or blank.
   *
   * @param filePath the file path to validate
   * @return true if valid, false otherwise
   */
  private boolean validateFilePath(String filePath) {
    if (filePath == null || filePath.isBlank()) {
      this.log.log("CSV file path must not be null or empty.");
      return false;
    }
    return true;
  }

  /**
   * Reads all lines from the file at the given path.
   * Uses try-with-resources to ensure the reader is always closed.
   *
   * @param path the file path to read
   * @return a list of lines read from the file; empty list if an error occurs
   */
  private List<String> readFile(String path) {
    List<String> lines = new ArrayList<>();
    // try-with-resources: BufferedReader is auto-closed even if exception occurs
    try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
      String line;
      while ((line = reader.readLine()) != null) {
        lines.add(line);
      }
    } catch (FileNotFoundException e) {
      this.log.log("File not found: " + path);
    } catch (IOException e) {
      this.log.log("IO error while reading file: " + path + ": " + e.getMessage());
    }
    return lines;
  }

  /**
   * Processes the raw lines of a CSV file into a list of maps.
   * The first line is treated as headers; subsequent lines are data rows.
   * Each map uses the header name as the key and the cell value as the value.
   *
   * <p>Fields containing commas are supported because each field is matched
   * by the pattern {@code "([^"]*)"}, which captures everything between
   * the outermost double quotes.</p>
   *
   * @param lines the raw lines read from the CSV file
   * @return a list of maps, one per data row
   */
  private List<Map<String, String>> processInput(List<String> lines) {
    List<Map<String, String>> members = new ArrayList<>();

    // Matches any content enclosed in double quotes (fields may contain commas)
    Pattern fieldPattern = Pattern.compile("\"([^\"]*)\"");

    // --- Parse header row ---
    Matcher headerMatcher = fieldPattern.matcher(lines.get(0));
    List<String> headers = new ArrayList<>();
    while (headerMatcher.find()) {
      headers.add(headerMatcher.group(1));
    }

    if (headers.isEmpty()) {
      this.log.log("CSV file has no valid header row.");
      return members;
    }

    // --- Parse data rows ---
    for (int i = 1; i < lines.size(); i++) {
      String line = lines.get(i);
      if (line.isBlank()) continue; // skip empty lines

      Map<String, String> member = new HashMap<>();
      Matcher memberMatcher = fieldPattern.matcher(line);
      int col = 0;

      while (memberMatcher.find()) {
        if (col >= headers.size()) {
          this.log.log("Row " + i + " has more fields than headers. Extra fields ignored.");
          break;
        }
        member.put(headers.get(col), memberMatcher.group(1));
        col++;
      }

      // Warn if a row has fewer fields than headers (partial row)
      if (col < headers.size()) {
        this.log.log("Row " + i + " has fewer fields than headers (" + col
                + " found, " + headers.size() + " expected).");
      }

      members.add(member);
    }

    return members;
  }

  /**
   * Returns the parsed data as a list of maps.
   * Each map represents one data row, keyed by CSV column header.
   *
   * @return the parsed data; may be empty if parsing failed
   */
  public List<Map<String, String>> getData() {
    return data;
  }

  /**
   * Returns the ErrorLogger containing any errors encountered during parsing.
   *
   * @return the ErrorLogger instance
   */
  public ErrorLogger getLog() {
    return log;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CSVParser that = (CSVParser) o;
    return Objects.equals(data, that.data) &&
            Objects.equals(log, that.log);
  }

  @Override
  public int hashCode() {
    return Objects.hash(data, log);
  }

  @Override
  public String toString() {
    return "CSVParser{data=" + data + ", log=" + log + '}';
  }
}