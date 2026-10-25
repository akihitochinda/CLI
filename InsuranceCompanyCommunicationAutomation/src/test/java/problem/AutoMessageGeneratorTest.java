package problem;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AutoMessageGeneratorTest {

  private AutoMessageGenerator generator1;
  private AutoMessageGenerator generator2;
  private AutoMessageGenerator generator3;

  @BeforeEach
  void setUp() throws Exception {
    TemplateHandler template = new TemplateHandler("src/main/java/problem/email-template.txt");
    TemplateHandler template2 = new TemplateHandler("src/main/java/problem/letter-template.txt");
    generator1 = new AutoMessageGenerator(template, "Email");
    generator2 = new AutoMessageGenerator(template, "Email");
    generator3 = new AutoMessageGenerator(template2, "Letter");
  }

  @Test
  void testGenerator(@TempDir Path tempDir) throws Exception {
    // create a template file in temp dir, then use it in generator
    Path templateFile = tempDir.resolve("test-template.txt");
    String templateText = "Hello [[first_name]] [[last_name]]\n" +
        "Email: [[email]]\n";
    Files.writeString(templateFile, templateText);

    TemplateHandler th = new TemplateHandler(templateFile.toString());
    AutoMessageGenerator gen = new AutoMessageGenerator(th, "Email");

    List<Map<String, String>> members = new ArrayList<>();
    Map<String, String> alice = new HashMap<>();
    alice.put("first_name", "Alice");
    alice.put("last_name", "Smith");
    alice.put("email", "alice@example.com");
    members.add(alice);

    Map<String, String> bob = new HashMap<>();
    bob.put("first_name", "Bob");
    bob.put("last_name", "Lee");
    bob.put("email", "bob@example.com");
    members.add(bob);

    gen.generator(tempDir.toString(), members);

    Path out1 = tempDir.resolve("User1_Email.txt");
    Path out2 = tempDir.resolve("User2_Email.txt");

    assertTrue(Files.exists(out1));
    assertTrue(Files.exists(out2));

    String content1 = Files.readString(out1);
    String content2 = Files.readString(out2);

    assertTrue(content1.contains("Hello Alice Smith"));
    assertTrue(content1.contains("alice@example.com"));
    assertFalse(content1.contains("[[first_name]]"));

    assertTrue(content2.contains("Hello Bob Lee"));
    assertTrue(content2.contains("bob@example.com"));
    assertFalse(content2.contains("[[first_name]]"));
  }

  @Test
  void testToString() {
    String result = generator1.toString();
    assertTrue(result.contains("AutoMessageGenerator"));
    assertTrue(result.contains("type='Email'"));
  }

  @Test
  void testEquals() {
    assertEquals(generator1, generator2); // same template and type
    assertNotEquals(generator1, generator3); // different type
    assertNotEquals(generator1, null);
    assertNotEquals(generator1, "not a generator");
  }

  @Test
  void testHashCode() {
    assertEquals(generator1.hashCode(), generator2.hashCode()); // equal objects have same hash
    assertNotEquals(generator1.hashCode(), generator3.hashCode()); // different objects may have different hash
  }
}