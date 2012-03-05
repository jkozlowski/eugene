/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.simulation.agent;

import com.google.common.base.Stopwatch;
import eugene.market.book.Order;
import eugene.market.esma.MarketAgent;
import eugene.market.ontology.MarketOntology;
import eugene.simulation.agent.impl.BootstrapOrderBookBehaviour;
import eugene.simulation.agent.impl.InitializeMarketAgentBehaviour;
import eugene.simulation.agent.impl.ReceiveLogonCompleteMessages;
import eugene.simulation.agent.impl.StartAgentsBehaviour;
import eugene.simulation.agent.impl.StartSimulationBehaviour;
import eugene.simulation.agent.impl.StopSimulationBehaviour;
import eugene.simulation.ontology.SimulationOntology;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implements the Simulation Agent that starts the Market Agent and Trader Agents and synchronises the start and
 * finish of the simulation.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class SimulationAgent extends Agent {

    private final Logger LOG = LoggerFactory.getLogger(SimulationAgent.class);

    public static final String NAME = "simulation-agent";

    private final Symbol symbol;

    private final int length;

    private final Set<Order> initialOrders;

    private final Set<Agent> agents;

    /**
     * Creates a {@link SimulationAgent} for this <code>symbol</code>, that will initialize the simulation with
     * <code>initialOrders</code> and will start <code>agents</code>.
     *
     * @param symbol        symbol for this simulation.
     * @param length        length of the simulation in milliseconds.
     * @param initialOrders orders to initialize the simulation with.
     * @param agents        {@link Agent}s to start.
     */
    public SimulationAgent(final Symbol symbol, int length, final Set<Order> initialOrders, final Set<Agent> agents) {
        checkNotNull(symbol);
        checkArgument(length > 0);
        checkNotNull(initialOrders);
        checkNotNull(agents);
        checkArgument(!agents.isEmpty());
        this.symbol = symbol;
        this.length = length;
        this.initialOrders = new HashSet(initialOrders);
        this.agents = new HashSet(agents);
    }

    @Override
    public void setup() {
        getContentManager().registerLanguage(MarketOntology.getCodec());
        getContentManager().registerLanguage(SimulationOntology.getCodec());
        getContentManager().registerOntology(MarketOntology.getInstance());
        getContentManager().registerOntology(SimulationOntology.getInstance());

        final Stopwatch stopwatch = new Stopwatch().start();

        final SequentialBehaviour initSequence = new SequentialBehaviour();

        final InitializeMarketAgentBehaviour initMarket
                = new InitializeMarketAgentBehaviour(new MarketAgent(symbol.getName()));
        final StartAgentsBehaviour startAgents = new StartAgentsBehaviour(initMarket.getResult(), symbol, agents);
        final ReceiveLogonCompleteMessages receiveLogon = new ReceiveLogonCompleteMessages(startAgents.getResult());
        final StartSimulationBehaviour startSimulation = new StartSimulationBehaviour(length, startAgents.getResult());
        final StopSimulationBehaviour stopSimulation = new StopSimulationBehaviour(startSimulation.getResult(),
                                                                                   length, startAgents.getResult(),
                                                                                   initMarket.getResult());

        initSequence.addSubBehaviour(initMarket);
        initSequence.addSubBehaviour(startAgents);
        initSequence.addSubBehaviour(receiveLogon);

        if (!initialOrders.isEmpty()) {
            final BootstrapOrderBookBehaviour bootstrap = new BootstrapOrderBookBehaviour(initMarket.getResult(),
                                                                                          symbol.getName(),
                                                                                          initialOrders);
            initSequence.addSubBehaviour(bootstrap);
        }

        initSequence.addSubBehaviour(startSimulation);
        initSequence.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                LOG.info("Start sequence took {}", stopwatch.stop());
            }
        });
        initSequence.addSubBehaviour(stopSimulation);

        addBehaviour(initSequence);
    }
}
