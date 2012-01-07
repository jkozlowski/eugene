package eugene.market.esma;

import eugene.market.esma.behaviours.MarketDataServer;
import eugene.market.esma.behaviours.OrderServer;
import eugene.market.esma.execution.ExecutionEngine;
import eugene.market.ontology.MarketOntology;
import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.core.behaviours.OntologyServer;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static jade.domain.DFService.register;
import static jade.lang.acl.ACLMessage.REQUEST;

/**
 * Implements the Agent that acts as a Stock Market Exchange Order Matching Engine.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public class MarketAgent extends Agent {

    private static final Logger LOG = Logger.getLogger(MarketAgent.class.getName());

    public static final String SERVICE_TYPE = "order-execution";

    public static final String SYMBOL_PROPERTY_NAME = "symbol";

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
        try {
            getContentManager().registerLanguage(new SLCodec(), MarketOntology.LANGUAGE);
            getContentManager().registerOntology(MarketOntology.getInstance());

            final DFAgentDescription agentDescription = getDFAgentDescription();
            final ServiceDescription serviceDescription = getServiceDescription();
            serviceDescription.setName(getLocalName() + "-" + SERVICE_TYPE);
            serviceDescription.addProperties(new Property(SYMBOL_PROPERTY_NAME, symbol));
            agentDescription.setName(getAID());
            agentDescription.addServices(serviceDescription);
            register(this, agentDescription);


            final OrderServer orderServer = new OrderServer(this, executionEngine, repository, symbol);

            addBehaviour(new OntologyServer(this, MarketOntology.getInstance(), REQUEST, orderServer));
            addBehaviour(new MarketDataServer(this, executionEngine.getMarketDataEngine(), repository, symbol));
        }
        catch (FIPAException e) {
            e.printStackTrace();
            LOG.severe(e.toString());
        }
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

    /**
     * Gets the template {@link DFAgentDescription} for searching for {@link MarketAgent}s,
     * with {@link DFAgentDescription#addLanguages(String)} and {@link DFAgentDescription#addOntologies(String)}
     * correctly set.
     *
     * The Agents should use {@link MarketAgent#getServiceDescription()} to get the template for {@link
     * ServiceDescription} to set using {@link DFAgentDescription#addServices(ServiceDescription)}.
     *
     * @return template {@link DFAgentDescription}.
     */
    private static DFAgentDescription getDFAgentDescription() {
        final DFAgentDescription agentDescription = new DFAgentDescription();
        agentDescription.addLanguages(MarketOntology.LANGUAGE);
        agentDescription.addOntologies(MarketOntology.NAME);
        return agentDescription;
    }

    /**
     * Gets the template {@link ServiceDescription} that instances of {@link MarketAgent} register with the {@link
     * DFService}.
     *
     * When using {@link DFService#search(Agent, DFAgentDescription)},
     * the Agents should set the property {@link MarketAgent#SYMBOL_PROPERTY_NAME} to a {@link String} representing
     * the symbol that the Agent wishes to trade, using {@link ServiceDescription#addProperties(Property)}.
     *
     * @return template {@link ServiceDescription}.
     */
    private static ServiceDescription getServiceDescription() {
        final ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(SERVICE_TYPE);
        return serviceDescription;
    }

    /**
     * Gets the {@link DFAgentDescription} to search for {@link MarketAgent}s that handle this <code>symbol</code>.
     *
     * @param symbol symbol to lookup.
     *
     * @return template {@link DFAgentDescription}.
     */
    public static DFAgentDescription getDFAgentDescription(final String symbol) {
        checkNotNull(symbol);
        final DFAgentDescription agentDescription = getDFAgentDescription();
        final ServiceDescription serviceDescription = getServiceDescription();
        serviceDescription.addProperties(new Property(SYMBOL_PROPERTY_NAME, symbol));
        agentDescription.addServices(serviceDescription);
        return agentDescription;
    }
}
