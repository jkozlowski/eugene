package eugene.market.book;

import org.testng.annotations.Test;

import static eugene.market.book.MockOrders.buy;
import static eugene.market.book.MockOrders.order;
import static eugene.market.book.MockOrders.sell;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

/**
 * Tests {@link TradeReport}.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public class TradeReportTest {

    private static final OrderStatus BUY_ORDER_STATUS = new OrderStatus(order(buy()));

    private static final OrderStatus SELL_ORDER_STATUS = new OrderStatus(order(sell()));

    private static final Double price = Order.NO_PRICE + 1.0D;

    private static final Long quantity = Order.NO_QTY + 1L;

    private static final TradeReport TRADE_REPORT = new TradeReport(BUY_ORDER_STATUS, SELL_ORDER_STATUS, price,
                                                                    quantity);

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullBuyExecutionReport() {
        new TradeReport(null, SELL_ORDER_STATUS, price, quantity);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorBuyExecutionReportOrderSell() {
        new TradeReport(SELL_ORDER_STATUS, SELL_ORDER_STATUS, price, quantity);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullSellExecutionReport() {
        new TradeReport(BUY_ORDER_STATUS, null, price, quantity);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorSellExecutionReportOrderSell() {
        new TradeReport(BUY_ORDER_STATUS, BUY_ORDER_STATUS, price, quantity);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullPrice() {
        new TradeReport(BUY_ORDER_STATUS, SELL_ORDER_STATUS, null, quantity);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorNoPrice() {
        new TradeReport(BUY_ORDER_STATUS, SELL_ORDER_STATUS, Order.NO_PRICE, quantity);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullQuantity() {
        new TradeReport(BUY_ORDER_STATUS, SELL_ORDER_STATUS, price, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorNoQuantity() {
        new TradeReport(BUY_ORDER_STATUS, SELL_ORDER_STATUS, price, Order.NO_QTY);
    }

    @Test
    public void testGetBuyOrderExecutionReport() {
        assertThat(TRADE_REPORT.getBuyOrderStatus(), sameInstance(BUY_ORDER_STATUS));
    }

    @Test
    public void testGetSellOrderExecutionReport() {
        assertThat(TRADE_REPORT.getSellOrderStatus(), sameInstance(SELL_ORDER_STATUS));
    }

    @Test
    public void testGetPrice() {
        assertThat(TRADE_REPORT.getPrice(), is(price));
    }

    @Test
    public void testGetQuantity() {
        assertThat(TRADE_REPORT.getQuantity(), is(quantity));
    }
}
