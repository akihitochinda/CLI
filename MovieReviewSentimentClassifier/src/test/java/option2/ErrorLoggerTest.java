package option2;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ErrorLoggerTest {

  private ErrorLogger logger;

  @BeforeEach
  void setUp() {
    logger = new ErrorLogger();
  }

  // ---- isEmpty ----

  @Test
  void testNewLoggerIsEmpty() {
    assertTrue(logger.isEmpty());
  }

  @Test
  void testLogMakesLoggerNonEmpty() {
    logger.log("Something went wrong");
    assertFalse(logger.isEmpty());
  }

  @Test
  void testMultipleLogsAreAllStored() {
    logger.log("Error A");
    logger.log("Error B");
    logger.log("Error C");
    assertFalse(logger.isEmpty());
  }

  // ---- log / toString ----

  @Test
  void testToStringContainsSingleLoggedMessage() {
    logger.log("test error");
    assertTrue(logger.toString().contains("test error"));
  }

  @Test
  void testToStringContainsAllLoggedMessages() {
    logger.log("first");
    logger.log("second");
    String out = logger.toString();
    assertTrue(out.contains("first"));
    assertTrue(out.contains("second"));
  }

  @Test
  void testToStringWhenEmptyReturnsEmptyLog() {
    // Documented behaviour: empty logger toString returns "Empty log"
    assertEquals("Empty log", logger.toString());
  }

  // ---- equals / hashCode ----

  @Test
  void testEqualLoggersWithSameMessages() {
    ErrorLogger a = new ErrorLogger();
    ErrorLogger b = new ErrorLogger();
    a.log("same");
    b.log("same");
    assertEquals(a, b);
  }

  @Test
  void testNotEqualWithDifferentMessages() {
    ErrorLogger a = new ErrorLogger();
    ErrorLogger b = new ErrorLogger();
    a.log("foo");
    b.log("bar");
    assertNotEquals(a, b);
  }

  @Test
  void testEqualEmptyLoggers() {
    assertEquals(new ErrorLogger(), new ErrorLogger());
  }

  @Test
  void testEqualsSameObject() {
    assertEquals(logger, logger);
  }

  @Test
  void testNotEqualsNull() {
    assertNotEquals(null, logger);
  }

  @Test
  void testNotEqualsDifferentType() {
    assertNotEquals("not a logger", logger);
  }

  @Test
  void testHashCodeSameForEqualLoggers() {
    ErrorLogger a = new ErrorLogger();
    ErrorLogger b = new ErrorLogger();
    a.log("msg");
    b.log("msg");
    assertEquals(a.hashCode(), b.hashCode());
  }

  @Test
  void testHashCodeEmptyLoggers() {
    assertEquals(new ErrorLogger().hashCode(), new ErrorLogger().hashCode());
  }

  // ---- ordering ----

  @Test
  void testToStringPreservesInsertionOrder() {
    logger.log("first");
    logger.log("second");
    String out = logger.toString();
    assertTrue(out.indexOf("first") < out.indexOf("second"));
  }
}
