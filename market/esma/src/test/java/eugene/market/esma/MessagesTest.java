package eugene.market.esma;

import eugene.market.esma.enums.ExecType;
import eugene.market.esma.enums.OrdStatus;
import eugene.market.esma.execution.book.Order;
import eugene.market.esma.execution.book.OrderStatus;
import eugene.market.esma.execution.book.TradeReport;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.data.OrderExecuted;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static eugene.market.esma.Defaults.defaultOrdQty;
import static eugene.market.esma.Defaults.defaultPrice;
import static eugene.market.esma.Defaults.defaultSymbol;
import static eugene.market.esma.Defaults.defaultTuple;
import static eugene.market.esma.Messages.executionReport;
import static eugene.market.esma.Messages.orderExecuted;
import static eugene.market.esma.execution.MockOrders.buy;
import static eugene.market.esma.execution.MockOrders.order;
import static eugene.market.esma.execution.MockOrders.sell;
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

    public static final String ORDER_STATUS_PROVIDER = "order-status-provider";

    @DataProvider(name = ORDER_STATUS_PROVIDER)
    public OrderStatus[][] getOrderStatus() {
        final List<OrderStatus[]> orders = new ArrayList<OrderStatus[]>();

        final Order order = order(buy());

        final OrderStatus newOrderStatus = new OrderStatus(order);
        orders.add(new OrderStatus[]{newOrderStatus});

        final OrderStatus partiallyFilledOrderStatus = new OrderStatus(order, defaultPrice, defaultOrdQty - 1L, 1L);
        orders.add(new OrderStatus[]{partiallyFilledOrderStatus});

        final OrderStatus filledOrderStatus = new OrderStatus(order, defaultPrice, 0L, defaultOrdQty);
        orders.add(new OrderStatus[]{filledOrderStatus});

        return orders.toArray(new OrderStatus[][]{});
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
            assertThat(executionReport.getExecType(), is(ExecType.FILL.field()));
            assertThat(executionReport.getOrdStatus(), is(OrdStatus.FILLED.field()));
        }
        else {
            if (orderStatus.isEmpty()) {
                assertThat(executionReport.getExecType(), is(ExecType.NEW.field()));
                assertThat(executionReport.getOrdStatus(), is(OrdStatus.NEW.field()));
            }
            else {
                assertThat(executionReport.getExecType(), is(ExecType.PARTIAL_FILL.field()));
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
        orderExecuted(null, mock(TradeReport.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testOrderExecutedNullTradeReport() {
        orderExecuted(mock(OrderStatus.class), null);
    }

    @Test
    public void testOrderExecuted() {
        final Order buy = order(buy());
        final Order sell = order(sell());
        final OrderStatus buyOrderStatus = new OrderStatus(buy, defaultPrice, defaultOrdQty - 2L, 2L);
        final OrderStatus sellOrderStatus = new OrderStatus(sell, defaultPrice, defaultOrdQty - 1L, 1L);
        final TradeReport tradeReport = new TradeReport(buyOrderStatus, sellOrderStatus, defaultPrice,
                                                        defaultOrdQty - 1L);
        
        final OrderExecuted orderExecuted = orderExecuted(buyOrderStatus, tradeReport);
        
        assertThat(orderExecuted.getLastPx().getValue(), is(tradeReport.getPrice()));
        assertThat(orderExecuted.getLastQty().getValue(), is(tradeReport.getQuantity()));
        assertThat(orderExecuted.getLeavesQty().getValue(), is(buyOrderStatus.getLeavesQty()));
        assertThat(orderExecuted.getOrderID().getValue(), is(buy.getOrderID().toString()));
    }
}
