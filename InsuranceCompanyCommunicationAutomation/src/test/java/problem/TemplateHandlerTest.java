package problem;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TemplateHandlerTest {
  private TemplateHandler testTemplateHandler;

  @BeforeEach
  void setUp() throws InvalidArgumentsException {
    testTemplateHandler = new TemplateHandler("src/main/java/problem/email-template.txt");
  }

  @Test
  void readTemplate_throwIllegalException() {
    assertThrows(InvalidArgumentsException.class,() -> new TemplateHandler("src/test/java/problem/email-template2.txt"));
  }

  @Test
  void replacePlaceholders() {
    Map<String,String> replacements = new HashMap<>();
    // add key value to the map
    replacements.put("first_name", "Jane");
    replacements.put("last_name", "Doe");
    String result = testTemplateHandler.replacePlaceholders(replacements);
    assertTrue(result.contains("Doe"));
    assertTrue(result.contains("Jane"));
    assertFalse(result.contains("[[Jane]]"));
  }

  @Test
  void testToString() {
    String expectedString = "TemplateHandler{" +
        "template='" + testTemplateHandler.getTemplate() + '\'' +
        '}';
    assertEquals(expectedString, testTemplateHandler.toString());
  }

  @Test
  void testEquals_SameObject() {
    assertEquals(testTemplateHandler, testTemplateHandler);
  }

  @Test
  void testEquals_ObjectNull() {
    assertFalse(testTemplateHandler.equals(null));
  }

  @Test
  void testEquals_DifferentDataType() {
    assertFalse(testTemplateHandler.equals("String"));
  }

  @Test
  void testHashCode() {
    int expectedHashCode = Objects.hashCode(testTemplateHandler.getTemplate());
    assertEquals(expectedHashCode, testTemplateHandler.hashCode());
  }
}