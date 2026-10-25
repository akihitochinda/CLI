package option2;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ReviewFileManagerTest {

  @TempDir
  Path tempDir;

  // Helper: create a .txt review file in the temp directory
  private Path createReview(String name, String content) throws IOException {
    Path file = tempDir.resolve(name);
    Files.writeString(file, content);
    return file;
  }

  // ---- scanReviewFiles / getFileCount ----

  @Test
  void testEmptyDirectoryHasNoReviews() {
    ReviewFileManager manager = new ReviewFileManager(tempDir.toString() + java.io.File.separator);
    assertEquals(0, manager.getFileCount());
  }

  @Test
  void testSingleTxtFileIsDetected() throws IOException {
    createReview("review1.txt", "This is a great movie!");
    ReviewFileManager manager = new ReviewFileManager(tempDir.toString() + java.io.File.separator);
    assertEquals(1, manager.getFileCount());
  }

  @Test
  void testMultipleTxtFilesAreAllDetected() throws IOException {
    createReview("review1.txt", "Great film.");
    createReview("review2.txt", "Terrible film.");
    createReview("review3.txt", "Okay film.");
    ReviewFileManager manager = new ReviewFileManager(tempDir.toString() + java.io.File.separator);
    assertEquals(3, manager.getFileCount());
  }

  @Test
  void testNonTxtFilesAreIgnored() throws IOException {
    createReview("review1.txt", "Great film.");
    Files.writeString(tempDir.resolve("lexicon.csv"), "word,score\n");
    Files.writeString(tempDir.resolve("notes.md"), "some notes");
    ReviewFileManager manager = new ReviewFileManager(tempDir.toString() + java.io.File.separator);
    // Only the .txt file should be counted
    assertEquals(1, manager.getFileCount());
  }

  // ---- getReviewFiles ----

  @Test
  void testGetReviewFilesReturnsCorrectNames() throws IOException {
    createReview("review_alpha.txt", "Alpha review.");
    ReviewFileManager manager = new ReviewFileManager(tempDir.toString() + java.io.File.separator);
    Map<Integer, String> files = manager.getReviewFiles();
    assertTrue(files.containsValue("review_alpha.txt"));
  }

  @Test
  void testGetReviewFilesKeysStartAtOne() throws IOException {
    createReview("review1.txt", "content");
    ReviewFileManager manager = new ReviewFileManager(tempDir.toString() + java.io.File.separator);
    assertTrue(manager.getReviewFiles().containsKey(1));
  }

  @Test
  void testGetReviewFilesHasNoKeyZero() throws IOException {
    createReview("review1.txt", "content");
    ReviewFileManager manager = new ReviewFileManager(tempDir.toString() + java.io.File.separator);
    assertFalse(manager.getReviewFiles().containsKey(0));
  }

  // ---- readReviewFile ----

  @Test
  void testReadReviewFileReturnsCorrectContent() throws Exception {
    createReview("review1.txt", "This movie was absolutely wonderful.");
    ReviewFileManager manager = new ReviewFileManager(tempDir.toString() + java.io.File.separator);
    String content = manager.readReviewFile(1);
    assertTrue(content.contains("This movie was absolutely wonderful."));
  }

  @Test
  void testReadReviewFileMultiLine() throws Exception {
    createReview("review1.txt", "Line one.\nLine two.\nLine three.");
    ReviewFileManager manager = new ReviewFileManager(tempDir.toString() + java.io.File.separator);
    String content = manager.readReviewFile(1);
    assertTrue(content.contains("Line one."));
    assertTrue(content.contains("Line two."));
    assertTrue(content.contains("Line three."));
  }

  @Test
  void testReadReviewFileAppendsNewlineAfterEachLine() throws Exception {
    createReview("review1.txt", "first line\nsecond line");
    ReviewFileManager manager = new ReviewFileManager(tempDir.toString() + java.io.File.separator);
    String content = manager.readReviewFile(1);
    // The content should contain at least one system line separator
    assertTrue(content.contains(System.lineSeparator()));
  }

  // ---- printReviewList ----

  @Test
  void testPrintReviewListDoesNotThrowWithFiles() throws IOException {
    createReview("review1.txt", "content");
    ReviewFileManager manager = new ReviewFileManager(tempDir.toString() + java.io.File.separator);
    assertDoesNotThrow(manager::printReviewList);
  }

  @Test
  void testPrintReviewListDoesNotThrowWithEmptyDirectory() {
    ReviewFileManager manager = new ReviewFileManager(tempDir.toString() + java.io.File.separator);
    assertDoesNotThrow(manager::printReviewList);
  }

  // ---- equals / hashCode / toString ----

  @Test
  void testEqualManagersWithSameDirectory() throws IOException {
    createReview("review1.txt", "content");
    String dir = tempDir.toString() + java.io.File.separator;
    ReviewFileManager m1 = new ReviewFileManager(dir);
    ReviewFileManager m2 = new ReviewFileManager(dir);
    assertEquals(m1, m2);
    assertEquals(m1.hashCode(), m2.hashCode());
  }

  @Test
  void testEqualsSameObject() throws IOException {
    createReview("review1.txt", "content");
    ReviewFileManager m = new ReviewFileManager(tempDir.toString() + java.io.File.separator);
    assertEquals(m, m);
  }

  @Test
  void testNotEqualsNull() throws IOException {
    createReview("review1.txt", "content");
    ReviewFileManager m = new ReviewFileManager(tempDir.toString() + java.io.File.separator);
    assertNotEquals(null, m);
  }

  @Test
  void testNotEqualsDifferentType() throws IOException {
    createReview("review1.txt", "content");
    ReviewFileManager m = new ReviewFileManager(tempDir.toString() + java.io.File.separator);
    assertNotEquals("a string", m);
  }

  @Test
  void testToStringContainsInputDir() throws IOException {
    createReview("review1.txt", "content");
    ReviewFileManager m = new ReviewFileManager(tempDir.toString() + java.io.File.separator);
    assertTrue(m.toString().contains("inputDir") || m.toString().contains(tempDir.toString()));
  }

  @Test
  void testToStringContainsReviewFiles() throws IOException {
    createReview("review1.txt", "content");
    ReviewFileManager m = new ReviewFileManager(tempDir.toString() + java.io.File.separator);
    assertTrue(m.toString().contains("reviewFiles") || m.toString().contains("review1.txt"));
  }
}
