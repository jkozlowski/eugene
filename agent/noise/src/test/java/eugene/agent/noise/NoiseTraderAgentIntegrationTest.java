package eugene.agent.noise;

import eugene.market.esma.MarketAgent;
import eugene.market.ontology.MarketOntology;
import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.core.Profile;
import jade.imtp.memory.MemoryProfile;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import jade.wrapper.gateway.GatewayAgent;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static eugene.market.ontology.Defaults.defaultSymbol;
import static jade.core.Runtime.instance;

/**
 * Tests {@link NoiseTraderAgent} inside a JADE container.
 *
 * @author Jakub D Kozlowski
 * @since 0.5
 */
public class NoiseTraderAgentIntegrationTest {

    public static final String GATEWAY_AGENT = "gateway";

    public static final String MARKET_AGENT = "market";

    public Agent traderAgent;

    public AgentController gatewayAgentController;

    public AgentController marketAgentController;

    public AgentContainer agentContainer;

    /**
     * Creates the container.
     */
    @BeforeMethod
    public void setupContainer() throws StaleProxyException {
        final Profile profile = new MemoryProfile();
        agentContainer = instance().createMainContainer(profile);

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

    @Test(enabled = false)
    public void testNoiseTraderAgent() throws StaleProxyException, InterruptedException {

        for (int i = 0; i < 30; i++) {
            final NoiseTraderAgent noiseTraderAgent = new NoiseTraderAgent(defaultSymbol);
            final AgentController traderAgentController = agentContainer.acceptNewAgent("noise-trader" + i,
                                                                                  noiseTraderAgent);
            traderAgentController.start();
        }

        Thread.sleep(30000L);
    }

}
