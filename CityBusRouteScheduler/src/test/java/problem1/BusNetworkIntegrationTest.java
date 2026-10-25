package problem1;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BusNetworkIntegrationTest {

  private final PrintStream originalOut = System.out;
  private final java.io.InputStream originalIn = System.in;
  private ByteArrayOutputStream outContent;

  @BeforeEach
  void setUp() {
    outContent = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outContent));
  }

  @AfterEach
  void tearDown() {
    System.setOut(originalOut);
    System.setIn(originalIn);
  }

  @Test
  void schedulerServiceShouldProcessBoardAndShowCommands() {
    // Set up BusNetwork like in BusSystem.main()
    Route route1 = new Route(12,
        "Downtown Loop", 40, List.of(2,9),
        List.of("Queen Anne Hill", "Lower Queen Anne", "3rd Ave & Cedar St",
            "3rd Ave & Union St", "St Jackson St & 5th Ave", "westLake Station",
            "Pine Street", "John St", "Terry Ave", "westLake ave"));
    Route route2 = new Route(2,
        "Northgate loop", 40, List.of(1,7),
        List.of("Northgate Station", "Lake City", "40th Ave",
            "35th Ave", "30th Ave", "northGate Station",
            "Northgate ave", "25th Ave", "125th st", "120th st"));
    List<Route> routes = new ArrayList<>();
    routes.add(route1);
    routes.add(route2);
    BusNetwork busNetwork = new BusNetwork("Seattle", routes);

    // Simulate user input
    String userInput = String.join(System.lineSeparator(),
        "board 12",
        "Alice",
        "yes",
        "2",
        "9",
        "show 12",
        "routes",
        "done") + System.lineSeparator();

    System.setIn(new ByteArrayInputStream(userInput.getBytes()));

    // Create SchedulerService and run command
    SchedulerService schedulerService = new SchedulerService();
    schedulerService.command(busNetwork);

    String output = outContent.toString();
    assertTrue(output.contains("What would you like to do?"), "Should prompt for commands.");
    assertTrue(output.contains("Passenger name?"), "Board command should prompt for name.");
    assertTrue(output.contains("Is this an express trip?"), "Board command should prompt for express trip.");
    assertTrue(output.contains("Boarding at which stop?"), "Board command should prompt for boarding stop.");
    assertTrue(output.contains("Destination stop?"), "Board command should prompt for destination.");
    assertTrue(output.contains("Alice has boarded Route 12"), "Board command should confirm booking.");
    assertTrue(output.contains("Route 12 – Downtown Loop"), "Show command should display route details.");
    assertTrue(output.contains("Available routes:"), "Routes command should list routes.");
    assertTrue(output.contains("Safe travels!"), "Done command should end with goodbye message.");
  }

  @Test
  void boardCommandShouldHandleInvalidInputs() {
    Route route1 = new Route(12,
        "Downtown Loop", 40, List.of(2,9),
        List.of("Queen Anne Hill", "Lower Queen Anne", "3rd Ave & Cedar St",
            "3rd Ave & Union St", "St Jackson St & 5th Ave", "westLake Station",
            "Pine Street", "John St", "Terry Ave", "westLake ave"));
    List<Route> routes = new ArrayList<>();
    routes.add(route1);
    BusNetwork busNetwork = new BusNetwork("Seattle", routes);

    String userInput = String.join(System.lineSeparator(),
        "board 99", // Invalid route
        "Alice",
        "yes",
        "1",
        "5",
        "done") + System.lineSeparator();

    System.setIn(new ByteArrayInputStream(userInput.getBytes()));

    SchedulerService schedulerService = new SchedulerService();
    schedulerService.command(busNetwork);

    String output = outContent.toString();
    assertTrue(output.contains("Sorry, Route 99 does not exist."), "Should reject invalid route.");
  }

  @Test
  void schedulerServiceShouldHandleInvalidExpressAnswer() {
    // Set up BusNetwork
    Route route1 = new Route(12,
        "Downtown Loop", 40, List.of(2,9),
        List.of("Queen Anne Hill", "Lower Queen Anne", "3rd Ave & Cedar St",
            "3rd Ave & Union St", "St Jackson St & 5th Ave", "westLake Station",
            "Pine Street", "John St", "Terry Ave", "westLake ave"));
    List<Route> routes = List.of(route1);
    BusNetwork busNetwork = new BusNetwork("Seattle", routes);

    // Simulate user input with invalid express answer
    String userInput = String.join(System.lineSeparator(),
        "board 12",
        "Alice",
        "maybe",
        "done") + System.lineSeparator();

    ByteArrayInputStream in = new ByteArrayInputStream(userInput.getBytes());
    System.setIn(in);

    SchedulerService schedulerService = new SchedulerService();
    schedulerService.command(busNetwork);

    String output = outContent.toString();
    assertTrue(output.contains("Sorry, please answer yes or no."), "Should handle invalid express answer.");
    assertTrue(output.contains("Safe travels!"), "Done command should end with goodbye message.");
  }

  @Test
  void schedulerServiceShouldHandleInvalidExpressTrip() {
    // Set up BusNetwork
    Route route1 = new Route(12,
        "Downtown Loop", 40, List.of(2,9),
        List.of("Queen Anne Hill", "Lower Queen Anne", "3rd Ave & Cedar St",
            "3rd Ave & Union St", "St Jackson St & 5th Ave", "westLake Station",
            "Pine Street", "John St", "Terry Ave", "westLake ave"));
    List<Route> routes = List.of(route1);
    BusNetwork busNetwork = new BusNetwork("Seattle", routes);

    // Simulate user input with express trip but non-express destination
    String userInput = String.join(System.lineSeparator(),
        "board 12",
        "Alice",
        "yes",
        "2",
        "3",  // 3 is not express
        "done") + System.lineSeparator();

    ByteArrayInputStream in = new ByteArrayInputStream(userInput.getBytes());
    System.setIn(in);

    SchedulerService schedulerService = new SchedulerService();
    schedulerService.command(busNetwork);

    String output = outContent.toString();
    assertTrue(output.contains("Sorry, destination stop 3 is not an express stop."), "Should validate express trip stops.");
    assertTrue(output.contains("Safe travels!"), "Done command should end with goodbye message.");
  }

  @Test
  void schedulerServiceShouldHandleInvalidBoardingStop() {
    Route route1 = new Route(12,
        "Downtown Loop", 40, List.of(2,9),
        List.of("Queen Anne Hill", "Lower Queen Anne", "3rd Ave & Cedar St",
            "3rd Ave & Union St", "St Jackson St & 5th Ave", "westLake Station",
            "Pine Street", "John St", "Terry Ave", "westLake ave"));
    BusNetwork busNetwork = new BusNetwork("Seattle", List.of(route1));

    String userInput = String.join(System.lineSeparator(),
        "board 12",
        "Alice",
        "yes",
        "11",
        "2",
        "done") + System.lineSeparator();

    System.setIn(new ByteArrayInputStream(userInput.getBytes()));

    SchedulerService schedulerService = new SchedulerService();
    schedulerService.command(busNetwork);

    String output = outContent.toString();
    assertTrue(output.contains("Sorry, stop 11 does not exist on this route."), "Should reject invalid boarding stop.");
  }

  @Test
  void schedulerServiceShouldHandleInvalidDestinationStop() {
    Route route1 = new Route(12,
        "Downtown Loop", 40, List.of(2,9),
        List.of("Queen Anne Hill", "Lower Queen Anne", "3rd Ave & Cedar St",
            "3rd Ave & Union St", "St Jackson St & 5th Ave", "westLake Station",
            "Pine Street", "John St", "Terry Ave", "westLake ave"));
    BusNetwork busNetwork = new BusNetwork("Seattle", List.of(route1));

    String userInput = String.join(System.lineSeparator(),
        "board 12",
        "Alice",
        "no",
        "2",
        "11",
        "done") + System.lineSeparator();

    System.setIn(new ByteArrayInputStream(userInput.getBytes()));

    SchedulerService schedulerService = new SchedulerService();
    schedulerService.command(busNetwork);

    String output = outContent.toString();
    assertTrue(output.contains("Sorry, stop 11 does not exist on this route."), "Should reject invalid destination stop.");
  }

  @Test
  void schedulerServiceShouldHandleInvalidDirection() {
    Route route1 = new Route(12,
        "Downtown Loop", 40, List.of(2,9),
        List.of("Queen Anne Hill", "Lower Queen Anne", "3rd Ave & Cedar St",
            "3rd Ave & Union St", "St Jackson St & 5th Ave", "westLake Station",
            "Pine Street", "John St", "Terry Ave", "westLake ave"));
    BusNetwork busNetwork = new BusNetwork("Seattle", List.of(route1));

    String userInput = String.join(System.lineSeparator(),
        "board 12",
        "Alice",
        "no",
        "5",
        "3",
        "done") + System.lineSeparator();

    System.setIn(new ByteArrayInputStream(userInput.getBytes()));

    SchedulerService schedulerService = new SchedulerService();
    schedulerService.command(busNetwork);

    String output = outContent.toString();
    assertTrue(output.contains("Sorry, destination must come after boarding stop."), "Should reject invalid direction.");
  }

  @Test
  void schedulerServiceShouldHandleInvalidCapacity() {
    Route routeSmall = new Route(1,
        "Short Route", 1, List.of(1),
        List.of("Stop1", "Stop2", "Stop3"));
    routeSmall.findStop(2).addPassenger(new Passenger("Bob", 2, 3));
    BusNetwork busNetwork = new BusNetwork("City", List.of(routeSmall));

    String userInput = String.join(System.lineSeparator(),
        "board 1",
        "Alice",
        "no",
        "2",
        "3",
        "done") + System.lineSeparator();

    System.setIn(new ByteArrayInputStream(userInput.getBytes()));

    SchedulerService schedulerService = new SchedulerService();
    schedulerService.command(busNetwork);

    String output = outContent.toString();
    assertTrue(output.contains("Sorry, the bus is full between those stops."), "Should reject when route capacity is full.");
  }

  @Test
  void schedulerServiceShouldShowInvalidRoute() {
    Route route1 = new Route(12,
        "Downtown Loop", 40, List.of(2,9),
        List.of("Queen Anne Hill", "Lower Queen Anne", "3rd Ave & Cedar St",
            "3rd Ave & Union St", "St Jackson St & 5th Ave", "westLake Station",
            "Pine Street", "John St", "Terry Ave", "westLake ave"));
    BusNetwork busNetwork = new BusNetwork("Seattle", List.of(route1));

    String userInput = String.join(System.lineSeparator(),
        "show 99",
        "done") + System.lineSeparator();

    System.setIn(new ByteArrayInputStream(userInput.getBytes()));

    SchedulerService schedulerService = new SchedulerService();
    schedulerService.command(busNetwork);

    String output = outContent.toString();
    assertTrue(output.contains("Sorry, Route 99 does not exist."), "ShowCommand should reject invalid route.");
  }

  @Test
  void schedulerServiceShouldRejectExpressTripWhenBoardingStopIsNotExpress() {
    Route route1 = new Route(12,
        "Downtown Loop", 40, List.of(2,9),
        List.of("Queen Anne Hill", "Lower Queen Anne", "3rd Ave & Cedar St",
            "3rd Ave & Union St", "St Jackson St & 5th Ave", "westLake Station",
            "Pine Street", "John St", "Terry Ave", "westLake ave"));
    BusNetwork busNetwork = new BusNetwork("Seattle", List.of(route1));

    String userInput = String.join(System.lineSeparator(),
        "board 12",
        "Alice",
        "yes",
        "1",
        "9",
        "done") + System.lineSeparator();

    System.setIn(new ByteArrayInputStream(userInput.getBytes()));

    SchedulerService schedulerService = new SchedulerService();
    schedulerService.command(busNetwork);

    String output = outContent.toString();
    assertTrue(output.contains("Sorry, boarding stop 1 is not an express stop."), "Should reject express trip when boarding stop is not express.");
  }

  @Test
  void schedulerServiceShouldRejectNonExpressTripWhenBoardingStopIsExpress() {
    Route route1 = new Route(12,
        "Downtown Loop", 40, List.of(2,9),
        List.of("Queen Anne Hill", "Lower Queen Anne", "3rd Ave & Cedar St",
            "3rd Ave & Union St", "St Jackson St & 5th Ave", "westLake Station",
            "Pine Street", "John St", "Terry Ave", "westLake ave"));
    BusNetwork busNetwork = new BusNetwork("Seattle", List.of(route1));

    String userInput = String.join(System.lineSeparator(),
        "board 12",
        "Alice",
        "no",
        "2",
        "3",
        "done") + System.lineSeparator();

    System.setIn(new ByteArrayInputStream(userInput.getBytes()));

    SchedulerService schedulerService = new SchedulerService();
    schedulerService.command(busNetwork);

    String output = outContent.toString();
    assertTrue(output.contains("Sorry, boarding stop 2 is an express stop and requires an express trip."), "Should reject non-express trip when boarding stop is express.");
  }
}
