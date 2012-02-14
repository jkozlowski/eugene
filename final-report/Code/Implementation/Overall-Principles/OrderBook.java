public interface OrderBook {

  /**
   * Inserts an unexecuted <code>order</code> into this {@link OrderBook}.
   *
   * @param order order to insert.
   *
   * @return status of this <code>order</code>.
   *
   * @throws NullPointerException     if <code>order</code> is null.
   * @throws IllegalArgumentException if <code>order</code> is {@link OrdType#MARKET}.
   */ 
  OrderStatus insert(final Order order) 
    throws NullPointerException, IllegalArgumentException;

  // The rest of the interface.
}
