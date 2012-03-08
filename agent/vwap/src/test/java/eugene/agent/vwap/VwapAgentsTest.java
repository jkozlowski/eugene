/*
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */

package eugene.agent.vwap;

import eugene.agent.vwap.impl.VwapAgent;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

/**
 * Tests {@link VwapAgents}.
 *
 * @author Jakub D Kozlowski
 * @since 0.8
 */
public class VwapAgentsTest {

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testConstructor() {
        new VwapAgents();
    }

    @Test
    public void testVwapNoError() {
        assertThat(VwapAgents.vwapNoError(mock(VwapExecution.class)), is(VwapAgent.class));
    }

    @Test
    public void testVwapError() {
        assertThat(VwapAgents.vwapError(mock(VwapExecution.class)), is(VwapAgent.class));
    }
}
