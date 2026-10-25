package problem;

/**
 * Exception thrown if invalid arguments are provided at the command  line.
 */
public class InvalidArgumentsException extends Exception {
  public InvalidArgumentsException(String message) {
    super(message);
  }
}