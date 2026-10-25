package problem;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * The type Main.
 */
public class Main {

  /**
   * The entry point of application.
   *
   * @param args the input arguments, as String[]
   */
  public static void main(String[] args) {
    try {
        CommandLineParser parser = new CommandLineParser(args); // order-insensitive, can be passed in any order
        CSVParser processor = new CSVParser(parser.getCsvFile()); // CSVParser(String filePath)
        List<Map<String, String>> members = processor.getData(); // the data should be read once at main
        if (processor.getLog().isEmpty()) { // event log handler
          if (parser.getGenerateEmail()) {
            // TemplateHandler(String path)
            TemplateHandler emailTemplate = new TemplateHandler(parser.getEmailTemplate()); // --email-template <path/to/file>
            // AutoMessageGenerator(TemplateHandler templateHandler, String type)
            AutoMessageGenerator emailOutput = new AutoMessageGenerator(emailTemplate, "Email");
            // generator(String outputDir, List<Map<String, String>> members)
            emailOutput.generator(parser.getOutputDir(), members); // the output directory from Main, and the data read from CSVParser
          }
          if (parser.getGenerateLetter()) { // A user can request both emails and letters
            TemplateHandler letterTemplate = new TemplateHandler(parser.getLetterTemplate()); // --letter-template <path/to/file>
            AutoMessageGenerator letterOutput = new AutoMessageGenerator(letterTemplate, "Letter");
            letterOutput.generator(parser.getOutputDir(), members); // the output directory from Main, and the data read from CSVParser
          }
        } else {
          System.out.println(processor.getLog().toString()); // generate event log
        }
    } catch (InvalidArgumentsException e) {
        System.out.println(e.getMessage());
    } catch (IOException e) { // for generator IOException
        System.out.println("ERROR: " + e.getMessage());
    }
  }
}
