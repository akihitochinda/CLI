package problem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The type Social post.
 * Builder Pattern: an observable class SocialPost.
 * Prototype Pattern: SocialPost cloneable for quick duplication.
 */
public class SocialPost implements PostComponent, PostObservable, Cloneable {

  private static final double DEFAULT_ENGAGEMENT_SCORE = 5.0;
  private static final double LIKE_INCREMENT = 10.0;
  private static final double SHARE_INCREMENT = 15.0;

  private final String content;
  private final String author;
  private final PostType type;
  private final List<String> tags;
  private double engagementScore;
  private final List<PostObserver> observers;
  private Strategy visibilityStrategy;
  private Event currentEvent;

  private SocialPost(String content, String author, PostType type, List<String> tags) {
    this.content = content;
    this.author = author;
    this.type = type;
    this.tags = new ArrayList<String>(tags);
    this.engagementScore = DEFAULT_ENGAGEMENT_SCORE;
    this.observers = new ArrayList<PostObserver>();
    this.visibilityStrategy = new PublicStrategy();
    this.currentEvent = null;
  }

  public String getContent() {
    return content;
  }

  public String getAuthor() {
    return author;
  }

  public PostType getType() {
    return type;
  }

  public List<String> getTags() {
    return Collections.unmodifiableList(tags);
  }

  public void applyStrategy(Strategy strategy) {
    if (strategy == null) {
      return;
    }
    this.visibilityStrategy = strategy;
    strategy.adoptStrategy();
  }

  @Override
  public double getEngagementScore() {
    return engagementScore;
  }

  public void publish() {
    currentEvent = Event.PUBLISHED;
    System.out.println("Post published!");
    notifyObservers();
  }

  public void like() {
    engagementScore += LIKE_INCREMENT;
    currentEvent = Event.LIKED;
    System.out.println("Post liked!");
    notifyObservers();
  }

  public void share() {
    engagementScore += SHARE_INCREMENT;
    currentEvent = Event.SHARED;
    System.out.println("Post shared!");
    notifyObservers();
  }

  /**
   * addObserver(PostObserver postObserver)
   *  – method adds an observer into the list of PostObservers.
   *  If the given observer already exists in the list, method does nothing.
   */
  @Override
  public void addObserver(PostObserver postObserver) {
    if (postObserver == null) {
      return;
    }
    if (!observers.contains(postObserver)) {
      observers.add(postObserver);
    }
  }

  /**
   * removeObserver(PostObserver postObserver)
   *  - method removes the given observer from the list of PostObservers.
   *  If the given observer does not exist in the list, method throws an IllegalArgumentException.
   */
  @Override
  public void removeObserver(PostObserver postObserver) {
    if (!observers.remove(postObserver)) {
      throw new IllegalArgumentException("Observer not found.");
    }
  }

  /**
   * notifyObservers()
   *  - method notifies all PostObservers when a post event occurs.
   *  Our simple notification system recognizes three different post events:
   *  PUBLISHED, LIKED, SHARED, and all events are stored into an enumeration data type, Event.
   */
  @Override
  public void notifyObservers() {
    for (PostObserver observer : observers) {
      observer.update(currentEvent, this);
    }
  }

  public int countObservers() {
    return observers.size();
  }

  @Override
  public SocialPost clone() {
    SocialPost cloned = new SocialPost(content, author, type, tags);
    cloned.engagementScore = engagementScore;
    cloned.visibilityStrategy = visibilityStrategy;
    return cloned;
  }

  public static class PostBuilder {
    private String content;
    private String author;
    private PostType type;
    private final List<String> tags = new ArrayList<String>();

    public PostBuilder setContent(String content) {
      this.content = content;
      return this;
    }

    public PostBuilder setAuthor(String author) {
      this.author = author;
      return this;
    }

    public PostBuilder setType(PostType type) {
      this.type = type;
      return this;
    }

    public PostBuilder addTag(String tag) {
      if (tag != null && !tag.trim().isEmpty()) {
        this.tags.add(tag);
      }
      return this;
    }

    public SocialPost build() {
      if (content == null || content.trim().isEmpty()) {
        throw new IllegalArgumentException("Content cannot be empty.");
      }
      if (author == null || author.trim().isEmpty()) {
        throw new IllegalArgumentException("Author cannot be empty.");
      }
      if (type == null) {
        throw new IllegalArgumentException("PostType must be set.");
      }
      return new SocialPost(content, author, type, tags);
    }
  }
}
