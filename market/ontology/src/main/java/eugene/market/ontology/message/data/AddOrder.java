package eugene.market.ontology.message.data;

import eugene.market.ontology.Message;
import eugene.market.ontology.field.OrderID;
import eugene.market.ontology.field.OrderQty;
import eugene.market.ontology.field.Price;
import eugene.market.ontology.field.Side;
import eugene.market.ontology.field.Symbol;
import jade.content.onto.annotations.Element;
import jade.content.onto.annotations.Slot;

/**
 * {@link AddOrder} message represents a newly accepted visible order on the order book. It includes a
 * day-specific {@link OrderID} assigned by the Market to the order.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
@Element(name = AddOrder.TYPE)
public class AddOrder extends Message {

    public static final String TYPE = "BP0x21";

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
     * Gets the orderQty.
     *
     * @return the orderQty.
     */
    @Slot(mandatory = true)
    public OrderQty getOrderQty() {
        return getField(OrderQty.TAGi);
    }

    /**
     * Sets the orderQty.
     *
     * @param orderQty new orderQty.
     */
    public void setOrderQty(OrderQty orderQty) {
        setField(OrderQty.TAGi, orderQty);
    }

    /**
     * Gets the price.
     *
     * @return the price.
     */
    @Slot(mandatory = true)
    public Price getPrice() {
        return getField(Price.TAGi);
    }

    /**
     * Set the price.
     *
     * @param price new price.
     */
    public void setPrice(Price price) {
        setField(Price.TAGi, price);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return TYPE;
    }
}
