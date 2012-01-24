package eugene.agent.noise;

import org.testng.annotations.Test;

/**
 * Tests {@link NoiseTraderAgent}.
 *
 * @author Jakub D Kozlowski
 * @since 0.5
 */
public class NoiseTraderAgentTest {
    
    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullSymbol() {
        new NoiseTraderAgent(null);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorEmptySymbol() {
        new NoiseTraderAgent("");
    }
}
