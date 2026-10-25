package problem;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Component;
import java.awt.Container;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SocialPostGuiTest {

  private final PrintStream originalOut = System.out;
  private ByteArrayOutputStream outContent;
  private SocialPostGui gui;

  @BeforeEach
  void setUp() throws Exception {
    outContent = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outContent, true));

    SwingUtilities.invokeAndWait(() -> {
      gui = new SocialPostGui(false);
      gui.setVisible(false);
    });
  }

  @AfterEach
  void tearDown() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      if (gui != null) {
        gui.dispose();
      }
    });
    System.setOut(originalOut);
  }

  @Test
  void guiShouldCreatePostAndPrintBasePost() throws Exception {
    runOnEdt(() -> {
      setTextField("contentField", "Hello World!");
      setTextField("authorField", "Alice");
      getComboBox("typeCombo").setSelectedItem(PostType.TEXT);
      setTextField("tagsField", "#greeting");
      findButton("Create Post").doClick();
    });

    String output = outContent.toString();
    assertTrue(output.contains("Base Post: \"Hello World!\" by Alice - Engagement: 5"));
  }

  @Test
  void guiShouldApplyPrivateStrategy() throws Exception {
    runOnEdt(() -> {
      setTextField("contentField", "Hello World!");
      setTextField("authorField", "Alice");
      getComboBox("typeCombo").setSelectedItem(PostType.TEXT);
      setTextField("tagsField", "#greeting");
      findButton("Create Post").doClick();
      findButton("Private Strategy").doClick();
    });

    String output = outContent.toString();
    assertTrue(output.contains("Using Private Strategy: Post visible to author only."));
  }

  @Test
  void guiShouldAddLikeAndShareFeatureButtons() throws Exception {
    runOnEdt(() -> {
      setTextField("contentField", "Hello World!");
      setTextField("authorField", "Alice");
      getComboBox("typeCombo").setSelectedItem(PostType.TEXT);
      setTextField("tagsField", "#greeting");
      findButton("Create Post").doClick();
      findButton("Add Like Feature").doClick();
      findButton("Add Share Feature").doClick();
    });

    String output = outContent.toString();
    assertTrue(output.contains("With like feature: Engagement: 15"));
    assertTrue(output.contains("With share feature: Engagement: 30"));
  }

  private void runOnEdt(ThrowingRunnable action) throws Exception {
    if (SwingUtilities.isEventDispatchThread()) {
      action.run();
    } else {
      final Exception[] exception = new Exception[1];
      SwingUtilities.invokeAndWait(() -> {
        try {
          action.run();
        } catch (Exception e) {
          exception[0] = e;
        }
      });
      if (exception[0] != null) {
        throw exception[0];
      }
    }
  }

  @FunctionalInterface
  private interface ThrowingRunnable {
    void run() throws Exception;
  }

  private void setTextField(String fieldName, String value) throws Exception {
    Field field = SocialPostGui.class.getDeclaredField(fieldName);
    field.setAccessible(true);
    JTextField textField = (JTextField) field.get(gui);
    textField.setText(value);
  }

  @SuppressWarnings("unchecked")
  private JComboBox<PostType> getComboBox(String fieldName) throws Exception {
    Field field = SocialPostGui.class.getDeclaredField(fieldName);
    field.setAccessible(true);
    return (JComboBox<PostType>) field.get(gui);
  }

  private AbstractButton findButton(String text) {
    return findComponent(gui.getContentPane(), AbstractButton.class, text);
  }

  private <T extends Component> T findComponent(Container container, Class<T> clazz, String text) {
    for (Component component : container.getComponents()) {
      if (clazz.isInstance(component)) {
        if (component instanceof AbstractButton && ((AbstractButton) component).getText().equals(text)) {
          return clazz.cast(component);
        }
      }
      if (component instanceof Container) {
        T found = findComponent((Container) component, clazz, text);
        if (found != null) {
          return found;
        }
      }
    }
    return null;
  }
}
