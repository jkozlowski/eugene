package eugene.market.esma.impl.execution.data;

import eugene.market.book.Order;
import eugene.market.esma.impl.execution.Execution;

/**
 * Indicates that a pair of {@link Order}s have been executed.
 */
public final class ExecutionEvent extends MarketDataEvent<Execution> {

    /**
     * {@inheritDoc}
     */
    public ExecutionEvent(final Long eventId, final Execution execution) {
        this(eventId, System.nanoTime(), execution);
    }

    /**
     * {@inheritDoc}
     */
    public ExecutionEvent(final Long eventId, final Long time, final Execution execution) {
        super(eventId, time, execution);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void accept(MarketDataEventHandler handler) {
        handler.handle(this);
    }
}
