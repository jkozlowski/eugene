package eugene.market.ontology.message;

import eugene.market.ontology.Defaults;
import eugene.market.ontology.Message;
import eugene.market.ontology.field.SessionStatus;
import eugene.market.ontology.field.Symbol;
import jade.util.Event;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests sending {@link Logon}.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public class LogonTest extends MessageTest {

    @Test
    public void testSendLogonWithSessionStatus() throws InterruptedException, StaleProxyException,
                                                        IllegalAccessException {

        final Set<Message> toSend = new HashSet<Message>();
        final Logon logon = new Logon();
        logon.setSymbol(new Symbol(Defaults.defaultSymbol));
        logon.setSessionStatus(new SessionStatus(SessionStatus.SESSION_ACTIVE));
        toSend.add(logon);

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
        assertThat(receiverBehaviour.received, is(toSend));
    }

    @Test
    public void testSendLogonWithoutSessionStatus() throws InterruptedException, StaleProxyException,
                                                           IllegalAccessException {

        final Set<Message> toSend = new HashSet<Message>();
        final Logon logon = new Logon();
        logon.setSymbol(new Symbol(Defaults.defaultSymbol));
        toSend.add(logon);

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
        assertThat(receiverBehaviour.received, is(toSend));
    }
}
