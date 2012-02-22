/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.esma.impl.execution.data;

/**
 * Interface for handling {@link MarketDataEvent}s.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public interface MarketDataEventHandler {

    /**
     * Handles {@link MarketDataEvent.NewOrderEvent}.
     *
     * @param newOrderEvent {@link MarketDataEvent.NewOrderEvent} to handle.
     */
    public void handle(final MarketDataEvent.NewOrderEvent newOrderEvent);

    /**
     * Handles {@link MarketDataEvent.RejectOrderEvent}.
     *
     * @param rejectOrderEvent {@link MarketDataEvent.RejectOrderEvent} to handle.
     */
    public void handle(final MarketDataEvent.RejectOrderEvent rejectOrderEvent);

    /**
     * Handles {@link MarketDataEvent.AddOrderEvent}.
     *
     * @param addOrderEvent {@link MarketDataEvent.AddOrderEvent} to handle.
     */
    public void handle(final MarketDataEvent.AddOrderEvent addOrderEvent);

    /**
     * Handles {@link MarketDataEvent.ExecutionEvent}.
     *
     * @param executionEvent {@link MarketDataEvent.ExecutionEvent} to handle.
     */
    public void handle(final MarketDataEvent.ExecutionEvent executionEvent);

    /**
     * Handles {@link MarketDataEvent.CancelOrderEvent}.
     *
     * @param cancelOrderEvent {@link MarketDataEvent.CancelOrderEvent} to handle.
     */
    public void handle(final MarketDataEvent.CancelOrderEvent cancelOrderEvent);
}
