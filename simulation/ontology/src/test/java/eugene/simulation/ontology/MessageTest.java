package eugene.simulation.ontology;

import eugene.market.ontology.Defaults;
import jade.content.AgentAction;
import jade.core.Agent;
import jade.core.Profile;
import jade.imtp.memory.MemoryProfile;
import jade.util.Event;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import jade.wrapper.gateway.GatewayAgent;
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
    public AgentAction[][] setupDictionary() {
        final Set<AgentAction[]> messages = new HashSet<AgentAction[]>();
        messages.add(new AgentAction[] {new LogonComplete()});
        messages.add(new AgentAction[] {new Start()});
        messages.add(new AgentAction[] {new Started()});
        messages.add(new AgentAction[] {new Stop()});
        messages.add(new AgentAction[] {new Stopped()});
        return messages.toArray(new AgentAction[][]{});
    }

    @Test(dataProvider = DATA_PROVIDER)
    public void testSendLogonComplete(final AgentAction message) throws InterruptedException, StaleProxyException,
                                                                    IllegalAccessException {

        final Profile profile = new MemoryProfile();
        final AgentContainer container = instance().createMainContainer(profile);

        final GatewayAgent senderAgent = new GatewayAgent();
        final AgentController senderController = container.acceptNewAgent(Defaults.SENDER_AGENT, senderAgent);
        initAgent(senderAgent);

        final GatewayAgent receiverAgent = new GatewayAgent();
        final AgentController receiverController = container.acceptNewAgent(Defaults.RECEIVER_AGENT, 
                                                                                  receiverAgent);
        initAgent(receiverAgent);
        
        final Set<AgentAction> toSend = new HashSet<AgentAction>();
        toSend.add(message);

        final ReceiverBehaviour receiverBehaviour = new ReceiverBehaviour(toSend.size());
        final Event receiverEvent = new Event(-1, receiverBehaviour);
        receiverController.start();
        receiverController.putO2AObject(receiverEvent, AgentController.ASYNC);

        final SenderBehaviour senderBehaviour = new SenderBehaviour(toSend);
        final Event senderEvent = new Event(-1, senderBehaviour);
        senderController.start();
        senderController.putO2AObject(senderEvent, AgentController.ASYNC);
        senderEvent.waitUntilProcessed();

        assertThat(senderBehaviour.failed.isEmpty(), is(true));
        assertThat(senderBehaviour.sent, is(toSend));

        receiverEvent.waitUntilProcessed();

        assertThat(receiverBehaviour.failed.isEmpty(), is(true));
        assertThat(receiverBehaviour.received.iterator().next(), is(message.getClass()));

        container.kill();
    }
}
