package eugene.simulation.agent.impl;

import eugene.simulation.agent.Simulation;
import jade.core.AID;
import org.testng.annotations.Test;

import static eugene.market.ontology.Defaults.defaultSymbol;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
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
        new SimulationImpl(null, mock(AID.class), defaultSymbol);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullMarketAgent() {
        new SimulationImpl(mock(AID.class), null, defaultSymbol);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullSymbol() {
        new SimulationImpl(mock(AID.class), mock(AID.class), null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorEmptySymbol() {
        new SimulationImpl(mock(AID.class), mock(AID.class), "");
    }
    
    @Test
    public void testConstructor() {
        final AID simulationAgent = mock(AID.class);
        final AID marketAgent = mock(AID.class);
        final Simulation simulation = new SimulationImpl(simulationAgent, marketAgent, defaultSymbol);
        
        assertThat(simulation.getSimulationAgent(), sameInstance(simulationAgent));
        assertThat(simulation.getMarketAgent(), sameInstance(marketAgent));
        assertThat(simulation.getSymbol(), is(defaultSymbol));
    }
}
