/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.client;

import eugene.market.client.impl.OrderReferenceListenerProxyImpl;

/**
 * Factory for instances of {@link OrderReferenceListener}s.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public final class OrderReferenceListeners {

    private static final String ERROR_MSG = "This class should not be instantiated";

    /**
     * This class should not be instantiated.
     *
     * @throws UnsupportedOperationException this class should not be instantiated.
     */
    public OrderReferenceListeners() {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    /**
     * Returns a proxy {@link OrderReferenceListener} for <code>listeners</code>.
     *
     * @param listeners listeners to proxy to.
     *
     * @return proxy for <code>listeners</code>.
     */
    public static OrderReferenceListenerProxy proxy(final OrderReferenceListener... listeners) {
        return new OrderReferenceListenerProxyImpl(listeners);
    }
}
