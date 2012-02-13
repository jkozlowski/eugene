package eugene.market.client;

import eugene.market.book.OrderBook;
import eugene.market.client.impl.OrderBookApplication;
import eugene.market.client.impl.ProxyApplicationImpl;
import eugene.market.client.impl.TopOfBookPrinter;
import eugene.market.ontology.Message;

/**
 * Factory methods for creating {@link Application}s.
 *
 * @author Jakub D Kozlowski
 * @since 0.5
 */
public abstract class Applications {

    /**
     * Gets a default {@link ProxyApplication} implementation.
     *
     * @param applications {@link Application}s to proxy {@link Message}s to.
     *
     * @return proxy for <code>applications</code>.
     *
     * @throws NullPointerException     if <code>applications</code> is null.
     * @throws IllegalArgumentException if <code>applications</code> is empty.
     */
    public static ProxyApplication proxy(final Application... applications) throws NullPointerException,
                                                                        IllegalArgumentException {
        return new ProxyApplicationImpl(applications);
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
    public static Application orderBookApplication(final OrderBook orderBook) throws NullPointerException {
        return new OrderBookApplication(orderBook);
    }

    /**
     * Gets an {@link Application} that will print the top of this <code>orderBook</code>.
     *
     * @param orderBook {@link OrderBook} to check.
     *
     * @return {@link Application} that will print the top of this <code>orderBook</code>.
     *
     * @throws NullPointerException if <code>orderBook</code> is null.
     */
    public static Application topOfBookPrinterApplication(final OrderBook orderBook) throws NullPointerException {
        return new TopOfBookPrinter(orderBook);
    }
}
