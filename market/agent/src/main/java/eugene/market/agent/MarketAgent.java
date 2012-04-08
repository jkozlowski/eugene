/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.agent;

import eugene.market.agent.impl.Repository;
import eugene.market.agent.impl.behaviours.MarketDataServer;
import eugene.market.agent.impl.behaviours.OrderServer;
import eugene.market.agent.impl.behaviours.SimulationOntologyServer;
import eugene.market.agent.impl.execution.ExecutionEngine;
import eugene.market.ontology.MarketOntology;
import eugene.simulation.ontology.SimulationOntology;
import jade.core.Agent;
import jade.core.behaviours.OntologyServer;
import jade.core.behaviours.ThreadedBehaviourFactory;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static jade.lang.acl.ACLMessage.REQUEST;

/**
 * Implements the Agent that acts as a Stock Market Exchange Order Matching Engine.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public class MarketAgent extends Agent {

    public static final String AGENT_NAME = "market-agent";

    private final String symbol;

    private final ExecutionEngine executionEngine;

    private final Repository repository;

    /**
     * Creates a {@link MarketAgent} that will handle this <code>symbol</code>.
     *
     * @param symbol symbol to handle.
     */
    public MarketAgent(final String symbol) {
        checkNotNull(symbol);
        checkArgument(!symbol.isEmpty());
        this.symbol = symbol;
        this.executionEngine = new ExecutionEngine();
        this.repository = new Repository();
    }

    @Override
    public void setup() {
        getContentManager().registerLanguage(MarketOntology.getCodec());
        getContentManager().registerLanguage(SimulationOntology.getCodec());
        getContentManager().registerOntology(MarketOntology.getInstance());
        getContentManager().registerOntology(SimulationOntology.getInstance());

        final SimulationOntologyServer simulationServer = new SimulationOntologyServer(this);
        final OrderServer orderServer = new OrderServer(this, executionEngine, repository, symbol);
        final ThreadedBehaviourFactory factory = new ThreadedBehaviourFactory();
        final MarketDataServer dataServer = new MarketDataServer(this, executionEngine.getMarketDataEngine(),
                                                                 repository, symbol);

        addBehaviour(new OntologyServer(this, SimulationOntology.getInstance(), REQUEST, simulationServer));
        addBehaviour(new OntologyServer(this, MarketOntology.getInstance(), REQUEST, orderServer));
        addBehaviour(factory.wrap(dataServer));
    }
}
