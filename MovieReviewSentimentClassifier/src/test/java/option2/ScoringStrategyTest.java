package option2;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ScoringStrategyTest {

  private static final Map<String, Double> LEXICON =
      Map.of("good", 0.8, "excellent", 1.0, "bad", -0.8, "terrible", -1.0, "okay", 0.0);

  // ── Strategy contract ─────────────────────────────────────────────────────

  @Test
  void testSentimentScoreGeneratorImplementsScoringStrategy() {
    // DESIGN PATTERN: Strategy — the concrete class must be assignable to the interface
    ScoringStrategy strategy = new SentimentScoreGenerator(LEXICON, "good");
    assertInstanceOf(ScoringStrategy.class, strategy);
  }

  @Test
  void testScoreInterfaceMethodMatchesCalculateSentimentScore() {
    // score() is the Strategy interface method; it must delegate to the same logic
    SentimentScoreGenerator gen = new SentimentScoreGenerator(LEXICON, "good");
    assertEquals(gen.calculateSentimentScore("good"), gen.score("good"), 0.001);
  }

  @Test
  void testStrategyCanBeReferencedThroughInterface() {
    // A caller holding only a ScoringStrategy reference should get the correct result
    ScoringStrategy strategy = new SentimentScoreGenerator(LEXICON, "good");
    assertEquals(0.8, strategy.score("good"), 0.001);
  }

  @Test
  void testDifferentTextViaInterfaceMethod() {
    ScoringStrategy strategy = new SentimentScoreGenerator(LEXICON, "good");
    // score() is called with different text than the constructor — verifies it's not cached
    assertEquals(-0.8, strategy.score("bad"), 0.001);
  }

  // ── Scoring correctness ───────────────────────────────────────────────────

  @Test
  void testSingleMatchedToken() {
    SentimentScoreGenerator gen = new SentimentScoreGenerator(LEXICON, "good");
    assertEquals(0.8, gen.getScore(), 0.001);
  }

  @Test
  void testTwoMatchedTokensAveraged() {
    // (0.8 + 1.0) / 2 = 0.9
    SentimentScoreGenerator gen = new SentimentScoreGenerator(LEXICON, "good excellent");
    assertEquals(0.9, gen.getScore(), 0.001);
  }

  @Test
  void testUnmatchedTokensCountInDenominator() {
    // "good unknown" — "unknown" not in lexicon, but still counts as a token
    // score = 0.8 / 2 = 0.4
    SentimentScoreGenerator gen = new SentimentScoreGenerator(LEXICON, "good unknown");
    assertEquals(0.4, gen.getScore(), 0.001);
  }

  @Test
  void testAllTokensUnmatched() {
    SentimentScoreGenerator gen = new SentimentScoreGenerator(LEXICON, "zzz yyy xxx");
    assertEquals(0.0, gen.getScore(), 0.001);
  }

  @Test
  void testEmptyTextReturnsZero() {
    SentimentScoreGenerator gen = new SentimentScoreGenerator(LEXICON, "");
    assertEquals(0.0, gen.getScore(), 0.001);
  }

  @Test
  void testWhitespaceOnlyTextReturnsZero() {
    SentimentScoreGenerator gen = new SentimentScoreGenerator(LEXICON, "   ");
    assertEquals(0.0, gen.getScore(), 0.001);
  }

  // ── Tokenization: case and punctuation ───────────────────────────────────

  @Test
  void testUppercaseInputIsNormalized() {
    // "GOOD" should match "good" in the lexicon after lowercasing
    SentimentScoreGenerator gen = new SentimentScoreGenerator(LEXICON, "GOOD");
    assertEquals(0.8, gen.getScore(), 0.001);
  }

  @Test
  void testMixedCaseInputIsNormalized() {
    SentimentScoreGenerator gen = new SentimentScoreGenerator(LEXICON, "Good");
    assertEquals(0.8, gen.getScore(), 0.001);
  }

  @Test
  void testPunctuationIsStripped() {
    // "good!" → "good" after stripping — must match lexicon
    SentimentScoreGenerator gen = new SentimentScoreGenerator(LEXICON, "good!");
    assertEquals(0.8, gen.getScore(), 0.001);
  }

  @Test
  void testCommaIsStrippedNotTreatedAsSeparator() {
    SentimentScoreGenerator gen = new SentimentScoreGenerator(LEXICON, "good,excellent");
    assertEquals(0.0, gen.getScore(), 0.001);
  }

  @Test
  void testCommaWithSpaceCorrectlyTokenizesTwoWords() {
    // With a space present the split works: "good, excellent" → ["good", "excellent"]
    SentimentScoreGenerator gen = new SentimentScoreGenerator(LEXICON, "good, excellent");
    assertEquals(0.9, gen.getScore(), 0.001);
  }

  // ── Tokenization: whitespace splitting fix (split("\\s+")) ────────────────

  @Test
  void testTrailingNewlineDoesNotBreakTokenization() {
    // readReviewFile with lineSeparator removed returns plain text, but \s+ also
    // handles the case defensively — "good\n" must still match "good"
    SentimentScoreGenerator gen = new SentimentScoreGenerator(LEXICON, "good\n");
    assertEquals(0.8, gen.getScore(), 0.001);
  }

  @Test
  void testMultipleSpacesBetweenTokens() {
    // split("\\s+") must collapse multiple spaces into one split
    SentimentScoreGenerator gen = new SentimentScoreGenerator(LEXICON, "good   excellent");
    assertEquals(0.9, gen.getScore(), 0.001);
  }

  @Test
  void testTabSeparatedTokens() {
    SentimentScoreGenerator gen = new SentimentScoreGenerator(LEXICON, "good\texcellent");
    assertEquals(0.9, gen.getScore(), 0.001);
  }

  @Test
  void testMultilineReviewText() {
    // Simulates a review file with multiple lines joined without separator
    SentimentScoreGenerator gen = new SentimentScoreGenerator(LEXICON, "good\nexcellent");
    assertEquals(0.9, gen.getScore(), 0.001);
  }

  // ── classifySentiment boundaries ─────────────────────────────────────────

  @Test
  void testScoreAbovePositiveThresholdIsPositive() {
    SentimentScoreGenerator gen = new SentimentScoreGenerator(LEXICON, "good");
    assertEquals("POSITIVE", gen.classifySentiment(0.06));
  }

  @Test
  void testScoreExactlyAtPositiveThresholdIsNeutral() {
    // threshold is > 0.05, so 0.05 itself is NEUTRAL
    SentimentScoreGenerator gen = new SentimentScoreGenerator(LEXICON, "good");
    assertEquals("NEUTRAL", gen.classifySentiment(0.05));
  }

  @Test
  void testScoreBelowNegativeThresholdIsNegative() {
    SentimentScoreGenerator gen = new SentimentScoreGenerator(LEXICON, "good");
    assertEquals("NEGATIVE", gen.classifySentiment(-0.06));
  }

  @Test
  void testScoreExactlyAtNegativeThresholdIsNeutral() {
    // threshold is < -0.05, so -0.05 itself is NEUTRAL
    SentimentScoreGenerator gen = new SentimentScoreGenerator(LEXICON, "good");
    assertEquals("NEUTRAL", gen.classifySentiment(-0.05));
  }

  @Test
  void testZeroScoreIsNeutral() {
    SentimentScoreGenerator gen = new SentimentScoreGenerator(LEXICON, "good");
    assertEquals("NEUTRAL", gen.classifySentiment(0.0));
  }

  // ── getScore / getClassification eagerly computed from constructor ─────────

  @Test
  void testGetScoreMatchesCalculateForConstructorText() {
    SentimentScoreGenerator gen = new SentimentScoreGenerator(LEXICON, "good excellent");
    assertEquals(gen.calculateSentimentScore("good excellent"), gen.getScore(), 0.001);
  }

  @Test
  void testGetClassificationMatchesClassifySentimentForConstructorScore() {
    SentimentScoreGenerator gen = new SentimentScoreGenerator(LEXICON, "good");
    assertEquals(gen.classifySentiment(gen.getScore()), gen.getClassification());
  }

  @Test
  void testPositiveReviewClassification() {
    SentimentScoreGenerator gen = new SentimentScoreGenerator(LEXICON, "good excellent");
    assertEquals("POSITIVE", gen.getClassification());
  }

  @Test
  void testNegativeReviewClassification() {
    SentimentScoreGenerator gen = new SentimentScoreGenerator(LEXICON, "bad terrible");
    assertEquals("NEGATIVE", gen.getClassification());
  }

  @Test
  void testNeutralReviewClassification() {
    SentimentScoreGenerator gen = new SentimentScoreGenerator(LEXICON, "okay");
    assertEquals("NEUTRAL", gen.getClassification());
  }

  // ── toString ──────────────────────────────────────────────────────────────

  @Test
  void testToStringContainsSentimentScore() {
    SentimentScoreGenerator gen = new SentimentScoreGenerator(LEXICON, "good");
    assertTrue(gen.toString().contains("Sentiment Score"));
  }

  @Test
  void testToStringContainsClassificationLabel() {
    SentimentScoreGenerator gen = new SentimentScoreGenerator(LEXICON, "good");
    assertTrue(gen.toString().contains("POSITIVE"));
  }

  @Test
  void testToStringFormatsScoreToTwoDecimalPlaces() {
    SentimentScoreGenerator gen = new SentimentScoreGenerator(LEXICON, "good");
    // "0.80" — two decimal places
    assertTrue(gen.toString().contains("0.80"));
  }
}
