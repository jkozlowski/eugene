/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.book.impl;

import eugene.market.book.Order;
import eugene.market.book.OrderBook;
import eugene.market.book.OrderStatus;
import eugene.market.ontology.field.enums.Side;

import java.math.BigDecimal;
import java.util.SortedSet;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Read-Only implementation of {@link OrderBook}, that delegates read method calls to an underlying delegate and
 * throws {@link UnsupportedOperationException} from the write method calls.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public final class ReadOnlyOrderBook implements OrderBook {

    public static final String ERROR_MESSAGE = "This order book is read-only.";

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

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException this {@link OrderBook} is read-only.
     */
    @Override
    public OrderStatus insert(Order order) throws UnsupportedOperationException {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException this {@link OrderBook} is read-only.
     */
    @Override
    public OrderStatus insert(Order order, OrderStatus orderStatus) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException this {@link OrderBook} is read-only.
     */
    @Override
    public OrderStatus execute(Side side, Long orderQty, BigDecimal price) throws NullPointerException,
                                                                              IllegalArgumentException,
                                                                              UnsupportedOperationException {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnsupportedOperationException this {@link OrderBook} is read-only.
     */
    @Override
    public OrderStatus cancel(Order order) throws UnsupportedOperationException {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size(Side side) {
        return delegate.size(side);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty(Side side) {
        return delegate.isEmpty(side);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Order peek(Side side) {
        return delegate.peek(side);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderStatus getOrderStatus(Order order) {
        return delegate.getOrderStatus(order);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SortedSet<Order> getBuyOrders() {
        return delegate.getBuyOrders();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SortedSet<Order> getSellOrders() {
        return delegate.getSellOrders();
    }
}
