package eugene.market.esma;

import eugene.market.esma.Repository.Tuple;
import eugene.market.esma.enums.ExecType;
import eugene.market.esma.enums.OrdStatus;
import eugene.market.esma.execution.book.OrderStatus;
import eugene.market.esma.execution.book.TradeReport;
import eugene.market.ontology.Message;
import eugene.market.ontology.field.AvgPx;
import eugene.market.ontology.field.ClOrdID;
import eugene.market.ontology.field.CumQty;
import eugene.market.ontology.field.LastPx;
import eugene.market.ontology.field.LastQty;
import eugene.market.ontology.field.LeavesQty;
import eugene.market.ontology.field.OrderID;
import eugene.market.ontology.field.Symbol;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.data.DeleteOrder;
import eugene.market.ontology.message.data.OrderExecuted;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Utility methods for creating {@link Message}s.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public final class Messages {

    /**
     * Gets a {@link ExecutionReport} message for this <code>orderStatus</code>,
     * <code>tuple</code> and <code>symbol</code>.
     *
     * @param orderStatus {@link OrderStatus} to initialize the {@link ExecutionReport} with.
     * @param tuple       {@link Tuple} to initialize the {@link ExecutionReport} with.
     * @param symbol      <code>symbol</code> to initialize the {@link ExecutionReport} with.
     *
     * @return initialized {@link ExecutionReport}.
     */
    public static ExecutionReport executionReport(final OrderStatus orderStatus, final Tuple tuple,
                                                  final String symbol) {
        checkNotNull(orderStatus);
        checkNotNull(tuple);
        final ExecutionReport executionReport = new ExecutionReport();
        executionReport.setClOrdID(new ClOrdID(tuple.getClOrdID()));
        executionReport.setAvgPx(new AvgPx(orderStatus.getAvgPx()));
        executionReport.setCumQty(new CumQty(orderStatus.getCumQty()));
        executionReport.setExecType(orderStatus.isEmpty() ? ExecType.NEW.field() : (orderStatus.isFilled() ?
                ExecType.FILL.field() : ExecType.PARTIAL_FILL.field()));
        executionReport.setLeavesQty(new LeavesQty(orderStatus.getLeavesQty()));
        executionReport.setOrderID(new OrderID(orderStatus.getOrder().getOrderID().toString()));
        executionReport.setOrdStatus(orderStatus.isEmpty() ? OrdStatus.NEW.field() : (orderStatus.isFilled() ?
                OrdStatus.FILLED.field() :
                OrdStatus.PARTIALLY_FILLED.field()));
        executionReport.setSide(orderStatus.getOrder().getSide().field());
        executionReport.setSymbol(new Symbol(symbol));
        return executionReport;
    }

    /**
     * Gets a {@link OrderExecuted} message for this <code>orderStatus</code> and <code>tradeReport</code>.
     *
     * @param orderStatus {@link OrderStatus} to initialize the {@link OrderExecuted} with.
     * @param tradeReport {@link TradeReport} to initialize the {@link OrderExecuted} with.
     *
     * @return initialized {@link OrderExecuted}.
     */
    public static OrderExecuted orderExecuted(final OrderStatus orderStatus, final TradeReport tradeReport) {
        checkNotNull(orderStatus);
        checkNotNull(tradeReport);
        final OrderExecuted orderExecuted = new OrderExecuted();
        orderExecuted.setLastPx(new LastPx(tradeReport.getPrice()));
        orderExecuted.setLeavesQty(new LeavesQty(orderStatus.getLeavesQty()));
        orderExecuted.setLastQty(new LastQty(tradeReport.getQuantity()));
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
}
