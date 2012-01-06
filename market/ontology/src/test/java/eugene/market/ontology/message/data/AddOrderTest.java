package eugene.market.ontology.message.data;

import eugene.market.ontology.Defaults;
import eugene.market.ontology.Message;
import eugene.market.ontology.field.OrderID;
import eugene.market.ontology.field.OrderQty;
import eugene.market.ontology.field.Price;
import eugene.market.ontology.field.Side;
import eugene.market.ontology.field.Symbol;
import eugene.market.ontology.message.MessageTest;
import eugene.market.ontology.message.ReceiverBehaviour;
import eugene.market.ontology.message.SenderBehaviour;
import jade.util.Event;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

import static eugene.market.ontology.Defaults.defaultOrdQty;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests sending {@link AddOrder}.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public class AddOrderTest extends MessageTest {

    @Test
    public void testSendAddOrder() throws InterruptedException, StaleProxyException, IllegalAccessException {

        final Set<Message> toSend = new HashSet<Message>();
        final AddOrder addOrder = new AddOrder();
        addOrder.setOrderID(new OrderID(Defaults.defaultOrderID));
        addOrder.setOrderQty(new OrderQty(defaultOrdQty));
        addOrder.setPrice(new Price(Defaults.Price));
        addOrder.setSide(new Side(Side.BUY));
        addOrder.setSymbol(new Symbol(Defaults.defaultSymbol));
        toSend.add(addOrder);

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