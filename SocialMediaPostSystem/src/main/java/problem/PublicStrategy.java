package problem;

/**
 * The type Public strategy.
 * Strategy Pattern: implements interface Strategy
 * Simply print a message on the command line:
 * PublicStrategy – “Using Public Strategy: Post visible to everyone.“
 */
public class PublicStrategy implements Strategy {

  @Override
  public void adoptStrategy() {
    System.out.println("Using Public Strategy: Post visible to everyone.");
  }
}
