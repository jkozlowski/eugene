package eugene.market.esma.impl.behaviours;

import org.testng.annotations.Test;

/**
 * Tests {@link SimulationOntologyServer}.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class SimulationOntologyServerTest {
    
    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullMarketAgent() {
        new SimulationOntologyServer(null);
    }
}
