package eugene.market.esma;

import eugene.market.esma.enums.OrdType;
import eugene.market.esma.enums.Side;
import eugene.market.ontology.message.NewOrderSingle;
import jade.core.AID;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Utility methods for dealing with {@link Order}s.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public final class Orders {

    /**
     * Gets an instance of {@link Order} constructed from this {@link NewOrderSingle}.
     *
     * @param newOrderSingle instance of {@link NewOrderSingle} to intitialize an {@link Order} with.
     *
     * @return {@link Order} intialized from this {@link NewOrderSingle}.
     */
    public static Order newOrder(final NewOrderSingle newOrderSingle, final AID aid) {
        checkNotNull(newOrderSingle, "newOrderSingle cannot be null");
        checkNotNull(aid, "aid cannot be null");

        return new Order(newOrderSingle.getOrderQty().getValue(), newOrderSingle.getClOrdID().getValue(),
                         newOrderSingle.getSymbol().getValue(), aid, Side.getSide(newOrderSingle),
                         OrdType.getOrdType(newOrderSingle), newOrderSingle.getPrice().getValue());
    }
}
