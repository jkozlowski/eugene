package eugene.market.esma.impl.behaviours;

import eugene.market.book.Order;
import eugene.market.esma.MarketAgent;
import eugene.market.esma.impl.Repository;
import eugene.market.esma.impl.execution.ExecutionEngine;
import eugene.market.ontology.Message;
import eugene.market.ontology.field.ExecType;
import eugene.market.ontology.field.OrdStatus;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.NewOrderSingle;
import jade.wrapper.AgentContainer;
import jade.wrapper.ControllerException;
import org.testng.annotations.Test;

import static eugene.market.esma.AbstractMarketAgentTest.getContainer;
import static eugene.market.esma.AbstractMarketAgentTest.send;
import static eugene.market.ontology.MockMessages.newOrderSingle;
import static eugene.market.ontology.Defaults.defaultSymbol;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;

/**
 * Tests {@link OrderServer}.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public class OrderServerTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullAgent() {
        new OrderServer(null, defaultSymbol);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullSymbol() {
        new OrderServer(mock(MarketAgent.class), null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullExecutionEngine() {
        new OrderServer(mock(MarketAgent.class), null, mock(Repository.class), defaultSymbol);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullOrderRepository() {
        new OrderServer(mock(MarketAgent.class), mock(ExecutionEngine.class), null, defaultSymbol);
    }

    @Test
    public void testSendNewOrderSingleNoExecution() throws ControllerException, InterruptedException {

        final AgentContainer container = getContainer();

        final NewOrderSingle newOrder = newOrderSingle();

        final Message received = send(newOrder, container);

        assertThat(received, notNullValue());
        assertThat(received, is(instanceOf(ExecutionReport.class)));

        final ExecutionReport executionReport = (ExecutionReport) received;
        assertThat(executionReport.getAvgPx().getValue(), is(Order.NO_PRICE));
        assertThat(executionReport.getCumQty().getValue(), is(Order.NO_QTY));
        assertThat(executionReport.getLeavesQty().getValue(), is(newOrder.getOrderQty().getValue()));
        assertThat(executionReport.getExecType().getValue(), is(ExecType.NEW));
        assertThat(executionReport.getClOrdID().getValue(), is(newOrder.getClOrdID().getValue()));
        assertThat(executionReport.getOrdStatus().getValue(), is(OrdStatus.NEW));
        assertThat(executionReport.getSide().getValue(), is(newOrder.getSide().getValue()));
        assertThat(executionReport.getSymbol().getValue(), is(newOrder.getSymbol().getValue()));
    }


}
