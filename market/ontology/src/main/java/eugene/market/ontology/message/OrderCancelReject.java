package eugene.market.ontology.message;

import eugene.market.ontology.Message;
import eugene.market.ontology.field.ClOrdID;
import eugene.market.ontology.field.OrdStatus;
import eugene.market.ontology.field.OrderID;
import jade.content.onto.annotations.Element;
import jade.content.onto.annotations.Slot;

/**
 * {@link OrderCancelReject} message is issued by the Market upon receipt of a {@link OrderCancelRequest} message which
 * cannot be honored.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
@Element(name = OrderCancelReject.TYPE)
public class OrderCancelReject extends Message {

    public static final String TYPE = "9";

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
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return TYPE;
    }
}
