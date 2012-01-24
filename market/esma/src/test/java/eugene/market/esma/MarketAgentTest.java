package eugene.market.esma;

import org.testng.annotations.Test;

/**
 * Tests {@link MarketAgent}.
 * 
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class MarketAgentTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullSymbol() {
        new MarketAgent(null);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorEmptySymbol() {
        new MarketAgent("");
    }
}
