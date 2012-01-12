package eugene.market.ontology.message.data;

import eugene.market.ontology.Message;
import eugene.market.ontology.field.OrderID;
import jade.content.onto.annotations.Element;
import jade.content.onto.annotations.Slot;

/**
 * {@link DeleteOrder} message is sent whenever an open order is completely cancelled. The {@link OrderID} refers to
 * the {@link OrderID} of the original {@link AddOrder} message.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
@Element(name = DeleteOrder.TYPE)
public class DeleteOrder extends Message {

    public static final String TYPE = "BP0x29";

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
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return TYPE;
    }
}
