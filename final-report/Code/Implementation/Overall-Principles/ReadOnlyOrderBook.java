package eugene.market.book.impl;

/* Unneccessary imports ommitted. */

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Read-Only implementation of {@link OrderBook}, that delegates read method 
 * calls to an underlying delegate and throws {@link UnsupportedOperationException} 
 * from the write method calls.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public final class ReadOnlyOrderBook implements OrderBook {

  private final OrderBook delegate;

  /**
   * Creates a {@link ReadOnlyOrderBook} that will delegate to <code>delegate</code>.
   *
   * @param delegate {@link OrderBook} instance to delegate to.
   */
  public ReadOnlyOrderBook(final OrderBook delegate) {
    checkNotNull(delegate);
    this.delegate = delegate;
  }

  /* The rest of the implementation follows */
}

