package eugene.market.ontology.message.data;

import eugene.market.ontology.Message;
import eugene.market.ontology.field.Symbol;
import jade.content.onto.annotations.Element;
import jade.content.onto.annotations.Slot;

/**
 * The login message is the first message sent by the Agent to the Market in order to subscribe to the data feed of a
 * particular {@link Symbol}.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
@Element(name = Login.TYPE)
public class Login extends Message {

    public static final String TYPE = "BP0x01";

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
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return TYPE;
    }
}
