package eugene.market.esma.execution.data;

import eugene.market.book.Order;
import eugene.market.book.TradeReport;

/**
 * Indicates that a pair of {@link Order}s have been executed.
 */
public final class TradeEvent extends MarketDataEvent<TradeReport> {

    /**
     * {@inheritDoc}
     */
    public TradeEvent(final Long eventId, final TradeReport tradeReport) {
        super(eventId, System.nanoTime(), tradeReport);
    }

    /**
     * {@inheritDoc}
     */
    public TradeEvent(final Long eventId, final Long time, final TradeReport tradeReport) {
        super(eventId, time, tradeReport);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(MarketDataEventHandler handler) {
        handler.handle(this);
    }
}
