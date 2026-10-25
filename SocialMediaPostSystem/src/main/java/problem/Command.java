package problem;

import java.util.List;
import java.util.Scanner;

/**
 * The interface Command.
 */
interface Command {

  /**
   * Execute.
   */
  void execute();
}

/**
 * The type Create command.
 */
class CreateCommand implements Command {

  private final SocialPostManager manager;
  private final Scanner sc;

  public CreateCommand(SocialPostManager manager, Scanner sc) {
    this.manager = manager;
    this.sc = sc;
  }

  @Override
  public void execute() {
    System.out.print("Content? ");
    String content = sc.nextLine();
    System.out.print("Author? ");
    String author = sc.nextLine();
    System.out.print("Post type (text/image/video)? ");
    String typeInput = sc.nextLine().toUpperCase();
    PostType type;
    try {
      type = PostType.valueOf(typeInput);
    } catch (IllegalArgumentException e) {
      System.out.println("Invalid post type.");
      return;
    }
    System.out.print("Tags (comma separated)? ");
    String tagsInput = sc.nextLine();
    List<String> tags = List.of(tagsInput.split("\\s*,\\s*"));
    manager.createPost(content, author, type, tags);
  }
}

/**
 * The type Add feature command.
 */
class AddFeatureCommand implements Command {

  private final SocialPostManager manager;
  private final String feature;

  public AddFeatureCommand(SocialPostManager manager, String feature) {
    this.manager = manager;
    this.feature = feature;
  }

  @Override
  public void execute() {
    manager.addFeature(feature);
  }
}

/**
 * The type Strategy command.
 */
class StrategyCommand implements Command {

  private final SocialPostManager manager;
  private final String strategy;

  public StrategyCommand(SocialPostManager manager, String strategy) {
    this.manager = manager;
    this.strategy = strategy;
  }

  @Override
  public void execute() {
    manager.applyStrategy(strategy);
  }
}

/**
 * The type Save template command.
 */
class SaveTemplateCommand implements Command {

  private final SocialPostManager manager;
  private final String templateName;

  public SaveTemplateCommand(SocialPostManager manager, String templateName) {
    this.manager = manager;
    this.templateName = templateName;
  }

  @Override
  public void execute() {
    manager.saveTemplate(templateName);
  }
}

/**
 * The type Clone command.
 */
class CloneCommand implements Command {

  private final SocialPostManager manager;
  private final String templateName;

  public CloneCommand(SocialPostManager manager, String templateName) {
    this.manager = manager;
    this.templateName = templateName;
  }

  @Override
  public void execute() {
    manager.cloneTemplate(templateName);
  }
}

/**
 * The type Publish command.
 */
class PublishCommand implements Command {

  private final SocialPostManager manager;

  public PublishCommand(SocialPostManager manager) {
    this.manager = manager;
  }

  @Override
  public void execute() {
    manager.publish();
  }
}

/**
 * The type Like command.
 */
class LikeCommand implements Command {

  private final SocialPostManager manager;

  public LikeCommand(SocialPostManager manager) {
    this.manager = manager;
  }

  @Override
  public void execute() {
    manager.like();
  }
}

/**
 * The type Share command.
 */
class ShareCommand implements Command {

  private final SocialPostManager manager;

  public ShareCommand(SocialPostManager manager) {
    this.manager = manager;
  }

  @Override
  public void execute() {
    manager.share();
  }
}
