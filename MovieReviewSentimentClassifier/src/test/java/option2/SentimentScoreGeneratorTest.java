package option2;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SentimentScoreGeneratorTest {

  private Map<String, Double> lexicon;

  @BeforeEach
  void setUp() {
    lexicon = new HashMap<>();
    lexicon.put("good",      0.8);
    lexicon.put("great",     0.9);
    lexicon.put("excellent", 1.0);
    lexicon.put("bad",      -0.7);
    lexicon.put("terrible", -0.9);
    lexicon.put("awful",    -1.0);
    lexicon.put("okay",      0.0);
  }

  // ---- classifySentiment ----

  @Test
  void testPositiveClassificationAboveThreshold() {
    SentimentScoreGeneratorPart1 gen = new SentimentScoreGeneratorPart1(lexicon, "good great excellent");
    assertEquals("POSITIVE", gen.getClassification());
  }

  @Test
  void testNegativeClassificationBelowThreshold() {
    SentimentScoreGeneratorPart1 gen = new SentimentScoreGeneratorPart1(lexicon, "bad terrible awful");
    assertEquals("NEGATIVE", gen.getClassification());
  }

  @Test
  void testNeutralClassificationAtZero() {
    // "okay" scores 0.0 → NEUTRAL
    SentimentScoreGeneratorPart1 gen = new SentimentScoreGeneratorPart1(lexicon, "okay");
    assertEquals("NEUTRAL", gen.getClassification());
  }

  @Test
  void testNeutralJustInsidePositiveThreshold() {
    // Score exactly at 0.05 is NOT > 0.05 → NEUTRAL
    // Construct a review where score rounds to exactly 0.05
    Map<String, Double> tiny = new HashMap<>();
    tiny.put("word", 0.05);
    SentimentScoreGeneratorPart1 gen = new SentimentScoreGeneratorPart1(tiny, "word");
    assertEquals("NEUTRAL", gen.getClassification());
  }

  @Test
  void testNeutralJustInsideNegativeThreshold() {
    Map<String, Double> tiny = new HashMap<>();
    tiny.put("word", -0.05);
    SentimentScoreGeneratorPart1 gen = new SentimentScoreGeneratorPart1(tiny, "word");
    assertEquals("NEUTRAL", gen.getClassification());
  }

  @Test
  void testPositiveJustAboveThreshold() {
    Map<String, Double> tiny = new HashMap<>();
    tiny.put("word", 0.06);
    SentimentScoreGeneratorPart1 gen = new SentimentScoreGeneratorPart1(tiny, "word");
    assertEquals("POSITIVE", gen.getClassification());
  }

  @Test
  void testNegativeJustBelowThreshold() {
    Map<String, Double> tiny = new HashMap<>();
    tiny.put("word", -0.06);
    SentimentScoreGeneratorPart1 gen = new SentimentScoreGeneratorPart1(tiny, "word");
    assertEquals("NEGATIVE", gen.getClassification());
  }

  // ---- calculateSentimentScore ----

  @Test
  void testScoreIsNormalizedByTokenCount() {
    // "good" = 0.8; 4 tokens total (3 unknown + good) → 0.8 / 4 = 0.2
    SentimentScoreGeneratorPart1 gen = new SentimentScoreGeneratorPart1(lexicon, "good unknown1 unknown2 unknown3");
    assertEquals(0.2, gen.getScore(), 1e-9);
  }

  @Test
  void testAllUnknownWordsScoreZero() {
    SentimentScoreGeneratorPart1 gen = new SentimentScoreGeneratorPart1(lexicon, "foo bar baz");
    assertEquals(0.0, gen.getScore(), 1e-9);
  }

  @Test
  void testEmptyReviewScoreZero() {
    SentimentScoreGeneratorPart1 gen = new SentimentScoreGeneratorPart1(lexicon, "");
    assertEquals(0.0, gen.getScore(), 1e-9);
  }

  @Test
  void testPunctuationIsStrippedBeforeScoring() {
    // "good!" should match "good" after stripping punctuation
    SentimentScoreGeneratorPart1 gen = new SentimentScoreGeneratorPart1(lexicon, "good!");
    assertEquals(lexicon.get("good"), gen.getScore(), 1e-9);
  }

  @Test
  void testCaseIsNormalisedBeforeScoring() {
    // "GOOD" should match "good" after lowercase conversion
    SentimentScoreGeneratorPart1 gen = new SentimentScoreGeneratorPart1(lexicon, "GOOD");
    assertEquals(lexicon.get("good"), gen.getScore(), 1e-9);
  }

  @Test
  void testMixedCaseAndPunctuation() {
    SentimentScoreGeneratorPart1 gen = new SentimentScoreGeneratorPart1(lexicon, "Great! Terrible...");
    // tokens: ["great", "terrible"]; scores: 0.9 + (-0.9) = 0.0; / 2 = 0.0
    assertEquals(0.0, gen.getScore(), 1e-9);
  }

  @Test
  void testMultiLinereviewIsHandled() {
    // Reviews read from file may contain newlines
    SentimentScoreGeneratorPart1 gen = new SentimentScoreGeneratorPart1(lexicon,
        "This movie was good.\nThe acting was excellent.");
    assertTrue(gen.getScore() > 0.05);
    assertEquals("POSITIVE", gen.getClassification());
  }

  // ---- toString ----

  @Test
  void testToStringContainsScoreAndClassification() {
    SentimentScoreGeneratorPart1 gen = new SentimentScoreGeneratorPart1(lexicon, "good great");
    String out = gen.toString();
    assertTrue(out.contains("Sentiment Score"));
    assertTrue(out.contains("POSITIVE") || out.contains("NEUTRAL") || out.contains("NEGATIVE"));
  }

  @Test
  void testToStringFormatsScoreToTwoDecimalPlaces() {
    SentimentScoreGeneratorPart1 gen = new SentimentScoreGeneratorPart1(lexicon, "good");
    // 0.8 should appear as "0.80"
    assertTrue(gen.toString().contains("0.80"));
  }

  @Test
  void testToStringNegativeScore() {
    SentimentScoreGeneratorPart1 gen = new SentimentScoreGeneratorPart1(lexicon, "bad");
    assertTrue(gen.toString().contains("NEGATIVE"));
    assertTrue(gen.toString().contains("-0.70"));
  }

  // ---- getters ----

  @Test
  void testGetScoreMatchesCalculation() {
    SentimentScoreGeneratorPart1 gen = new SentimentScoreGeneratorPart1(lexicon, "good");
    // single token "good" → 0.8 / 1 = 0.8
    assertEquals(0.8, gen.getScore(), 1e-9);
  }

  @Test
  void testGetClassificationMatchesScore() {
    SentimentScoreGeneratorPart1 gen = new SentimentScoreGeneratorPart1(lexicon, "bad");
    assertEquals("NEGATIVE", gen.getClassification());
  }
}
