/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
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
