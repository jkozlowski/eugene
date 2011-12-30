package eugene.market.ontology.message;

import eugene.market.ontology.Message;
import eugene.market.ontology.field.ClOrdID;
import eugene.market.ontology.field.OrdStatus;
import eugene.market.ontology.field.OrderID;
import jade.util.Event;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests sending {@link OrderCancelReject}.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public class OrderCancelRejectTest extends MessageTest {

    @Test
    public void testSendOrderCancelReject() throws InterruptedException, StaleProxyException,
                                                   IllegalAccessException {

        final Set<Message> toSend = new HashSet<Message>();
        final OrderCancelReject orderCancelReject = new OrderCancelReject();
        orderCancelReject.setClOrdID(new ClOrdID(ClOrdID));
        orderCancelReject.setOrderID(new OrderID(OrderID));
        orderCancelReject.setOrdStatus(new OrdStatus(OrdStatus.FILLED));
        toSend.add(orderCancelReject);

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
