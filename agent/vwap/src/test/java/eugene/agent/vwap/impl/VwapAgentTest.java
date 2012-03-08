/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.agent.vwap.impl;

import eugene.agent.vwap.VwapExecution;
import eugene.agent.vwap.impl.state.State;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;

/**
 * Tests {@link VwapAgent}.
 * 
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public class VwapAgentTest {
    
    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullVwapExecution() {
        new VwapAgent(null, mock(State.class));
    }
    
    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullSendLimitState() {
        new VwapAgent(mock(VwapExecution.class), null);
    }
}
