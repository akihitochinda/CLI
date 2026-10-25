package problem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorLoggerTest {

    private ErrorLogger logger;

    @BeforeEach
    void setUp() {
        logger = new ErrorLogger();
    }

    @Test
    void testNewLoggerIsEmpty() {
        assertTrue(logger.isEmpty());
    }

    @Test
    void testLogAddsMessage() {
        logger.log("Something went wrong");
        assertFalse(logger.isEmpty());
    }

    @Test
    void testGetMessagesReturnsAllInOrder() {
        logger.log("Error A");
        logger.log("Error B");
        assertEquals(2, logger.getMessages().size());
        assertEquals("Error A", logger.getMessages().get(0));
        assertEquals("Error B", logger.getMessages().get(1));
    }

    @Test
    void testGetMessagesIsUnmodifiable() {
        logger.log("msg");
        assertThrows(UnsupportedOperationException.class,
                () -> logger.getMessages().add("hack"));
    }

    @Test
    void testLogNullThrows() {
        assertThrows(IllegalArgumentException.class, () -> logger.log(null));
    }

    @Test
    void testPrintAll() {
        logger.log("Error A");
        logger.log("Error B");
        assertDoesNotThrow(() -> logger.printAll());
    }

    @Test
    void testPrintAll_EmptyLog() {
        assertDoesNotThrow(() -> logger.printAll());
    }

    @Test
    void testEqualsAndHashCode() {
        ErrorLogger a = new ErrorLogger();
        ErrorLogger b = new ErrorLogger();
        a.log("same");
        b.log("same");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
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
    void testToStringContainsMessages() {
        logger.log("test error");
        assertTrue(logger.toString().contains("test error"));
    }
}