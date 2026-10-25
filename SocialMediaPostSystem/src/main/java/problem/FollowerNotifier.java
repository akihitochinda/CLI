package problem;

/**
 * The type Follower notifier.
 * Observer Pattern: implements interface PostObserver
 * Method update() prints the following messages to the command line for every of three events:
 *  - PUBLISHED: “Followed Notifier: [number of post observers] followers notified”
 *  - LIKED: “Follower Notifier: Like notification sent to [Author]”
 *  - SHARED: “Follower Notifier: Share notification sent to [Author]”
 */
public class FollowerNotifier implements PostObserver {

  @Override
  public void update(Event event, SocialPost post) {
    if (event == null || post == null) {
      return;
    }
    switch (event) {
      case PUBLISHED:
        System.out.println("- Follower Notifier: " + post.countObservers() + " followers notified");
        break;
      case LIKED:
        System.out.println("- Follower Notifier: Like notification sent to " + post.getAuthor());
        break;
      case SHARED:
        System.out.println("- Follower Notifier: Share notification sent to " + post.getAuthor());
        break;
    }
  }
}
