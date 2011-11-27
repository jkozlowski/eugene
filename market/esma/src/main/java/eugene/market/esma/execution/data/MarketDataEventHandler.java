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
     * Handles {@link TradeEvent}.
     * 
     * @param tradeEvent {@link TradeEvent} to handle.
     */
    public void handle(final TradeEvent tradeEvent);
}
