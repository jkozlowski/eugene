package eugene.market.esma;

import eugene.market.book.Order;
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
import jade.wrapper.AgentContainer;
import jade.wrapper.ControllerException;
import org.testng.annotations.Test;

import static eugene.market.esma.AbstractMarketAgentTest.getContainer;
import static eugene.market.esma.AbstractMarketAgentTest.send;
import static eugene.market.ontology.Defaults.defaultClOrdID;
import static eugene.market.ontology.Defaults.defaultOrdQty;
import static eugene.market.ontology.Defaults.defaultSymbol;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Tests {@link MarketAgent}.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class MarketAgentTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullSymbol() {
        new MarketAgent(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorEmptySymbol() {
        new MarketAgent("");
    }

    @Test
    public void testSendMarketNewOrderSingleNoLiquidity() throws ControllerException, InterruptedException {

        final AgentContainer container = getContainer();

        final NewOrderSingle newOrder = new NewOrderSingle();
        newOrder.setClOrdID(new ClOrdID(defaultClOrdID));
        newOrder.setSide(new Side(Side.BUY));
        newOrder.setOrderQty(new OrderQty(defaultOrdQty));
        newOrder.setSymbol(new Symbol(defaultSymbol));
        newOrder.setOrdType(new OrdType(OrdType.MARKET));

        final Message received = send(newOrder, container);

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
