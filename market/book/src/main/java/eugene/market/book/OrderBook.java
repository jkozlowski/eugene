/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.book;

import com.google.common.base.Optional;
import eugene.market.ontology.field.enums.OrdStatus;
import eugene.market.ontology.field.enums.OrdType;
import eugene.market.ontology.field.enums.Side;

import java.math.BigDecimal;
import java.util.SortedSet;

/**
 * Maintains lists of outstanding buy and sell {@link Order}s.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
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
    OrderStatus insert(final Order order) throws NullPointerException, IllegalArgumentException;

    /**
     * Inserts a partially executed <code>order</code> into this {@link OrderBook}.
     *
     * @param order       order to insert.
     * @param orderStatus status of execution of this <code>order</code>.
     *
     * @return status of this <code>order</code>.
     *
     * @throws NullPointerException     if <code>order</code> is null.
     * @throws IllegalArgumentException if <code>order</code> is {@link OrdType#MARKET} or <code>orderStatus</code>
     *                                  is not about this <code>order</code>.
     */
    OrderStatus insert(final Order order, final OrderStatus orderStatus)
            throws NullPointerException, IllegalArgumentException;

    /**
     * Executes the <code>orderQty</code> of the {@link Order} at the top of the book on this <code>side</code> at
     * this <code>price</code>.
     *
     * @param side     {@link Side} of the {@link Order} to execute.
     * @param orderQty quantity to execute.
     * @param price    price of execution.
     *
     * @return {@link OrderStatus} after the execution.
     *
     * @throws NullPointerException     if <code>side</code>, <code>orderQty</code> or <code>price</code> are
     *                                  <code>null</code>.
     * @throws IllegalArgumentException if <code>price <= {@link Order#NO_PRICE}</code>,
     *                                  or <code>orderQty <= {@link Order#NO_QTY}</code>,
     *                                  or <code>orderQty > {@link OrderStatus#getLeavesQty()}</code>
     *                                  or the {@link OrderBook} is empty on this <code>side</code>.
     */
    OrderStatus execute(final Side side, final Long orderQty, final BigDecimal price) throws NullPointerException,
                                                                                             IllegalArgumentException;

    /**
     * Cancels this <code>order</code>.
     *
     * @param order {@link Order} to cancel.
     *
     * @return {@link OrderStatus} of cancelled {@link Order} with {@link OrdStatus#CANCELED} or {@link
     *         Optional#absent()} if {@code order} does not exist.
     *
     * @throws NullPointerException if <code>order</code> is null.
     */
    Optional<OrderStatus> cancel(final Order order) throws NullPointerException;

    /**
     * Gets the size of the book on this <code>side</code>.
     *
     * @param side the size of the book will be returned for this <code>side</code>.
     *
     * @return size of the book on this <code>side</code>.
     */
    int size(final Side side);

    /**
     * Checks if the book is empty on this <code>side</code>.
     *
     * @param side the side of the book to check.
     *
     * @return <code>true</code> is this <code>side</code> of the book is empty, <code>false</code> otherwise.
     */
    boolean isEmpty(final Side side);

    /**
     * Gets the first {@link Order} on this {@code side}.
     *
     * @param side side of {@link Order} to get.
     *
     * @return first {@link Order} on this {@code side} or {@link Optional#absent()} if the book is empty on this
     *         {@code side}.
     */
    Optional<Order> peek(final Side side);

    /**
     * Gets the {@link OrderStatus} for this {@code order}.
     *
     * @param order {@link Order} to return the {@link OrderStatus} for.
     *
     * @return the {@link OrderStatus} for this {@code order} or {@link Optional#absent()} if this {@link Order} is not
     *         in this {@link OrderBook}.
     */
    Optional<OrderStatus> getOrderStatus(final Order order);

    /**
     * Gets a {@link SortedSet} of {@link Side#BUY} orders.
     *
     * @return {@link SortedSet} of {@link Side#BUY} orders.
     */
    SortedSet<Order> getBuyOrders();

    /**
     * Gets a {@link SortedSet} of {@link Side#SELL} orders.
     *
     * @return {@link SortedSet} of {@link Side#SELL} orders.
     */
    SortedSet<Order> getSellOrders();
}
