package eugene.market.ontology.message.data;

import eugene.market.ontology.field.TradeID;
import eugene.market.ontology.Message;
import eugene.market.ontology.field.LastPx;
import eugene.market.ontology.field.LastQty;
import eugene.market.ontology.field.LeavesQty;
import eugene.market.ontology.field.OrderID;
import jade.content.onto.annotations.Element;
import jade.content.onto.annotations.Slot;

/**
 * {@link OrderExecuted} messages are sent when a visible order on the order book is executed in whole or in part at
 * a price. The {@link OrderID} refers to the {@link OrderID} of the original {@link AddOrder} message.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
@Element(name = OrderExecuted.TYPE)
public class OrderExecuted extends Message {

    public static final String TYPE = "BP0x23";

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
     * Gets the lastQty.
     *
     * @return the lastQty.
     */
    @Slot(mandatory = true)
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
     * Gets the lastPx.
     *
     * @return the lastPx.
     */
    @Slot(mandatory = true)
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
     * Gets the tradeID.
     *
     * @return the tradeID.
     */
    @Slot(mandatory = true)
    public TradeID getTradeID() {
        return getField(TradeID.TAGi);
    }

    /**
     * Sets the tradeID.
     *
     * @param tradeID new tradeID.
     */
    public void setTradeID(TradeID tradeID) {
        setField(TradeID.TAGi, tradeID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return TYPE;
    }
}
