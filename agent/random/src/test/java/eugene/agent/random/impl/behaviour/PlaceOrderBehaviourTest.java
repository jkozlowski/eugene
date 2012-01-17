package eugene.agent.random.impl.behaviour;

import org.testng.annotations.Test;

/**
 * Tests {@link PlaceOrderBehaviour}.
 *
 * @author Jakub D Kozlowski
 * @since 0.5
 */
public class PlaceOrderBehaviourTest {
    
    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullAgent() {
        new PlaceOrderBehaviour(null);
    }
}
