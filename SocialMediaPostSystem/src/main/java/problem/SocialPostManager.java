package problem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import problem.SocialPost.PostBuilder;

/**
 * The type Social post manager.
 */
public class SocialPostManager {

  private SocialPost currentPost;
  private PostComponent currentComponent;
  private final Map<String, SocialPost> templates = new HashMap<String, SocialPost>();

  // Example usage:
  SocialPost post = new PostBuilder()
    .setContent("Hello World!")
    .setAuthor("Alice")
    .setType(PostType.TEXT)
    .addTag("#greeting")
    .build();

  /**
   * Creates the current social post.
   *
   * @param content the post content
   * @param author  the author name
   * @param type    the post type
   * @param tags    the list of hashtags
   */
  public void createPost(String content, String author, PostType type, List<String> tags) {
    SocialPost.PostBuilder builder = new SocialPost.PostBuilder()
        .setContent(content)
        .setAuthor(author)
        .setType(type);
    for (String tag : tags) {
      builder.addTag(tag);
    }
    currentPost = builder.build();
    currentComponent = currentPost;
    currentPost.addObserver(new FollowerNotifier());
    currentPost.addObserver(new AnalyticsTracker());
    System.out.println("Base Post: \"" + currentPost.getContent() + "\" by " + currentPost.getAuthor() + " - Engagement: " + (int) currentComponent.getEngagementScore());
  }

  private boolean hasActivePost() {
    if (currentPost == null) {
      System.out.println("No active post. Use create command first.");
      return false;
    }
    return true;
  }

  /**
   * Adds a feature to the current post.
   *
   * @param feature the feature name
   */
  public void addFeature(String feature) {
    if (!hasActivePost()) {
      return;
    }
    switch (feature.toLowerCase()) {
      case "like" -> currentComponent = new LikeButtonDecorator(currentComponent);
      case "share" -> currentComponent = new ShareButtonDecorator(currentComponent);
      case "comment" -> currentComponent = new CommentSectionDecorator(currentComponent);
      default -> {
        System.out.println("Unknown feature.");
        return;
      }
    }
    System.out.print("With " + feature + " feature: ");
    System.out.println("Engagement: " + (int) currentComponent.getEngagementScore());
  }

  /**
   * Applies a visibility strategy to the current post.
   *
   * @param strategyName the strategy name
   */
  public void applyStrategy(String strategyName) {
    if (!hasActivePost()) {
      return;
    }
    Strategy strategy;
    switch (strategyName.toLowerCase()) {
      case "public" -> strategy = new PublicStrategy();
      case "friends" -> strategy = new FriendsOnlyStrategy();
      case "private" -> strategy = new PrivateStrategy();
      default -> {
        System.out.println("Unknown strategy.");
        return;
      }
    }
    currentPost.applyStrategy(strategy);
  }

  /**
   * Saves the current post as a template.
   *
   * @param name the template name
   */
  public void saveTemplate(String name) {
    if (!hasActivePost()) {
      return;
    }
    if (name == null || name.trim().isEmpty()) {
      System.out.println("Template name cannot be empty.");
      return;
    }
    templates.put(name, currentPost.clone());
    System.out.println("Template saved: " + name);
  }

  /**
   * Clones a saved template into the active post.
   *
   * @param name the template name
   */
  public void cloneTemplate(String name) {
    SocialPost template = templates.get(name);
    if (template == null) {
      System.out.println("Template not found.");
      return;
    }
    currentPost = template.clone();
    currentComponent = currentPost;
    currentPost.addObserver(new FollowerNotifier());
    currentPost.addObserver(new AnalyticsTracker());
    System.out.println("Template post cloned: " + name + " ready");
  }

  /**
   * Publishes the current post.
   */
  public void publish() {
    if (!hasActivePost()) {
      return;
    }
    currentPost.publish();
  }

  /**
   * Likes the current post.
   */
  public void like() {
    if (!hasActivePost()) {
      return;
    }
    currentPost.like();
  }

  /**
   * Shares the current post.
   */
  public void share() {
    if (!hasActivePost()) {
      return;
    }
    currentPost.share();
  }
}
