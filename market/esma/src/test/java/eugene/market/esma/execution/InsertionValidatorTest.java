package eugene.market.esma.execution;

import eugene.market.esma.enums.OrdType;
import eugene.market.esma.enums.Side;
import eugene.market.esma.execution.book.Order;
import eugene.market.esma.execution.book.OrderBook;
import org.testng.annotations.Test;

import static eugene.market.esma.execution.MockOrders.buy;
import static eugene.market.esma.Defaults.defaultPrice;
import static eugene.market.esma.execution.MockOrders.limitPrice;
import static eugene.market.esma.execution.MockOrders.ordType;
import static eugene.market.esma.execution.MockOrders.order;
import static eugene.market.esma.execution.MockOrders.sell;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests {@link InsertionValidator}.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public class InsertionValidatorTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void testValidateNullOrderBook() {
        validate(null, mock(Order.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testValidateNullOrder() {
        validate(mock(OrderBook.class), null);
    }

    @Test
    public void testValidateOrdTypeLimit() {
        assertThat(validate(mock(OrderBook.class), order(limitPrice(buy(), defaultPrice))), is(true));
    }

    @Test
    public void testValidateOrdTypeMarketSell() {
        final OrderBook orderBook = when(mock(OrderBook.class).isEmpty(Side.BUY)).thenReturn(false).getMock();
        assertThat(validate(orderBook, order(ordType(sell(), OrdType.MARKET))), is(true));
    }

    @Test
    public void testValidateOrdTypeMarketSellNoBuys() {
        final OrderBook orderBook = when(mock(OrderBook.class).isEmpty(Side.BUY)).thenReturn(true).getMock();
        assertThat(validate(orderBook, order(ordType(sell(), OrdType.MARKET))), is(false));
    }

    @Test
    public void testValidateOrdTypeMarketBuy() {
        final OrderBook orderBook = when(mock(OrderBook.class).isEmpty(Side.SELL)).thenReturn(false).getMock();
        assertThat(validate(orderBook, order(ordType(buy(), OrdType.MARKET))), is(true));
    }

    @Test
    public void testValidateOrdTypeMarketBuyNoSells() {
        final OrderBook orderBook = when(mock(OrderBook.class).isEmpty(Side.SELL)).thenReturn(true).getMock();
        assertThat(validate(orderBook, order(ordType(buy(), OrdType.MARKET))), is(false));
    }

    /**
     * Wraps {@link InsertionValidator#validate(OrderBook, Order)}.
     *
     * @see InsertionValidator#validate(OrderBook, Order)
     */
    public static boolean validate(final OrderBook orderBook, final Order order) {
        return InsertionValidator.getInstance().validate(orderBook, order);
    }
}


