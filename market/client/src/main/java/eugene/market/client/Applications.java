package eugene.market.client;

import eugene.market.book.OrderBook;
import eugene.market.client.impl.OrderBookApplication;
import eugene.market.client.impl.ProxyApplication;
import eugene.market.esma.Messages;

/**
 * Factory methods for creating {@link Application}s.
 *
 * @author Jakub D Kozlowski
 * @since 0.5
 */
public final class Applications {

    private Applications() {
    }

    /**
     * Gets an {@link Application} that will proxy {@link Messages}s to <code>applications</code>.
     *
     * @param applications {@link Application}s to proxy {@link Messages} to.
     *
     * @return proxy for <code>applications</code>.
     *
     * @throws NullPointerException     if <code>applications</code> is null.
     * @throws IllegalArgumentException if <code>applications</code> is empty.
     */
    public static Application proxy(final Application... applications) throws NullPointerException,
                                                                              IllegalArgumentException {
        return new ProxyApplication(applications);
    }

    /**
     * Gets an {@link Application} that will build this <code>orderBook</code>.
     *
     * @param orderBook {@link OrderBook} to build.
     *
     * @return {@link Application} that will build this <code>orderBook</code>.
     *
     * @throws NullPointerException if <code>orderBook</code> is null.
     */
    public static Application orderBook(final OrderBook orderBook) throws NullPointerException {
        return new OrderBookApplication(orderBook);
    }
}
