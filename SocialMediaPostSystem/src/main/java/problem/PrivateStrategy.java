package problem;

/**
 * The type Private strategy.
 * Strategy Pattern: implements interface Strategy
 * Simply print a message on the command line:
 * PrivateStrategy - “Using Private Strategy: post visible to author only.“
 */
public class PrivateStrategy implements Strategy {

  @Override
  public void adoptStrategy() {
    System.out.println("Using Private Strategy: Post visible to author only.");
  }
}
