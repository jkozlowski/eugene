package eugene.market.esma.impl;

import eugene.market.esma.Order;
import eugene.market.esma.enums.OrdType;
import eugene.market.esma.enums.Side;
import jade.core.AID;

import static eugene.market.esma.enums.OrdType.LIMIT;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Utility methods for constructing utility {@link Order} mocks.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public final class MockOrders {

    public static final AID aid = new AID("test", AID.ISGUID);

    public static final String defaultSymbol = "VOD.L";

    public static final Long defaultOrdQty = 100L;

    public static final Double defaultPrice = 100.0D;

    /**
     * Gets a mocked {@link Order} with {@link MockOrders#defaultOrdQty}, {@link MockOrders#defaultPrice}, {@link
     * Side#BUY, {@link OrdType#LIMIT} and {@link System#nanoTime()}.
     *
     * @return mocked {@link Order}.
     */
    public static Order mockOrder() {
        final Order mock = mock(Order.class);
        when(mock.getOrderQty()).thenReturn(defaultOrdQty);
        when(mock.getSide()).thenReturn(Side.BUY);
        when(mock.getOrdType()).thenReturn(OrdType.LIMIT);
        when(mock.getPrice()).thenReturn(defaultPrice);
        when(mock.getEntryTime()).thenReturn(System.nanoTime());
        return mock;
    }

    /**
     * Gets a mocked {@link Order} with {@link Side#BUY}.
     *
     * @return mocked {@link Order}.
     */
    public static Order buy() {
        final Order order = when(mockOrder().getSide()).thenReturn(Side.BUY).getMock();
        return order;
    }

    /**
     * Gets a mocked {@link Order} with {@link Side#SELL}.
     *
     * @return mocked {@link Order}.
     */
    public static Order sell() {
        final Order order = when(mockOrder().getSide()).thenReturn(Side.SELL).getMock();
        return order;
    }

    /**
     * Gets a mocked {@link Order} with {@link Order#getOrdType()} equal to <code>ordType</code>.
     *
     * @param order   order to set.
     * @param ordType {@link OrdType} to set.
     *
     * @return mocked {@link Order}.
     */
    public static Order ordType(final Order order, final OrdType ordType) {
        return when(order.getOrdType()).thenReturn(ordType).getMock();
    }

    /**
     * Gets a mocked {@link Order} with {@link Order#getOrderQty()} equal to <code>orderQty</code>.
     *
     * @param order   order to set.
     * @param orderQty quantity to set.
     *
     * @return mocked {@link Order}.
     */
    public static Order orderQty(final Order order, final Long orderQty) {
        return when(order.getOrderQty()).thenReturn(orderQty).getMock();
    }

    /**
     * Gets a mocked {@link Order} with {@link Order#getOrdType()} equal to <code>ordType</code> and {@link
     * Order#getEntryTime()} equal to <code>entryTime</code>.
     *
     * @param order     order to set.
     * @param ordType   {@link OrdType} to set.
     * @param entryTime entryTime to set.
     *
     * @return mocked {@link Order}.
     */
    public static Order ordTypeEntryTime(final Order order, final OrdType ordType, final Long entryTime) {
        return when(ordType(order, ordType).getEntryTime()).thenReturn(entryTime).getMock();
    }

    /**
     * Gets a mocked {@link Order} with {@link OrdType#LIMIT} and <code>price</code>.
     *
     * @param order order to set.
     * @param price price to set.
     *
     * @return mocked {@link Order}.
     */
    public static Order limitPrice(final Order order, final Double price) {
        return when(ordType(order, LIMIT).getPrice()).thenReturn(price).getMock();
    }

    /**
     * Gets a mocked {@link Order} with {@link OrdType#LIMIT}, <code>price</code> and <code>orderQty</code>.
     *
     * @param order    order to set.
     * @param orderQty quantity to set.
     * @param price    price to set.
     *
     * @return mocked {@link Order}.
     */
    public static Order limitOrdQtyPrice(final Order order, final Long orderQty, final Double price) {
        final Order mock = when(ordType(order, LIMIT).getPrice()).thenReturn(price).getMock();
        return when(mock.getOrderQty()).thenReturn(orderQty).getMock();
    }

    /**
     * Gets a mocked {@link Order} with {@link Order#getOrdType()} equal to {@link OrdType#LIMIT}, {@link
     * Order#getPrice()} equal to <code>100.0D</code> and {@link Order#getEntryTime()} equal to <code>entryTime</code>.
     *
     * @param order     order to set.
     * @param entryTime entryTime to set.
     *
     * @return mocked {@link Order}.
     */
    public static Order limitConstPriceEntryTime(final Order order, final Long entryTime) {
        return when(limitPrice(order, 100.0D).getEntryTime()).thenReturn(entryTime).getMock();
    }

    /**
     * Gets an {@link Order} from this <code>mock</code>.
     *
     * @param mock mock to turn into {@link Order}.
     *
     * @return {@link Order} from this <code>mock</code>.
     */
    public static Order order(final Order mock) {
        return new Order(mock.getOrderQty(), mock.toString(), defaultSymbol, aid, mock.getSide(), mock
                .getOrdType(), mock.getPrice(), mock.getEntryTime());
    }
}
