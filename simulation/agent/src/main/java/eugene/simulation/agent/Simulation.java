package eugene.simulation.agent;

import eugene.market.esma.MarketAgent;
import jade.core.AID;
import jade.core.Agent;

import javax.annotation.concurrent.Immutable;

/**
 * Parameters of the simulation, passed from the {@link SimulationAgent} to Trader Agents and Market Agent. {@link
 * Simulation} instance can be retrieved using {@link Agent#getArguments()}.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
@Immutable
public interface Simulation {

    /**
     * Gets the {@link AID} of the {@link SimulationAgent}.
     *
     * @return {@link AID} of the {@link SimulationAgent}.
     */
    AID getSimulationAgent();

    /**
     * Gets the {@link AID} of the {@link MarketAgent}.
     *
     * @return {@link AID} of the {@link MarketAgent}.
     */
    AID getMarketAgent();

    /**
     * Gets the symbol for this {@link Simulation}.
     *
     * @return the symbol for this {@link Simulation}.
     */
    String getSymbol();
}
