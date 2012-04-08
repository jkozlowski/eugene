/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.agent.impl;

import eugene.market.agent.impl.Messages;
import eugene.market.agent.impl.execution.Execution;
import eugene.market.book.Order;
import eugene.market.book.OrderStatus;
import eugene.market.agent.impl.Repository.Tuple;
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
import static eugene.market.agent.impl.Messages.addOrder;
import static eugene.market.agent.impl.Messages.deleteOrder;
import static eugene.market.agent.impl.Messages.executionReport;
import static eugene.market.agent.impl.Messages.orderExecuted;
import static eugene.market.agent.impl.Messages.tradeExecutionReport;
import static eugene.market.ontology.Defaults.defaultClOrdID;
import static eugene.market.ontology.Defaults.defaultLastPx;
import static eugene.market.ontology.Defaults.defaultLastQty;
import static eugene.market.ontology.Defaults.defaultOrdQty;
import static eugene.market.ontology.Defaults.defaultPrice;
import static eugene.market.ontology.Defaults.defaultSymbol;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
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

    public static final String TRADE_ORDER_STATUS_PROVIDER = "trade-order-status-provider";

    public static final Long execID = 1L;

    private final Execution execution = new Execution(execID, new OrderStatus(order(buy()), defaultPrice,
    defaultOrdQty - 2L, 2L, OrdStatus.NEW), new OrderStatus(order(sell()), defaultPrice, defaultOrdQty - 1L, 1L, OrdStatus.NEW), defaultPrice,
                                              defaultOrdQty - 1L);

    @DataProvider(name = ORDER_STATUS_PROVIDER)
    public OrderStatus[][] getOrderStatus() {
        final List<OrderStatus[]> orders = new ArrayList<OrderStatus[]>();

        final Order order = order(buy());

        final OrderStatus newOrderStatus = new OrderStatus(order);
        orders.add(new OrderStatus[]{newOrderStatus});

        final OrderStatus rejectedNewOrderStatus = newOrderStatus.reject();
        orders.add(new OrderStatus[]{rejectedNewOrderStatus});

        final OrderStatus partiallyFilledOrderStatus = new OrderStatus(order, defaultPrice, defaultOrdQty - 1L,
                                                                       1L, OrdStatus.PARTIALLY_FILLED);

        final OrderStatus partiallyFilledCanceled = partiallyFilledOrderStatus.cancel();
        orders.add(new OrderStatus[]{partiallyFilledCanceled});

        return orders.toArray(new OrderStatus[][]{});
    }

    @DataProvider(name = TRADE_ORDER_STATUS_PROVIDER)
    public OrderStatus[][] getTradeOrderStatus() {
        final List<OrderStatus[]> orders = new ArrayList<OrderStatus[]>();

        final Order order = order(buy());

        final OrderStatus partiallyFilledOrderStatus = new OrderStatus(order, defaultPrice, defaultOrdQty - 1L,
                                                                       1L, OrdStatus.PARTIALLY_FILLED);
        orders.add(new OrderStatus[]{partiallyFilledOrderStatus});

        final OrderStatus filledOrderStatus = new OrderStatus(order, defaultPrice, 0L, defaultOrdQty, OrdStatus.FILLED);
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

    @Test(dataProvider = ORDER_STATUS_PROVIDER, expectedExceptions = NullPointerException.class)
    public void testExecutionReportNullSymbol(final OrderStatus orderStatus) {
        executionReport(orderStatus, defaultTuple, null);
    }

    @Test(dataProvider = ORDER_STATUS_PROVIDER, expectedExceptions = IllegalArgumentException.class)
    public void testExecutionReportEmptySymbol(final OrderStatus orderStatus) {
        executionReport(orderStatus, defaultTuple, "");
    }

    @Test(dataProvider = TRADE_ORDER_STATUS_PROVIDER, expectedExceptions = IllegalArgumentException.class)
    public void testExecutionReportTradeOrderStatus(final OrderStatus orderStatus) {
        executionReport(orderStatus, defaultTuple, defaultSymbol);
    }

    @Test(dataProvider = ORDER_STATUS_PROVIDER)
    public void testExecutionReport(final OrderStatus orderStatus) {
        final Order order = orderStatus.getOrder();

        final ExecutionReport executionReport = executionReport(orderStatus, defaultTuple, defaultSymbol);

        if (orderStatus.getOrdStatus().isCanceled()) {
            assertThat(executionReport.getExecType(), is(ExecType.CANCELED.field()));
            assertThat(executionReport.getOrdStatus(), is(OrdStatus.CANCELED.field()));
        }
        else if (orderStatus.getOrdStatus().isRejected()) {
            assertThat(executionReport.getExecType(), is(ExecType.REJECTED.field()));
            assertThat(executionReport.getOrdStatus(), is(OrdStatus.REJECTED.field()));
        }

        assertThat(executionReport.getAvgPx().getValue(), is(orderStatus.getAvgPx()));
        assertThat(executionReport.getClOrdID().getValue(), is(defaultTuple.getClOrdID()));
        assertThat(executionReport.getCumQty().getValue(), is(orderStatus.getCumQty()));
        assertThat(executionReport.getLeavesQty().getValue(), is(orderStatus.getLeavesQty()));
        assertThat(executionReport.getOrderID().getValue(), is(order.getOrderID().toString()));
        assertThat(executionReport.getSide(), is(order.getSide().field()));
        assertThat(executionReport.getSymbol().getValue(), is(defaultSymbol));
        assertThat(executionReport.getLastPx(), nullValue());
        assertThat(executionReport.getLastQty(), nullValue());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testTradeExecutionReportNullOrderStatus() {
        final Order buy = order(buy());
        final Order sell = order(sell());
        final OrderStatus buyOrderStatus = new OrderStatus(buy, defaultPrice, defaultOrdQty - 2L, 2L, OrdStatus.NEW);
        final OrderStatus sellOrderStatus = new OrderStatus(sell, defaultPrice, defaultOrdQty - 1L, 1L, OrdStatus.NEW);
        final Execution execution = new Execution(execID, buyOrderStatus, sellOrderStatus, defaultPrice,
                                                  defaultOrdQty - 1L);
        tradeExecutionReport(null, execution, defaultTuple, defaultSymbol);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testTradeExecutionReportExecution() {
        tradeExecutionReport(mock(OrderStatus.class), null, defaultTuple, defaultSymbol);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testTradeExecutionReportNullExecution() {
        tradeExecutionReport(mock(OrderStatus.class), null, defaultTuple, defaultSymbol);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testTradeExecutionReportNullTuple() {
        final Order buy = order(buy());
        final Order sell = order(sell());
        final OrderStatus buyOrderStatus = new OrderStatus(buy, defaultPrice, defaultOrdQty - 2L, 2L, OrdStatus.NEW);
        final OrderStatus sellOrderStatus = new OrderStatus(sell, defaultPrice, defaultOrdQty - 1L, 1L, OrdStatus.NEW);
        final Execution execution = new Execution(execID, buyOrderStatus, sellOrderStatus, defaultPrice,
                                                  defaultOrdQty - 1L);
        tradeExecutionReport(mock(OrderStatus.class), execution, null, defaultSymbol);
    }

    @Test(dataProvider = TRADE_ORDER_STATUS_PROVIDER, expectedExceptions = NullPointerException.class)
    public void testTradeExecutionReportNullSymbol(final OrderStatus orderStatus) {
        tradeExecutionReport(orderStatus, execution, defaultTuple, null);
    }

    @Test(dataProvider = TRADE_ORDER_STATUS_PROVIDER, expectedExceptions = IllegalArgumentException.class)
    public void testTradeExecutionReportEmptySymbol(final OrderStatus orderStatus) {
        tradeExecutionReport(orderStatus, execution, defaultTuple, "");
    }

    @Test(dataProvider = ORDER_STATUS_PROVIDER, expectedExceptions = IllegalArgumentException.class)
    public void testTradeExecutionReportTradeOrderStatus(final OrderStatus orderStatus) {
        tradeExecutionReport(orderStatus, execution, defaultTuple, defaultSymbol);
    }

    @Test(dataProvider = TRADE_ORDER_STATUS_PROVIDER)
    public void testTradeExecutionReport(final OrderStatus orderStatus) {
        final Order order = orderStatus.getOrder();

        final ExecutionReport executionReport = tradeExecutionReport(orderStatus, execution, defaultTuple,
                                                                     defaultSymbol);

        if (orderStatus.getOrdStatus().isFilled()) {
            assertThat(executionReport.getExecType(), is(ExecType.TRADE.field()));
            assertThat(executionReport.getOrdStatus(), is(OrdStatus.FILLED.field()));
        }
        else if (orderStatus.getOrdStatus().isPartiallyFilled()) {
            assertThat(executionReport.getExecType(), is(ExecType.TRADE.field()));
            assertThat(executionReport.getOrdStatus(), is(OrdStatus.PARTIALLY_FILLED.field()));
        }

        assertThat(executionReport.getAvgPx().getValue(), is(orderStatus.getAvgPx()));
        assertThat(executionReport.getClOrdID().getValue(), is(defaultTuple.getClOrdID()));
        assertThat(executionReport.getCumQty().getValue(), is(orderStatus.getCumQty()));
        assertThat(executionReport.getLeavesQty().getValue(), is(orderStatus.getLeavesQty()));
        assertThat(executionReport.getOrderID().getValue(), is(order.getOrderID().toString()));
        assertThat(executionReport.getSide(), is(order.getSide().field()));
        assertThat(executionReport.getSymbol().getValue(), is(defaultSymbol));
        assertThat(executionReport.getLastPx().getValue(), is(defaultLastPx));
        assertThat(executionReport.getLastQty().getValue(), is(defaultLastQty));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testOrderExecutedNullOrderStatus() {
        orderExecuted(null, mock(Execution.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testOrderExecutedNullExecution() {
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
