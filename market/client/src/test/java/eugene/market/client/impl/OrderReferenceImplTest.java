package eugene.market.client.impl;

import eugene.market.book.Order;
import eugene.market.client.OrderReference;
import eugene.market.client.Session;
import eugene.market.ontology.field.enums.OrdStatus;
import eugene.market.ontology.field.enums.OrdType;
import eugene.market.ontology.field.enums.Side;
import org.testng.annotations.Test;

import static eugene.market.ontology.Defaults.defaultClOrdID;
import static eugene.market.ontology.Defaults.defaultOrdQty;
import static eugene.market.ontology.Defaults.defaultPrice;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

/**
 * Tests {@link OrderReferenceImpl}.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public class OrderReferenceImplTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullSession() {
        new OrderReferenceImpl(null, defaultClOrdID, System.currentTimeMillis(), OrdType.LIMIT, Side.BUY,
                               defaultOrdQty, defaultPrice);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullClOrdID() {
        new OrderReferenceImpl(mock(Session.class), null, System.currentTimeMillis(), OrdType.LIMIT,
                               Side.BUY, defaultOrdQty, defaultPrice);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorEmptyClOrdID() {
        new OrderReferenceImpl(mock(Session.class), "", System.currentTimeMillis(), OrdType.LIMIT,
                               Side.BUY, defaultOrdQty, defaultPrice);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullCreationTime() {
        new OrderReferenceImpl(mock(Session.class), defaultClOrdID, null, OrdType.LIMIT, Side.BUY,
                               defaultOrdQty, defaultPrice);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullOrdType() {
        new OrderReferenceImpl(mock(Session.class), defaultClOrdID, System.currentTimeMillis(), null, Side.BUY,
                               defaultOrdQty, defaultPrice);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullSide() {
        new OrderReferenceImpl(mock(Session.class), defaultClOrdID, System.currentTimeMillis(), OrdType.LIMIT, null,
                               defaultOrdQty, defaultPrice);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullOrdQty() {
        new OrderReferenceImpl(mock(Session.class), defaultClOrdID, System.currentTimeMillis(), OrdType.LIMIT, Side.BUY,
                               null, defaultPrice);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorZeroQuantity() {
        new OrderReferenceImpl(mock(Session.class), defaultClOrdID, System.currentTimeMillis(), OrdType.LIMIT, Side.BUY,
                               0L, defaultPrice);
    }


    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullPrice() {
        new OrderReferenceImpl(mock(Session.class), defaultClOrdID, System.currentTimeMillis(), OrdType.LIMIT, Side.BUY,
                               defaultOrdQty, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorLimitNoPrice() {
        new OrderReferenceImpl(mock(Session.class), defaultClOrdID, System.currentTimeMillis(), OrdType.LIMIT, Side.BUY,
                               defaultOrdQty, 0.0D);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorMarketWithPrice() {
        new OrderReferenceImpl(mock(Session.class), defaultClOrdID, System.currentTimeMillis(), OrdType.MARKET,
                               Side.BUY,
                               defaultOrdQty, defaultPrice);
    }

    @Test
    public void testConstructor() {
        final OrderReference ref = new OrderReferenceImpl(mock(Session.class), defaultClOrdID, 123L, OrdType.LIMIT,
                                                          Side.BUY, defaultOrdQty, defaultPrice);

        assertThat(ref.getAvgPx(), is(Order.NO_PRICE));
        assertThat(ref.getClOrdID(), is(defaultClOrdID));
        assertThat(ref.getCreationTime(), is(123L));
        assertThat(ref.getCumQty(), is(Order.NO_QTY));
        assertThat(ref.getLeavesQty(), is(defaultOrdQty));
        assertThat(ref.getOrderQty(), is(defaultOrdQty));
        assertThat(ref.getOrdStatus(), is(OrdStatus.NEW));
        assertThat(ref.getOrdType(), is(OrdType.LIMIT));
        assertThat(ref.getPrice(), is(defaultPrice));
        assertThat(ref.getSide(), is(Side.BUY));
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testRejectCancelled() {
        final OrderReferenceImpl ref = new OrderReferenceImpl(mock(Session.class), defaultClOrdID, 123L, OrdType.LIMIT,
                                                              Side.BUY, defaultOrdQty, defaultPrice);
        ref.cancel();
        ref.reject();
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testCancelRejected() {
        final OrderReferenceImpl ref = new OrderReferenceImpl(mock(Session.class), defaultClOrdID, 123L, OrdType.LIMIT,
                                                              Side.BUY, defaultOrdQty, defaultPrice);

        ref.reject();
        ref.cancel();
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testExecuteNullPrice() {
        final OrderReferenceImpl ref = new OrderReferenceImpl(mock(Session.class), defaultClOrdID, 123L, OrdType.LIMIT,
                                                              Side.BUY, defaultOrdQty, defaultPrice);
        ref.execute(null, defaultOrdQty);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testExecuteNoPrice() {
        final OrderReferenceImpl ref = new OrderReferenceImpl(mock(Session.class), defaultClOrdID, 123L, OrdType.LIMIT,
                                                              Side.BUY, defaultOrdQty, defaultPrice);
        ref.execute(Order.NO_PRICE, defaultOrdQty);
    }


    @Test(expectedExceptions = NullPointerException.class)
    public void testExecuteNullQuantity() {
        final OrderReferenceImpl ref = new OrderReferenceImpl(mock(Session.class), defaultClOrdID, 123L, OrdType.LIMIT,
                                                              Side.BUY, defaultOrdQty, defaultPrice);
        ref.execute(defaultPrice, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testExecuteNoQuantity() {
        final OrderReferenceImpl ref = new OrderReferenceImpl(mock(Session.class), defaultClOrdID, 123L, OrdType.LIMIT,
                                                              Side.BUY, defaultOrdQty, defaultPrice);
        ref.execute(defaultPrice, Order.NO_QTY);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testExecuteQuantityGreaterThanLeavesQty() {
        final OrderReferenceImpl ref = new OrderReferenceImpl(mock(Session.class), defaultClOrdID, 123L, OrdType.LIMIT,
                                                              Side.BUY, defaultOrdQty, defaultPrice);
        ref.execute(defaultPrice, defaultOrdQty + 1L);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testExecuteOrderCanceled() {
        final OrderReferenceImpl ref = new OrderReferenceImpl(mock(Session.class), defaultClOrdID, 123L, OrdType.LIMIT,
                                                              Side.BUY, defaultOrdQty, defaultPrice);
        ref.cancel();
        ref.execute(defaultPrice, defaultOrdQty);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testExecuteOrderRejected() {
        final OrderReferenceImpl ref = new OrderReferenceImpl(mock(Session.class), defaultClOrdID, 123L, OrdType.LIMIT,
                                                              Side.BUY, defaultOrdQty, defaultPrice);
        ref.reject();
        ref.execute(defaultPrice, defaultOrdQty);
    }

    @Test
    public void testExecutePartialFill() {
        final OrderReferenceImpl ref = new OrderReferenceImpl(mock(Session.class), defaultClOrdID, 123L, OrdType.LIMIT,
                                                              Side.BUY, defaultOrdQty, defaultPrice);

        ref.execute(defaultPrice, defaultOrdQty - 1L);

        assertThat(ref.getCumQty(), is(defaultOrdQty - 1L));
        assertThat(ref.getLeavesQty(), is(1L));
        assertThat(ref.getOrdStatus(), is(OrdStatus.PARTIALLY_FILLED));
        assertThat(ref.getAvgPx(), is(defaultPrice));
    }

    @Test
    public void testExecuteFill() {
        final OrderReferenceImpl ref = new OrderReferenceImpl(mock(Session.class), defaultClOrdID, 123L, OrdType.LIMIT,
                                                              Side.BUY, defaultOrdQty, defaultPrice);

        ref.execute(defaultPrice, defaultOrdQty);

        assertThat(ref.getCumQty(), is(defaultOrdQty));
        assertThat(ref.getLeavesQty(), is(0L));
        assertThat(ref.getOrdStatus(), is(OrdStatus.FILLED));
        assertThat(ref.getAvgPx(), is(defaultPrice));
    }
}
