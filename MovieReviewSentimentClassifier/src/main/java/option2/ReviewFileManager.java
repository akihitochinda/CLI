package option2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The type Review file manager.
 * Manages reading and scanning review text files from a folder
 */
public class ReviewFileManager {

  private String inputDir;
  private Map<Integer, String> reviewFiles;

  /**
   * Constructor for the ReviewFileManager Class
   *
   * @param inputDir The folder path containing review files
   */
  public ReviewFileManager(String inputDir) {
    this.inputDir = inputDir;
    this.reviewFiles = this.scanReviewFiles();
  }

  /**
   * Scans the input directory for review files
   *
   * @return A map of file indices to file names
   */
  private Map<Integer, String> scanReviewFiles() {
    Map<Integer, String> fileMap = new HashMap<>();
    File folder = new File(this.inputDir);
    File[] files = folder.listFiles(); // get all files in the folder
    int index = 1;
    
    if (files != null) {
      for (File file : files) {
        if (file.isFile() && file.getName().endsWith(".txt")) { // only consider .txt files
          fileMap.put(index, file.getName());
          index++;
        }
      }
    }
    return fileMap;
  }

  /**
   * Reads the content of a specific review file by index
   *
   * @param index index of the file (1, 2, 3, etc.)
   * @return The full content of the file as a string
   * @throws InvalidArgumentsException if index is out of range
   */
  public String readReviewFile(int index) throws InvalidArgumentsException {

    String fileName = this.reviewFiles.get(index);
    String path = this.inputDir + fileName;
    
    StringBuilder lines = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
      String line = "";
      while ((line = reader.readLine()) != null) {
        lines.append(line);
        lines.append(System.lineSeparator()); // add a newline after each line read — avoid multi-line reviews from printing without blank lines between them when shown to the user
      }
    } catch(FileNotFoundException e){
        throw new InvalidArgumentsException(path + " not found");
    } catch(IOException e){
        throw new InvalidArgumentsException("IO Exception");
    }
    return lines.toString();
  }

  /**
   * Prints all review files in a numbered list format
   */
  public void printReviewList() {
    if (this.reviewFiles.isEmpty()) {
      System.out.println("No review files found in the folder.");
      return;
    }
    
    System.out.println("Available reviews:");
    for (Map.Entry<Integer, String> entry : this.reviewFiles.entrySet()) { // entrySet() returns a set of key-value pairs
      System.out.println(" " + entry.getKey() + ". " + entry.getValue());
    }
  }

  /**
   * Gets the list of review file names
   *
   * @return Map of index to file name
   */
  public Map<Integer, String> getReviewFiles() {
    return this.reviewFiles;
  }

  /**
   * Gets the total number of review files found
   *
   * @return Number of .txt files in the folder
   */
  public int getFileCount() {
    return this.reviewFiles.size();
  }

  @Override
  public String toString() {
    return "ReviewFileManager{" +
           "inputDir='" + inputDir + '\'' +
           ", reviewFiles=" + reviewFiles +
           '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ReviewFileManager that = (ReviewFileManager) o;
    return Objects.equals(inputDir, that.inputDir) &&
           Objects.equals(reviewFiles, that.reviewFiles);
  }

  @Override
  public int hashCode() {
    return Objects.hash(inputDir, reviewFiles);
  }
}
