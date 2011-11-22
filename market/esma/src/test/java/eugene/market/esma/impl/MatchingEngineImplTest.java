package eugene.market.esma.impl;

import eugene.market.esma.MatchingEngine;
import eugene.market.esma.MatchingEngine.Match;
import eugene.market.esma.MatchingEngine.MatchingResult;
import eugene.market.esma.Order;
import eugene.market.esma.OrderBook;
import eugene.market.esma.enums.OrdType;
import org.testng.annotations.Test;

import static eugene.market.esma.impl.MockOrders.buy;
import static eugene.market.esma.impl.MockOrders.limitPrice;
import static eugene.market.esma.impl.MockOrders.ordType;
import static eugene.market.esma.impl.MockOrders.order;
import static eugene.market.esma.impl.MockOrders.sell;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests {@link MatchingEngineImpl}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public class MatchingEngineImplTest {

    public static final Double defaultLastMarketPrice = new Double(100.0D);

    public static final MatchingResult defaultMatchingResult = new MatchingResult(Match.YES, defaultLastMarketPrice);

    public static final MatchingEngine matchingEngine = new MatchingEngineImpl();

    @Test(expectedExceptions = NullPointerException.class)
    public void testMatchNullOrderBook() {
        matchingEngine.match(null, mock(Order.class), mock(Order.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testMatchNullBuyOrder() {
        matchingEngine.match(mock(OrderBook.class), null, mock(Order.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testMatchNullSellOrder() {
        matchingEngine.match(mock(OrderBook.class), mock(Order.class), null);
    }

    @Test
    public void testMatchMarketWithMarketLastMarketPrice() {
        final Order buy = order(ordType(buy(), OrdType.MARKET));
        final Order sell = order(ordType(sell(), OrdType.MARKET));
        final OrderBook orderBook =
                when(mock(OrderBook.class).getLastMarketPrice()).thenReturn(defaultLastMarketPrice).getMock();
        final MatchingResult expected = new MatchingResult(Match.YES, defaultLastMarketPrice);
        assertThat(matchingEngine.match(orderBook, buy, sell), is(expected));
    }

    @Test
    public void testMatchMarketWithMarketNoLastMarketPrice() {
        final Order buy = order(ordType(buy(), OrdType.MARKET));
        final Order sell = order(ordType(sell(), OrdType.MARKET));
        final OrderBook orderBook =
                when(mock(OrderBook.class).getLastMarketPrice()).thenReturn(Order.NO_PRICE).getMock();
        assertThat(matchingEngine.match(orderBook, buy, sell), is(equalTo(MatchingResult.NO_MATCH)));
    }

    @Test
    public void testMatchMarketLimit() {
        final Order buy = order(ordType(buy(), OrdType.MARKET));
        final Order sell = order(limitPrice(buy(), defaultLastMarketPrice));
        final OrderBook orderBook = mock(OrderBook.class);
        assertThat(matchingEngine.match(orderBook, buy, sell), is(equalTo(defaultMatchingResult)));
    }

    @Test
    public void testMatchLimitMarket() {
        final Order buy = order(limitPrice(buy(), defaultLastMarketPrice));
        final Order sell = order(ordType(sell(), OrdType.MARKET));
        final OrderBook orderBook = mock(OrderBook.class);
        assertThat(matchingEngine.match(orderBook, buy, sell), is(equalTo(defaultMatchingResult)));
    }

    @Test
    public void testMatchLimitLimitPriceEqual() {
        final Order buy = order(limitPrice(buy(), defaultLastMarketPrice));
        final Order sell = order(limitPrice(sell(), defaultLastMarketPrice));
        final OrderBook orderBook = mock(OrderBook.class);
        assertThat(matchingEngine.match(orderBook, buy, sell), is(equalTo(defaultMatchingResult)));
    }

    @Test
    public void testMatchLimitLimitBuyCross() {
        final Order buy = order(limitPrice(buy(), defaultLastMarketPrice + 1.0D));
        final Order sell = order(limitPrice(sell(), defaultLastMarketPrice));
        final OrderBook orderBook = mock(OrderBook.class);
        assertThat(matchingEngine.match(orderBook, buy, sell), is(equalTo(defaultMatchingResult)));
    }

    @Test
    public void testMatchLimitLimitNoCross() {
        final Order buy = order(limitPrice(buy(), defaultLastMarketPrice));
        final Order sell = order(limitPrice(sell(), defaultLastMarketPrice + 1.0D));
        final OrderBook orderBook = mock(OrderBook.class);
        assertThat(matchingEngine.match(orderBook, buy, sell), is(equalTo(MatchingResult.NO_MATCH)));
    }
}
