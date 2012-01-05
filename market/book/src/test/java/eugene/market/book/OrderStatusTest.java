package eugene.market.book;

import org.testng.annotations.Test;

import static eugene.market.book.MockOrders.order;
import static eugene.market.book.MockOrders.orderQty;
import static eugene.market.book.MockOrders.sell;
import static eugene.market.ontology.Defaults.defaultOrdQty;
import static eugene.market.ontology.Defaults.defaultPrice;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests {@link OrderStatus}.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public class OrderStatusTest {

    @Test
    public void testConstructor() {
        final Order order = orderQty(sell(), defaultOrdQty);
        final OrderStatus orderStatus = new OrderStatus(order, Order.NO_PRICE,
                                                        defaultOrdQty, Order.NO_QTY);
        assertThat(orderStatus.getOrder(), is(order));
        assertThat(orderStatus.getAvgPx(), is(Order.NO_PRICE));
        assertThat(orderStatus.getLeavesQty(), is(defaultOrdQty));
        assertThat(orderStatus.getCumQty(), is(Order.NO_QTY));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullOrder() {
        new OrderStatus(null, Order.NO_PRICE, defaultOrdQty, Order.NO_QTY);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullAvgPx() {
        new OrderStatus(sell(), null, defaultOrdQty, Order.NO_QTY);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullLeavesQty() {
        new OrderStatus(sell(), Order.NO_PRICE, null, Order.NO_QTY);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullCumQty() {
        new OrderStatus(sell(), Order.NO_PRICE, defaultOrdQty, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorAvgPxLessThanNoPrice() {
        new OrderStatus(orderQty(sell(), defaultOrdQty), Order.NO_PRICE - 1, defaultOrdQty, Order.NO_QTY);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorLeavesQtyLessThanNoQty() {
        new OrderStatus(orderQty(sell(), defaultOrdQty), Order.NO_PRICE, Order.NO_QTY - 1, Order.NO_QTY);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorLeavesQtyGreaterThanOrderQty() {
        new OrderStatus(orderQty(sell(), defaultOrdQty), Order.NO_PRICE, defaultOrdQty + 1, Order.NO_QTY);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorCumQtyLessThanNoQty() {
        new OrderStatus(orderQty(sell(), defaultOrdQty), Order.NO_PRICE, Order.NO_QTY, Order.NO_QTY - 1);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorCumQtyGreaterThanOrderQty() {
        new OrderStatus(orderQty(sell(), defaultOrdQty), Order.NO_PRICE, Order.NO_QTY, defaultOrdQty + 1);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorOrderQtyNotEqualToCumQtyPlusLeavesQty() {
        new OrderStatus(orderQty(sell(), defaultOrdQty), Order.NO_PRICE, 1L, defaultOrdQty);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testExecuteNullPrice() {
        final Order order = orderQty(sell(), defaultOrdQty);
        final OrderStatus orderStatus = new OrderStatus(order, Order.NO_PRICE, defaultOrdQty, Order.NO_QTY);

        orderStatus.execute(null, defaultOrdQty);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testExecuteNoPrice() {
        final Order order = orderQty(sell(), defaultOrdQty);
        final OrderStatus orderStatus = new OrderStatus(order, Order.NO_PRICE, defaultOrdQty, Order.NO_QTY);

        orderStatus.execute(Order.NO_PRICE, defaultOrdQty);
    }


    @Test(expectedExceptions = NullPointerException.class)
    public void testExecuteNullQuantity() {
        final Order order = orderQty(sell(), defaultOrdQty);
        final OrderStatus orderStatus = new OrderStatus(order, Order.NO_PRICE, defaultOrdQty, Order.NO_QTY);

        orderStatus.execute(defaultPrice, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testExecuteNoQuantity() {
        final Order order = orderQty(sell(), defaultOrdQty);
        final OrderStatus orderStatus = new OrderStatus(order, Order.NO_PRICE, defaultOrdQty, Order.NO_QTY);

        orderStatus.execute(defaultPrice, Order.NO_QTY);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testExecuteQuantityGreaterThanLeavesQty() {
        final Order order = orderQty(sell(), defaultOrdQty);
        final OrderStatus orderStatus = new OrderStatus(order, Order.NO_PRICE, defaultOrdQty, Order.NO_QTY);

        orderStatus.execute(defaultPrice, defaultOrdQty + 1L);
    }

    @Test
    public void testExecute() {
        final Order order = order(orderQty(sell(), defaultOrdQty));
        final OrderStatus orderStatus = new OrderStatus(order, defaultPrice, defaultOrdQty, Order.NO_QTY);

        final OrderStatus actual = orderStatus.execute(defaultPrice, defaultOrdQty - 1L);

        assertThat(actual.getCumQty(), is(defaultOrdQty - 1L));
        assertThat(actual.getLeavesQty(), is(1L));
        assertThat(actual.getAvgPx(), is(defaultPrice));
    }
}
