package eugene.market.esma.execution.data;

import eugene.market.esma.execution.book.Order;
import eugene.market.esma.execution.book.OrderBook;

/**
 * Indicates that a new {@link Order} has been inserted into the {@link OrderBook}.
 */
public final class NewOrderEvent extends MarketDataEvent<Order> {

    /**
     * {@inheritDoc}
     */
    public NewOrderEvent(final Long eventId, final Order order) {
        this(eventId, System.nanoTime(), order);
    }

    /**
     * {@inheritDoc}
     */
    public NewOrderEvent(final Long eventId, final Long time, final Order order) {
        super(eventId, time, order);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(MarketDataEventHandler handler) {
        handler.handle(this);
    }
}
