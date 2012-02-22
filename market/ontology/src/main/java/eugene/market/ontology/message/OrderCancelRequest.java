/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.ontology.message;

import eugene.market.ontology.Message;
import eugene.market.ontology.field.ClOrdID;
import eugene.market.ontology.field.OrderQty;
import eugene.market.ontology.field.Side;
import eugene.market.ontology.field.Symbol;
import jade.content.onto.annotations.Element;
import jade.content.onto.annotations.Slot;

/**
 * {@link OrderCancelRequest} message requests the cancellation of all of the remaining quantity of an existing order.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
@Element(name = OrderCancelRequest.TYPE)
public class OrderCancelRequest extends Message {

    public static final String TYPE = "F";

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
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return TYPE;
    }
}
