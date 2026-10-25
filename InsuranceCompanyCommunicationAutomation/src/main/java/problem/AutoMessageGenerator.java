package problem;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The type Auto message generator.
 */
public class AutoMessageGenerator {
  private TemplateHandler templateHandler;
  private String type;

  /**
   * Instantiates a new Auto message generator.
   *
   * @param templateHandler the template handler
   * @param type            the type email or letter
   */
  public AutoMessageGenerator(TemplateHandler templateHandler, String type) {
    this.templateHandler = templateHandler;
    this.type = type;
  }

  /**
   * Output for each member one by one and generate email for each single member
   *
   * @param outputDir the output directory from Main
   * @param members   the data read from CSVParser
   * @throws IOException throws exception
   */
  public void generator(String outputDir, List<Map<String, String>> members) throws IOException {
    for (int i = 0; i < members.size(); i++) {
      Map<String, String> member = members.get(i); // get each member's info
      String content = templateHandler.replacePlaceholders(member); // replace content as string
      OutputFileWriter output = new OutputFileWriter(outputDir, "User" + (i+1) + "_" + type + ".txt");
      output.fileWrite(content); // fileWrite needs IOException throws
    }
  }

  @Override
  public String toString() {
    return "AutoMessageGenerator{" +
        "templateHandler=" + templateHandler +
        ", type='" + type + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AutoMessageGenerator that = (AutoMessageGenerator) o;
    return Objects.equals(templateHandler, that.templateHandler)
        && Objects.equals(type, that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(templateHandler, type);
  }
}
