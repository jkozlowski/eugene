package eugene.market.esma.behaviours;

import eugene.market.book.Order;
import eugene.market.esma.AbstractMarketAgentTest;
import eugene.market.ontology.Message;
import eugene.market.ontology.field.ClOrdID;
import eugene.market.ontology.field.ExecType;
import eugene.market.ontology.field.OrdStatus;
import eugene.market.ontology.field.OrdType;
import eugene.market.ontology.field.OrderQty;
import eugene.market.ontology.field.Side;
import eugene.market.ontology.field.Symbol;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.NewOrderSingle;
import jade.wrapper.StaleProxyException;
import org.testng.annotations.Test;

import static eugene.market.ontology.Defaults.defaultClOrdID;
import static eugene.market.ontology.Defaults.defaultOrdQty;
import static eugene.market.ontology.Defaults.defaultSymbol;
import static eugene.market.esma.MockMessages.newOrderSingle;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Tests {@link OrderServer}'s integration with <code>JADE</code>.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public class OrderServerIntegrationTest extends AbstractMarketAgentTest {

    @Test
    public void testSendNewOrderSingleNoExecution() throws StaleProxyException, InterruptedException {
        final NewOrderSingle newOrder = newOrderSingle();

        final Message received = send(newOrder);

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

    @Test
    public void testSendMarketNewOrderSingleNoLiquidity() throws StaleProxyException, InterruptedException {
        final NewOrderSingle newOrder = new NewOrderSingle();
        newOrder.setClOrdID(new ClOrdID(defaultClOrdID));
        newOrder.setSide(new Side(Side.BUY));
        newOrder.setOrderQty(new OrderQty(defaultOrdQty));
        newOrder.setSymbol(new Symbol(defaultSymbol));
        newOrder.setOrdType(new OrdType(OrdType.MARKET));

        final Message received = send(newOrder);

        assertThat(received, notNullValue());
        assertThat(received, is(instanceOf(ExecutionReport.class)));

        final ExecutionReport executionReport = (ExecutionReport) received;
        assertThat(executionReport.getAvgPx().getValue(), is(Order.NO_PRICE));
        assertThat(executionReport.getCumQty().getValue(), is(Order.NO_QTY));
        assertThat(executionReport.getLeavesQty().getValue(), is(newOrder.getOrderQty().getValue()));
        assertThat(executionReport.getExecType().getValue(), is(ExecType.REJECTED));
        assertThat(executionReport.getClOrdID().getValue(), is(newOrder.getClOrdID().getValue()));
        assertThat(executionReport.getOrdStatus().getValue(), is(OrdStatus.REJECTED));
        assertThat(executionReport.getSide().getValue(), is(newOrder.getSide().getValue()));
        assertThat(executionReport.getSymbol().getValue(), is(newOrder.getSymbol().getValue()));
    }
}
