package eugene.market.esma.impl.execution.data;

import eugene.market.book.Order;
import eugene.market.book.OrderBook;
import eugene.market.book.OrderStatus;
import eugene.market.esma.impl.execution.Execution;
import eugene.market.esma.impl.execution.ExecutionEngine;
import eugene.market.ontology.field.enums.OrdType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * {@link MarketDataEngine} that caches all events in memory.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public class MarketDataEngine {

    private static final AtomicLong currentEventId = new AtomicLong(1L);

    private final Map<Long, MarketDataEvent> events = new ConcurrentHashMap<Long, MarketDataEvent>();

    /**
     * Indicates that a new {@link Order} has been accepted by {@link ExecutionEngine}.
     *
     * @param orderStatus status of a new {@link Order}.
     */
    public void newOrder(final OrderStatus orderStatus) {
        final Long eventId = currentEventId.getAndIncrement();
        final MarketDataEvent.NewOrderEvent event = new MarketDataEvent.NewOrderEvent(eventId, orderStatus);
        events.put(eventId, event);
    }

    /**
     * Indicates that a {@link OrdType#MARKET} has been rejected by the {@link ExecutionEngine}.
     *
     * @param orderStatus status rejected {@link Order}.
     */
    public void reject(final OrderStatus orderStatus) {
        final Long eventId = currentEventId.getAndIncrement();
        final MarketDataEvent.RejectOrderEvent event = new MarketDataEvent.RejectOrderEvent(eventId, orderStatus);
        events.put(eventId, event);
    }

    /**
     * Indicates that a pair of {@link Order}s has been executed.
     *
     * @param execution summary of the execution.
     */
    public void execution(final Execution execution) {
        final Long eventId = currentEventId.getAndIncrement();
        final MarketDataEvent.ExecutionEvent event = new MarketDataEvent.ExecutionEvent(eventId, execution);
        events.put(eventId, event);
    }

    /**
     * Indicates that a new {@link Order} has been inserted into the {@link OrderBook}.
     *
     * @param orderStatus new {@link Order}.
     */
    public void addOrder(final OrderStatus orderStatus) {
        final Long eventId = currentEventId.getAndIncrement();
        final MarketDataEvent.AddOrderEvent event = new MarketDataEvent.AddOrderEvent(eventId, orderStatus);
        events.put(eventId, event);
    }

    /**
     * Indicates that an {@link Order} has been cancelled.
     *
     * @param orderStatus summary of the {@link Order}'s execution.
     */
    public void cancel(final OrderStatus orderStatus) {
        final Long eventId = currentEventId.getAndIncrement();
        final MarketDataEvent.CancelOrderEvent event = new MarketDataEvent.CancelOrderEvent(eventId, orderStatus);
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
