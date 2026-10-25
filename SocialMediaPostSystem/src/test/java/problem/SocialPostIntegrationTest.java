package problem;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SocialPostIntegrationTest {

  private final PrintStream originalOut = System.out;
  private final java.io.InputStream originalIn = System.in;
  private ByteArrayOutputStream outContent;

  @BeforeEach
  void setUp() {
    outContent = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outContent));
  }

  @AfterEach
  void tearDown() {
    System.setOut(originalOut);
    System.setIn(originalIn);
  }

  @Test
  void createCommandShouldBuildPostThenAddFeature() {
    String userInput = String.join(System.lineSeparator(),
        "create",
        "Hello World!",
        "Alice",
        "TEXT",
        "#greeting",
        "strategy public",
        "feature like",
        "feature share",
        "done") + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(userInput.getBytes()));

    Main.main(new String[0]);

    String output = outContent.toString();
    assertTrue(output.contains("Base Post: \"Hello World!\" by Alice - Engagement: 5"));
    assertTrue(output.contains("Using Public Strategy: Post visible to everyone."));
    assertTrue(output.contains("With like feature: Engagement: 15"));
    assertTrue(output.contains("With share feature: Engagement: 30"));
  }

  @Test
  void likeAndShareCommandsShouldUpdateEngagement() {
    SocialPostManager manager = new SocialPostManager();
    java.util.Scanner scanner = new java.util.Scanner(
        String.join(System.lineSeparator(), "Hello World!", "Alice", "TEXT", "#greeting") + System.lineSeparator());
    new CreateCommand(manager, scanner).execute();

    outContent.reset();
    new LikeCommand(manager).execute();
    new ShareCommand(manager).execute();

    String output = outContent.toString();
    assertTrue(output.contains("Post liked!"));
    assertTrue(output.contains("Post shared!"));
  }

  @Test
  void commentSectionDecoratorShouldIncreaseEngagement() {
    SocialPost post = new SocialPost.PostBuilder()
        .setContent("Hello World!")
        .setAuthor("Alice")
        .setType(PostType.TEXT)
        .addTag("#greeting")
        .build();

    PostComponent commented = new CommentSectionDecorator(post);
    assertEquals(25, (int) commented.getEngagementScore());
  }

  @Test
  void strategyCommandShouldPrintChosenStrategy() {
    String userInput = String.join(System.lineSeparator(),
        "create",
        "Hello World!",
        "Alice",
        "TEXT",
        "#greeting",
        "strategy friends",
        "done") + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(userInput.getBytes()));

    Main.main(new String[0]);

    String output = outContent.toString();
    assertTrue(output.contains("Using Friends Only Strategy: Post visible to friends."));
  }

  @Test
  void privateStrategyCommandShouldPrintPrivateStrategy() {
    String userInput = String.join(System.lineSeparator(),
        "create",
        "Hello World!",
        "Alice",
        "TEXT",
        "#greeting",
        "strategy private",
        "done") + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(userInput.getBytes()));

    Main.main(new String[0]);

    String output = outContent.toString();
    assertTrue(output.contains("Using Private Strategy: Post visible to author only."));
  }

  @Test
  void saveTemplateAndCloneCommandShouldRestoreTemplate() {
    String userInput = String.join(System.lineSeparator(),
        "create",
        "Hello World!",
        "Alice",
        "TEXT",
        "#greeting",
        "save greeting",
        "create",
        "Another Post",
        "Bob",
        "TEXT",
        "#work",
        "clone greeting",
        "done") + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(userInput.getBytes()));

    Main.main(new String[0]);

    String output = outContent.toString();
    assertTrue(output.contains("Template saved: greeting"));
    assertTrue(output.contains("Template post cloned: greeting ready"));
  }

  @Test
  void publishCommandShouldExecutePublishFlow() {
    String userInput = String.join(System.lineSeparator(),
        "create",
        "Hello World!",
        "Alice",
        "TEXT",
        "#greeting",
        "publish",
        "done") + System.lineSeparator();
    System.setIn(new ByteArrayInputStream(userInput.getBytes()));

    Main.main(new String[0]);

    String output = outContent.toString();
    assertTrue(output.contains("Base Post: \"Hello World!\" by Alice"));
    assertTrue(output.contains("Post published!"));
    assertTrue(output.contains("- Follower Notifier: 2 followers notified") || output.contains("- Follower Notifier:"));
    assertTrue(output.contains("- Analytics Tracker: Post engagement logged"));
  }

  @Test
  void socialPostShouldExposeTypeAndTags() {
    SocialPost post = new SocialPost.PostBuilder()
        .setContent("Hello World!")
        .setAuthor("Alice")
        .setType(PostType.TEXT)
        .addTag("#greeting")
        .addTag("#fun")
        .build();

    assertEquals(PostType.TEXT, post.getType());
    assertEquals(2, post.getTags().size());
    assertTrue(post.getTags().contains("#greeting"));
    assertTrue(post.getTags().contains("#fun"));
  }

  @Test
  void socialPostShouldCountAndRemoveObservers() {
    SocialPost post = new SocialPost.PostBuilder()
        .setContent("Observer post")
        .setAuthor("Alice")
        .setType(PostType.TEXT)
        .build();

    PostObserver observer1 = (event, source) -> {};
    PostObserver observer2 = (event, source) -> {};
    post.addObserver(observer1);
    post.addObserver(observer2);

    assertEquals(2, post.countObservers());
    post.removeObserver(observer1);
    assertEquals(1, post.countObservers());
    assertThrows(IllegalArgumentException.class, () -> post.removeObserver(observer1));
  }

  @Test
  void publishShouldNotifyObservers() {
    SocialPost post = new SocialPost.PostBuilder()
        .setContent("Hello World!")
        .setAuthor("Alice")
        .setType(PostType.TEXT)
        .build();

    StringBuilder notified = new StringBuilder();
    PostObserver observer = (event, source) -> {
      if (event == Event.PUBLISHED) {
        notified.append("published");
      }
    };
    post.addObserver(observer);

    outContent.reset();
    post.publish();

    String output = outContent.toString();
    assertTrue(output.contains("Post published!"));
    assertEquals("published", notified.toString());
  }
}
