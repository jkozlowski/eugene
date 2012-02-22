/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.client;

/**
 * Proxies {@link OrderReference} events to {@link OrderReferenceListener}s.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public interface OrderReferenceListenerProxy extends OrderReferenceListener {

    /**
     * Adds <code>listener</code> to this proxy.
     *
     * @param listener {@link OrderReferenceListener} to add.
     *
     * @return <tt>true</tt> if added.
     */
    boolean addListener(OrderReferenceListener listener);

    /**
     * Removes <code>listener</code> from this proxy.
     *
     * @param listener {@link OrderReferenceListener} to remove.
     *
     * @return <tt>true</tt> if this proxy contained <code>listener</code>.
     */
    boolean removeListener(OrderReferenceListener listener);
}
