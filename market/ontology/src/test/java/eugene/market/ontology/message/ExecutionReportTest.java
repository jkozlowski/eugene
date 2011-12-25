package eugene.market.ontology.message;

import eugene.market.ontology.Message;
import eugene.market.ontology.field.AvgPx;
import eugene.market.ontology.field.ClOrdID;
import eugene.market.ontology.field.CumQty;
import eugene.market.ontology.field.ExecType;
import eugene.market.ontology.field.LeavesQty;
import eugene.market.ontology.field.OrdStatus;
import eugene.market.ontology.field.OrderID;
import eugene.market.ontology.field.Side;
import eugene.market.ontology.field.Symbol;
import jade.util.Event;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests sending {@link ExecutionReport}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public class ExecutionReportTest extends MessageTest {

    @Test
    public void testSendExecutionReport() throws InterruptedException, StaleProxyException, IllegalAccessException {

        final Set<Message> toSend = new HashSet<Message>();
        final ExecutionReport executionReport = new ExecutionReport();
        executionReport.setExecType(new ExecType(ExecType.NEW));
        executionReport.setSymbol(new Symbol(Symbol));
        executionReport.setSide(new Side(Side.BUY));
        executionReport.setLeavesQty(new LeavesQty(LeavesQty));
        executionReport.setCumQty(new CumQty(CumQty));
        executionReport.setAvgPx(new AvgPx(AvgPx));
        executionReport.setOrdStatus(new OrdStatus(OrdStatus.FILLED));
        executionReport.setOrderID(new OrderID(OrderID));
        executionReport.setClOrdID(new ClOrdID(ClOrdID));
        toSend.add(executionReport);

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
