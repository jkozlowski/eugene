/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.simulation.agent.impl;

import eugene.simulation.agent.Simulation;
import eugene.simulation.agent.Symbol;
import jade.core.AID;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default {@link Simulation} implementation.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public final class SimulationImpl implements Simulation {

    private final AID simulationAgent;

    private final AID marketAgent;

    private final Symbol symbol;

    public SimulationImpl(final AID simulationAgent, final AID marketAgent, final Symbol symbol) {
        checkNotNull(simulationAgent);
        checkNotNull(marketAgent);
        checkNotNull(symbol);
        this.simulationAgent = simulationAgent;
        this.marketAgent = marketAgent;
        this.symbol = symbol;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AID getSimulationAgent() {
        return simulationAgent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AID getMarketAgent() {
        return marketAgent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Symbol getSymbol() {
        return symbol;
    }
}
