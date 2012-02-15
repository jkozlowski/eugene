package eugene.agent.vwap;

import org.testng.annotations.Test;

/**
 * Tests {@link VwapAgent}.
 * 
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public class VwapAgentTest {
    
    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullVwapExecution() {
        new VwapAgent(null);
    }
}
