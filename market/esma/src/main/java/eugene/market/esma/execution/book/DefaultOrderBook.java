package eugene.market.esma.execution.book;

import eugene.market.ontology.field.enums.Side;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.primitives.Doubles.compare;
import static com.google.common.primitives.Longs.max;

/**
 * Default implementation of {@link OrderBook}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public class DefaultOrderBook implements OrderBook {

    public static final String SEPARATOR = "*****************\n";

    private final Map<Order, OrderStatus> executionReportMap;

    private final Queue<Order> buyOrders;

    private final Queue<Order> sellOrders;

    /**
     * Creates an empty {@link DefaultOrderBook}.
     */
    public DefaultOrderBook() {
        this.buyOrders = new PriorityQueue<Order>();
        this.sellOrders = new PriorityQueue<Order>();
        this.executionReportMap = new HashMap<Order, OrderStatus>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderStatus insertOrder(final Order order) {
        checkNotNull(order);
        getQueue(order.getSide()).offer(order);
        final OrderStatus orderStatus = new OrderStatus(order);
        executionReportMap.put(order, orderStatus);
        return orderStatus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TradeReport execute(final Double price) {
        checkNotNull(price);
        checkArgument(compare(price, Order.NO_PRICE) == 1);
        checkState(!buyOrders.isEmpty());
        checkState(!sellOrders.isEmpty());

        final Order buyOrder = peek(Side.BUY);
        final Order sellOrder = peek(Side.SELL);

        final Long quantity = max(buyOrder.getOrderQty(), sellOrder.getOrderQty());

        final OrderStatus buyOrderStatus = getExecutionReport(buyOrder).execute(price, quantity);
        final OrderStatus sellOrderStatus = getExecutionReport(sellOrder).execute(price, quantity);

        if (buyOrderStatus.isFilled()) {
            getQueue(Side.BUY).poll();
            executionReportMap.remove(buyOrder);
        }
        
        if (sellOrderStatus.isFilled()) {
            getQueue(Side.SELL).poll();
            executionReportMap.remove(sellOrder);
        }
        
        return new TradeReport(buyOrderStatus, sellOrderStatus, price, quantity);
    }

    @Override
    public OrderStatus cancel(final Order order) {
        checkNotNull(order);
        getQueue(order.getSide()).remove(order);
        return executionReportMap.remove(order);
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
    public OrderStatus getExecutionReport(final Order order) {
        return executionReportMap.get(order);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Order[] getBuyOrders() {
        final Order[] buyOrders = this.buyOrders.toArray(new Order[this.buyOrders.size()]);
        Arrays.sort(buyOrders);
        return buyOrders;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Order[] getSellOrders() {
        final Order[] sellOrders = this.sellOrders.toArray(new Order[this.sellOrders.size()]);
        Arrays.sort(sellOrders);
        return sellOrders;
    }

    private Queue<Order> getQueue(final Side side) {
        if (side.isSell()) {
            return sellOrders;
        }
        return buyOrders;
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
