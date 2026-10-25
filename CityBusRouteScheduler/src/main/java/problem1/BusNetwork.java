package problem1;

import java.util.List;

/**
 * The type Bus network.
 */
public class BusNetwork {
  private String cityName;
  private List<Route> routeList;

  /**
   * Instantiates a new Bus network.
   *
   * @param cityName  the city name
   * @param routeList the route list
   */
  public BusNetwork(String cityName, List<Route> routeList) {
    this.cityName = cityName;
    this.routeList = routeList;
  }

  /**
   * Gets city name.
   *
   * @return the city name
   */
  public String getCityName() {
    return cityName;
  }

  /**
   * Gets route list.
   *
   * @return the route list
   */
  public List<Route> getRouteList() {
    return routeList;
  }

  /**
   * Find route by number route.
   *
   * @param number the number
   * @return the route
   */
  public Route findRouteByNumber(int number) {
    for (Route route : routeList) {
      if (route.getRouteNumber().equals(number)) {
        return route;
      }
    } return null;
  }
}
