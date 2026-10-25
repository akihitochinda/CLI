package problem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Records error and warning messages generated during program execution.
 * Messages are stored in the order they were logged and can be retrieved
 * or printed for diagnostics.
 */
public class ErrorLogger {

  private List<String> log;

  /**
   * Constructs an empty ErrorLogger.
   */
  public ErrorLogger() {
    this.log = new ArrayList<>();
  }

  /**
   * Adds a message to the log.
   *
   * @param message the error or warning message to record; must not be null
   * @throws IllegalArgumentException if message is null
   */
  public void log(String message) {
    if (message == null) {
      throw new IllegalArgumentException("Log message must not be null.");
    }
    this.log.add(message);
  }

  /**
   * Returns whether the log contains any messages.
   *
   * @return true if no messages have been logged, false otherwise
   */
  public boolean isEmpty() {
    return this.log.isEmpty();
  }

  /**
   * Returns an unmodifiable view of all logged messages.
   * The list is in the order messages were logged.
   *
   * @return an unmodifiable list of log messages
   */
  public List<String> getMessages() {
    return Collections.unmodifiableList(this.log);
  }

  /**
   * Prints all logged messages to standard error, one per line.
   * Does nothing if the log is empty.
   */
  public void printAll() {
    for (String msg : this.log) {
      System.err.println("[ERROR] " + msg);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ErrorLogger that = (ErrorLogger) o;
    return Objects.equals(log, that.log);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(log);
  }

  @Override
  public String toString() {
    return "ErrorLogger{log=" + log + '}';
  }
}