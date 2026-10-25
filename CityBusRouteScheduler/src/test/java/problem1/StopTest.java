package problem1;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

class StopTest {

    @Test
    void testPassengerSize() {
        List<Passenger> passengers = new ArrayList<>();
        passengers.add(new Passenger("Alice", 1, 2));
        Stop stop = new Stop(1, "Stop1", passengers);
        assertEquals(1, stop.passengerSize());
    }

    @Test
    void testToString() {
        List<Passenger> passengers = new ArrayList<>();
        Stop stop = new Stop(1, "Stop1", passengers);
        String expected = "Stop{stopNumber=1, stopName='Stop1', passengerList=[]}";
        assertEquals(expected, stop.toString());
    }

    @Test
    void testEquals() {
        List<Passenger> passengers1 = new ArrayList<>();
        List<Passenger> passengers2 = new ArrayList<>();
        Stop stop1 = new Stop(1, "Stop1", passengers1);
        Stop stop2 = new Stop(1, "Stop1", passengers2);
        assertEquals(stop1, stop2);
    }

    @Test
    void testHashCode() {
        List<Passenger> passengers1 = new ArrayList<>();
        List<Passenger> passengers2 = new ArrayList<>();
        Stop stop1 = new Stop(1, "Stop1", passengers1);
        Stop stop2 = new Stop(1, "Stop1", passengers2);
        assertEquals(stop1.hashCode(), stop2.hashCode());
    }
}