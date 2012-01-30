package eugene.simulation.ontology;

import eugene.market.ontology.Defaults;
import jade.content.Concept;
import jade.core.Agent;
import jade.core.Profile;
import jade.imtp.memory.MemoryProfile;
import jade.util.Event;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import jade.wrapper.gateway.GatewayAgent;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static jade.core.Runtime.instance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Base class for testing {@link SimulationOntology}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public class MessageTest {
    
    private static final String DATA_PROVIDER = "message-provider";

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
        a.getContentManager().registerLanguage(SimulationOntology.getCodec(), SimulationOntology.LANGUAGE);
        a.getContentManager().registerOntology(SimulationOntology.getInstance());
    }

    @DataProvider(name = DATA_PROVIDER)
    public Concept[][] setupDictionary() {
        final Set<Concept[]> messages = new HashSet<Concept[]>();
        messages.add(new Concept[] {new LogonComplete()});
        messages.add(new Concept[] {new Start()});
        messages.add(new Concept[] {new Started()});
        messages.add(new Concept[] {new Stop()});
        messages.add(new Concept[] {new Stopped()});
        return messages.toArray(new Concept[][]{});
    }

    @Test(dataProvider = DATA_PROVIDER)
    public void testSendLogonComplete(final Concept message) throws InterruptedException, StaleProxyException,
                                                                    IllegalAccessException {

        final Set<Concept> toSend = new HashSet<Concept>();
        toSend.add(message);

        receiverBehaviour = new ReceiverBehaviour(toSend.size());
        final Event receiverEvent = new Event(-1, receiverBehaviour);
        receiverAgentController.start();
        receiverAgentController.putO2AObject(receiverEvent, AgentController.ASYNC);

        final SenderBehaviour senderBehaviour = new SenderBehaviour(toSend);
        final Event senderEvent = new Event(-1, senderBehaviour);
        senderAgentController.start();
        senderAgentController.putO2AObject(senderEvent, AgentController.ASYNC);
        senderEvent.waitUntilProcessed();

        assertThat(senderBehaviour.failed.isEmpty(), is(true));
        assertThat(senderBehaviour.sent, is(toSend));

        receiverEvent.waitUntilProcessed();

        assertThat(receiverBehaviour.failed.isEmpty(), is(true));
        assertThat(receiverBehaviour.received.iterator().next(), is(message.getClass()));
    }
}
