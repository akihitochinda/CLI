package problem;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MainTest {

  @Test
  void testMainEmail(@TempDir Path tempDir) throws Exception {
    Path csv = tempDir.resolve("members.csv");
    Files.writeString(csv,
        "\"first_name\",\"last_name\",\"email\"\n" +
            "\"Alice\",\"Smith\",\"alice@example.com\"\n");

    Path template = tempDir.resolve("email-template.txt");
    Files.writeString(template, "Hello [[first_name]] [[last_name]]\nEmail:[[email]]\n");

    String[] args = {
        "--email",
        "--email-template", template.toString(),
        "--output-dir", tempDir.toString(),
        "--csv-file", csv.toString()
    };

    assertDoesNotThrow(() -> Main.main(args));

    Path out1 = tempDir.resolve("User1_Email.txt");
    assertTrue(Files.exists(out1));
    assertTrue(Files.readString(out1).contains("Hello Alice Smith"));
  }

  @Test
  void testMainLetter(@TempDir Path tempDir) throws Exception {
    Path csv = tempDir.resolve("members.csv");
    Files.writeString(csv,
        "\"first_name\",\"last_name\",\"email\"\n" +
            "\"Bob\",\"Lee\",\"bob@example.com\"\n");

    Path template = tempDir.resolve("letter-template.txt");
    Files.writeString(template, "Dear [[first_name]] [[last_name]],\nRegards\n");

    String[] args = {
        "--letter",
        "--letter-template", template.toString(),
        "--output-dir", tempDir.toString(),
        "--csv-file", csv.toString()
    };

    assertDoesNotThrow(() -> Main.main(args));

    Path out1 = tempDir.resolve("User1_Letter.txt");
    assertTrue(Files.exists(out1));
    assertTrue(Files.readString(out1).contains("Dear Bob Lee"));
  }

  @Test
  void testMainMissingTemplate(@TempDir Path tempDir) throws Exception {
    Path csv = tempDir.resolve("members.csv");
    Files.writeString(csv,
        "\"first_name\",\"last_name\",\"email\"\n" +
            "\"Alice\",\"Smith\",\"alice@example.com\"\n");

    String[] args = {
        "--email",
        "--email-template", "template.txt",
        "--output-dir", tempDir.toString(),
        "--csv-file", csv.toString()
    };

    // Main.main() catches InvalidArgumentsException and prints error
    assertDoesNotThrow(() -> Main.main(args));
  }

  @Test
  void testMainInvalidArgs(@TempDir Path tempDir) throws Exception {
    String[] args = {
        "--invalid-arg"
    };

    // Main.main() catches InvalidArgumentsException and prints error
    assertDoesNotThrow(() -> Main.main(args));
  }
}