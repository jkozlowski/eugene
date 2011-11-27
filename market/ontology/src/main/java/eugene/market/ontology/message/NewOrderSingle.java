package eugene.market.ontology.message;

import eugene.market.ontology.Message;
import eugene.market.ontology.field.ClOrdID;
import eugene.market.ontology.field.OrdType;
import eugene.market.ontology.field.OrderQty;
import eugene.market.ontology.field.Price;
import eugene.market.ontology.field.Side;
import eugene.market.ontology.field.Symbol;
import jade.content.onto.annotations.Element;
import jade.content.onto.annotations.Slot;

/**
 * The {@link NewOrderSingle} message type is used by agents wishing to submit securities orders to a broker for
 * execution.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
@Element(name = NewOrderSingle.TYPE)
public class NewOrderSingle extends Message {

    public static final String TYPE = "D";

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
     * Gets the ordType.
     *
     * @return the ordType.
     */
    @Slot(mandatory = true)
    public OrdType getOrdType() {
        return getField(OrdType.TAGi);
    }

    /**
     * Sets the ordType.
     *
     * @param ordType new ordType.
     */
    public void setOrdType(OrdType ordType) {
        setField(OrdType.TAGi, ordType);
    }

    /**
     * Gets the price.
     *
     * @return the price.
     */
    @Slot
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
