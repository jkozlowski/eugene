package eugene.market.client.impl.application;

import eugene.market.book.Order;
import eugene.market.book.OrderBook;
import eugene.market.book.OrderStatus;
import eugene.market.client.ApplicationAdapter;
import eugene.market.client.Session;
import eugene.market.ontology.Message;
import eugene.market.ontology.field.enums.OrdType;
import eugene.market.ontology.field.enums.Side;
import eugene.market.ontology.message.data.AddOrder;
import eugene.market.ontology.message.data.DeleteOrder;
import eugene.market.ontology.message.data.OrderExecuted;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Collections.unmodifiableMap;

/**
 * Builds the {@link OrderBook} from {@link Message}s.
 *
 * @author Jakub D Kozlowski
 * @since 0.5
 */
public class OrderBookApplication extends ApplicationAdapter {

    private final Map<String, Order> orderMap;

    private final OrderBook orderBook;

    /**
     * Creates a {@link OrderBookApplication} that will build this <code>orderBook</code>.
     *
     * @param orderBook {@link OrderBook} to build.
     */
    public OrderBookApplication(final OrderBook orderBook) {
        checkNotNull(orderBook);
        this.orderBook = orderBook;
        this.orderMap = new HashMap<String, Order>();
    }

    /**
     * Gets a read-only view of the <code>orderMap</code>.
     *
     * @return view of the <code>orderMap</code>.
     */
    public Map<String, Order> getOrderMap() {
        return unmodifiableMap(orderMap);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NumberFormatException if {@link AddOrder#getOrderID()} is not parseable by {@link Long#parseLong
     *                               (String)}.
     */
    @Override
    public void toApp(final AddOrder addOrder, final Session session) throws NumberFormatException {

        final Long orderID = Long.parseLong(addOrder.getOrderID().getValue());
        final Order order = new Order(orderID, OrdType.LIMIT, Side.parse(addOrder.getSide()),
                                      addOrder.getOrderQty().getValue(), addOrder.getPrice().getValue());

        orderBook.insertOrder(order);
        orderMap.put(addOrder.getOrderID().getValue(), order);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if <code>deleteOrder</code> refers to an {@link Order} for which a {@link
     *                              AddOrder} message has not been previously received.
     */
    @Override
    public void toApp(final DeleteOrder deleteOrder, final Session session) throws NullPointerException {

        final Order order = orderMap.get(deleteOrder.getOrderID().getValue());

        checkNotNull(order);

        orderBook.cancel(order);
        orderMap.remove(deleteOrder.getOrderID().getValue());
    }

    @Override
    public void toApp(final OrderExecuted orderExecuted, final Session session) {

        final Order order = orderMap.get(orderExecuted.getOrderID().getValue());

        checkNotNull(order);
        checkState(order.equals(orderBook.peek(order.getSide())));

        final OrderStatus orderStatus = orderBook.execute(order.getSide(), orderExecuted.getLastQty().getValue(),
                                                          orderExecuted.getLastPx().getValue());

        checkState(orderStatus.getLeavesQty().equals(orderExecuted.getLeavesQty().getValue()));
    }
}
