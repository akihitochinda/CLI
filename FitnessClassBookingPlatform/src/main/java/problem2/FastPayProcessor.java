package problem2;

/**
 * The type Fast pay processor.
 */
// This is a fixed, external interface you cannot modify.
class FastPayProcessor {

  /**
   * Initiate charge boolean.
   *
   * @param memberName the member name, as String
   * @param cents      the cents, as double
   * @return the boolean
   */
  public boolean initiateCharge(String memberName, double cents) {
    // Simulated payment gateway behavior.
    // In real life this would talk to FastPay's API.
    return cents > 0 && !memberName.isBlank();
  }

  /**
   * Fetch transaction log string.
   *
   * @param memberName the member name, as String
   * @return the string
   */
  public String fetchTransactionLog(String memberName) {
    return "FastPay transactions for " + memberName;
  }
}
