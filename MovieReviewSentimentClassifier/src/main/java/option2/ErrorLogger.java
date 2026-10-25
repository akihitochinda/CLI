package option2;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The type Error logger.
 */
public class ErrorLogger {
    private List<String> log;

    /**
     * Instantiates a new Error logger.
     */
    public ErrorLogger() {
        this.log = new ArrayList<>();
    }

    /**
     * Log.
     *
     * @param event the event
     */
    public void log(String event) {
        this.log.add(event);
    }

    /**
     * Is empty boolean.
     *
     * @return the boolean
     */
    public boolean isEmpty() {
        return this.log.size() == 0;
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
        return Objects.hash(log);
    }

    @Override
    public String toString() {
        if (this.log.isEmpty())
            return "Empty log";
        String out = "";
        for (String entry: this.log) {
            out += entry + System.lineSeparator();
        }
        return out;
    }
}