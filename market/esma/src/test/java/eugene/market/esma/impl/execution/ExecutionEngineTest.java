package eugene.market.esma.impl.execution;

import eugene.market.book.Order;
import eugene.market.book.OrderBook;
import eugene.market.book.OrderStatus;
import eugene.market.book.impl.DefaultOrderBook;
import eugene.market.esma.impl.execution.data.MarketDataEngine;
import eugene.market.ontology.field.enums.Side;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import static eugene.market.book.MockOrders.buy;
import static eugene.market.book.MockOrders.limitOrdQtyPrice;
import static eugene.market.book.MockOrders.order;
import static eugene.market.book.MockOrders.orderQty;
import static eugene.market.book.MockOrders.sell;
import static eugene.market.ontology.Defaults.defaultOrdQty;
import static eugene.market.ontology.Defaults.defaultPrice;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests {@link ExecutionEngine}.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public class ExecutionEngineTest {

    @Test
    public void testDefaultConstructor() {
        final ExecutionEngine executionEngine = new ExecutionEngine();
        assertThat(executionEngine.getOrderBook(), is(DefaultOrderBook.class));
        assertThat(executionEngine.getMatchingEngine(), is(MatchingEngine.class));
        assertThat(executionEngine.getInsertionValidator(), is(InsertionValidator.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullInsertionValidator() {
        new ExecutionEngine(null, mock(OrderBook.class), mock(MatchingEngine.class), mock(
                MarketDataEngine.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullOrderBook() {
        new ExecutionEngine(mock(InsertionValidator.class), null, mock(MatchingEngine.class),
                            mock(MarketDataEngine.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullMatchingEngine() {
        new ExecutionEngine(mock(InsertionValidator.class), mock(OrderBook.class), null,
                            mock(MarketDataEngine.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullMarketDataEngine() {
        new ExecutionEngine(mock(InsertionValidator.class), mock(OrderBook.class), mock(MatchingEngine.class),
                            null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testInsertNullOrder() {
        getExecutionEngine().insertOrder(null);
    }

    @Test
    public void testInsertOrderNotValid() {
        final ExecutionEngine executionEngine = getExecutionEngine();
        when(executionEngine.getInsertionValidator().validate(any(OrderBook.class),
                                                              any(Order.class))).thenReturn(false);
        assertThat(executionEngine.insertOrder(mock(Order.class)), nullValue());
    }

    @Test
    public void testInsertOrderValid() {
        final ExecutionEngine executionEngine = getExecutionEngine();
        final Order order = order(buy());

        assertThat(executionEngine.insertOrder(order), sameInstance(executionEngine.getOrderBook().getOrderStatus
                (order)));
        verify(executionEngine.getMarketDataEngine()).newOrder(order);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testCancelNullOrder() {
        final ExecutionEngine executionEngine = getExecutionEngine();
        executionEngine.cancel(null);
    }

    @Test
    public void testCancelOrderDoesNotExist() {
        assertThat(getExecutionEngine().cancel(order(buy())), nullValue());
    }

    @Test
    public void testCancelOrderValid() {
        final ExecutionEngine executionEngine = getExecutionEngine();

        final Order order = order(buy());
        final OrderBook orderBook = executionEngine.getOrderBook();
        final OrderStatus orderStatus = orderBook.insert(order);

        assertThat(executionEngine.cancel(order), sameInstance(orderStatus));
        verify(executionEngine.getMarketDataEngine()).cancel(orderStatus);
    }

    @Test
    public void testExecuteBuySideEmpty() {
        final ExecutionEngine executionEngine = getExecutionEngine();
        final OrderBook orderBook = executionEngine.getOrderBook();
        final Order sell = order(sell());
        orderBook.insert(sell);

        executionEngine.execute();

        verifyZeroInteractions(executionEngine.getMarketDataEngine());

        assertThat(orderBook.isEmpty(Side.SELL), is(false));
        assertThat(orderBook.peek(Side.SELL), sameInstance(sell));
        assertThat(orderBook.size(Side.SELL), is(1));
        assertThat(orderBook.isEmpty(Side.BUY), is(true));
    }

    @Test
    public void testExecuteSellSideEmpty() {
        final ExecutionEngine executionEngine = getExecutionEngine();
        final OrderBook orderBook = executionEngine.getOrderBook();
        final Order buy = order(buy());
        orderBook.insert(buy);

        executionEngine.execute();

        verifyZeroInteractions(executionEngine.getMarketDataEngine());

        assertThat(orderBook.isEmpty(Side.BUY), is(false));
        assertThat(orderBook.peek(Side.BUY), sameInstance(buy));
        assertThat(orderBook.size(Side.BUY), is(1));
        assertThat(orderBook.isEmpty(Side.SELL), is(true));
    }

    @Test
    public void testExecuteOrderValidNoMatch() {
        final Order buy = order(limitOrdQtyPrice(buy(), defaultOrdQty, defaultPrice - 1L));
        final Order sell = order(limitOrdQtyPrice(sell(), defaultOrdQty, defaultPrice));
        final ExecutionEngine executionEngine = getExecutionEngine(buy, sell);

        executionEngine.execute();

        final OrderBook orderBook = executionEngine.getOrderBook();

        verifyZeroInteractions(executionEngine.getMarketDataEngine());

        assertThat(orderBook.isEmpty(Side.SELL), is(false));
        assertThat(orderBook.peek(Side.SELL), sameInstance(sell));
        assertThat(orderBook.size(Side.SELL), is(1));
        assertThat(orderBook.isEmpty(Side.BUY), is(false));
        assertThat(orderBook.peek(Side.BUY), sameInstance(buy));
        assertThat(orderBook.size(Side.BUY), is(1));
    }

    @Test
    public void testExecuteOrderValidMatch() {
        final ArgumentCaptor<Execution> tradeReport = ArgumentCaptor.forClass(Execution.class);
        final Order buy = order(orderQty(buy(), defaultOrdQty));
        final Order sell = order(orderQty(sell(), defaultOrdQty - 1L));
        final ExecutionEngine executionEngine = getExecutionEngine(buy, sell);

        executionEngine.execute();

        final OrderBook orderBook = executionEngine.getOrderBook();
        final MarketDataEngine marketDataEngine = executionEngine.getMarketDataEngine();

        verify(orderBook).execute(Side.BUY, defaultOrdQty - 1L, defaultPrice);
        verify(orderBook).execute(Side.SELL, defaultOrdQty - 1L, defaultPrice);
        verify(marketDataEngine).execution(tradeReport.capture());

        assertThat(tradeReport.getValue().getExecID(), is(executionEngine.getCurExecID() - 1L));
        assertThat(tradeReport.getValue().getPrice(), is(defaultPrice));
        assertThat(tradeReport.getValue().getQuantity(), is(defaultOrdQty - 1L));

        assertThat(tradeReport.getValue().getBuyOrderStatus().getOrder(), sameInstance(buy));
        assertThat(tradeReport.getValue().getSellOrderStatus().getOrder(), sameInstance(sell));

        assertThat(tradeReport.getValue().getBuyOrderStatus().getLeavesQty(), is(1L));
        assertThat(tradeReport.getValue().getBuyOrderStatus().getCumQty(), is(defaultOrdQty - 1L));

        assertThat(tradeReport.getValue().getSellOrderStatus().isFilled(), is(true));

        assertThat(orderBook.isEmpty(Side.BUY), is(false));
        assertThat(orderBook.peek(Side.BUY), sameInstance(buy));
        assertThat(orderBook.isEmpty(Side.SELL), is(true));
    }

    /**
     * Gets an {@link ExecutionEngine} with:
     * <ul>
     * <li>{@link OrderBook} wrapped with {@link Mockito#spy(Object)}.</li>
     * <li>{@link InsertionValidator} that returns <code>true</code> when {@link InsertionValidator#validate
     * (OrderBook, Order)} is called with any arguments.</li>
     * </ul>
     *
     * @return an instance of {@link ExecutionEngine} with mocked dependencies.
     */
    public static ExecutionEngine getExecutionEngine() {
        final OrderBook orderBook = spy(new DefaultOrderBook());
        final InsertionValidator insertionValidator = mock(InsertionValidator.class);
        when(insertionValidator.validate(any(OrderBook.class), any(Order.class))).thenReturn(true);
        return new ExecutionEngine(insertionValidator, orderBook,
                                   MatchingEngine.getInstance(),
                                   mock(MarketDataEngine.class));
    }

    /**
     * Gets an {@link ExecutionEngine} with the attributes of {@link ExecutionEngineTest#getExecutionEngine()
     * } and <code>buyOrder</code> and <code>sellOrder</code> added to the {@link OrderBook}.
     *
     * @param buyOrder  {@link Order} to add to {@link OrderBook}.
     * @param sellOrder {@link Order} to add to {@link OrderBook}.
     *
     * @return an instance of {@link ExecutionEngine} with mocked dependencies.
     */
    public static ExecutionEngine getExecutionEngine(final Order buyOrder,
                                                     final Order sellOrder) {
        final ExecutionEngine executionEngine = getExecutionEngine();
        final OrderBook orderBook = executionEngine.getOrderBook();
        orderBook.insert(buyOrder);
        orderBook.insert(sellOrder);
        return executionEngine;
    }
}
