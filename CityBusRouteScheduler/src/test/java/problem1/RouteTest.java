package problem1;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class RouteTest {

    @Test
    void testValidateRoute_Valid() {
        List<String> stopNames = List.of("Stop1", "Stop2");
        List<Integer> expressStops = List.of(1);
        assertDoesNotThrow(() -> new Route(1, "Route1", 10, expressStops, stopNames));
    }

    @Test
    void testValidateRoute_Invalid_LessThanTwoStops() {
        List<String> stopNames = List.of("Stop1");
        List<Integer> expressStops = List.of(1);
        assertThrows(IllegalArgumentException.class, () -> new Route(1, "Route1", 10, expressStops, stopNames));
    }

    @Test
    void testValidateRoute_Invalid_NoExpressStops() {
        List<String> stopNames = List.of("Stop1", "Stop2");
        List<Integer> expressStops = List.of();
        assertThrows(IllegalArgumentException.class, () -> new Route(1, "Route1", 10, expressStops, stopNames));
    }

    @Test
    void testValidateCapacity_Valid() {
        List<String> stopNames = List.of("Stop1", "Stop2");
        List<Integer> expressStops = List.of(1);
        assertDoesNotThrow(() -> new Route(1, "Route1", 1, expressStops, stopNames));
    }

    @Test
    void testValidateCapacity_Invalid() {
        List<String> stopNames = List.of("Stop1", "Stop2");
        List<Integer> expressStops = List.of(1);
        assertThrows(IllegalArgumentException.class, () -> new Route(1, "Route1", 0, expressStops, stopNames));
    }

    @Test
    void testToString() {
        List<String> stopNames = List.of("Stop1", "Stop2");
        List<Integer> expressStops = List.of(1);
        Route route = new Route(1, "Route1", 10, expressStops, stopNames);
        String expected = "Route{routeNumber=1, routeName='Route1', maxPassengerCapacity=10, expressStopNumbers=[1]}";
        assertEquals(expected, route.toString());
    }

    @Test
    void testEquals() {
        List<String> stopNames = List.of("Stop1", "Stop2");
        List<Integer> expressStops = List.of(1);
        Route route1 = new Route(1, "Route1", 10, expressStops, stopNames);
        Route route2 = new Route(1, "Route1", 10, expressStops, stopNames);
        assertEquals(route1, route2);
    }

    @Test
    void testHashCode() {
        List<String> stopNames = List.of("Stop1", "Stop2");
        List<Integer> expressStops = List.of(1);
        Route route1 = new Route(1, "Route1", 10, expressStops, stopNames);
        Route route2 = new Route(1, "Route1", 10, expressStops, stopNames);
        assertEquals(route1.hashCode(), route2.hashCode());
    }
}