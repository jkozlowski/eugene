/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.client;

import eugene.market.book.OrderBook;
import eugene.market.client.impl.OrderBookApplication;
import eugene.market.client.impl.ProxyApplicationImpl;
import eugene.market.client.impl.TopOfBookApplicationImpl;
import eugene.market.client.impl.TopOfBookPrinter;
import eugene.market.ontology.Message;
import eugene.simulation.agent.Symbol;

/**
 * Factory methods for creating {@link Application}s.
 *
 * @author Jakub D Kozlowski
 * @since 0.5
 */
public final class Applications {

    private static final String ERROR_MSG = "This class should not be instantiated";

    /**
     * This class should not be instantiated.
     *
     * @throws UnsupportedOperationException this class should not be instantiated.
     */
    public Applications() {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

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

    /**
     * Gets a default {@link TopOfBookApplication} implementation.
     *
     * @param symbol symbol to use.
     *
     * @return default {@link TopOfBookApplication} implementation.
     *
     * @throws NullPointerException if <code>symbol</code> is null.
     */
    public static TopOfBookApplication topOfBookApplication(final Symbol symbol) throws NullPointerException {
        return new TopOfBookApplicationImpl(symbol);
    }
}
