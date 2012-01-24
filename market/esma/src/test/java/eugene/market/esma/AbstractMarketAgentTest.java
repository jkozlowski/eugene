package eugene.market.esma;

import eugene.market.ontology.MarketOntology;
import eugene.market.ontology.Message;
import jade.content.ContentElement;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.imtp.memory.MemoryProfile;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import jade.util.Event;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import jade.wrapper.gateway.GatewayAgent;
import org.testng.annotations.BeforeMethod;

import java.util.concurrent.atomic.AtomicReference;

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

    /**
     * Utility Trader {@link Behaviour} used by {@link AbstractMarketAgentTest}.
     *
     * @author Jakub D Kozlowski
     * @since 0.2
     */
    public static class SendMessage extends SequentialBehaviour {

        public final AID marketAgent = new AID(MARKET_AGENT, AID.ISLOCALNAME);

        public final AtomicReference<Message> received = new AtomicReference<Message>(null);

        public SendMessage(final Agent agent, final Message toSend) {
            super(agent);

            try {
                final Action a = new Action(marketAgent, toSend);
                final ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
                aclMessage.addReceiver(new AID(MARKET_AGENT, AID.ISLOCALNAME));
                aclMessage.setOntology(MarketOntology.getInstance().getName());
                aclMessage.setLanguage(MarketOntology.LANGUAGE);
                myAgent.getContentManager().fillContent(aclMessage, a);
                addSubBehaviour(new SenderBehaviour(agent, aclMessage));
            }
            catch (Exception e) {
            }
        }

        private class SenderBehaviour extends AchieveREInitiator {

            public SenderBehaviour(final Agent agent, final ACLMessage aclMessage) {
                super(agent, aclMessage);
            }

            @Override
            public void handleInform(ACLMessage inform) {

                try {
                    final ContentElement a = myAgent.getContentManager().extractContent(inform);

                    if (!(a instanceof Action) || !(((Action) a).getAction() instanceof Message)) {
                        return;
                    }

                    received.set((Message) ((Action) a).getAction());
                }
                catch (Exception e) {
                }
            }

            @Override
            public void handleRefuse(ACLMessage aclMessage) {
                handleInform(aclMessage);
            }
        }
    }
}
