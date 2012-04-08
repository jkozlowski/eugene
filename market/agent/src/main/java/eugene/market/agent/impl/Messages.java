/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.agent.impl;

import com.google.common.annotations.VisibleForTesting;
import eugene.market.agent.impl.execution.Execution;
import eugene.market.book.Order;
import eugene.market.book.OrderStatus;
import eugene.market.agent.impl.Repository.Tuple;
import eugene.market.ontology.Message;
import eugene.market.ontology.field.AvgPx;
import eugene.market.ontology.field.ClOrdID;
import eugene.market.ontology.field.CumQty;
import eugene.market.ontology.field.LastPx;
import eugene.market.ontology.field.LastQty;
import eugene.market.ontology.field.LeavesQty;
import eugene.market.ontology.field.OrderID;
import eugene.market.ontology.field.OrderQty;
import eugene.market.ontology.field.Price;
import eugene.market.ontology.field.Symbol;
import eugene.market.ontology.field.TradeID;
import eugene.market.ontology.field.enums.ExecType;
import eugene.market.ontology.field.enums.OrdStatus;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.data.AddOrder;
import eugene.market.ontology.message.data.DeleteOrder;
import eugene.market.ontology.message.data.OrderExecuted;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Utility methods for creating {@link Message}s.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public final class Messages {

    private static final String ERROR_MESSAGE = "This class should not be instantiated";

    /**
     * This constructor should not be invoked. It is only visible for the purposes of keeping global test coverage
     * high.
     *
     * @throws UnsupportedOperationException this constructor should not be invoked.
     */
    @VisibleForTesting
    public Messages() throws UnsupportedOperationException {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    /**
     * Gets a {@link ExecutionReport} {@link OrdStatus#CANCELED}, {@link OrdStatus#REJECTED} or {@link OrdStatus#NEW}
     * message for this <code>orderStatus</code>, <code>tuple</code> and <code>symbol</code>.
     *
     * @param orderStatus {@link OrderStatus} to initialize the {@link ExecutionReport} with.
     * @param tuple       {@link Tuple} to initialize the {@link ExecutionReport} with.
     * @param symbol      <code>symbol</code> to initialize the {@link ExecutionReport} with.
     *
     * @return initialized {@link ExecutionReport}.
     *
     * @throws NullPointerException     if either argument is null.
     * @throws IllegalArgumentException if <code>symbol</code> is empty.
     * @throws IllegalArgumentException if {@link OrderStatus#getOrdStatus()} is {@link OrdStatus#FILLED} or {@link
     *                                  OrdStatus#PARTIALLY_FILLED}.
     * @see #tradeExecutionReport(OrderStatus, Execution, Tuple, String)
     */
    public static ExecutionReport executionReport(final OrderStatus orderStatus, final Tuple tuple,
                                                  final String symbol) {
        checkNotNull(orderStatus);
        checkNotNull(tuple);
        checkNotNull(symbol);
        checkArgument(!symbol.isEmpty());
        checkArgument(!orderStatus.getOrdStatus().isFilled() && !orderStatus.getOrdStatus().isPartiallyFilled());

        final ExecutionReport executionReport = new ExecutionReport();

        switch (orderStatus.getOrdStatus()) {

            case NEW:
                executionReport.setExecType(ExecType.NEW.field());
                executionReport.setOrdStatus(OrdStatus.NEW.field());
                break;

            case CANCELED:
                executionReport.setExecType(ExecType.CANCELED.field());
                executionReport.setOrdStatus(OrdStatus.CANCELED.field());
                break;

            case REJECTED:
                executionReport.setExecType(ExecType.REJECTED.field());
                executionReport.setOrdStatus(OrdStatus.REJECTED.field());
                break;

            default:
                throw new IllegalArgumentException();
        }

        executionReport.setClOrdID(new ClOrdID(tuple.getClOrdID()));
        executionReport.setAvgPx(new AvgPx(orderStatus.getAvgPx()));
        executionReport.setCumQty(new CumQty(orderStatus.getCumQty()));
        executionReport.setLeavesQty(new LeavesQty(orderStatus.getLeavesQty()));
        executionReport.setOrderID(new OrderID(orderStatus.getOrder().getOrderID().toString()));
        executionReport.setSide(orderStatus.getOrder().getSide().field());
        executionReport.setSymbol(new Symbol(symbol));

        return executionReport;
    }

    /**
     * Gets a {@link ExecutionReport} {@link OrdStatus#FILLED} or {@link OrdStatus#PARTIALLY_FILLED} message for this
     * <code>orderStatus</code>, <code>lastQty</code>, <code>lastPx</code>, <code>tuple</code> and <code>symbol</code>.
     *
     * @param orderStatus {@link OrderStatus} to initialize the {@link ExecutionReport} with.
     * @param execution   {@link Execution} to initialize the {@link ExecutionReport} with.
     * @param tuple       {@link Tuple} to initialize the {@link ExecutionReport} with.
     * @param symbol      <code>symbol</code> to initialize the {@link ExecutionReport} with.
     *
     * @return initialized {@link ExecutionReport}.
     *
     * @throws NullPointerException     if either argument is null.
     * @throws IllegalArgumentException if <code>symbol</code> is empty.
     * @throws IllegalArgumentException if {@link OrderStatus#getOrdStatus()} is {@link OrdStatus#NEW}, {@link
     *                                  OrdStatus#CANCELED} or {@link OrdStatus#REJECTED}.
     * @see #executionReport(OrderStatus, Tuple, String)
     */
    public static ExecutionReport tradeExecutionReport(final OrderStatus orderStatus, final Execution execution,
                                                       final Tuple tuple, final String symbol) {

        checkNotNull(orderStatus);
        checkNotNull(tuple);
        checkNotNull(symbol);
        checkArgument(!symbol.isEmpty());
        checkArgument(orderStatus.getOrdStatus().isFilled() || orderStatus.getOrdStatus().isPartiallyFilled());

        final ExecutionReport executionReport = new ExecutionReport();

        switch (orderStatus.getOrdStatus()) {

            case FILLED:
                executionReport.setExecType(ExecType.TRADE.field());
                executionReport.setOrdStatus(OrdStatus.FILLED.field());
                break;

            case PARTIALLY_FILLED:
                executionReport.setExecType(ExecType.TRADE.field());
                executionReport.setOrdStatus(OrdStatus.PARTIALLY_FILLED.field());
                break;

            default:
                throw new IllegalArgumentException();
        }

        executionReport.setClOrdID(new ClOrdID(tuple.getClOrdID()));
        executionReport.setAvgPx(new AvgPx(orderStatus.getAvgPx()));
        executionReport.setCumQty(new CumQty(orderStatus.getCumQty()));
        executionReport.setLeavesQty(new LeavesQty(orderStatus.getLeavesQty()));
        executionReport.setOrderID(new OrderID(orderStatus.getOrder().getOrderID().toString()));
        executionReport.setSide(orderStatus.getOrder().getSide().field());
        executionReport.setSymbol(new Symbol(symbol));
        executionReport.setLastQty(new LastQty(execution.getQuantity()));
        executionReport.setLastPx(new LastPx(execution.getPrice()));

        return executionReport;
    }


    /**
     * Gets a {@link OrderExecuted} message for this <code>orderStatus</code> and <code>execution</code>.
     *
     * @param orderStatus {@link OrderStatus} to initialize the {@link OrderExecuted} with.
     * @param execution   {@link Execution} to initialize the {@link OrderExecuted} with.
     *
     * @return initialized {@link OrderExecuted}.
     */
    public static OrderExecuted orderExecuted(final OrderStatus orderStatus, final Execution execution) {
        checkNotNull(orderStatus);
        checkNotNull(execution);
        final OrderExecuted orderExecuted = new OrderExecuted();
        orderExecuted.setTradeID(new TradeID(execution.getExecID().toString()));
        orderExecuted.setLastPx(new LastPx(execution.getPrice()));
        orderExecuted.setLeavesQty(new LeavesQty(orderStatus.getLeavesQty()));
        orderExecuted.setLastQty(new LastQty(execution.getQuantity()));
        orderExecuted.setOrderID(new OrderID(orderStatus.getOrder().getOrderID().toString()));
        return orderExecuted;
    }

    /**
     * Gets a {@link DeleteOrder} message for this <code>orderStatus</code>.
     *
     * @param orderStatus {@link OrderStatus} to initialize the {@link DeleteOrder} with.
     *
     * @return initialized {@link DeleteOrder}.
     */
    public static DeleteOrder deleteOrder(final OrderStatus orderStatus) {
        checkNotNull(orderStatus);
        final DeleteOrder deleteOrder = new DeleteOrder();
        deleteOrder.setOrderID(new OrderID(orderStatus.getOrder().getOrderID().toString()));
        return deleteOrder;
    }

    /**
     * Gets an instance of {@link AddOrder} constructed from this {@link OrderStatus} and <code>symbol</code>.
     *
     * @param orderStatus instance of {@link OrderStatus} to initialize this {@link AddOrder} with.
     * @param symbol      symbol for this {@link Order}.
     *
     * @return {@link AddOrder} initialized from this {@link Order} and <code>symbol</code>.
     */
    public static AddOrder addOrder(final OrderStatus orderStatus, final String symbol) {

        checkNotNull(orderStatus);
        checkArgument(orderStatus.getOrder().getOrdType().isLimit());
        checkNotNull(symbol);

        final Order order = orderStatus.getOrder();

        final AddOrder addOrder = new AddOrder();
        addOrder.setOrderID(new OrderID(order.getOrderID().toString()));
        addOrder.setOrderQty(new OrderQty(orderStatus.getLeavesQty()));
        addOrder.setPrice(new Price(order.getPrice()));
        addOrder.setSide(order.getSide().field());
        addOrder.setSymbol(new Symbol(symbol));

        return addOrder;
    }
}
