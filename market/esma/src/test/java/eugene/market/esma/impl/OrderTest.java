package eugene.market.esma.impl;

import eugene.market.esma.Order;
import eugene.market.esma.enums.OrdType;
import org.testng.annotations.Test;

import static eugene.market.esma.impl.MockOrders.buy;
import static eugene.market.esma.impl.MockOrders.limitConstPriceEntryTime;
import static eugene.market.esma.impl.MockOrders.limitPrice;
import static eugene.market.esma.impl.MockOrders.ordType;
import static eugene.market.esma.impl.MockOrders.ordTypeEntryTime;
import static eugene.market.esma.impl.MockOrders.order;
import static eugene.market.esma.impl.MockOrders.sell;
import static eugene.market.esma.Order.AFTER;
import static eugene.market.esma.Order.BEFORE;
import static eugene.market.esma.Order.SAME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests {@link Order}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public class OrderTest {

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
    public void testCompareLimitToMarket() {
        assertThat(compare(order(ordType(buy(), OrdType.LIMIT)),
                           order(ordType(buy(), OrdType.MARKET))), is(AFTER));
        assertThat(compare(order(ordType(sell(), OrdType.LIMIT)),
                           order(ordType(sell(), OrdType.MARKET))), is(AFTER));
    }

    @Test
    public void testCompareMarketToLimit() {
        assertThat(compare(order(ordType(buy(), OrdType.MARKET)),
                           order(ordType(buy(), OrdType.LIMIT))), is(BEFORE));
        assertThat(compare(order(ordType(sell(), OrdType.MARKET)),
                           order(ordType(sell(), OrdType.LIMIT))), is(BEFORE));
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
        assertThat(compare(order(limitPrice(buy(), 100.0D)),
                           order(limitPrice(buy(), 100.1D))), is(AFTER));
        assertThat(compare(order(limitPrice(sell(), 100.0D)),
                           order(limitPrice(sell(), 100.1D))), is(BEFORE));
    }

    @Test
    public void testLimitToLimitFirstOrderHigherPrice() {
        assertThat(compare(order(limitPrice(buy(), 100.1D)),
                           order(limitPrice(buy(), 100.0D))), is(BEFORE));
        assertThat(compare(order(limitPrice(sell(), 100.1D)),
                           order(limitPrice(sell(), 100.0D))), is(AFTER));
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
     * Returns the result of comparing <code>o1</code> to <code>o2</code> using <code>orderComparator</code>.
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
