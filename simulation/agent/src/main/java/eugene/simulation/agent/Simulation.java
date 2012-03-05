/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.simulation.agent;

import eugene.market.esma.MarketAgent;
import eugene.utils.annotation.Immutable;
import jade.core.AID;
import jade.core.Agent;

/**
 * Parameters of the simulation, passed from the {@link SimulationAgent} to Trader Agents. {@link
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
     * @return the symbol.
     */
    Symbol getSymbol();
}
