package eugene.market.esma.execution.data;

import eugene.market.book.Order;
import eugene.market.book.OrderStatus;

/**
 * Indicates that an {@link Order} has been cancelled.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public class CancelOrderEvent extends MarketDataEvent<OrderStatus> {

    /**
     * {@inheritDoc}
     */
    public CancelOrderEvent(final Long eventId, final OrderStatus orderStatus) {
        super(eventId, System.nanoTime(), orderStatus);
    }

    /**
     * {@inheritDoc}
     */
    public CancelOrderEvent(final Long eventId, final Long time, final OrderStatus orderStatus) {
        super(eventId, time, orderStatus);
    }

    @Override
    public void accept(final MarketDataEventHandler handler) {
        handler.handle(this);
    }
}
