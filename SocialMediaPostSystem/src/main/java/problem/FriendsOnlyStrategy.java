package problem;

/**
 * The type Friends only strategy.
 * Strategy Pattern: implements interface Strategy
 * Simply print a message on the command line:
 * FriendsOnlyStrategy – “Using Friends Only Strategy: Post visible to friends.“
 */
public class FriendsOnlyStrategy implements Strategy {

  @Override
  public void adoptStrategy() {
    System.out.println("Using Friends Only Strategy: Post visible to friends.");
  }
}
