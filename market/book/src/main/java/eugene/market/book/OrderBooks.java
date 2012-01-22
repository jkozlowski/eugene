package eugene.market.book;

import com.google.common.annotations.VisibleForTesting;
import eugene.market.book.impl.DefaultOrderBook;
import eugene.market.book.impl.ReadOnlyOrderBook;

/**
 * Factory for creating {@link OrderBook}s.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public final class OrderBooks {

    public static final String ERROR_MESSAGE = "This class should not be instantiated";

    /**
     * This constructor should not be invoked. It is only visible for the purposes of keeping global test coverage
     * high.
     *
     * @throws UnsupportedOperationException this constructor should not be invoked.
     */
    @VisibleForTesting
    public OrderBooks() throws UnsupportedOperationException {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    /**
     * Gets an instance of the default, not thread-safe implementation of {@link OrderBook}.
     *
     * @return instance of default {@link OrderBook}.
     */
    public static OrderBook defaultOrderBook() {
        return new DefaultOrderBook();
    }

    /**
     * Gets an instance of a read-only implementation of {@link OrderBook}, that delegates read method calls to
     * <code>delegate</code> and throws {@link UnsupportedOperationException} from the write method calls.
     *
     * @param delegate {@link OrderBook} instance to delegate to.
     *
     * @return instance of a read-only {@link OrderBook}.
     *
     * @throws NullPointerException if <code>delegate</code> is null.
     */
    public static OrderBook readOnlyOrderBook(final OrderBook delegate) throws NullPointerException {
        return new ReadOnlyOrderBook(delegate);
    }
}
