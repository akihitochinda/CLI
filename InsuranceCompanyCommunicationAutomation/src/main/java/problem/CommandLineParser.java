package problem;

import java.util.Objects;

/**
 *  Processes and validates command line arguments. This class doesn't *do* anything with the values provided by the user
 *  beyond initial validation. It is another class' responsibility to determine what to do with the user input.
 */
public class CommandLineParser {
  public static final String EMAIL = "--email";
  public static final String EMAIL_TEMPLATE = "--email-template";
  public static final String LETTER = "--letter";
  public static final String LETTER_TEMPLATE = "--letter-template";
  public static final String OUTPUT_DIR = "--output-dir";
  public static final String CSV_FILE = "--csv-file";

  private Boolean generateEmail = false;
  private Boolean generateLetter = false;
  private String emailTemplate = null;
  private String letterTemplate = null;
  private String outputDir = null;
  private String csvFile = null;

  /**
   * Constructor for the CommandLineParser Class
   * @param args The arguments provided by the user
   * @throws InvalidArgumentsException if the arguments supplied are invalid
   */
  public CommandLineParser(String[] args) throws InvalidArgumentsException{
    this.processArgs(args);
    this.validateOptionArguments();
  }

  /**
   * Helper method to populate the instance variables.
   * @param args The args supplied by the user
   * @throws InvalidArgumentsException If an invalid argument is provided at the command line
   */
  private void processArgs(String[] args) throws InvalidArgumentsException {
    for (int i = 0; i < args.length; i++) {
      try {
        switch (args[i]) {
          case EMAIL:
            this.generateEmail = true;
            break;
          case EMAIL_TEMPLATE:
            this.emailTemplate = args[++i]; // grab next argument as the value
            break;
          case LETTER:
            this.generateLetter = true;
            break;
          case LETTER_TEMPLATE:
            this.letterTemplate = args[++i]; // grab next argument as the value
            break;
          case OUTPUT_DIR:
            this.outputDir = args[++i]; // grab next argument as the value
            break;
          case CSV_FILE:
            this.csvFile = args[++i]; // grab next argument as the value
            break;
          default:
            throw new InvalidArgumentsException("Unknown argument " + args[i]);
        }
      } catch(ArrayIndexOutOfBoundsException e){
        throw new InvalidArgumentsException(args[i] + "requires one more argument");
      }
    }
  }

  /**
   * To validate Option arguments combinations
   * @throws InvalidArgumentsException throw exception when invalid option arguments combination
   */
  private void validateOptionArguments() throws InvalidArgumentsException {
    // One of --email or --letter should be provided as argument
    if (!this.generateEmail && !this.generateLetter) {
      throw new InvalidArgumentsException("At least one of email or letter should be provided");
    }
    // email provided but email-template is not provided
    if (this.generateEmail && this.emailTemplate == null) {
      throw new InvalidArgumentsException("Error: --email provided but no --email-template was given.");
    }
    // letter provided but letter-template not provided
    if (this.generateLetter && this.letterTemplate == null) {
      throw new InvalidArgumentsException("Error: --letter provided but no --letter-template was given.");
    }
    // outputDir is not provided
    if (this.outputDir == null) {
      throw new InvalidArgumentsException("Error: --output-dir is required as argument");
    }
    // csvFile is not provided
    if (this.csvFile == null) {
      throw new InvalidArgumentsException("Error: --csv file is required as argument");
    }
  }

  /**
   * Getter for GenerateEmail
   * @return generateEmail
   */
  public Boolean getGenerateEmail() {
    return this.generateEmail;
  }

  /**
   * Getter for GenerateLetter
   * @return generateLetter
   */
  public Boolean getGenerateLetter() {
    return this.generateLetter;
  }

  /**
   * Getter for Email Template
   * @return emailTemplates
   */
  public String getEmailTemplate() {
    return this.emailTemplate;
  }

  /**
   * Getter for Letter Template
   * @return letterTemplate
   */
  public String getLetterTemplate() {
    return this.letterTemplate;
  }

  /**
   * Getter for OutputDir
   * @return outputDir
   */
  public String getOutputDir() {
    return this.outputDir;
  }

  /**
   * Getter for CSV file
   * @return csvFile
   */
  public String getCsvFile() {
    return this.csvFile;
  }

  @Override
  public String toString() {
    return "CommandLineParser{" +
        "generateEmail=" + generateEmail +
        ", generateLetter=" + generateLetter +
        ", emailTemplate='" + emailTemplate + '\'' +
        ", letterTemplate='" + letterTemplate + '\'' +
        ", outputDir='" + outputDir + '\'' +
        ", csvFile='" + csvFile + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CommandLineParser that = (CommandLineParser) o;
    return Objects.equals(generateEmail, that.generateEmail) && Objects.equals(
        generateLetter, that.generateLetter) && Objects.equals(emailTemplate,
        that.emailTemplate) && Objects.equals(letterTemplate, that.letterTemplate)
        && Objects.equals(outputDir, that.outputDir) && Objects.equals(csvFile,
        that.csvFile);
  }

  @Override
  public int hashCode() {
    return Objects.hash(generateEmail, generateLetter, emailTemplate, letterTemplate, outputDir,
        csvFile);
  }
}
