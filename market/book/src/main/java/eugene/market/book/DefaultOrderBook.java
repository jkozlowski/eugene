package eugene.market.book;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Longs;
import eugene.market.ontology.field.enums.Side;

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

    public static final String SEPARATOR = "*****************\n";

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
    public OrderStatus insertOrder(final Order order) {
        checkNotNull(order);
        getQueue(order.getSide()).offer(order);
        final OrderStatus orderStatus = new OrderStatus(order);
        orderStatusMap.put(order, orderStatus);
        return orderStatus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderStatus execute(final Side side, final Long orderQty, final Double price) {

        checkNotNull(side);
        checkNotNull(orderQty);
        checkNotNull(price);

        checkArgument(Doubles.compare(price, Order.NO_PRICE) == 1);
        checkArgument(Longs.compare(orderQty, Order.NO_QTY) == 1);

        checkState(!getQueue(side).isEmpty());

        final Order order = peek(side);

        final OrderStatus newOrderStatus = getOrderStatus(order).execute(price, orderQty);

        if (newOrderStatus.isFilled()) {
            final Order removedOrder = getQueue(side).poll();
            checkState(order.equals(removedOrder));
            orderStatusMap.remove(order);
        }
        else {
            orderStatusMap.put(order, newOrderStatus);
        }

        return newOrderStatus;
    }

    @Override
    public OrderStatus cancel(final Order order) {
        checkNotNull(order);
        getQueue(order.getSide()).remove(order);
        return orderStatusMap.remove(order);
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
    public Order peek(final Side side) {
        checkNotNull(side);
        return getQueue(side).peek();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderStatus getOrderStatus(final Order order) {
        return orderStatusMap.get(order);
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

        b.append(SEPARATOR);

        for (Order o : getBuyOrders()) {
            b.append(o).append("\n");
        }

        return b.toString();
    }
}
