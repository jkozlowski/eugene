package eugene.market.esma;

import eugene.market.book.Order;
import eugene.market.ontology.field.OrderID;
import eugene.market.ontology.field.OrderQty;
import eugene.market.ontology.field.Price;
import eugene.market.ontology.field.Symbol;
import eugene.market.ontology.field.enums.OrdType;
import eugene.market.ontology.field.enums.Side;
import eugene.market.ontology.message.NewOrderSingle;
import eugene.market.ontology.message.data.AddOrder;

import java.util.concurrent.atomic.AtomicLong;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Utility methods for dealing with {@link Order}s.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public final class Orders {

    public static final AtomicLong curOrderID = new AtomicLong(1L);

    /**
     * Gets an instance of {@link Order} constructed from this {@link NewOrderSingle}.
     *
     * @param newOrderSingle instance of {@link NewOrderSingle} to initialize an {@link Order} with.
     *
     * @return {@link Order} initialized from this {@link NewOrderSingle}.
     */
    public static Order newOrder(final NewOrderSingle newOrderSingle) throws NewOrderSingleValidationException {

        try {
            checkNotNull(newOrderSingle);

            return new Order(curOrderID.getAndIncrement(), OrdType.getOrdType(newOrderSingle),
                             Side.getSide(newOrderSingle), newOrderSingle.getOrderQty().getValue(),
                             null == newOrderSingle.getPrice().getValue() ? Order.NO_PRICE : newOrderSingle.getPrice().getValue());
        }
        catch (Exception e) {
            throw new NewOrderSingleValidationException(e);
        }
    }

    /**
     * Gets an instance of {@link AddOrder} constructed from this {@link Order} and <code>symbol</code>.
     *
     * @param order  instance of {@link Order} to initialize this {@link AddOrder} with.
     * @param symbol symbol for this {@link Order}.
     *
     * @return {@link AddOrder} initialized from this {@link Order} and <code>symbol</code>.
     */
    public static AddOrder addOrder(final Order order, final String symbol) {
        checkNotNull(order);
        checkNotNull(symbol);

        final AddOrder addOrder = new AddOrder();
        addOrder.setOrderID(new OrderID(order.getOrderID().toString()));
        addOrder.setOrderQty(new OrderQty(order.getOrderQty()));
        addOrder.setPrice(new Price(order.getPrice()));
        addOrder.setSide(order.getSide().field());
        addOrder.setSymbol(new Symbol(symbol));

        return addOrder;
    }

    /**
     * Indicates that {@link NewOrderSingle} message was formed incorrectly.
     */
    public static class NewOrderSingleValidationException extends Exception {

        public NewOrderSingleValidationException(final Throwable throwable) {
            super("NewOrderSingle message was formed incorrectly", throwable);
        }
    }
}
