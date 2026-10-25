package problem;

/**
 * The interface Post observable.
 * Observer Pattern: an interface PostObservable with:
 * addObserver(PostObserver postObserver) method
 * removeObserver(PostObserver postObserver) method
 * notifyObservers() method update() method
 */
public interface PostObservable {

  /**
   * Adds an observer.
   *
   * @param postObserver the post observer
   */
  void addObserver(PostObserver postObserver);

  /**
   * Removes an observer.
   *
   * @param postObserver the post observer
   */
  void removeObserver(PostObserver postObserver);

  /**
   * Notifies all observers.
   */
  void notifyObservers();

}
