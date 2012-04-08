/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.agent.impl.execution;

import eugene.market.book.Order;
import eugene.market.agent.impl.execution.MatchingEngine.Match;
import eugene.market.agent.impl.execution.MatchingEngine.MatchingResult;
import eugene.market.ontology.field.enums.OrdType;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static eugene.market.book.MockOrders.buy;
import static eugene.market.book.MockOrders.limitPrice;
import static eugene.market.book.MockOrders.ordType;
import static eugene.market.book.MockOrders.order;
import static eugene.market.book.MockOrders.sell;
import static eugene.market.ontology.Defaults.defaultPrice;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.mock;

/**
 * Tests {@link MatchingEngine}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public class MatchingEngineTest {

    public static final MatchingResult defaultMatchingResult = new MatchingResult(Match.YES, defaultPrice);

    public static final MatchingEngine matchingEngine = MatchingEngine.getInstance();

    @Test(expectedExceptions = NullPointerException.class)
    public void testMatchNullOrderBook() {
        match(mock(Order.class), mock(Order.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testMatchNullBuyOrder() {
        match(null, mock(Order.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testMatchNullSellOrder() {
        match(mock(Order.class), null);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testMatchBuyWithBuy() {
        final Order buy = order(ordType(buy(), OrdType.LIMIT));
        final Order sell = order(ordType(buy(), OrdType.LIMIT));
        match(buy, sell);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testMatchSellWithSell() {
        final Order buy = order(ordType(sell(), OrdType.LIMIT));
        final Order sell = order(ordType(sell(), OrdType.LIMIT));
        match(buy, sell);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testMatchMarketWithMarket() {
        final Order buy = order(ordType(buy(), OrdType.MARKET));
        final Order sell = order(ordType(sell(), OrdType.MARKET));
        match(buy, sell);
    }

    @Test
    public void testMatchMarketLimit() {
        final Order buy = order(ordType(buy(), OrdType.MARKET));
        final Order sell = order(limitPrice(sell(), defaultPrice));
        assertThat(match(buy, sell), is(equalTo(defaultMatchingResult)));
    }

    @Test
    public void testMatchLimitMarket() {
        final Order buy = order(ordType(buy(), OrdType.MARKET));
        final Order sell = order(limitPrice(sell(), defaultPrice));
        assertThat(match(buy, sell), is(equalTo(defaultMatchingResult)));
    }

    @Test
    public void testMatchLimitLimitPriceEqual() {
        final Order buy = order(limitPrice(buy(), defaultPrice));
        final Order sell = order(limitPrice(sell(), defaultPrice));
        assertThat(match(buy, sell), is(equalTo(defaultMatchingResult)));
    }

    @Test
    public void testMatchLimitLimitBuyCross() {
        final Order buy = order(limitPrice(buy(), defaultPrice.add(BigDecimal.ONE)));
        final Order sell = order(limitPrice(sell(), defaultPrice));
        assertThat(match(buy, sell), is(equalTo(defaultMatchingResult)));
    }

    @Test
    public void testMatchLimitLimitNoCross() {
        final Order buy = order(limitPrice(buy(), defaultPrice));
        final Order sell = order(limitPrice(sell(), defaultPrice.add(BigDecimal.ONE)));
        assertThat(match(buy, sell), is(equalTo(MatchingResult.NO_MATCH)));
    }

    /**
     * Delegates the matching to {@link MatchingEngineTest#matchingEngine}.
     */
    public static MatchingResult match(final Order buyOrder, final Order sellOrder) {
        return matchingEngine.match(buyOrder, sellOrder);
    }
}
