/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.book;

import eugene.market.ontology.field.enums.OrdType;
import eugene.market.ontology.field.enums.Side;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static eugene.market.book.MockOrders.buy;
import static eugene.market.book.MockOrders.limitConstPriceEntryTime;
import static eugene.market.book.MockOrders.limitPrice;
import static eugene.market.book.MockOrders.ordType;
import static eugene.market.book.MockOrders.ordTypeEntryTime;
import static eugene.market.book.MockOrders.order;
import static eugene.market.book.MockOrders.sell;
import static eugene.market.book.Order.AFTER;
import static eugene.market.book.Order.BEFORE;
import static eugene.market.book.Order.SAME;
import static eugene.market.ontology.Defaults.curOrderID;
import static eugene.market.ontology.Defaults.defaultOrdQty;
import static eugene.market.ontology.Defaults.defaultPrice;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

/**
 * Tests {@link Order}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public class OrderTest {

    @Test
    public void testConstructor() {
        final Long start = System.nanoTime();
        final Long orderID = orderID();
        final Order order = new Order(orderID, OrdType.LIMIT, Side.SELL, defaultOrdQty, defaultPrice);
        final Long stop = System.nanoTime();
        assertThat(order.getOrderID(), is(orderID));
        assertThat(order.getOrdType(), is(OrdType.LIMIT));
        assertThat(order.getSide(), is(Side.SELL));
        assertThat(order.getOrderQty(), is(defaultOrdQty));
        assertThat(order.getPrice(), is(defaultPrice));
        assertThat(order.getEntryTime(), is(greaterThanOrEqualTo(start)));
        assertThat(order.getEntryTime(), is(lessThanOrEqualTo(stop)));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullOrderID() {
        new Order(null, System.nanoTime(), OrdType.LIMIT, Side.SELL, defaultOrdQty, defaultPrice);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullEntryTime() {
        new Order(orderID(), null, OrdType.LIMIT, Side.SELL, defaultOrdQty, defaultPrice);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullOrdType() {
        new Order(orderID(), System.nanoTime(), null, Side.SELL, defaultOrdQty, defaultPrice);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullSide() {
        new Order(orderID(), System.nanoTime(), OrdType.LIMIT, null, defaultOrdQty, defaultPrice);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullOrdQty() {
        new Order(orderID(), System.nanoTime(), OrdType.LIMIT, Side.SELL, null, defaultPrice);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorOrdQtyLessThanOne() {
        new Order(orderID(), System.nanoTime(), OrdType.LIMIT, Side.SELL, Order.NO_QTY, defaultPrice);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullPrice() {
        new Order(orderID(), System.nanoTime(), OrdType.LIMIT, Side.SELL, defaultOrdQty, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorLimitOrderPriceLessThanOrEqualThanNoPrice() {
        new Order(orderID(), System.nanoTime(), OrdType.LIMIT, Side.SELL, defaultOrdQty, Order.NO_PRICE);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorMarketOrderPriceNotNoPrice() {
        new Order(orderID(), System.nanoTime(), OrdType.MARKET, Side.SELL, defaultOrdQty, defaultPrice);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testCompareFirstNull() {
        compare(null, order(buy()));
        compare(null, order(sell()));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testCompareSecondNull() {
        compare(order(buy()), null);
        compare(order(sell()), null);
    }

    @Test
    public void testCompareSameOrder() {
        final Order order = order(buy());
        assertThat(compare(order, order), is(SAME));
    }

    @Test
    public void testCompareLimitToMarket() {
        assertThat(compare(order(ordType(buy(), OrdType.LIMIT)), order(ordType(buy(), OrdType.MARKET))), is(AFTER));
        assertThat(compare(order(ordType(sell(), OrdType.LIMIT)), order(ordType(sell(), OrdType.MARKET))), is(AFTER));
    }

    @Test
    public void testCompareMarketToLimit() {
        assertThat(compare(order(ordType(buy(), OrdType.MARKET)), order(ordType(buy(), OrdType.LIMIT))), is(BEFORE));
        assertThat(compare(order(ordType(sell(), OrdType.MARKET)), order(ordType(sell(), OrdType.LIMIT))), is(BEFORE));
    }

    @Test
    public void testCompareMarketToMarketFirstOrderOld() {
        assertThat(compare(order(ordTypeEntryTime(buy(), OrdType.MARKET, 100L)),
                           order(ordTypeEntryTime(buy(), OrdType.MARKET, 101L))), is(BEFORE));
        assertThat(compare(order(ordTypeEntryTime(sell(), OrdType.MARKET, 100L)),
                           order(ordTypeEntryTime(sell(), OrdType.MARKET, 101L))), is(BEFORE));
    }

    @Test
    public void testCompareMarketToMarketFirstOrderNew() {
        assertThat(compare(order(ordTypeEntryTime(buy(), OrdType.MARKET, 101L)),
                           order(ordTypeEntryTime(buy(), OrdType.MARKET, 100L))), is(AFTER));
        assertThat(compare(order(ordTypeEntryTime(sell(), OrdType.MARKET, 101L)),
                           order(ordTypeEntryTime(sell(), OrdType.MARKET, 100L))), is(AFTER));
    }

    @Test
    public void testCompareMarketToMarketOrdersArrivedAtTheSameTime() {
        assertThat(compare(order(ordTypeEntryTime(buy(), OrdType.MARKET, 100L)),
                           order(ordTypeEntryTime(buy(), OrdType.MARKET, 100L))), is(SAME));
        assertThat(compare(order(ordTypeEntryTime(sell(), OrdType.MARKET, 100L)),
                           order(ordTypeEntryTime(sell(), OrdType.MARKET, 100L))), is(SAME));
    }

    @Test
    public void testLimitToLimitFirstOrderLowerPrice() {
        assertThat(compare(order(limitPrice(buy(), defaultPrice)),
                           order(limitPrice(buy(), new BigDecimal("100.1")))), is(AFTER));
        assertThat(compare(order(limitPrice(sell(), defaultPrice)),
                           order(limitPrice(sell(), new BigDecimal("100.1")))), is(BEFORE));
    }

    @Test
    public void testLimitToLimitFirstOrderHigherPrice() {
        assertThat(compare(order(limitPrice(buy(), new BigDecimal("100.1"))),
                           order(limitPrice(buy(), defaultPrice))), is(BEFORE));
        assertThat(compare(order(limitPrice(sell(), new BigDecimal("100.1"))),
                           order(limitPrice(sell(), defaultPrice))), is(AFTER));
    }

    @Test
    public void testLimitToLimitSamePriceFirstOrderOld() {
        assertThat(compare(order(limitConstPriceEntryTime(buy(), 100L)),
                           order(limitConstPriceEntryTime(buy(), 101L))), is(BEFORE));
        assertThat(compare(order(limitConstPriceEntryTime(sell(), 100L)),
                           order(limitConstPriceEntryTime(sell(), 101L))), is(BEFORE));
    }

    @Test
    public void testLimitToLimitSamePriceFirstOrderNew() {
        assertThat(compare(order(limitConstPriceEntryTime(buy(), 101L)),
                           order(limitConstPriceEntryTime(buy(), 100L))), is(AFTER));
        assertThat(compare(order(limitConstPriceEntryTime(sell(), 101L)),
                           order(limitConstPriceEntryTime(sell(), 100L))), is(AFTER));
    }

    @Test
    public void testLimitToLimitSamePriceOrdersArrivedAtTheSameTime() {
        assertThat(compare(order(limitConstPriceEntryTime(buy(), 100L)),
                           order(limitConstPriceEntryTime(buy(), 100L))), is(SAME));
        assertThat(compare(order(limitConstPriceEntryTime(sell(), 100L)),
                           order(limitConstPriceEntryTime(sell(), 100L))), is(SAME));
    }

    /**
     * Gets the next orderID.
     *
     * @return next orderID.
     */
    public static Long orderID() {
        return curOrderID.getAndIncrement();
    }

    /**
     * Returns the result of comparing <code>o1</code> to <code>o2</code> using {@link Order#compareTo(Order)}.
     *
     * @param o1 first order.
     * @param o2 second order.
     *
     * @return result of comparing <code>o1</code> to <code>o2</code>.
     */
    public static int compare(final Order o1, final Order o2) {
        return o1.compareTo(o2);
    }
}
