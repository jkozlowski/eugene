package eugene.market.esma.execution.data;

import eugene.market.book.Order;
import eugene.market.book.OrderBook;
import eugene.market.book.OrderStatus;
import eugene.market.book.TradeReport;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * {@link MarketDataEngine} that caches all events in memory.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public class MarketDataEngine {

    private static final AtomicLong currentEventId = new AtomicLong(1L);

    private final Map<Long, MarketDataEvent> events = new HashMap<Long, MarketDataEvent>();

    /**
     * Indicates that an new {@link Order} has been inserted into the {@link OrderBook}.
     *
     * @param order new {@link Order}.
     */
    public void newOrder(final Order order) {
        final Long eventId = currentEventId.getAndIncrement();
        final NewOrderEvent event = new NewOrderEvent(eventId, order);
        events.put(eventId, event);
    }

    /**
     * Indicates that a pair of {@link Order}s has been executed.
     *
     * @param tradeReport summary of the trade.
     */
    public void trade(final TradeReport tradeReport) {
        final Long eventId = currentEventId.getAndIncrement();
        final TradeEvent event = new TradeEvent(eventId, tradeReport);
        events.put(eventId, event);
    }

    /**
     * Indicates that an {@link Order} has been cancelled.
     *
     * @param orderStatus summary of the {@link Order}'s execution.
     */
    public void cancel(final OrderStatus orderStatus) {
        final Long eventId = currentEventId.getAndIncrement();
        final CancelOrderEvent event = new CancelOrderEvent(eventId, orderStatus);
        events.put(eventId, event);
    }

    /**
     * Gets a {@link MarketDataEvent} with this <code>eventId</code>.
     *
     * @param eventId <code>eventId</code> of {@link MarketDataEvent} to get.
     *
     * @return {@link MarketDataEvent} with this <code>eventId</code> or null if no such event.
     */
    public MarketDataEvent<?> getMarketDataEvent(final Long eventId) {
        return events.get(eventId);
    }

    /**
     * Gets the currentEventId.
     *
     * @return the currentEventId.
     */
    public Long getCurrentEventId() {
        return currentEventId.get();
    }
}
