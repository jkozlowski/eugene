package eugene.market.ontology.message;

import eugene.market.ontology.Message;
import eugene.market.ontology.field.AvgPx;
import eugene.market.ontology.field.ClOrdID;
import eugene.market.ontology.field.CumQty;
import eugene.market.ontology.field.ExecType;
import eugene.market.ontology.field.LastPx;
import eugene.market.ontology.field.LastQty;
import eugene.market.ontology.field.LeavesQty;
import eugene.market.ontology.field.OrdStatus;
import eugene.market.ontology.field.OrderID;
import eugene.market.ontology.field.Side;
import eugene.market.ontology.field.Symbol;
import jade.content.onto.annotations.Element;
import jade.content.onto.annotations.Slot;

/**
 * The {@link ExecutionReport} message is used to:
 * <ul>
 * <li>Confirm the receipt of an order.</li>
 * <li>Confirm changes to an existing order (i.e. accept cancel and replace requests).</li>
 * <li>Relay order status information.</li>
 * <li>Relay fill information on working orders.</li>
 * <li>Reject orders.</li>
 * </ul>
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
@Element(name = ExecutionReport.TYPE)
public class ExecutionReport extends Message {

    public static final String TYPE = "8";

    /**
     * Gets the execType.
     *
     * @return the execType.
     */
    @Slot(mandatory = true)
    public ExecType getExecType() {
        return getField(ExecType.TAGi);
    }

    /**
     * Sets the execType.
     *
     * @param execType new execType.
     */
    public void setExecType(ExecType execType) {
        setField(ExecType.TAGi, execType);
    }

    /**
     * Gets the symbol.
     *
     * @return the symbol.
     */
    @Slot(mandatory = true)
    public Symbol getSymbol() {
        return getField(Symbol.TAGi);
    }

    /**
     * Sets the symbol.
     *
     * @param symbol new symbol.
     */
    public void setSymbol(Symbol symbol) {
        setField(Symbol.TAGi, symbol);
    }

    /**
     * Gets the side.
     *
     * @return the side.
     */
    @Slot(mandatory = true)
    public Side getSide() {
        return getField(Side.TAGi);
    }

    /**
     * Sets the side.
     *
     * @param side new side.
     */
    public void setSide(Side side) {
        setField(Side.TAGi, side);
    }

    /**
     * Gets the leavesQty.
     *
     * @return the leavesQty.
     */
    @Slot(mandatory = true)
    public LeavesQty getLeavesQty() {
        return getField(LeavesQty.TAGi);
    }

    /**
     * Sets the leavesQty.
     *
     * @param leavesQty new leavesQty.
     */
    public void setLeavesQty(LeavesQty leavesQty) {
        setField(LeavesQty.TAGi, leavesQty);
    }

    /**
     * Gets the cumQty.
     *
     * @return the cumQty.
     */
    @Slot(mandatory = true)
    public CumQty getCumQty() {
        return getField(CumQty.TAGi);
    }

    /**
     * Sets the cumQty.
     *
     * @param cumQty new cumQty.
     */
    public void setCumQty(CumQty cumQty) {
        setField(CumQty.TAGi, cumQty);
    }

    /**
     * Gets the avgPx.
     *
     * @return the avgPx.
     */
    @Slot
    public AvgPx getAvgPx() {
        return getField(AvgPx.TAGi);
    }

    /**
     * Sets the avgPx.
     *
     * @param avgPx new avgPx.
     */
    public void setAvgPx(AvgPx avgPx) {
        setField(AvgPx.TAGi, avgPx);
    }

    /**
     * Gets the ordStatus.
     *
     * @return the ordStatus.
     */
    @Slot(mandatory = true)
    public OrdStatus getOrdStatus() {
        return getField(OrdStatus.TAGi);
    }

    /**
     * Sets the ordStatus.
     *
     * @param ordStatus new ordStatus.
     */
    public void setOrdStatus(OrdStatus ordStatus) {
        setField(OrdStatus.TAGi, ordStatus);
    }

    /**
     * Gets the orderID.
     *
     * @return the orderID.
     */
    @Slot(mandatory = true)
    public OrderID getOrderID() {
        return getField(OrderID.TAGi);
    }

    /**
     * Sets the orderID.
     *
     * @param orderID new orderID.
     */
    public void setOrderID(OrderID orderID) {
        setField(OrderID.TAGi, orderID);
    }

    /**
     * Gets the clOrdID.
     *
     * @return the clOrdID.
     */
    @Slot(mandatory = true)
    public ClOrdID getClOrdID() {
        return getField(ClOrdID.TAGi);
    }

    /**
     * Sets the clOrdID.
     *
     * @param clOrdID new clOrdID.
     */
    public void setClOrdID(ClOrdID clOrdID) {
        setField(ClOrdID.TAGi, clOrdID);
    }

    /**
     * Gets the lastPx.
     *
     * @return the lastPx.
     */
    @Slot(mandatory = false)
    public LastPx getLastPx() {
        return getField(LastPx.TAGi);
    }

    /**
     * Sets the lastPx.
     *
     * @param lastPx new lastPx.
     */
    public void setLastPx(LastPx lastPx) {
        setField(LastPx.TAGi, lastPx);
    }

    /**
     * Gets the lastQty.
     *
     * @return the lastQty.
     */
    @Slot(mandatory = false)
    public LastQty getLastQty() {
        return getField(LastQty.TAGi);
    }

    /**
     * Sets the lastQty.
     *
     * @param lastQty new lastQty.
     */
    public void setLastQty(LastQty lastQty) {
        setField(LastQty.TAGi, lastQty);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return TYPE;
    }
}
