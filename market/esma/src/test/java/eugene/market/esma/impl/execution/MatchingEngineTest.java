package eugene.market.esma.impl.execution;

import eugene.market.book.Order;
import eugene.market.esma.impl.execution.MatchingEngine.Match;
import eugene.market.esma.impl.execution.MatchingEngine.MatchingResult;
import eugene.market.ontology.field.enums.OrdType;
import org.testng.annotations.Test;

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

    @Test(expectedExceptions = IllegalStateException.class)
    public void testMatchMarketWithMarket() {
        final Order buy = order(ordType(buy(), OrdType.MARKET));
        final Order sell = order(ordType(sell(), OrdType.MARKET));
        match(buy, sell);
    }

    @Test
    public void testMatchMarketLimit() {
        final Order buy = order(ordType(buy(), OrdType.MARKET));
        final Order sell = order(limitPrice(buy(), defaultPrice));
        assertThat(match(buy, sell), is(equalTo(defaultMatchingResult)));
    }

    @Test
    public void testMatchLimitMarket() {
        final Order buy = order(limitPrice(buy(), defaultPrice));
        final Order sell = order(ordType(sell(), OrdType.MARKET));
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
        final Order buy = order(limitPrice(buy(), defaultPrice + 1.0D));
        final Order sell = order(limitPrice(sell(), defaultPrice));
        assertThat(match(buy, sell), is(equalTo(defaultMatchingResult)));
    }

    @Test
    public void testMatchLimitLimitNoCross() {
        final Order buy = order(limitPrice(buy(), defaultPrice));
        final Order sell = order(limitPrice(sell(), defaultPrice + 1.0D));
        assertThat(match(buy, sell), is(equalTo(MatchingResult.NO_MATCH)));
    }

    /**
     * Delegates the matching to {@link MatchingEngineTest#matchingEngine}.
     */
    public static MatchingResult match(final Order buyOrder, final Order sellOrder) {
        return matchingEngine.match(buyOrder, sellOrder);
    }
}
