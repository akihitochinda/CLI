package problem1;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BusSystemTest {

  @BeforeEach
  void setUp() {
  }

  @Test
  void main() {
    String simulatedInput = "done\n";
    System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
    assertDoesNotThrow(() -> BusSystem.main(new String[]{}));
  }
}