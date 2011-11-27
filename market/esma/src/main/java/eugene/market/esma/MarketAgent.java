package eugene.market.esma;

import eugene.market.esma.behaviours.MarketDataServer;
import eugene.market.esma.behaviours.OrderServer;
import eugene.market.esma.execution.ExecutionEngine;
import eugene.market.ontology.MarketOntology;
import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.core.behaviours.OntologyServer;

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
        getContentManager().registerLanguage(new SLCodec(), MarketOntology.LANGUAGE);
        getContentManager().registerOntology(MarketOntology.getInstance());

        final OrderServer orderServer = new OrderServer(this, executionEngine, repository, symbol);

        addBehaviour(new OntologyServer(this, MarketOntology.getInstance(), REQUEST, orderServer));
        addBehaviour(new MarketDataServer(this, executionEngine.getMarketDataEngine(), repository, symbol));
    }

    /**
     * Gets the executionEngine.
     *
     * @return the executionEngine.
     */
    public ExecutionEngine getExecutionEngine() {
        return executionEngine;
    }

    /**
     * Gets the repository.
     *
     * @return the repository.
     */
    public Repository getRepository() {
        return repository;
    }
}
