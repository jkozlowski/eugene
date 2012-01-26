package eugene.market.esma.impl;

import eugene.market.book.Order;
import eugene.market.book.OrderStatus;
import eugene.market.esma.impl.Repository.Tuple;
import eugene.market.esma.impl.execution.Execution;
import eugene.market.ontology.field.enums.ExecType;
import eugene.market.ontology.field.enums.OrdStatus;
import eugene.market.ontology.field.enums.OrdType;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.data.AddOrder;
import eugene.market.ontology.message.data.DeleteOrder;
import eugene.market.ontology.message.data.OrderExecuted;
import jade.lang.acl.ACLMessage;
import org.hamcrest.CoreMatchers;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static eugene.market.book.MockOrders.buy;
import static eugene.market.book.MockOrders.ordType;
import static eugene.market.book.MockOrders.order;
import static eugene.market.book.MockOrders.sell;
import static eugene.market.esma.impl.Messages.addOrder;
import static eugene.market.esma.impl.Messages.deleteOrder;
import static eugene.market.esma.impl.Messages.executionReport;
import static eugene.market.esma.impl.Messages.orderExecuted;
import static eugene.market.ontology.Defaults.defaultClOrdID;
import static eugene.market.ontology.Defaults.defaultOrdQty;
import static eugene.market.ontology.Defaults.defaultPrice;
import static eugene.market.ontology.Defaults.defaultSymbol;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

/**
 * Tests {@link Messages}.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public class MessagesTest {

    public static final Tuple defaultTuple = new Tuple(mock(ACLMessage.class), defaultClOrdID);

    public static final String ORDER_STATUS_PROVIDER = "order-status-provider";

    public static final Long execID = 1L;

    @DataProvider(name = ORDER_STATUS_PROVIDER)
    public OrderStatus[][] getOrderStatus() {
        final List<OrderStatus[]> orders = new ArrayList<OrderStatus[]>();

        final Order order = order(buy());

        final OrderStatus newOrderStatus = new OrderStatus(order);
        orders.add(new OrderStatus[]{newOrderStatus});

        final OrderStatus partiallyFilledOrderStatus = new OrderStatus(order, defaultPrice, defaultOrdQty - 1L,
                                                                       1L, OrdStatus.NEW);
        orders.add(new OrderStatus[]{partiallyFilledOrderStatus});

        final OrderStatus filledOrderStatus = new OrderStatus(order, defaultPrice, 0L, defaultOrdQty, OrdStatus.NEW);
        orders.add(new OrderStatus[]{filledOrderStatus});

        return orders.toArray(new OrderStatus[][]{});
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testConstructor() {
        new Messages();
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testExecutionReportNullOrderStatus() {
        executionReport(null, defaultTuple, defaultSymbol);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testExecutionReportNullTuple() {
        executionReport(mock(OrderStatus.class), null, defaultSymbol);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testExecutionReportNullSymbol() {
        executionReport(mock(OrderStatus.class), defaultTuple, null);
    }

    @Test(dataProvider = ORDER_STATUS_PROVIDER)
    public void testExecutionReportNew(final OrderStatus orderStatus) {
        final Order order = orderStatus.getOrder();

        final ExecutionReport executionReport = executionReport(orderStatus, defaultTuple, defaultSymbol);


        if (orderStatus.isFilled()) {
            assertThat(executionReport.getExecType(), is(ExecType.TRADE.field()));
            assertThat(executionReport.getOrdStatus(), is(OrdStatus.FILLED.field()));
        }
        else {
            if (orderStatus.isEmpty()) {
                assertThat(executionReport.getExecType(), is(ExecType.NEW.field()));
                assertThat(executionReport.getOrdStatus(), is(OrdStatus.NEW.field()));
            }
            else {
                assertThat(executionReport.getExecType(), is(ExecType.TRADE.field()));
                assertThat(executionReport.getOrdStatus(), is(OrdStatus.PARTIALLY_FILLED.field()));
            }
        }

        assertThat(executionReport.getAvgPx().getValue(), is(orderStatus.getAvgPx()));
        assertThat(executionReport.getClOrdID().getValue(), is(defaultTuple.getClOrdID()));
        assertThat(executionReport.getCumQty().getValue(), is(orderStatus.getCumQty()));
        assertThat(executionReport.getLeavesQty().getValue(), is(orderStatus.getLeavesQty()));
        assertThat(executionReport.getOrderID().getValue(), is(order.getOrderID().toString()));
        assertThat(executionReport.getSide(), is(order.getSide().field()));
        assertThat(executionReport.getSymbol().getValue(), is(defaultSymbol));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testOrderExecutedNullOrderStatus() {
        orderExecuted(null, mock(Execution.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testOrderExecutedNullTradeReport() {
        orderExecuted(mock(OrderStatus.class), null);
    }

    @Test
    public void testOrderExecuted() {
        final Order buy = order(buy());
        final Order sell = order(sell());
        final OrderStatus buyOrderStatus = new OrderStatus(buy, defaultPrice, defaultOrdQty - 2L, 2L, OrdStatus.NEW);
        final OrderStatus sellOrderStatus = new OrderStatus(sell, defaultPrice, defaultOrdQty - 1L, 1L, OrdStatus.NEW);
        final Execution execution = new Execution(execID, buyOrderStatus, sellOrderStatus, defaultPrice,
                                                  defaultOrdQty - 1L);

        final OrderExecuted orderExecuted = orderExecuted(buyOrderStatus, execution);

        assertThat(orderExecuted.getTradeID().getValue(), is(execution.getExecID().toString()));
        assertThat(orderExecuted.getLastPx().getValue(), is(execution.getPrice()));
        assertThat(orderExecuted.getLastQty().getValue(), is(execution.getQuantity()));
        assertThat(orderExecuted.getLeavesQty().getValue(), is(buyOrderStatus.getLeavesQty()));
        assertThat(orderExecuted.getOrderID().getValue(), is(buy.getOrderID().toString()));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testDeleteOrderNullOrderStatus() {
        deleteOrder(null);
    }

    @Test
    public void testDeleteOrder() {
        final Order order = order(buy());
        final OrderStatus orderStatus = new OrderStatus(order);
        final DeleteOrder deleteOrder = deleteOrder(orderStatus);
        assertThat(deleteOrder.getOrderID().getValue(), is(order.getOrderID().toString()));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testAddOrderNullOrder() {
        addOrder(null, defaultSymbol);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testAddOrderNullSymbol() {
        addOrder(mock(OrderStatus.class), null);
    }

    @Test
    public void testAddOrder() {
        final Order buy = order(buy());
        final OrderStatus orderStatus = new OrderStatus(buy).execute(defaultPrice, defaultOrdQty - 1L);
        final AddOrder addOrder = addOrder(orderStatus, defaultSymbol);
        assertThat(addOrder.getOrderID().getValue(), CoreMatchers.is(buy.getOrderID().toString()));
        assertThat(addOrder.getOrderQty().getValue(), CoreMatchers.is(orderStatus.getLeavesQty()));
        assertThat(addOrder.getPrice().getValue(), CoreMatchers.is(buy.getPrice()));
        assertThat(addOrder.getSide(), CoreMatchers.is(buy.getSide().field()));
        assertThat(addOrder.getSymbol().getValue(), CoreMatchers.is(defaultSymbol));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddOrderMarketOrder() {
        final Order buy = order(ordType(buy(), OrdType.MARKET));
        final OrderStatus orderStatus = new OrderStatus(buy);
        addOrder(orderStatus, defaultSymbol);
    }
}
