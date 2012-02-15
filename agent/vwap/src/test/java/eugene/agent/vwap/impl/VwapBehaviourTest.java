package eugene.agent.vwap.impl;

import eugene.agent.vwap.VwapExecution;
import eugene.market.book.OrderBook;
import eugene.market.client.Session;
import org.testng.annotations.Test;

import java.util.Calendar;

import static org.mockito.Mockito.mock;

/**
 * Tests {@link VwapBehaviour}.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public class VwapBehaviourTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullDeadline() {
        new VwapBehaviour(null, mock(VwapExecution.class), mock(OrderBook.class), mock(Session.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullVwapExecution() {
        new VwapBehaviour(Calendar.getInstance(), null, mock(OrderBook.class), mock(Session.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullOrderBook() {
        new VwapBehaviour(Calendar.getInstance(), mock(VwapExecution.class), null, mock(Session.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullSession() {
        new VwapBehaviour(Calendar.getInstance(), mock(VwapExecution.class), mock(OrderBook.class), null);
    }
}
