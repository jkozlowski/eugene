package eugene.market.ontology.message;

import eugene.market.ontology.Defaults;
import eugene.market.ontology.field.ClOrdID;
import eugene.market.ontology.field.OrdType;
import eugene.market.ontology.field.OrderQty;
import eugene.market.ontology.field.Price;
import eugene.market.ontology.field.Side;
import eugene.market.ontology.field.Symbol;
import jade.content.Concept;
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
 * Tests sending {@link NewOrderSingle}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public class NewOrderSingleTest extends MessageTest {

    @Test
    public void testSendNewOrderSingleWithPrice() throws InterruptedException, StaleProxyException,
                                                         IllegalAccessException {

        final Set<Concept> toSend = new HashSet<Concept>();
        final NewOrderSingle newOrder = new NewOrderSingle();
        newOrder.setClOrdID(new ClOrdID(Defaults.defaultClOrdID));
        newOrder.setSide(new Side(Side.BUY));
        newOrder.setOrderQty(new OrderQty(defaultOrdQty));
        newOrder.setPrice(new Price(Defaults.Price));
        newOrder.setSymbol(new Symbol(Defaults.defaultSymbol));
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

        assertThat(senderBehaviour.failed.isEmpty(), is(true));
        assertThat(senderBehaviour.sent, is(toSend));

        receiverEvent.waitUntilProcessed();

        assertThat(receiverBehaviour.failed.isEmpty(), is(true));
        assertThat(receiverBehaviour.received, is(toSend));
    }

    @Test
    public void testSendNewOrderSingleWithoutPrice() throws InterruptedException, StaleProxyException,
                                                            IllegalAccessException {

        final Set<Concept> toSend = new HashSet<Concept>();
        final NewOrderSingle newOrder = new NewOrderSingle();
        newOrder.setClOrdID(new ClOrdID(Defaults.defaultClOrdID));
        newOrder.setSide(new Side(Side.BUY));
        newOrder.setOrderQty(new OrderQty(defaultOrdQty));
        newOrder.setSymbol(new Symbol(Defaults.defaultSymbol));
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

        assertThat(senderBehaviour.failed.isEmpty(), is(true));
        assertThat(senderBehaviour.sent, is(toSend));

        receiverEvent.waitUntilProcessed();

        assertThat(receiverBehaviour.failed.isEmpty(), is(true));
        assertThat(receiverBehaviour.received, is(toSend));
    }
}
