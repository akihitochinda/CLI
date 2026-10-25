package option2;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Computes and classifies the sentiment of a review text using a lexicon.
 *
 * DESIGN PATTERN: Strategy (Concrete Strategy)
 *
 * This class implements ScoringStrategy, making the lexicon-based
 * scoring algorithm a swappable strategy. The caller (ReportGenerator
 * or interactive mode in sentimentAnalyzer) depends only on the
 * ScoringStrategy interface, so this implementation can be replaced
 * — e.g. with an ML-based scorer — without touching any other class.
 */
public class SentimentScoreGenerator implements ScoringStrategy {

  private Map<String, Double> sentimentLexicon;
  private double score;
  private String classification;
  private static final double POSITIVE_THRESHOLD = 0.05;
  private static final double NEGATIVE_THRESHOLD = -0.05;

  /**
   * Constructor for SentimentScoreGenerator.
   *
   * @param sentimentLexicon Map of words to sentiment scores from CSVParser
   * @param reviewText       the review text to score immediately on construction
   */
  public SentimentScoreGenerator(Map<String, Double> sentimentLexicon, String reviewText) {
    this.sentimentLexicon = sentimentLexicon;
    this.score = this.calculateSentimentScore(reviewText);
    this.classification = this.classifySentiment(this.score);
  }

  /**
   * Tokenizes the review text into individual words.
   * Converts to lowercase and strips punctuation, then splits on whitespace.
   *
   * @param reviewText the review text to tokenize
   * @return array of lowercase tokens with punctuation removed
   */
  private String[] tokenizeReview(String reviewText) {
    String lower = reviewText.toLowerCase();
    Pattern reg = Pattern.compile("[^a-z0-9\\s]");
    Matcher matcher = reg.matcher(lower);
    String stripped = matcher.replaceAll("");
    return stripped.split("\\s+");  // split on any whitespace, not just single space
  }

  /**
   * Calculates the normalized sentiment score for the review.
   * Sums lexicon scores for each matched token and divides by total token count.
   *
   * DESIGN PATTERN: Strategy
   * This method is the concrete implementation of ScoringStrategy#score.
   * Changing the scoring algorithm means providing a different ScoringStrategy
   * implementation — this class and its callers remain untouched.
   *
   * @param reviewText the full review text
   * @return normalized sentiment score in the range [-1.0, 1.0]
   */
  @Override
  public double score(String reviewText) {
    return calculateSentimentScore(reviewText);
  }

  /**
   * Internal scoring logic, also called from the constructor for eager evaluation.
   *
   * @param reviewText the full review text
   * @return normalised sentiment score
   */
  public double calculateSentimentScore(String reviewText) {
    String[] tokens = this.tokenizeReview(reviewText);
    double totalScore = 0.0;

    if (tokens.length == 0) {
      return 0.0;
    }

    for (String token : tokens) {
      if (!token.isEmpty() && this.sentimentLexicon.containsKey(token)) {
        totalScore += this.sentimentLexicon.get(token);
      }
    }

    return totalScore / tokens.length;
  }

  /**
   * Classifies the sentiment score into POSITIVE, NEGATIVE, or NEUTRAL.
   *
   * @param score the sentiment score
   * @return "POSITIVE", "NEGATIVE", or "NEUTRAL"
   */
  public String classifySentiment(double score) {
    if (score > POSITIVE_THRESHOLD) {
      return "POSITIVE";
    } else if (score < NEGATIVE_THRESHOLD) {
      return "NEGATIVE";
    } else {
      return "NEUTRAL";
    }
  }

  /**
   * Gets the pre-computed sentiment score.
   *
   * @return the score
   */
  public double getScore() {
    return this.score;
  }

  /**
   * Gets the pre-computed classification label.
   *
   * @return the classification
   */
  public String getClassification() {
    return this.classification;
  }

  @Override
  public String toString() {
    return String.format("Sentiment Score: %.2f -> %s", this.score, this.classification);
  }
}
