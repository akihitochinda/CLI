package problem;

/**
 * The type Analytics tracker.
 * Observer Pattern: implements interface PostObserver
 * Method update() prints the following messages to the command line for every of three events:
 *  - PUBLISHED: “Analytics Tracker: Post engagement logged”
 *  - LIKED: “Analytics Tracker: Like count increased to [engagement score - 10]”
 *  - SHARED: “Analytics Tracker: Shared count increased to [engagement score - 15]”
 */
public class AnalyticsTracker implements PostObserver {

  @Override
  public void update(Event event, SocialPost post) {
    if (event == null || post == null) {
      return;
    }
    switch (event) {
      case PUBLISHED:
        System.out.println("- Analytics Tracker: Post engagement logged");
        break;
      case LIKED:
        System.out.println("- Analytics Tracker: Like count increased to " + ((int) (post.getEngagementScore() - 10)));
        break;
      case SHARED:
        System.out.println("- Analytics Tracker: Shared count increased to " + ((int) (post.getEngagementScore() - 15)));
        break;
    }
  }
}
