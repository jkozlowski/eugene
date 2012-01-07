package eugene.market.esma;

import eugene.market.ontology.MarketOntology;
import eugene.market.ontology.Message;
import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.core.Profile;
import jade.imtp.memory.MemoryProfile;
import jade.util.Event;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import jade.wrapper.gateway.GatewayAgent;
import org.testng.annotations.BeforeMethod;

import static eugene.market.ontology.Defaults.defaultSymbol;
import static jade.core.Runtime.instance;

/**
 * Sets up the {@link MarketAgent} behaviours in a {@link GatewayAgent}.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public abstract class AbstractMarketAgentTest {

    public static final String GATEWAY_AGENT = "gateway";

    public static final String MARKET_AGENT = "market";

    public Agent traderAgent;

    public AgentController gatewayAgentController;

    public AgentController marketAgentController;

    /**
     * Creates the container.
     */
    @BeforeMethod
    public void setupContainer() throws StaleProxyException {
        final Profile profile = new MemoryProfile();
        final AgentContainer agentContainer = instance().createMainContainer(profile);

        initTraderAgent(agentContainer);
        initMarketAgent(agentContainer);
    }

    private void initTraderAgent(final AgentContainer agentContainer) throws StaleProxyException {
        traderAgent = new GatewayAgent();
        gatewayAgentController = agentContainer.acceptNewAgent(GATEWAY_AGENT, traderAgent);
        traderAgent.getContentManager().registerLanguage(new SLCodec(), MarketOntology.LANGUAGE);
        traderAgent.getContentManager().registerOntology(MarketOntology.getInstance());
        gatewayAgentController.start();
    }

    private void initMarketAgent(final AgentContainer agentContainer) throws StaleProxyException {
        final Agent marketAgent = new MarketAgent(defaultSymbol);
        marketAgentController = agentContainer.acceptNewAgent(MARKET_AGENT, marketAgent);
        marketAgentController.start();
    }
    
    public Message send(final Message msg) throws StaleProxyException, InterruptedException {
        final SendMessage sendMessage = new SendMessage(traderAgent, msg);
        final Event traderEvent = new Event(-1, sendMessage);
        gatewayAgentController.putO2AObject(traderEvent, AgentController.ASYNC);
        traderEvent.waitUntilProcessed();
        return sendMessage.received.get();
    }
}
