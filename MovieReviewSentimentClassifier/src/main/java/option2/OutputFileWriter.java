package option2;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

/**
 * Class OutputFileWriter that writes the text to a new file
 */
public class OutputFileWriter {
  private String outputDir;
  private String fileName;

  /**
   * Constructor for OutputFileWriter
   * @param outputDir the output direction, as a String
   * @param fileName the file name, as a String
   */
  public OutputFileWriter(String outputDir, String fileName) {
    this.outputDir = outputDir;
    this.fileName = fileName;
  }

  /**
   * Method to create a new file and write to it
   * @param content the content to write to the new file
   * @throws IOException throw exception when
   */
  public void fileWrite(String content) throws IOException {
    // initiate outputFile
    FileWriter outputFile = null;
    try {
      outputFile = new FileWriter(this.outputDir + this.fileName);
      outputFile.write(content);
      outputFile.close();
    } catch (FileNotFoundException e) {
      System.out.println("Whoops! A file was not found :" + e.getMessage());
      e.printStackTrace();
    } catch (IOException ioe) {
      System.out.println("Something went wrong! : " + ioe.getMessage());
      ioe.printStackTrace();
    }
  }

  /**
   * Getter for output direction
   * @return output file direction, as a String
   */
  public String getOutputDir() {
    return this.outputDir;
  }

  /**
   * Getter for FileName
   * @return file name, as a String
   */
  public String getFileName() {
    return this.fileName;
  }

  @Override
  public String toString() {
    return "OutputFileWriter{" +
        "outputDir='" + outputDir + '\'' +
        ", fileName='" + fileName + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OutputFileWriter that = (OutputFileWriter) o;
    return Objects.equals(outputDir, that.outputDir) && Objects.equals(fileName,
        that.fileName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(outputDir, fileName);
  }
}
