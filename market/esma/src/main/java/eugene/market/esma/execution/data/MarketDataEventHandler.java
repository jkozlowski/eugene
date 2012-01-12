package eugene.market.esma.execution.data;

/**
 * Interface for handling {@link MarketDataEvent}s.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public interface MarketDataEventHandler {

    /**
     * Handles {@link NewOrderEvent}.
     *
     * @param newOrderEvent {@link NewOrderEvent} to handle.
     */
    public void handle(final NewOrderEvent newOrderEvent);

    /**
     * Handles {@link ExecutionEvent}.
     *
     * @param executionEvent {@link ExecutionEvent} to handle.
     */
    public void handle(final ExecutionEvent executionEvent);

    /**
     * Handles {@link CancelOrderEvent}.
     *
     * @param cancelOrderEvent {@link CancelOrderEvent} to handle.
     */
    public void handle(final CancelOrderEvent cancelOrderEvent);
}
