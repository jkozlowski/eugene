package eugene.market.esma.execution;

import eugene.market.book.DefaultOrderBook;
import eugene.market.book.Order;
import eugene.market.book.OrderBook;
import eugene.market.book.OrderStatus;
import eugene.market.book.TradeReport;
import eugene.market.esma.execution.MatchingEngine.Match;
import eugene.market.esma.execution.MatchingEngine.MatchingResult;
import eugene.market.esma.execution.data.MarketDataEngine;
import eugene.market.ontology.field.enums.Side;
import org.mockito.InOrder;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
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
        final Order order = mock(Order.class);
        final OrderStatus orderStatus = mock(OrderStatus.class);
        when(executionEngine.getOrderBook().insertOrder(any(Order.class))).thenReturn(orderStatus);

        assertThat(executionEngine.insertOrder(order), sameInstance(orderStatus));
        verify(executionEngine.getOrderBook()).insertOrder(order);
        verify(executionEngine.getMarketDataEngine()).newOrder(order);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testCancelNullOrder() {
        final ExecutionEngine executionEngine = getExecutionEngine();
        executionEngine.cancel(null);
    }

    @Test
    public void testCancelOrderDoesNotExist() {
        final ExecutionEngine executionEngine = getExecutionEngine();

        final OrderBook orderBook = executionEngine.getOrderBook();
        when(orderBook.cancel(any(Order.class))).thenReturn(null);

        assertThat(executionEngine.cancel(mock(Order.class)), nullValue());
    }

    @Test
    public void testCancelOrderValid() {
        final ExecutionEngine executionEngine = getExecutionEngine();

        final Order order = mock(Order.class);
        final OrderStatus orderStatus = mock(OrderStatus.class);

        final OrderBook orderBook = executionEngine.getOrderBook();
        when(orderBook.cancel(order)).thenReturn(orderStatus);

        assertThat(executionEngine.cancel(order), sameInstance(orderStatus));
        verify(executionEngine.getMarketDataEngine()).cancel(orderStatus);
    }

    @Test
    public void testExecuteBuySideEmpty() {
        final ExecutionEngine executionEngine = getExecutionEngine();
        when(executionEngine.getOrderBook().isEmpty(Side.BUY)).thenReturn(true);
        when(executionEngine.getOrderBook().size(Side.BUY)).thenReturn(0);

        executionEngine.execute();

        final OrderBook orderBook = executionEngine.getOrderBook();
        final InOrder inOrder = inOrder(orderBook);
        inOrder.verify(orderBook).isEmpty(Side.BUY);
        inOrder.verifyNoMoreInteractions();

        verifyZeroInteractions(executionEngine.getMatchingEngine(), executionEngine.getMarketDataEngine());
    }

    @Test
    public void testExecuteSellSideEmpty() {
        final ExecutionEngine executionEngine = getExecutionEngine();
        when(executionEngine.getOrderBook().isEmpty(Side.BUY)).thenReturn(false);
        when(executionEngine.getOrderBook().size(Side.BUY)).thenReturn(1);
        when(executionEngine.getOrderBook().isEmpty(Side.SELL)).thenReturn(true);
        when(executionEngine.getOrderBook().size(Side.SELL)).thenReturn(0);

        executionEngine.execute();

        final OrderBook orderBook = executionEngine.getOrderBook();
        final InOrder inOrder = inOrder(orderBook);
        inOrder.verify(orderBook).isEmpty(Side.BUY);
        inOrder.verify(orderBook).isEmpty(Side.SELL);
        inOrder.verifyNoMoreInteractions();

        verifyZeroInteractions(executionEngine.getMatchingEngine(), executionEngine.getMarketDataEngine());
    }

    @Test
    public void testExecuteOrderValidNoMatch() {
        final Order buy = mock(Order.class);
        final Order sell = mock(Order.class);
        final ExecutionEngine executionEngine = getExecutionEngine(buy, sell);
        final MatchingResult matchingResult = MatchingResult.NO_MATCH;
        when(executionEngine.getMatchingEngine().match(buy, sell)).thenReturn(matchingResult);

        executionEngine.execute();

        final OrderBook orderBook = executionEngine.getOrderBook();
        final MatchingEngine matchingEngine = executionEngine.getMatchingEngine();
        final InOrder inOrder = inOrder(orderBook, matchingEngine);

        inOrder.verify(orderBook).isEmpty(Side.BUY);
        inOrder.verify(orderBook).isEmpty(Side.SELL);
        inOrder.verify(orderBook).peek(Side.BUY);
        inOrder.verify(orderBook).peek(Side.SELL);
        inOrder.verify(matchingEngine).match(buy, sell);
        inOrder.verifyNoMoreInteractions();

        verifyZeroInteractions(executionEngine.getMarketDataEngine());
    }

    @Test
    public void testExecuteOrderValidMatch() {
        final Order buy = mock(Order.class);
        final Order sell = mock(Order.class);
        final ExecutionEngine executionEngine = getExecutionEngineOrderOnEachSide(buy, sell);
        final MatchingResult matchingResult = new MatchingResult(Match.YES, Order.NO_PRICE);
        final TradeReport tradeReport = mock(TradeReport.class);
        when(executionEngine.getMatchingEngine().match(buy, sell)).thenReturn(matchingResult);
        when(executionEngine.getOrderBook().execute(Order.NO_PRICE)).thenReturn(tradeReport);

        executionEngine.execute();

        final OrderBook orderBook = executionEngine.getOrderBook();
        final MatchingEngine matchingEngine = executionEngine.getMatchingEngine();
        final MarketDataEngine marketDataEngine = executionEngine.getMarketDataEngine();
        final InOrder inOrder = inOrder(orderBook, matchingEngine, marketDataEngine);

        inOrder.verify(orderBook).isEmpty(Side.BUY);
        inOrder.verify(orderBook).isEmpty(Side.SELL);
        inOrder.verify(orderBook).peek(Side.BUY);
        inOrder.verify(orderBook).peek(Side.SELL);
        inOrder.verify(matchingEngine).match(buy, sell);
        inOrder.verify(orderBook).execute(Order.NO_PRICE);
        inOrder.verify(marketDataEngine).trade(tradeReport);
        inOrder.verify(orderBook).isEmpty(Side.BUY);
        inOrder.verifyNoMoreInteractions();
    }

    /**
     * Gets an {@link ExecutionEngine} with:
     * <ul>
     * <li>{@link OrderBook} that returns <code>true</code> when {@link OrderBook#isEmpty(Side)} is called with
     * any {@link Side}.</li>
     * <li>{@link InsertionValidator} that returns <code>true</code> when {@link InsertionValidator#validate
     * (OrderBook, Order)} is called with any arguments.</li>
     * </ul>
     *
     * @return an instance of {@link ExecutionEngine} with mocked dependencies.
     */
    public static ExecutionEngine getExecutionEngine() {
        final OrderBook orderBook = mock(OrderBook.class);
        when(orderBook.isEmpty(any(Side.class))).thenReturn(true);
        final InsertionValidator insertionValidator = mock(InsertionValidator.class);
        when(insertionValidator.validate(any(OrderBook.class), any(Order.class))).thenReturn(true);
        return new ExecutionEngine(insertionValidator, orderBook,
                                   mock(MatchingEngine.class),
                                   mock(MarketDataEngine.class));
    }

    /**
     * Gets an {@link ExecutionEngine} with the attributes of {@link ExecutionEngineTest#getExecutionEngine()
     * } except that:
     * <ul>
     * <li>{@link OrderBook#peek(Side)} returns <code>buyOrder</code> and <code>sellOrder</code></li>
     * </ul>
     *
     * @param buyOrder  {@link Order} to return when {@link OrderBook#peek(Side)} is called with {@link Side#BUY}.
     * @param sellOrder {@link Order} to return when {@link OrderBook#peek(Side)} is called with {@link Side#SELL}.
     *
     * @return an instance of {@link ExecutionEngine} with mocked dependencies.
     */
    public static ExecutionEngine getExecutionEngine(final Order buyOrder,
                                                     final Order sellOrder) {
        final ExecutionEngine executionEngine = getExecutionEngine();
        final OrderBook orderBook = executionEngine.getOrderBook();
        when(orderBook.isEmpty(any(Side.class))).thenReturn(false);
        when(orderBook.size(any(Side.class))).thenReturn(1);
        when(orderBook.peek(Side.BUY)).thenReturn(buyOrder);
        when(orderBook.peek(Side.SELL)).thenReturn(sellOrder);
        return executionEngine;
    }

    /**
     * Gets an {@link ExecutionEngine} with the attributes of {@link ExecutionEngineTest#getExecutionEngine()
     * } except that:
     * <ul>
     * <li>{@link OrderBook#peek(Side)} returns <code>buyOrder</code> and <code>sellOrder</code> first time it is
     * called and {@link OrderBook#isEmpty(Side)} and {@link OrderBook#size(Side)} work correctly.</li>
     * </ul>
     *
     * @param buyOrder  {@link Order} to return when {@link OrderBook#peek(Side)} is called with {@link Side#BUY}.
     * @param sellOrder {@link Order} to return when {@link OrderBook#peek(Side)} is called with {@link Side#SELL}.
     *
     * @return an instance of {@link ExecutionEngine} with mocked dependencies.
     */
    public static ExecutionEngine getExecutionEngineOrderOnEachSide(final Order buyOrder,
                                                                    final Order sellOrder) {
        final ExecutionEngine executionEngine = getExecutionEngine();
        final OrderBook orderBook = executionEngine.getOrderBook();
        when(orderBook.isEmpty(Side.SELL)).thenReturn(false).thenReturn(true);
        when(orderBook.isEmpty(Side.BUY)).thenReturn(false).thenReturn(true);
        when(orderBook.size(any(Side.class))).thenReturn(1).thenReturn(0);
        when(orderBook.peek(Side.BUY)).thenReturn(buyOrder).thenReturn(null);
        when(orderBook.peek(Side.SELL)).thenReturn(sellOrder).thenReturn(null);
        return executionEngine;
    }
}
