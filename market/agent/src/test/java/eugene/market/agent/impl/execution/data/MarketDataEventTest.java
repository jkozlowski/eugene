/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.agent.impl.execution.data;

import eugene.market.agent.impl.execution.Execution;
import eugene.market.agent.impl.execution.data.MarketDataEvent;
import eugene.market.agent.impl.execution.data.MarketDataEventHandler;
import eugene.market.book.OrderStatus;
import eugene.market.ontology.field.enums.OrdType;
import org.testng.annotations.Test;

import static eugene.market.book.MockOrders.buy;
import static eugene.market.book.MockOrders.ordType;
import static eugene.market.book.MockOrders.order;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests {@link MarketDataEvent}.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public class MarketDataEventTest {

    private final Object object = mock(Object.class);

    private final MarketDataEvent marketDataEvent = new TestMarketDataEvent(Long.MIN_VALUE, Long.MAX_VALUE,
                                                                            object);

    public static final class TestMarketDataEvent extends MarketDataEvent<Object> {

        public TestMarketDataEvent(final Long eventId, final Long time,
                                   final Object object) {
            super(eventId, time, object);
        }

        @Override
        public void accept(MarketDataEventHandler handler) {
        }
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullEventId() {
        new TestMarketDataEvent(null, Long.MAX_VALUE, mock(Object.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullTime() {
        new TestMarketDataEvent(Long.MIN_VALUE, null, mock(Object.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullObject() {
        new TestMarketDataEvent(Long.MIN_VALUE, Long.MAX_VALUE, null);
    }

    @Test
    public void testGetEventId() {
        assertThat(marketDataEvent.getEventId(), is(Long.MIN_VALUE));
    }

    @Test
    public void testGetTime() {
        assertThat(marketDataEvent.getTime(), is(Long.MAX_VALUE));
    }

    @Test
    public void testGetObject() {
        assertThat(marketDataEvent.getObject(), sameInstance(object));
    }
    
    @Test
    public void testConstructorCoverage() {
        new MarketDataEvent.ExecutionEvent(Long.MIN_VALUE, mock(Execution.class));
        new MarketDataEvent.ExecutionEvent(Long.MIN_VALUE, Long.MAX_VALUE, mock(Execution.class));
        new MarketDataEvent.CancelOrderEvent(Long.MIN_VALUE, mock(OrderStatus.class));
        new MarketDataEvent.CancelOrderEvent(Long.MIN_VALUE, Long.MAX_VALUE, mock(OrderStatus.class));
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddOrderEventConstructorNotLimit() {
        new MarketDataEvent.AddOrderEvent(Long.MIN_VALUE, new OrderStatus(order(ordType(buy(), OrdType.MARKET))));
    }

    @Test
    public void testAcceptCoverage() {
        final MarketDataEvent.ExecutionEvent executionEvent = new MarketDataEvent.ExecutionEvent(Long.MIN_VALUE, mock(Execution.class));
        final MarketDataEventHandler executionEventHandler = mock(MarketDataEventHandler.class);
        executionEvent.accept(executionEventHandler);
        verify(executionEventHandler).handle(executionEvent);
        
        final MarketDataEvent.CancelOrderEvent cancelOrderEvent = new MarketDataEvent.CancelOrderEvent(Long.MIN_VALUE, mock(OrderStatus.class));
        final MarketDataEventHandler cancelOrderEventHandler = mock(MarketDataEventHandler.class);
        cancelOrderEvent.accept(cancelOrderEventHandler);
        verify(cancelOrderEventHandler).handle(cancelOrderEvent);
    }
}
