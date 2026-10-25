package option2;

/**
 * DESIGN PATTERN: Strategy
 *
 * Defines the contract for a sentiment-scoring algorithm. Any class that
 * implements this interface can be plugged into SentimentScoreGenerator
 * (or any other caller) without changing the surrounding code.
 *
 * This is appropriate here because the scoring algorithm — normalized
 * lexicon lookup — is a discrete, swappable behavior. A future
 * implementation might use a machine-learning model or a different
 * normalization scheme; the rest of the program would need no changes.
 */
public interface ScoringStrategy {

  /**
   * Computes a sentiment score for the given review text.
   *
   * @param reviewText the full text of the review
   * @return a normalized score, typically in the range [-1.0, 1.0]
   */
  double score(String reviewText);
}
