package problem2;

/**
 * The type Fast pay adapter.
 * 
 * Adapter pattern: allows incompatible interfaces to work together by converting the interface of one class into another expected by clients.
 * FastPayAdapter implements the PaymentProcessor interface and internally uses FastPayProcessor to perform the actual payment processing.
 * This allows the existing code that relies on PaymentProcessor to use FastPay without modification.
 */
public class FastPayAdapter implements PaymentProcessor {

  private FastPayProcessor fastPay = new FastPayProcessor();

  @Override
  public boolean charge(String memberName, double amount) {
    return fastPay.initiateCharge(memberName, amount * 100);
 }


  @Override
  public String fetchTransactionLog(String memberName) {
    return fastPay.fetchTransactionLog(memberName);
  }
}
