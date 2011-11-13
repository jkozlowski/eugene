package eugene.market.ontology.message;

import eugene.market.ontology.Message;
import eugene.market.ontology.field.AvgPx;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests sending {@link ExecutionReport}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public class ExecutionReportTest extends MessageTest {

    public static final String id = "sdfaksjdfh";

    public static final String Symbol = "VOD.L";

    public static final Long LeavesQty = 1L;

    public static final Long CumQty = 2L;

    public static final Double AvgPx = 1.2;

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
        executionReport.setOrderID(new OrderID(id));
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

        receiverEvent.waitUntilProcessed();

        assertTrue(senderBehaviour.failed.isEmpty());
        assertTrue(receiverBehaviour.failed.isEmpty());
        assertEquals(toSend, senderBehaviour.sent);
        assertEquals(toSend, receiverBehaviour.received);
    }

}
