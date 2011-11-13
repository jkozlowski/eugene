package eugene.market.ontology.message;

import eugene.market.ontology.Message;
import eugene.market.ontology.field.ClOrdID;
import eugene.market.ontology.field.OrdType;
import eugene.market.ontology.field.OrderQty;
import eugene.market.ontology.field.Price;
import eugene.market.ontology.field.Side;
import eugene.market.ontology.field.Symbol;
import jade.util.Event;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests sending {@link NewOrderSingle}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public class NewOrderSingleTest extends MessageTest {

    public static final String ClOrdID = "11";

    public static final Long OrderQty = 2L;

    public static final Double Price = 1.2;

    public static final String Symbol = "VOD.L";

    @Test
    public void testSendNewOrderSingle() throws InterruptedException, StaleProxyException, IllegalAccessException {

        final Set<Message> toSend = new HashSet<Message>();
        final NewOrderSingle newOrder = new NewOrderSingle();
        newOrder.setClOrdID(new ClOrdID(ClOrdID));
        newOrder.setSide(new Side(Side.BUY));
        newOrder.setOrderQty(new OrderQty(OrderQty));
        newOrder.setPrice(new Price(Price));
        newOrder.setSymbol(new Symbol(Symbol));
        newOrder.setOrdType(new OrdType(OrdType.LIMIT));
        toSend.add(newOrder);

        receiverBehaviour = new ReceiverBehaviour(toSend.size());
        final Event receiverEvent = new Event(-1, receiverBehaviour);
        receiverAgentController.start();
        receiverAgentController.putO2AObject(receiverEvent, AgentController.ASYNC);

        final SenderBehaviour senderBehaviour = new SenderBehaviour(toSend);
        final Event senderEvent = new Event(-1, senderBehaviour);
        senderAgentController.start();
        senderAgentController.putO2AObject(senderEvent, AgentController.ASYNC);
        senderEvent.waitUntilProcessed();

        receiverEvent.waitUntilProcessed();

        assertTrue(senderBehaviour.failed.isEmpty());
        assertTrue(receiverBehaviour.failed.isEmpty());
        assertEquals(toSend, senderBehaviour.sent);
        assertEquals(toSend, receiverBehaviour.received);
    }
}
