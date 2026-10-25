package problem2;

/**
 * The interface Payment processor.
 */
public interface PaymentProcessor {

  /**
   * Charge boolean.
   *
   * @param memberName the member name, as String
   * @param amount     the amount, as double
   * @return the boolean
   */
  boolean charge(String memberName, double amount);

  /**
   * Fetch transaction log string.
   *
   * @param memberName the member name, as String
   * @return the string
   */
  String fetchTransactionLog(String memberName);
}
