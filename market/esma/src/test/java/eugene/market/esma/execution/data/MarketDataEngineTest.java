package eugene.market.esma.execution.data;

import eugene.market.book.Order;
import eugene.market.esma.execution.Execution;
import org.testng.annotations.Test;

import static eugene.market.book.MockOrders.buy;
import static eugene.market.book.MockOrders.order;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests {@link MarketDataEngine}.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public class MarketDataEngineTest {

    @Test
    public void testGetMarketDataEngineNewOrderEvent() {
        final MarketDataEngine marketDataEngine = new MarketDataEngine();
        final Order order = order(buy());
        final Long currentEventId = marketDataEngine.getCurrentEventId();

        marketDataEngine.newOrder(order);

        assertThat(marketDataEngine.getMarketDataEvent(currentEventId), is(NewOrderEvent.class));
        final NewOrderEvent newOrderEvent = (NewOrderEvent) marketDataEngine.getMarketDataEvent(currentEventId);
        assertThat(newOrderEvent.getEventId(), is(currentEventId));
        assertThat(newOrderEvent.getObject(), is(order));
    }

    @Test
    public void testGetMarketDataEngineTradeEvent() {
        final MarketDataEngine marketDataEngine = new MarketDataEngine();
        final Execution execution = mock(Execution.class);
        final Long currentEventId = marketDataEngine.getCurrentEventId();

        marketDataEngine.execution(execution);

        assertThat(marketDataEngine.getMarketDataEvent(currentEventId), is(ExecutionEvent.class));
        final ExecutionEvent newOrderEvent = (ExecutionEvent) marketDataEngine.getMarketDataEvent(currentEventId);
        assertThat(newOrderEvent.getEventId(), is(currentEventId));
        assertThat(newOrderEvent.getObject(), is(execution));
    }
}
