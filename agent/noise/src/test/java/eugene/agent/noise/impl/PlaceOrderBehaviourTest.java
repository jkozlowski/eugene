package eugene.agent.noise.impl;

import eugene.market.book.OrderBook;
import eugene.market.client.Session;
import eugene.market.ontology.field.enums.Side;
import eugene.market.ontology.message.NewOrderSingle;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.Test;

import static eugene.agent.noise.BetweenMatcher.between;
import static eugene.agent.noise.impl.PlaceOrderBehaviour.DEFAULT_CURRENT_PRICE;
import static eugene.agent.noise.impl.PlaceOrderBehaviour.DEFAULT_TICK;
import static eugene.agent.noise.impl.PlaceOrderBehaviour.MAX_ORDER_QTY;
import static eugene.agent.noise.impl.PlaceOrderBehaviour.MAX_SPREAD;
import static eugene.agent.noise.impl.PlaceOrderBehaviour.MIN_ORDER_QTY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests {@link PlaceOrderBehaviour}.
 *
 * @author Jakub D Kozlowski
 * @since 0.5
 */
public class PlaceOrderBehaviourTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullOrderBook() {
        new PlaceOrderBehaviour(null, mock(Session.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullSession() {
        new PlaceOrderBehaviour(mock(OrderBook.class), null);
    }

    @Test
    public void testOnTickOrderBookEmpty() {
        final OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.isEmpty(any(Side.class))).thenReturn(true);
        final Session session = mock(Session.class);
        final PlaceOrderBehaviour behaviour = new PlaceOrderBehaviour(orderBook, session);

        behaviour.onTick();

        final ArgumentCaptor<NewOrderSingle> captor = ArgumentCaptor.forClass(NewOrderSingle.class);
        verify(session).send(captor.capture());
        verify(orderBook).isEmpty(any(Side.class));

        final NewOrderSingle newOrderSingle = captor.getValue();
        assertThat(newOrderSingle.getSymbol(), nullValue());
        assertThat(newOrderSingle.getOrderQty().getValue(), is(between((long) MIN_ORDER_QTY,
                                                                       (long) MAX_ORDER_QTY)));
        assertThat(newOrderSingle.getPrice().getValue(),
                   is(between(DEFAULT_CURRENT_PRICE - MAX_SPREAD * DEFAULT_TICK,
                              DEFAULT_CURRENT_PRICE + MAX_SPREAD * DEFAULT_TICK
                   )));
        
    }


}
