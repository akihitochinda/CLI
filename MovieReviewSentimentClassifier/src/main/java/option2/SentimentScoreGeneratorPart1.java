package option2;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Sentiment score generator.
 * A sentiment lexicon maps individual English words to a score
 * between - 1.0 (very negative) and +1.0 (very positive).
 */
public class SentimentScoreGeneratorPart1 {
  
  private Map<String, Double> sentimentLexicon;
  private double score;
  private String classification;
  private static final double POSITIVE_THRESHOLD = 0.05;
  private static final double NEGATIVE_THRESHOLD = -0.05;

  /**
   * Constructor for SentimentScoreGenerator
   *
   * @param sentimentLexicon Map of words to sentiment scores from CSVParser
   * @param reviewText       the review text
   */
  public SentimentScoreGeneratorPart1(Map<String, Double> sentimentLexicon, String reviewText) {
    this.sentimentLexicon = sentimentLexicon;
    this.score = this.calculateSentimentScore(reviewText);
    this.classification = this.classifySentiment(this.score);
  }

  /**
   * Tokenize the review text into individual words.
   * (split on whitespace; convert to lowercase strip punctuation)
   *
   * @param reviewText The review text to tokenize
   * @return Array of tokens (lowercase, no punctuation)
   */
  private String[] tokenizeReview(String reviewText) {

    // Convert to lowercase
    String newReviewText = reviewText.toLowerCase();

    // Pattern to match any character that is NOT a lowercase letter, digit, or whitespace
    Pattern reg = Pattern.compile("[^a-z0-9\\s]");
    Matcher matcher = reg.matcher(newReviewText);

    // Strip punctuation using regex
    String strippedText = matcher.replaceAll("");

    // Split on whitespace, which handles any whitespace including \n
    String[] tokens = strippedText.split("\\s+");

    return tokens;
  }

  /**
   * Calculates the sentiment score for a review
   * For each token that appears in the lexicon, add its score to a running total.
   * Divide the total by the number of tokens in the review to obtain the normalized sentiment.
   *
   * @param reviewText The full review text
   * @return The normalized sentiment score (between -1.0 and 1.0)
   */
  public double calculateSentimentScore(String reviewText) {

    String[] tokens = this.tokenizeReview(reviewText);
    double totalScore = 0.0;
    
    if (tokens.length == 0) {
      return 0.0;
    }
  
    // Sum up scores for tokens found in lexicon
    for (String token : tokens) {
      if (!token.isEmpty() && this.sentimentLexicon.containsKey(token)) {
        totalScore += this.sentimentLexicon.get(token);
      }
    }
    
    // Normalize by number of tokens
    double normalizedScore = totalScore / tokens.length;
    
    return normalizedScore;
  }

  /**
   * Classifies the sentiment based on score
   * Classify the review:
   * POSITIVE if score > 0.05, NEGATIVE if score < -0.05, NEUTRAL otherwise.
   *
   * @param score The sentiment score
   * @return Classification: "POSITIVE", "NEGATIVE", or "NEUTRAL"
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
   * Gets score.
   *
   * @return the score
   */
  public double getScore() {
    return this.score;
  }

  /**
   * Gets classification.
   *
   * @return the classification
   */
  public String getClassification() {
    return this.classification;
  }

  @Override
  public String toString() {
    // Format score to 2 decimal places
    return String.format("Sentiment Score: %.2f -> %s", this.score, this.classification);
  }
}
