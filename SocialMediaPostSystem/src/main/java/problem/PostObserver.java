package problem;

/**
 * The interface Post observer.
 * Strategy Pattern: an interface PostObserver with update() method
 */
public interface PostObserver {

  /**
   * Updates the observer for the given post event.
   *
   * @param event the event that occurred
   * @param post  the post that triggered the event
   */
  void update(Event event, SocialPost post);
}
