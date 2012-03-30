/**
 * Entry for a single order in an {@link OrderBook}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public class Order implements Comparable<Order> {

  // All the fields are declared final.
  private final Long orderID;
 
  public Order(final Long orderID, /* other parameters */) { 
    // Constructor implementation.
  }


  // Getter without a corresponding setter.
  /**
   * Gets the orderID.
   *
   * @return the orderID.
   */
  public Long getOrderID() {
    return orderID;
  }

  // The rest of the class.
}
