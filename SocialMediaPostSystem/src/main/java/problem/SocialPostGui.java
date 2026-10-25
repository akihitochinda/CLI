package problem;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * A simple Swing UI to drive the social post system and demonstrate patterns.
 */
public class SocialPostGui extends JFrame {

  private final SocialPostManager manager = new SocialPostManager();
  private final JTextArea outputArea = new JTextArea();
  private final JTextField contentField = new JTextField(20);
  private final JTextField authorField = new JTextField(20);
  private final JComboBox<PostType> typeCombo = new JComboBox<>(PostType.values());
  private final JTextField tagsField = new JTextField(20);
  private final JTextField templateField = new JTextField(15);

  public SocialPostGui() {
    this(true);
  }

  SocialPostGui(boolean redirectOutput) {
    super("Social Post GUI");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(new Dimension(900, 620));
    setLocationRelativeTo(null);
    buildUi();
    if (redirectOutput) {
      redirectSystemOutput();
    }
  }

  private void buildUi() {
    JPanel topPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(6, 6, 6, 6);
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;

    gbc.gridx = 0;
    gbc.gridy = 0;
    topPanel.add(new JLabel("Content:"), gbc);
    gbc.gridx = 1;
    topPanel.add(contentField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    topPanel.add(new JLabel("Author:"), gbc);
    gbc.gridx = 1;
    topPanel.add(authorField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 2;
    topPanel.add(new JLabel("Post Type:"), gbc);
    gbc.gridx = 1;
    topPanel.add(typeCombo, gbc);

    gbc.gridx = 0;
    gbc.gridy = 3;
    topPanel.add(new JLabel("Tags (# separated by comma):"), gbc);
    gbc.gridx = 1;
    topPanel.add(tagsField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 4;
    topPanel.add(new JLabel("Template name:"), gbc);
    gbc.gridx = 1;
    topPanel.add(templateField, gbc);

    JButton createButton = new JButton("Create Post");
    createButton.addActionListener(e -> createPost());
    JButton likeFeatureButton = new JButton("Add Like Feature");
    likeFeatureButton.addActionListener(e -> executeCommand(new AddFeatureCommand(manager, "like")));
    JButton shareFeatureButton = new JButton("Add Share Feature");
    shareFeatureButton.addActionListener(e -> executeCommand(new AddFeatureCommand(manager, "share")));
    JButton commentFeatureButton = new JButton("Add Comment Feature");
    commentFeatureButton.addActionListener(e -> executeCommand(new AddFeatureCommand(manager, "comment")));
    JButton publicStrategyButton = new JButton("Public Strategy");
    publicStrategyButton.addActionListener(e -> executeCommand(new StrategyCommand(manager, "public")));
    JButton friendsStrategyButton = new JButton("Friends Strategy");
    friendsStrategyButton.addActionListener(e -> executeCommand(new StrategyCommand(manager, "friends")));
    JButton privateStrategyButton = new JButton("Private Strategy");
    privateStrategyButton.addActionListener(e -> executeCommand(new StrategyCommand(manager, "private")));
    JButton saveTemplateButton = new JButton("Save Template");
    saveTemplateButton.addActionListener(e -> executeCommand(new SaveTemplateCommand(manager, templateField.getText().trim())));
    JButton cloneTemplateButton = new JButton("Clone Template");
    cloneTemplateButton.addActionListener(e -> executeCommand(new CloneCommand(manager, templateField.getText().trim())));
    JButton publishButton = new JButton("Publish");
    publishButton.addActionListener(e -> executeCommand(new PublishCommand(manager)));
    JButton likeButton = new JButton("Like");
    likeButton.addActionListener(e -> executeCommand(new LikeCommand(manager)));
    JButton shareButton = new JButton("Share");
    shareButton.addActionListener(e -> executeCommand(new ShareCommand(manager)));

    gbc.gridx = 0;
    gbc.gridy = 5;
    gbc.gridwidth = 2;
    JPanel buttonPanel = new JPanel();
    buttonPanel.add(createButton);
    buttonPanel.add(likeFeatureButton);
    buttonPanel.add(shareFeatureButton);
    buttonPanel.add(commentFeatureButton);
    buttonPanel.add(publicStrategyButton);
    buttonPanel.add(friendsStrategyButton);
    buttonPanel.add(privateStrategyButton);
    buttonPanel.add(saveTemplateButton);
    buttonPanel.add(cloneTemplateButton);
    buttonPanel.add(publishButton);
    buttonPanel.add(likeButton);
    buttonPanel.add(shareButton);
    topPanel.add(buttonPanel, gbc);

    outputArea.setEditable(false);
    JScrollPane scrollPane = new JScrollPane(outputArea);
    scrollPane.setPreferredSize(new Dimension(860, 380));

    add(topPanel, BorderLayout.NORTH);
    add(scrollPane, BorderLayout.CENTER);
  }

  private void createPost() {
    String content = contentField.getText().trim();
    String author = authorField.getText().trim();
    PostType type = (PostType) typeCombo.getSelectedItem();
    String tagsInput = tagsField.getText().trim();
    List<String> tags = tagsInput.isEmpty() ? List.of() : List.of(tagsInput.split("\\s*,\\s*"));
    manager.createPost(content, author, type, tags);
  }

  private void executeCommand(Command command) {
    if (command != null) {
      command.execute();
    } else {
      appendOutput("Invalid command.");
    }
  }

  private void appendOutput(String text) {
    outputArea.append(text + "\n");
    outputArea.setCaretPosition(outputArea.getDocument().getLength());
  }

  private void redirectSystemOutput() {
    TextAreaOutputStream textAreaOutputStream = new TextAreaOutputStream(outputArea);
    PrintStream printStream = new PrintStream(textAreaOutputStream, true);
    System.setOut(printStream);
    System.setErr(printStream);
  }

  private static final class TextAreaOutputStream extends OutputStream {

    private final JTextArea textArea;

    private TextAreaOutputStream(JTextArea textArea) {
      this.textArea = textArea;
    }

    @Override
    public void write(int b) throws IOException {
      textArea.append(String.valueOf((char) b));
      textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
      textArea.append(new String(b, off, len));
      textArea.setCaretPosition(textArea.getDocument().getLength());
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      SocialPostGui gui = new SocialPostGui();
      gui.setVisible(true);
    });
  }
}
