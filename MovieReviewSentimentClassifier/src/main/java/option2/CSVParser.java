package option2;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The type Csv parser.
 */
public class CSVParser {
  private Map<String, Double> data;
  private ErrorLogger log;

  /**
   * Instantiates a new Csv parser.
   *
   * @param args the args
   */
  public CSVParser(String[] args) {
    this.log = new ErrorLogger();
    this.data =  this.populateData(args);
  }

  /**
   * Checks if the input arguments are valid.
   * 
   * @param args the args
   * @return the boolean
   */
  private boolean verifyInput(String[] args) {
    if (args.length != 1) {
      this.log.log("Exactly one argument expected: the file path");
      return false;
    }
    return true;
  }

  /**
   * Populate data map from CSV file.
   *
   * @param args the args
   * @return the map
   */
  private Map<String, Double> populateData(String[] args) {
    Map<String, Double> data = new HashMap<>();
    if (!this.verifyInput(args))
      return data;
    List<String> lines = this.readFile(args[0]);
    return this.processInput(lines);
  }

  /**
   * Reads all lines from the file at the given path.
   *
   * @param path the file path to read
   * @return a list of lines read from the file; empty list if an error occurs
   */
  private List<String> readFile(String path) {
    List<String> lines = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
      String line = "";
      while ((line = reader.readLine()) != null)
        lines.add(line);
    } catch (FileNotFoundException e) {
      this.log.log("File not found");
    } catch (IOException e) {
      this.log.log("IO exception");
    }
    return lines;
  }

  /**
   * Processes the list of lines from the CSV file and populates the data map.
   *
   * @param lines the lines read from the CSV file
   * @return a map of words to their corresponding scores
   */
  private Map<String, Double> processInput(List<String> lines) {
    Map<String, Double> data = new HashMap<>();

    for (int i = 1; i < lines.size(); i++) {  // skip header line
      String line = lines.get(i);
      String[] parts = line.split(",");
      if (parts.length == 2) {
        try {
          String word = parts[0].toLowerCase();
          double score = Double.parseDouble(parts[1]);
          data.put(word, score);
        } catch (NumberFormatException e) {
          this.log.log("Invalid number format in line: " + line);
        }
      }
    }
    return data;
  }

  /**
   * Gets data.
   *
   * @return the data
   */
  public Map<String, Double> getData() {
    return this.data;
  }

  /**
   * Gets log.
   *
   * @return the log
   */
  public ErrorLogger getLog() {
    return this.log;
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