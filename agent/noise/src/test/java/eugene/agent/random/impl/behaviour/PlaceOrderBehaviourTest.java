package eugene.agent.random.impl.behaviour;

import eugene.market.book.OrderBook;
import eugene.market.client.api.Session;
import jade.core.Agent;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockObjectFactory;
import org.testng.IObjectFactory;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;

/**
 * Tests {@link PlaceOrderBehaviour}.
 *
 * @author Jakub D Kozlowski
 * @since 0.5
 */
@PrepareForTest(Session.class)
public class PlaceOrderBehaviourTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullAgent() {
        new PlaceOrderBehaviour(null, mock(OrderBook.class), mock(Session.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullOrderBook() {
        new PlaceOrderBehaviour(mock(Agent.class), null, mock(Session.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullSession() {
        new PlaceOrderBehaviour(mock(Agent.class), mock(OrderBook.class), null);
    }

    @ObjectFactory
    public IObjectFactory getObjectFactory() {
        return new PowerMockObjectFactory();
    }
}
