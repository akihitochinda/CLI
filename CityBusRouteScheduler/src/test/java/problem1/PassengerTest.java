package problem1;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PassengerTest {

    @Test
    void testGetName() {
        Passenger passenger = new Passenger("Alice", 1, 2);
        assertEquals("Alice", passenger.getName());
    }

    @Test
    void testToString() {
        Passenger passenger = new Passenger("Alice", 1, 2);
        String expected = "Passenger{name='Alice', boardingNumber=1, destinationNumber=2}";
        assertEquals(expected, passenger.toString());
    }

    @Test
    void testEquals() {
        Passenger p1 = new Passenger("Alice", 1, 2);
        Passenger p2 = new Passenger("Alice", 1, 2);
        assertEquals(p1, p2);
    }

    @Test
    void testHashCode() {
        Passenger p1 = new Passenger("Alice", 1, 2);
        Passenger p2 = new Passenger("Alice", 1, 2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }
}