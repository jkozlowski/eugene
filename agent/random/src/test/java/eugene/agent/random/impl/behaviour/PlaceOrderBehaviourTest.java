package eugene.agent.random.impl.behaviour;

import eugene.market.book.OrderBook;
import jade.core.Agent;
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
    public void testConstructorNullAgent() {
        new PlaceOrderBehaviour(null, mock(OrderBook.class));
    }
    
    @Test(expectedExceptions = NullPointerException.class) 
    public void testConstructorNullOrderBook() {
        new PlaceOrderBehaviour(mock(Agent.class), null);
    }
}
