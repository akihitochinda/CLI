package problem;

/**
 * The type Like button decorator.
 * Decorator Pattern: implements interface PostComponent
 * LikeButtonDecorator (+10 engagement score)
 */
public class LikeButtonDecorator implements PostComponent {

  private static final double ENGAGEMENT_BONUS = 10.0;
  private final PostComponent wrappedComponent;

  public LikeButtonDecorator(PostComponent wrappedComponent) {
    this.wrappedComponent = wrappedComponent;
  }

  @Override
  public double getEngagementScore() {
    return wrappedComponent.getEngagementScore() + ENGAGEMENT_BONUS;
  }
}
