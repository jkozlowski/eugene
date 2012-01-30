package eugene.market.book.impl;

import eugene.market.book.Order;
import eugene.market.book.OrderBook;
import eugene.market.book.OrderStatus;
import eugene.market.ontology.field.enums.OrdStatus;
import eugene.market.ontology.field.enums.OrdType;
import eugene.market.ontology.field.enums.Side;
import org.testng.annotations.Test;

import static eugene.market.book.MockOrders.buy;
import static eugene.market.book.MockOrders.ordType;
import static eugene.market.book.MockOrders.order;
import static eugene.market.book.MockOrders.orderQty;
import static eugene.market.book.MockOrders.sell;
import static eugene.market.ontology.Defaults.defaultOrdQty;
import static eugene.market.ontology.Defaults.defaultPrice;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

/**
 * Tests {@link DefaultOrderBook}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public class DefaultOrderBookTest {

    @Test
    public void testNewBookEmpty() {
        final OrderBook orderBook = new DefaultOrderBook();

        assertThat(orderBook.isEmpty(Side.SELL), is(true));
        assertThat(orderBook.size(Side.SELL), is(0));
        assertThat(orderBook.peek(Side.SELL), nullValue());

        assertThat(orderBook.isEmpty(Side.BUY), is(true));
        assertThat(orderBook.size(Side.BUY), is(0));
        assertThat(orderBook.peek(Side.BUY), nullValue());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testInsertNullOrder() {
        final OrderBook orderBook = new DefaultOrderBook();
        orderBook.insert(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testInsertNullOrderStatus() {
        final OrderBook orderBook = new DefaultOrderBook();
        orderBook.insert(order(buy()), null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInsertOrderStatusOfDifferentOrder() {
        final OrderBook orderBook = new DefaultOrderBook();
        orderBook.insert(order(buy()), new OrderStatus(order(buy())));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInsertMarket() {
        final OrderBook orderBook = new DefaultOrderBook();
        orderBook.insert(order(buy()), new OrderStatus(order(ordType(buy(), OrdType.MARKET))));
    }

    @Test
    public void testInsertLimitBuy() {
        final OrderBook orderBook = new DefaultOrderBook();
        final Order buy = order(buy());
        final OrderStatus orderStatus = orderBook.insert(buy);

        assertThat(orderBook.isEmpty(Side.SELL), is(true));
        assertThat(orderBook.size(Side.SELL), is(0));
        assertThat(orderBook.peek(Side.SELL), nullValue());
        assertThat(orderBook.isEmpty(Side.BUY), is(false));

        assertThat(orderBook.size(Side.BUY), is(1));
        assertThat(orderBook.peek(Side.BUY), is(buy));
        assertThat(buy, isIn(orderBook.getBuyOrders()));
        assertThat(orderStatus, sameInstance(orderBook.getOrderStatus(buy)));
        assertThat(orderStatus.isEmpty(), is(true));
    }

    @Test
    public void testInsertLimitSell() {
        final OrderBook orderBook = new DefaultOrderBook();
        final Order sell = order(sell());
        final OrderStatus orderStatus = orderBook.insert(sell);

        assertThat(orderBook.isEmpty(Side.BUY), is(true));
        assertThat(orderBook.size(Side.BUY), is(0));
        assertThat(orderBook.peek(Side.BUY), nullValue());

        assertThat(orderBook.isEmpty(Side.SELL), is(false));
        assertThat(orderBook.size(Side.SELL), is(1));
        assertThat(orderBook.peek(Side.SELL), is(sell));
        assertThat(sell, isIn(orderBook.getSellOrders()));
        assertThat(orderStatus, sameInstance(orderBook.getOrderStatus(sell)));
        assertThat(orderStatus.isEmpty(), is(true));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testInsertPartiallyExecutedNullOrderStatus() {
        new DefaultOrderBook().insert(order(sell()), null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testInsertPartiallyExecutedDifferentOrderInOrderStatus() {
        new DefaultOrderBook().insert(order(sell()), new OrderStatus(order(buy())));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testExecuteNullSide() {
        final OrderBook orderBook = new DefaultOrderBook();
        orderBook.execute(null, defaultOrdQty, defaultPrice);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testExecuteNullOrderQty() {
        final OrderBook orderBook = new DefaultOrderBook();
        orderBook.execute(Side.BUY, null, defaultPrice);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testExecuteNullPrice() {
        final OrderBook orderBook = new DefaultOrderBook();
        orderBook.execute(Side.BUY, defaultOrdQty, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testExecuteNoPrice() {
        final OrderBook orderBook = new DefaultOrderBook();
        orderBook.execute(Side.BUY, defaultOrdQty, Order.NO_PRICE);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testExecuteNoOrderQty() {
        final OrderBook orderBook = new DefaultOrderBook();
        orderBook.execute(Side.BUY, Order.NO_QTY, defaultPrice);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testExecuteSellEmptySellSide() {
        final OrderBook orderBook = new DefaultOrderBook();
        orderBook.execute(Side.SELL, defaultOrdQty, defaultPrice);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testExecuteBuyEmptyBuySide() {
        final OrderBook orderBook = new DefaultOrderBook();
        orderBook.execute(Side.BUY, defaultOrdQty, defaultPrice);
    }

    @Test
    public void testExecutePartialFill() {
        final OrderBook orderBook = new DefaultOrderBook();
        final Order buy = order(orderQty(buy(), defaultOrdQty));
        final Order sell = order(orderQty(sell(), defaultOrdQty));
        orderBook.insert(buy);
        final OrderStatus sellOrderStatus = orderBook.insert(sell);

        final OrderStatus orderStatus = orderBook.execute(Side.BUY, defaultOrdQty - 1L, defaultPrice);

        assertThat(orderStatus.getCumQty(), is(defaultOrdQty - 1));
        assertThat(orderStatus.getAvgPx(), is(defaultPrice));
        assertThat(orderStatus.getLeavesQty(), is(1L));
        assertThat(orderStatus.getOrder(), sameInstance(buy));
        assertThat(orderStatus.isFilled(), is(false));
        assertThat(orderStatus.isEmpty(), is(false));
        assertThat(orderBook.isEmpty(Side.BUY), is(false));
        assertThat(orderBook.isEmpty(Side.SELL), is(false));
        assertThat(orderBook.getOrderStatus(buy), sameInstance(orderStatus));
        assertThat(orderBook.getOrderStatus(sell), sameInstance(sellOrderStatus));
    }

    @Test
    public void testExecuteFill() {
        final OrderBook orderBook = new DefaultOrderBook();
        final Order buy = order(orderQty(buy(), defaultOrdQty));
        final Order sell = order(orderQty(sell(), defaultOrdQty));
        orderBook.insert(buy);
        final OrderStatus sellOrderStatus = orderBook.insert(sell);

        final OrderStatus orderStatus = orderBook.execute(Side.BUY, defaultOrdQty, defaultPrice);

        assertThat(orderStatus.getCumQty(), is(defaultOrdQty));
        assertThat(orderStatus.getAvgPx(), is(defaultPrice));
        assertThat(orderStatus.getLeavesQty(), is(Order.NO_QTY));
        assertThat(orderStatus.getOrder(), sameInstance(buy));
        assertThat(orderStatus.isFilled(), is(true));
        assertThat(orderStatus.isEmpty(), is(false));
        assertThat(orderBook.isEmpty(Side.BUY), is(true));
        assertThat(orderBook.isEmpty(Side.SELL), is(false));
        assertThat(orderBook.getOrderStatus(buy), nullValue());
        assertThat(orderBook.getOrderStatus(sell), sameInstance(sellOrderStatus));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testCancelNullOrder() {
        final OrderBook orderBook = new DefaultOrderBook();
        orderBook.cancel(null);
    }

    @Test
    public void testCancelOrderNotInOrderBook() {
        final OrderBook orderBook = new DefaultOrderBook();
        final Order order = order(buy());
        final OrderStatus orderStatus = orderBook.cancel(order);
        assertThat(orderStatus, nullValue());
    }

    @Test
    public void testCancelOrderInOrderBook() {
        final OrderBook orderBook = new DefaultOrderBook();
        final Order order = order(buy());
        final OrderStatus expected = orderBook.insert(order);
        final OrderStatus actual = orderBook.cancel(order);

        assertThat(actual.getOrdStatus(), is(OrdStatus.CANCELED));
        assertThat(actual.getAvgPx(), is(expected.getAvgPx()));
        assertThat(actual.getCumQty(), is(expected.getCumQty()));
        assertThat(actual.getLeavesQty(), is(expected.getLeavesQty()));
        assertThat(actual.getOrder(), is(expected.getOrder()));
        assertThat(orderBook.getOrderStatus(order), nullValue());
        assertThat(orderBook.getBuyOrders(), not(hasItem(order)));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testSizeNullSide() {
        final OrderBook orderBook = new DefaultOrderBook();
        orderBook.size(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testIsEmptyNullSide() {
        final OrderBook orderBook = new DefaultOrderBook();
        orderBook.isEmpty(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testPeekNullSide() {
        final OrderBook orderBook = new DefaultOrderBook();
        orderBook.peek(null);
    }

    @Test
    public void testToStringSell() {
        final OrderBook orderBook = new DefaultOrderBook();
        final Order buy = order(buy());
        orderBook.insert(buy);
        final Order sell = order(sell());
        orderBook.insert(sell);

        final StringBuilder expected = new StringBuilder();
        expected.append(sell).append("\n");
        expected.append(buy).append("\n");
        assertThat(orderBook, hasToString(equalToIgnoringCase(expected.toString())));
    }
}
