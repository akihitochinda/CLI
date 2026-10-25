package option2;

import java.io.IOException;

/**
 * DESIGN PATTERN: Factory Method (Product)
 *
 * Defines the common interface for all analyzer modes. Each concrete mode
 * (interactive or batch) encapsulates its own run logic behind this single
 * method, so sentimentAnalyzer.main can call mode.run() without knowing which mode it is working with.
 *
 * This is appropriate because the two modes differ only in their behavior,
 * not in how they are set up or invoked. The factory produces the right
 * product; the client just runs it.
 */
public interface AnalyzerMode {

  /**
   * Executes the analyzer mode (interactive loop or batch report).
   *
   * @throws InvalidArgumentsException if a review file cannot be read
   * @throws IOException               if a report file cannot be written
   */
  void run() throws InvalidArgumentsException, IOException;
}
