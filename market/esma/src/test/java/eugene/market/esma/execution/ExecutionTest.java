package eugene.market.esma.execution;

import eugene.market.book.Order;
import eugene.market.book.OrderStatus;
import org.testng.annotations.Test;

import static eugene.market.book.MockOrders.buy;
import static eugene.market.book.MockOrders.order;
import static eugene.market.book.MockOrders.sell;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

/**
 * Tests {@link Execution}.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public class ExecutionTest {

    private static final OrderStatus BUY_ORDER_STATUS = new OrderStatus(order(buy()));

    private static final OrderStatus SELL_ORDER_STATUS = new OrderStatus(order(sell()));

    private static final Double price = Order.NO_PRICE + 1.0D;

    private static final Long quantity = Order.NO_QTY + 1L;

    private static final Long execID = 1L;

    private static final Execution EXECUTION = new Execution(execID, BUY_ORDER_STATUS, SELL_ORDER_STATUS, price,
                                                             quantity);


    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullExecID() {
        new Execution(null, BUY_ORDER_STATUS, SELL_ORDER_STATUS, price, quantity);
    }
    
    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullBuyExecutionReport() {
        new Execution(execID, null, SELL_ORDER_STATUS, price, quantity);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorBuyExecutionReportOrderSell() {
        new Execution(execID, SELL_ORDER_STATUS, SELL_ORDER_STATUS, price, quantity);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullSellExecutionReport() {
        new Execution(execID, BUY_ORDER_STATUS, null, price, quantity);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorSellExecutionReportOrderSell() {
        new Execution(execID, BUY_ORDER_STATUS, BUY_ORDER_STATUS, price, quantity);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullPrice() {
        new Execution(execID, BUY_ORDER_STATUS, SELL_ORDER_STATUS, null, quantity);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorNoPrice() {
        new Execution(execID, BUY_ORDER_STATUS, SELL_ORDER_STATUS, Order.NO_PRICE, quantity);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullQuantity() {
        new Execution(execID, BUY_ORDER_STATUS, SELL_ORDER_STATUS, price, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorNoQuantity() {
        new Execution(execID, BUY_ORDER_STATUS, SELL_ORDER_STATUS, price, Order.NO_QTY);
    }
    
    @Test
    public void testGetExecID() {
        assertThat(EXECUTION.getExecID(), sameInstance(execID));
    }

    @Test
    public void testGetBuyOrderExecutionReport() {
        assertThat(EXECUTION.getBuyOrderStatus(), sameInstance(BUY_ORDER_STATUS));
    }

    @Test
    public void testGetSellOrderExecutionReport() {
        assertThat(EXECUTION.getSellOrderStatus(), sameInstance(SELL_ORDER_STATUS));
    }

    @Test
    public void testGetPrice() {
        assertThat(EXECUTION.getPrice(), is(price));
    }

    @Test
    public void testGetQuantity() {
        assertThat(EXECUTION.getQuantity(), is(quantity));
    }
}
