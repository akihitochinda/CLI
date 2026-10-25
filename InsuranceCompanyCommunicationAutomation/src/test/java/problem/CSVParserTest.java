package problem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CSVParserTest {

    @TempDir
    Path tempDir;

    private Path csvFile;

    @BeforeEach
    void setUp() throws IOException {
        csvFile = tempDir.resolve("test.csv");
    }

    // Helper: write lines to the temp CSV file
    private void writeCSV(String content) throws IOException {
        Files.writeString(csvFile, content);
    }

    // ---- Normal cases ----

    @Test
    void testBasicParsing() throws IOException {
        writeCSV("\"first_name\",\"last_name\",\"email\"\n"
                + "\"Art\",\"Venere\",\"art@venere.org\"\n");
        CSVParser parser = new CSVParser(csvFile.toString());
        List<Map<String, String>> data = parser.getData();

        assertEquals(1, data.size());
        assertEquals("Art",          data.get(0).get("first_name"));
        assertEquals("Venere",       data.get(0).get("last_name"));
        assertEquals("art@venere.org", data.get(0).get("email"));
        assertTrue(parser.getLog().isEmpty());
    }

    @Test
    void testFieldWithCommaInside() throws IOException {
        // "Chemel, James L Cpa" is one field, not two
        writeCSV("\"first_name\",\"company_name\"\n"
                + "\"Art\",\"Chemel, James L Cpa\"\n");
        CSVParser parser = new CSVParser(csvFile.toString());
        List<Map<String, String>> data = parser.getData();

        assertEquals(1, data.size());
        assertEquals("Chemel, James L Cpa", data.get(0).get("company_name"));
    }

    @Test
    void testMultipleRows() throws IOException {
        writeCSV("\"first_name\",\"last_name\"\n"
                + "\"Alice\",\"Smith\"\n"
                + "\"Bob\",\"Jones\"\n");
        CSVParser parser = new CSVParser(csvFile.toString());
        assertEquals(2, parser.getData().size());
        assertEquals("Alice", parser.getData().get(0).get("first_name"));
        assertEquals("Bob",   parser.getData().get(1).get("first_name"));
    }

    @Test
    void testEmptyLinesAreSkipped() throws IOException {
        writeCSV("\"first_name\",\"last_name\"\n"
                + "\"Alice\",\"Smith\"\n"
                + "\n"              // blank line
                + "\"Bob\",\"Jones\"\n");
        CSVParser parser = new CSVParser(csvFile.toString());
        // blank line skipped — only 2 data rows
        assertEquals(2, parser.getData().size());
    }

    // ---- Error / edge cases ----

    @Test
    void testNullFilePath() {
        CSVParser parser = new CSVParser(null);
        assertTrue(parser.getData().isEmpty());
        assertFalse(parser.getLog().isEmpty());
    }

    @Test
    void testBlankFilePath() {
        CSVParser parser = new CSVParser("   ");
        assertTrue(parser.getData().isEmpty());
        assertFalse(parser.getLog().isEmpty());
    }

    @Test
    void testFileNotFound() {
        CSVParser parser = new CSVParser("/nonexistent/path/file.csv");
        assertTrue(parser.getData().isEmpty());
        assertFalse(parser.getLog().isEmpty());
    }

    @Test
    void testHeaderOnlyFile() throws IOException {
        writeCSV("\"first_name\",\"last_name\"\n");
        CSVParser parser = new CSVParser(csvFile.toString());
        // No data rows — list should be empty, no errors
        assertTrue(parser.getData().isEmpty());
        assertTrue(parser.getLog().isEmpty());
    }

    @Test
    void testRowWithFewerFieldsThanHeadersLogsWarning() throws IOException {
        writeCSV("\"first_name\",\"last_name\",\"email\"\n"
                + "\"Art\",\"Venere\"\n"); // missing email
        CSVParser parser = new CSVParser(csvFile.toString());
        // Row still added (partial), but a warning is logged
        assertEquals(1, parser.getData().size());
        assertFalse(parser.getLog().isEmpty());
    }

    @Test
    void testGetDataReturnsCorrectHeaders() throws IOException {
        writeCSV("\"a\",\"b\",\"c\"\n"
                + "\"1\",\"2\",\"3\"\n");
        CSVParser parser = new CSVParser(csvFile.toString());
        Map<String, String> row = parser.getData().get(0);
        assertTrue(row.containsKey("a"));
        assertTrue(row.containsKey("b"));
        assertTrue(row.containsKey("c"));
    }

    @Test
    void testEqualParsersAreEqual() throws IOException {
        writeCSV("\"first_name\",\"last_name\"\n"
                + "\"Alice\",\"Smith\"\n");
        CSVParser parser1 = new CSVParser(csvFile.toString());
        CSVParser parser2 = new CSVParser(csvFile.toString());
        assertEquals(parser1, parser2);
        assertEquals(parser1.hashCode(), parser2.hashCode());
    }

    @Test
    void testNotEqualWithDifferentData() throws IOException {
        writeCSV("\"first_name\",\"last_name\"\n"
                + "\"Alice\",\"Smith\"\n");
        CSVParser parser1 = new CSVParser(csvFile.toString());

        Path csvFile2 = tempDir.resolve("test2.csv");
        Files.writeString(csvFile2, "\"first_name\",\"last_name\"\n"
                + "\"Bob\",\"Jones\"\n");
        CSVParser parser2 = new CSVParser(csvFile2.toString());

        assertNotEquals(parser1, parser2);
    }

    @Test
    void testToStringContainsData() throws IOException {
        writeCSV("\"first_name\",\"last_name\"\n"
                + "\"Alice\",\"Smith\"\n");
        CSVParser parser = new CSVParser(csvFile.toString());
        String str = parser.toString();
        assertTrue(str.contains("Alice") || str.contains("data"));
    }

    @Test
    void testEqualsWithSelf() throws IOException {
        writeCSV("\"first_name\"\n\"Alice\"\n");
        CSVParser parser = new CSVParser(csvFile.toString());
        assertEquals(parser, parser);
    }

    @Test
    void testNotEqualsNull() throws IOException {
        writeCSV("\"first_name\"\n\"Alice\"\n");
        CSVParser parser = new CSVParser(csvFile.toString());
        assertNotEquals(null, parser);
    }
}