package problem;

/**
 * The type Comment section decorator.
 * Decorator Pattern: implements interface PostComponent
 * CommentSectionDecorator (+20 engagement score)
 */
public class CommentSectionDecorator implements PostComponent {

  private static final double ENGAGEMENT_BONUS = 20.0;
  private final PostComponent wrappedComponent;

  public CommentSectionDecorator(PostComponent wrappedComponent) {
    this.wrappedComponent = wrappedComponent;
  }

  @Override
  public double getEngagementScore() {
    return wrappedComponent.getEngagementScore() + ENGAGEMENT_BONUS;
  }
}
