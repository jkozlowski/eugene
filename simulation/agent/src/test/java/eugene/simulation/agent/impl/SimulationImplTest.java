/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.simulation.agent.impl;

import eugene.simulation.agent.Simulation;
import eugene.simulation.agent.Symbol;
import jade.core.AID;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;

/**
 * Tests {@link SimulationImpl}.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class SimulationImplTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullSimulationAgent() {
        new SimulationImpl(null, mock(AID.class), mock(Symbol.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullMarketAgent() {
        new SimulationImpl(mock(AID.class), null, mock(Symbol.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullSymbol() {
        new SimulationImpl(mock(AID.class), mock(AID.class), null);
    }

    @Test
    public void testConstructor() {
        final AID simulationAgent = mock(AID.class);
        final AID marketAgent = mock(AID.class);
        final Symbol symbol = mock(Symbol.class);
        final Simulation simulation = new SimulationImpl(simulationAgent, marketAgent, symbol);

        assertThat(simulation.getSimulationAgent(), sameInstance(simulationAgent));
        assertThat(simulation.getMarketAgent(), sameInstance(marketAgent));
        assertThat(simulation.getSymbol(), sameInstance(symbol));
    }
}
