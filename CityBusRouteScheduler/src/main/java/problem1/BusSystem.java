package problem1;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Bus system.
 */
public class BusSystem {

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    // create two routes
    Route route1 = new Route(12,
        "Downtown Loop", 40, List.of(2,9),
        List.of("Queen Anne Hill", "Lower Queen Anne", "3rd Ave & Cedar St",
            "3rd Ave & Union St", "St Jackson St & 5th Ave", "westLake Station",
            "Pine Street", "John St", "Terry Ave", "westLake ave"));
    // the second route
    Route route2 = new Route(2,
        "Northgate loop", 40, List.of(1,7),
        List.of("Northgate Station", "Lake City", "40th Ave",
            "35th Ave", "30th Ave", "northGate Station",
            "Northgate ave", "25th Ave", "125th st", "120th st"));
    // creates a BusNetwork
    List<Route> routes = new ArrayList<>();
    routes.add(route1);
    routes.add(route2);
    BusNetwork busNetwork = new BusNetwork("Seattle", routes);
    // it calls schedulerService
    SchedulerService schedulerService = new SchedulerService();
    schedulerService.command(busNetwork);
  }
}
