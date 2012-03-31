/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.book.impl;

import com.google.common.base.Optional;
import com.google.common.primitives.Longs;
import eugene.market.book.Order;
import eugene.market.book.OrderBook;
import eugene.market.book.OrderStatus;
import eugene.market.ontology.field.enums.Side;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.SortedSet;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Sets.newTreeSet;

/**
 * Default implementation of {@link OrderBook}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public class DefaultOrderBook implements OrderBook {

    private final Map<Order, OrderStatus> orderStatusMap;

    private final Queue<Order> buyOrders;

    private final Queue<Order> sellOrders;

    /**
     * Creates an empty {@link DefaultOrderBook}.
     */
    public DefaultOrderBook() {
        this.buyOrders = new PriorityQueue<Order>();
        this.sellOrders = new PriorityQueue<Order>();
        this.orderStatusMap = new HashMap<Order, OrderStatus>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderStatus insert(final Order order) {
        return insert(order, new OrderStatus(order));
    }

    @Override
    public OrderStatus insert(final Order order, final OrderStatus orderStatus) {
        checkNotNull(order);
        checkNotNull(orderStatus);
        checkArgument(orderStatus.getOrder().equals(order));
        checkArgument(order.getOrdType().isLimit());
        checkArgument(getQueue(order.getSide()).offer(order));
        checkArgument(null == orderStatusMap.put(order, orderStatus));
        return orderStatus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderStatus execute(final Side side, final Long orderQty, final BigDecimal price) {

        checkNotNull(side);
        checkNotNull(orderQty);
        checkNotNull(price);

        checkArgument(price.compareTo(Order.NO_PRICE) == 1);
        checkArgument(Longs.compare(orderQty, Order.NO_QTY) == 1);

        final Optional<Order> order = peek(side);
        checkState(order.isPresent());

        final Optional<OrderStatus> oldOrderStatus = getOrderStatus(order.get());
        checkState(oldOrderStatus.isPresent());

        final OrderStatus newOrderStatus = oldOrderStatus.get().execute(price, orderQty);

        if (newOrderStatus.isFilled()) {
            final Order removedOrder = getQueue(side).poll();
            checkState(order.get().equals(removedOrder));
            orderStatusMap.remove(order.get());
        }
        else {
            orderStatusMap.put(order.get(), newOrderStatus);
        }

        return newOrderStatus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<OrderStatus> cancel(final Order order) {
        checkNotNull(order);
        getQueue(order.getSide()).remove(order);
        final OrderStatus orderStatus = orderStatusMap.remove(order);
        if (null != orderStatus) {
            return Optional.of(orderStatus.cancel());
        }
        return Optional.absent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size(final Side side) {
        checkNotNull(side);
        return getQueue(side).size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty(final Side side) {
        checkNotNull(side);
        return getQueue(side).isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Order> peek(final Side side) {
        checkNotNull(side);
        return Optional.fromNullable(getQueue(side).peek());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<OrderStatus> getOrderStatus(final Order order) {
        return Optional.fromNullable(orderStatusMap.get(order));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SortedSet<Order> getBuyOrders() {
        return newTreeSet(this.buyOrders);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SortedSet<Order> getSellOrders() {
        return newTreeSet(this.sellOrders);
    }

    private Queue<Order> getQueue(final Side side) {
        checkNotNull(side);
        return side.isSell() ? sellOrders : buyOrders;
    }

    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();

        for (Order o : getSellOrders()) {
            b.append(o).append("\n");
        }

        for (Order o : getBuyOrders()) {
            b.append(o).append("\n");
        }

        return b.toString();
    }
}
