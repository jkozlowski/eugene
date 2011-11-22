package eugene.market.esma.impl;

import com.google.common.annotations.VisibleForTesting;
import eugene.market.esma.MatchingEngine;
import eugene.market.esma.MatchingEngine.MatchingResult;
import eugene.market.esma.Order;
import eugene.market.esma.OrderBook;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Collection;
import java.util.PriorityQueue;
import java.util.Queue;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.unmodifiableCollection;

/**
 * Default implementation of {@link OrderBook}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public class OrderBookImpl implements OrderBook {

    private final MatchingEngine matchingEngine;

    private final PriorityQueue<Order> buyOrders;

    private final PriorityQueue<Order> sellOrders;

    private Double lastMarketPrice = Order.NO_PRICE;

    /**
     * Creates an {@link OrderBookImpl} that will use this <code>matchingEngine</code> and start with empty
     * <code>buyOrders</code> and <code>sellOrders</code> queues.
     *
     * @param matchingEngine {@link MatchingEngine} to use.
     */
    public OrderBookImpl(@NotNull final MatchingEngine matchingEngine) {
        this(matchingEngine, new PriorityQueue<Order>(), new PriorityQueue<Order>());
    }

    /**
     * Creates an {@link OrderBookImpl} that will use this <code>matchingEngine</code> and <code>buyOrders</code> and
     * <code>sellOrders</code> queues.
     *
     * @param matchingEngine {@link MatchingEngine} to use.
     * @param buyOrders      {@link Queue} for buy {@link Order}s.
     * @param sellOrders     {@link Queue} for sell {@link Order}s.
     */
    public OrderBookImpl(@NotNull final MatchingEngine matchingEngine, @NotNull final Queue<Order> buyOrders,
                         @NotNull final Queue<Order> sellOrders) {
        checkNotNull(buyOrders);
        checkNotNull(sellOrders);
        checkNotNull(matchingEngine);
        this.matchingEngine = matchingEngine;
        this.buyOrders = new PriorityQueue<Order>(buyOrders);
        this.sellOrders = new PriorityQueue<Order>(sellOrders);
    }

    /**
     * {@inheritDoc}
     */
    public boolean insertOrder(final @NotNull Order order) {
        checkNotNull(order);
        if (order.isSideSell()) {
            sellOrders.add(order);
        }
        else {
            buyOrders.add(order);
        }

        return Boolean.TRUE;
    }

    /**
     * Executes matching {@link Order}s in this {@link OrderBook}.
     *
     * @return set of executed {@link Order}s.
     */
    public void execute() {

        while (!buyOrders.isEmpty() && !sellOrders.isEmpty()) {

            final Order buyOrder = buyOrders.peek();
            final Order sellOrder = sellOrders.peek();
            final MatchingResult matchResult = matchingEngine.match(this, buyOrder, sellOrder);

            if (!matchResult.isMatch()) {
                break;
            }

            execute(buyOrder, sellOrder, matchResult.getPrice());

            if (buyOrder.isFilled()) {
                buyOrders.remove();
            }
            if (sellOrder.isFilled()) {
                sellOrders.remove();
            }
        }

    }

    private void execute(final Order buyOrder, final Order sellOrder, final Double price) {

        final Long quantity = buyOrder.getOpenQuantity() >= sellOrder.getOpenQuantity() ? sellOrder.getOpenQuantity()
                                                                                        : buyOrder.getOpenQuantity();

        buyOrder.execute(price, quantity);
        sellOrder.execute(price, quantity);
    }

    /**
     * {@inheritDoc}
     */
    @VisibleForTesting
    public Collection<Order> getBuyOrders() {
        return unmodifiableCollection(buyOrders);
    }

    /**
     * {@inheritDoc}
     */
    @VisibleForTesting
    public Collection<Order> getSellOrders() {
        return unmodifiableCollection(sellOrders);
    }

    @Override
    public Double getLastMarketPrice() {
        return lastMarketPrice;
    }

    @Override
    public void setLastMarketPrice(Double lastMarketPrice) {
        this.lastMarketPrice = lastMarketPrice;
    }

    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();

        final Order[] buyOrders = this.buyOrders.toArray(new Order[0]);
        Arrays.sort(buyOrders);

        final Order[] sellOrders = this.sellOrders.toArray(new Order[0]);
        Arrays.sort(sellOrders);

        b.append("Buys:\n");
        for (Order o : buyOrders) {
            b.append(o).append("\n");
        }

        b.append("Sells:\n");
        for (Order o : sellOrders) {
            b.append(o).append("\n");
        }

        return b.toString();
    }
}
