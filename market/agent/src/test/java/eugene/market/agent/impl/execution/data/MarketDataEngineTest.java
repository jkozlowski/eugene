/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.agent.impl.execution.data;

import eugene.market.agent.impl.execution.Execution;
import eugene.market.agent.impl.execution.data.MarketDataEngine;
import eugene.market.agent.impl.execution.data.MarketDataEvent;
import eugene.market.book.Order;
import eugene.market.book.OrderStatus;
import org.testng.annotations.Test;

import static eugene.market.book.MockOrders.buy;
import static eugene.market.book.MockOrders.order;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;

/**
 * Tests {@link MarketDataEngine}.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public class MarketDataEngineTest {

    @Test
    public void testNewOrder() {
        final MarketDataEngine marketDataEngine = new MarketDataEngine();
        final Order order = order(buy());
        final OrderStatus orderStatus = new OrderStatus(order);
        final Long currentEventId = marketDataEngine.getCurrentEventId();

        marketDataEngine.newOrder(orderStatus);

        assertThat(marketDataEngine.getMarketDataEvent(currentEventId), is(MarketDataEvent.NewOrderEvent.class));
        final MarketDataEvent.NewOrderEvent newOrderEvent = (MarketDataEvent.NewOrderEvent) marketDataEngine.getMarketDataEvent(currentEventId);
        assertThat(newOrderEvent.getEventId(), is(currentEventId));
        assertThat(newOrderEvent.getObject(), sameInstance(orderStatus));
    }

    @Test
    public void testExecution() {
        final MarketDataEngine marketDataEngine = new MarketDataEngine();
        final Execution execution = mock(Execution.class);
        final Long currentEventId = marketDataEngine.getCurrentEventId();

        marketDataEngine.execution(execution);

        assertThat(marketDataEngine.getMarketDataEvent(currentEventId), is(MarketDataEvent.ExecutionEvent.class));
        final MarketDataEvent.ExecutionEvent newOrderEvent = (MarketDataEvent.ExecutionEvent) marketDataEngine.getMarketDataEvent(currentEventId);
        assertThat(newOrderEvent.getEventId(), is(currentEventId));
        assertThat(newOrderEvent.getObject(), is(execution));
    }

    @Test
    public void testCancel() {
        final MarketDataEngine marketDataEngine = new MarketDataEngine();
        final OrderStatus orderStatus = mock(OrderStatus.class);
        final Long currentEventId = marketDataEngine.getCurrentEventId();

        marketDataEngine.cancel(orderStatus);

        assertThat(marketDataEngine.getMarketDataEvent(currentEventId), is(MarketDataEvent.CancelOrderEvent.class));
        final MarketDataEvent.CancelOrderEvent cancelOrderEvent = (MarketDataEvent.CancelOrderEvent) marketDataEngine.getMarketDataEvent(currentEventId);
        assertThat(cancelOrderEvent.getEventId(), is(currentEventId));
        assertThat(cancelOrderEvent.getObject(), is(orderStatus));
    }
}
