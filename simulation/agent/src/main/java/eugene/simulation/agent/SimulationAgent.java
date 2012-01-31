package eugene.simulation.agent;

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
import jade.core.behaviours.SequentialBehaviour;

import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.unmodifiableSet;

/**
 * Implements the Simulation Agent that starts the Market Agent and Trader Agents and synchronises the start and
 * finish of the simulation.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class SimulationAgent extends Agent {

    public static final String NAME = "simulation-agent";

    private final String symbol;

    private final int length;

    private final Set<Order> initialOrders;

    private final Set<Agent> agents;

    /**
     * Creates a {@link SimulationAgent} for this <code>symbol</code>, that will initialize the simulation with
     * <code>initialOrders</code> and will start <code>agents</code>.
     *
     * @param symbol        symbol for this simulation.
     * @param initialOrders orders to initialize the simulation with.
     * @param agents        {@link Agent}s to start.
     */
    public SimulationAgent(final String symbol, int length, final Set<Order> initialOrders, final Set<Agent> agents) {
        checkNotNull(symbol);
        checkArgument(!symbol.isEmpty());
        checkArgument(length > 0);
        checkNotNull(initialOrders);
        checkNotNull(agents);
        checkArgument(!agents.isEmpty());
        this.symbol = symbol;
        this.length = length;
        this.initialOrders = unmodifiableSet(initialOrders);
        this.agents = unmodifiableSet(agents);
    }

    @Override
    public void setup() {
        getContentManager().registerLanguage(MarketOntology.getCodec());
        getContentManager().registerLanguage(SimulationOntology.getCodec());
        getContentManager().registerOntology(MarketOntology.getInstance());
        getContentManager().registerOntology(SimulationOntology.getInstance());

        final SequentialBehaviour initSequence = new SequentialBehaviour();

        final InitializeMarketAgentBehaviour initMarket = new InitializeMarketAgentBehaviour(new MarketAgent(symbol));
        final StartAgentsBehaviour startAgents = new StartAgentsBehaviour(initMarket.getResult(), symbol, agents);
        final ReceiveLogonCompleteMessages receiveLogon = new ReceiveLogonCompleteMessages(startAgents.getResult());
        final StartSimulationBehaviour startSimulation = new StartSimulationBehaviour(startAgents.getResult());
        final StopSimulationBehaviour stopSimulation = new StopSimulationBehaviour(startSimulation.getResult(),
                                                                                   length, startAgents.getResult(),
                                                                                   initMarket.getResult());

        initSequence.addSubBehaviour(initMarket);
        initSequence.addSubBehaviour(startAgents);
        initSequence.addSubBehaviour(receiveLogon);

        if (!initialOrders.isEmpty()) {
            final BootstrapOrderBookBehaviour bootstrap = new BootstrapOrderBookBehaviour(initMarket.getResult(),
                                                                                          symbol,
                                                                                          initialOrders);
            initSequence.addSubBehaviour(bootstrap);
        }

        initSequence.addSubBehaviour(startSimulation);
        initSequence.addSubBehaviour(stopSimulation);

        addBehaviour(initSequence);
    }
}
