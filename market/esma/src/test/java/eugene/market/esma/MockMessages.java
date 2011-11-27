package eugene.market.esma;

import eugene.market.esma.enums.SessionStatus;
import eugene.market.ontology.Message;
import eugene.market.ontology.field.ClOrdID;
import eugene.market.ontology.field.OrdType;
import eugene.market.ontology.field.OrderQty;
import eugene.market.ontology.field.Price;
import eugene.market.ontology.field.Side;
import eugene.market.ontology.field.Symbol;
import eugene.market.ontology.message.Logon;
import eugene.market.ontology.message.NewOrderSingle;

import static eugene.market.esma.Defaults.defaultClOrdID;
import static eugene.market.esma.Defaults.defaultOrdQty;
import static eugene.market.esma.Defaults.defaultPrice;
import static eugene.market.esma.Defaults.defaultSymbol;

/**
 * Mock {@link Message}s.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public class MockMessages {

    /**
     * Gets the default {@link NewOrderSingle} message.
     *
     * @return default {@link NewOrderSingle} message.
     */
    public static NewOrderSingle newOrderSingle() {
        final NewOrderSingle newOrder = new NewOrderSingle();
        newOrder.setClOrdID(new ClOrdID(defaultClOrdID));
        newOrder.setSide(new Side(Side.BUY));
        newOrder.setOrderQty(new OrderQty(defaultOrdQty));
        newOrder.setPrice(new Price(defaultPrice));
        newOrder.setSymbol(new Symbol(defaultSymbol));
        newOrder.setOrdType(new OrdType(OrdType.LIMIT));
        return newOrder;
    }

    /**
     * Gets the default {@link Logon} message.
     *
     * @return default {@link Logon} message.
     */
    public static Logon login() {
        final Logon logon = new Logon();
        logon.setSymbol(new Symbol(defaultSymbol));
        return logon;
    }

    /**
     * Gets the default {@link Logon} reply message.
     *
     * @return default {@link Logon} reply message.
     */
    public static Logon logonReply() {
        final Logon logon = new Logon();
        logon.setSymbol(new Symbol(defaultSymbol));
        logon.setSessionStatus(SessionStatus.SESSION_ACTIVE.field());
        return logon;
    }
}
