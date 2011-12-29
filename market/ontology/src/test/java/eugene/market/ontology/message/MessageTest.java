package eugene.market.ontology.message;

import eugene.market.ontology.MarketOntology;
import eugene.market.ontology.Message;
import jade.content.lang.sl.SLCodec;
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

    public static final String SENDER_AGENT = "sender";

    public static final String RECEIVER_AGENT = "receiver";

    public static final String ClOrdID = "11";

    public static final Long OrderQty = 2L;

    public static final Double Price = 1.2;

    public static final String Symbol = "VOD.L";

    public static final String OrderID = "sdfaksjdfh";

    public static final Long LeavesQty = 1L;

    public static final Long CumQty = 2L;

    public static final Double AvgPx = 1.2;

    /**
     * Creates the container.
     *
     */
    @BeforeMethod
    public void setupContainer() throws StaleProxyException {
        final Profile profile = new MemoryProfile();
        agentContainer = instance().createMainContainer(profile);

        senderAgent = new GatewayAgent();
        senderAgentController = agentContainer.acceptNewAgent(SENDER_AGENT, senderAgent);
        initAgent(senderAgent);

        receiverAgent = new GatewayAgent();
        receiverAgentController = agentContainer.acceptNewAgent(RECEIVER_AGENT, receiverAgent);
        initAgent(receiverAgent);
    }

    /**
     * Initializes {@link Agent}'s language and ontology.
     *
     * @param a agent to initialize.
     */
    public static void initAgent(final Agent a) {
        a.getContentManager().registerLanguage(new SLCodec(), MarketOntology.LANGUAGE);
        a.getContentManager().registerOntology(MarketOntology.getInstance());
    }
}
