package eugene.market.ontology.message;

import eugene.market.ontology.Defaults;
import eugene.market.ontology.MarketOntology;
import eugene.market.ontology.Message;
import jade.core.Agent;
import jade.core.Profile;
import jade.imtp.memory.MemoryProfile;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import jade.wrapper.gateway.GatewayAgent;
import org.testng.annotations.BeforeMethod;

import static jade.core.Runtime.instance;

/**
 * Base class for testing {@link Message}s.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public abstract class MessageTest {

    public AgentContainer agentContainer;

    public GatewayAgent senderAgent;

    public GatewayAgent receiverAgent;

    public AgentController senderAgentController;

    public AgentController receiverAgentController;

    public ReceiverBehaviour receiverBehaviour;

    /**
     * Creates the container.
     */
    @BeforeMethod
    public void setupContainer() throws StaleProxyException {
        final Profile profile = new MemoryProfile();
        agentContainer = instance().createMainContainer(profile);

        senderAgent = new GatewayAgent();
        senderAgentController = agentContainer.acceptNewAgent(Defaults.SENDER_AGENT, senderAgent);
        initAgent(senderAgent);

        receiverAgent = new GatewayAgent();
        receiverAgentController = agentContainer.acceptNewAgent(Defaults.RECEIVER_AGENT, receiverAgent);
        initAgent(receiverAgent);
    }

    /**
     * Initializes {@link Agent}'s language and ontology.
     *
     * @param a agent to initialize.
     */
    public static void initAgent(final Agent a) {
        a.getContentManager().registerLanguage(MarketOntology.getCodec(), MarketOntology.LANGUAGE);
        a.getContentManager().registerOntology(MarketOntology.getInstance());
    }
}
