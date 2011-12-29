package eugene.market.ontology.message;

import eugene.market.ontology.Message;
import eugene.market.ontology.field.SessionStatus;
import eugene.market.ontology.field.Symbol;
import jade.content.onto.annotations.Element;
import jade.content.onto.annotations.Slot;

/**
 * {@link Logon} message is the first message sent by the Agent to the Market in order to register with the Market.
 * As a result, the Agent will receive data feed updates.
 *
 * If logon was successful, the Market will send back the same {@link Logon} message with {@link Logon#getSessionStatus
 * ()} equal to {@link SessionStatus#SESSION_ACTIVE}, otherwise this field will be <code>null</code>.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
@Element(name = Logon.TYPE)
public class Logon extends Message {

    public static final String TYPE = "A";

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
     * Gets the sessionStatus.
     *
     * @return the sessionStatus.
     */
    @Slot
    public SessionStatus getSessionStatus() {
        return getField(SessionStatus.TAGi);
    }

    /**
     * Sets the sessionStatus.
     *
     * @param sessionStatus new sessionStatus.
     */
    public void setSessionStatus(SessionStatus sessionStatus) {
        setField(SessionStatus.TAGi, sessionStatus);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return TYPE;
    }
}
