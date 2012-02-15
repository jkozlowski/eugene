package eugene.market.esma;

import eugene.market.ontology.MarketOntology;
import eugene.market.ontology.Message;
import eugene.simulation.ontology.SimulationOntology;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.behaviours.Behaviour;
import jade.imtp.memory.MemoryProfile;
import jade.util.Event;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import jade.wrapper.gateway.GatewayAgent;

import static eugene.market.ontology.Defaults.defaultSymbol;
import static jade.core.Runtime.instance;

/**
 * Sets up the {@link MarketAgent} behaviours in a {@link GatewayAgent}.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public abstract class AbstractMarketAgentTest {

    public static final int TIMEOUT = 60000;

    public static final String GATEWAY_AGENT = "gateway";

    public static final String MARKET_AGENT = "market";

    /**
     * Creates the container.
     */
    public static AgentContainer getContainer() throws StaleProxyException {
        final Profile profile = new MemoryProfile();
        final AgentContainer agentContainer = instance().createMainContainer(profile);

        final GatewayAgent trader = new GatewayAgent();
        final AgentController gatewayController = agentContainer.acceptNewAgent(GATEWAY_AGENT, trader);
        trader.getContentManager().registerLanguage(MarketOntology.getCodec());
        trader.getContentManager().registerOntology(MarketOntology.getInstance());
        trader.getContentManager().registerLanguage(SimulationOntology.getCodec());
        trader.getContentManager().registerOntology(SimulationOntology.getInstance());
        gatewayController.start();

        final Agent market = new MarketAgent(defaultSymbol);
        final AgentController marketController = agentContainer.acceptNewAgent(MARKET_AGENT, market);
        marketController.start();

        return agentContainer;
    }
    
    public static AgentContainer getNakedContainer() {
        final Profile profile = new MemoryProfile();
        return instance().createMainContainer(profile);
    }

    public static Message send(final Message msg, final AgentContainer container) throws ControllerException,
                                                                                         InterruptedException {
        final SendMessage sendMessage = new SendMessage(msg);
        final Event traderEvent = new Event(-1, sendMessage);
        final AgentController traderController = container.getAgent(GATEWAY_AGENT);
        traderController.putO2AObject(traderEvent, AgentController.ASYNC);
        traderEvent.waitUntilProcessed(TIMEOUT);
        return sendMessage.received.get();
    }

    public static void submit(final Behaviour behaviour, final AgentContainer container)
            throws ControllerException, InterruptedException {
        final Event traderEvent = new Event(-1, behaviour);
        final AgentController traderController = container.getAgent(GATEWAY_AGENT);
        traderController.putO2AObject(traderEvent, AgentController.ASYNC);
        traderEvent.waitUntilProcessed(TIMEOUT);
    }

    public static void submitNoWait(final Behaviour behaviour, final AgentContainer container)
            throws ControllerException, InterruptedException {
        final Event traderEvent = new Event(-1, behaviour);
        final AgentController traderController = container.getAgent(GATEWAY_AGENT);
        traderController.putO2AObject(traderEvent, AgentController.ASYNC);
    }

    public static AgentController getMarket(final AgentContainer container) throws ControllerException {
        return container.getAgent(MARKET_AGENT);
    }
}
