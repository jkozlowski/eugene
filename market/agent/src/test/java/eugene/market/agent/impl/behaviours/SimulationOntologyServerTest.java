/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.agent.impl.behaviours;

import eugene.market.agent.impl.behaviours.SimulationOntologyServer;
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
