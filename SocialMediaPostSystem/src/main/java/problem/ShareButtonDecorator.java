package problem;

/**
 * The type Share button decorator.
 * Decorator Pattern: implements interface PostComponent
 * ShareButtonDecorator (+15 engagement score)
 */
public class ShareButtonDecorator implements PostComponent {

  private static final double ENGAGEMENT_BONUS = 15.0;
  private final PostComponent wrappedComponent;

  public ShareButtonDecorator(PostComponent wrappedComponent) {
    this.wrappedComponent = wrappedComponent;
  }

  @Override
  public double getEngagementScore() {
    return wrappedComponent.getEngagementScore() + ENGAGEMENT_BONUS;
  }
}
