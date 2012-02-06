package eugene.agent.noise.impl;

import eugene.market.book.OrderBook;
import eugene.market.client.Session;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;

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
}
