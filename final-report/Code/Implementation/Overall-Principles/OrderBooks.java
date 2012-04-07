public final class OrderBooks {
    
  /**
   * Gets an instance of the default, not thread-safe implementation of 
   * {@link OrderBook}.
   *
   * @return instance of default {@link OrderBook}.
   */
  public static OrderBook defaultOrderBook() {
    return new DefaultOrderBook();
  }

  // The rest of the factory.
}
