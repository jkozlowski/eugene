package eugene.market.esma.impl.execution;

import eugene.market.book.Order;
import eugene.market.book.OrderBook;
import eugene.market.book.OrderBooks;
import eugene.market.book.OrderStatus;
import eugene.market.book.impl.DefaultOrderBook;
import eugene.market.esma.impl.execution.data.MarketDataEngine;
import eugene.market.ontology.field.enums.OrdStatus;
import eugene.market.ontology.field.enums.OrdType;
import eugene.market.ontology.field.enums.Side;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import static eugene.market.book.MockOrders.buy;
import static eugene.market.book.MockOrders.limitOrdQtyPrice;
import static eugene.market.book.MockOrders.ordType;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
        new ExecutionEngine(null, mock(OrderBook.class), mock(MatchingEngine.class), mock(MarketDataEngine.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullOrderBook() {
        new ExecutionEngine(mock(InsertionValidator.class), null, mock(MatchingEngine.class),
                            mock(MarketDataEngine.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullMatchingEngine() {
        new ExecutionEngine(mock(InsertionValidator.class), mock(OrderBook.class), null, mock(MarketDataEngine.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullMarketDataEngine() {
        new ExecutionEngine(mock(InsertionValidator.class), mock(OrderBook.class), mock(MatchingEngine.class), null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testExecuteNullOrder() {
        getExecutionEngine().execute(null);
    }

    @Test
    public void testExecuteMarketOrderNoLiquidity() {
        final ExecutionEngine executionEngine = getExecutionEngine();
        when(executionEngine.getInsertionValidator().validate(any(OrderBook.class),
                                                              any(Order.class))).thenReturn(false);
        
        final Order order = order(ordType(buy(), OrdType.MARKET));
        
        final OrderStatus orderStatus = executionEngine.execute(order);
        assertThat(orderStatus.getOrdStatus(), is(OrdStatus.REJECTED));
        assertThat(orderStatus.isEmpty(), is(true));
        
        verify(executionEngine.getMarketDataEngine()).reject(order);
        verifyNoMoreInteractions(executionEngine.getMarketDataEngine());
    }

    @Test
    public void testExecuteLimitOrderValidNoMatch() {

        final Order buy = order(limitOrdQtyPrice(buy(), defaultOrdQty, defaultPrice - 1L));
        final Order newSellOrder = order(limitOrdQtyPrice(sell(), defaultOrdQty, defaultPrice));

        final ExecutionEngine executionEngine = getExecutionEngine(buy);

        final OrderStatus newOrderStatus = executionEngine.execute(newSellOrder);

        final OrderBook orderBook = executionEngine.getOrderBook();
        final MarketDataEngine marketDataEngine = executionEngine.getMarketDataEngine();

        verify(marketDataEngine).newOrder(newSellOrder);
        verify(marketDataEngine).addOrder(newOrderStatus);
        verifyNoMoreInteractions(marketDataEngine);
        
        assertThat(newOrderStatus.isEmpty(), is(true));
        assertThat(newOrderStatus.getOrdStatus(), is(OrdStatus.NEW));
        assertThat(orderBook.isEmpty(Side.SELL), is(false));
        assertThat(orderBook.peek(Side.SELL), sameInstance(newSellOrder));
        assertThat(orderBook.getOrderStatus(newSellOrder), sameInstance(newOrderStatus));
        assertThat(orderBook.size(Side.SELL), is(1));
        assertThat(orderBook.isEmpty(Side.BUY), is(false));
        assertThat(orderBook.peek(Side.BUY), sameInstance(buy));
        assertThat(orderBook.size(Side.BUY), is(1));
    }
    
    @Test
    public void testExecuteMarketPartialFill() {

        final Order buy = order(limitOrdQtyPrice(buy(), defaultOrdQty, defaultPrice));
        final Order newSellOrder = order(orderQty(ordType(sell(), OrdType.MARKET), defaultOrdQty + 1L));

        final ExecutionEngine executionEngine = getExecutionEngine(buy);

        final OrderStatus newOrderStatus = executionEngine.execute(newSellOrder);

        final OrderBook orderBook = executionEngine.getOrderBook();
        final MarketDataEngine marketDataEngine = executionEngine.getMarketDataEngine();
        
        final ArgumentCaptor<Execution> executionArgumentCaptor = ArgumentCaptor.forClass(Execution.class);

        verify(marketDataEngine).newOrder(newSellOrder);
        verify(marketDataEngine).execution(executionArgumentCaptor.capture());
        verify(marketDataEngine).cancel(newOrderStatus);
        verifyNoMoreInteractions(marketDataEngine);

        assertThat(executionArgumentCaptor.getValue().getExecID(), is(executionEngine.getCurExecID() - 1));
        assertThat(executionArgumentCaptor.getValue().getPrice(), is(defaultPrice));
        assertThat(executionArgumentCaptor.getValue().getQuantity(), is(defaultOrdQty));
        assertThat(newOrderStatus.getOrdStatus(), is(OrdStatus.CANCELED));
        assertThat(newOrderStatus.getLeavesQty(), is(1L));
        assertThat(orderBook.isEmpty(Side.SELL), is(true));
        assertThat(orderBook.isEmpty(Side.BUY), is(true));
    }

    @Test
    public void testExecuteOrderValid() {
        final ExecutionEngine executionEngine = getExecutionEngine();
        final Order order = order(buy());
        
        final OrderStatus orderStatus = executionEngine.execute(order);

        assertThat(orderStatus, sameInstance(executionEngine.getOrderBook().getOrderStatus(order)));
        verify(executionEngine.getMarketDataEngine()).newOrder(order);
        verify(executionEngine.getMarketDataEngine()).addOrder(orderStatus);
        verifyNoMoreInteractions(executionEngine.getMarketDataEngine());
    }

    @Test
    public void testExecuteBuySideEmpty() {
        final ExecutionEngine executionEngine = getExecutionEngine();
        final OrderBook orderBook = executionEngine.getOrderBook();
        final Order sell = order(sell());

        executionEngine.execute(sell);

        final ArgumentCaptor<OrderStatus> orderStatusArgumentCaptor = ArgumentCaptor.forClass(OrderStatus.class);
        verify(executionEngine.getMarketDataEngine()).newOrder(sell);
        verify(executionEngine.getMarketDataEngine()).addOrder(orderStatusArgumentCaptor.capture());
        verifyNoMoreInteractions(executionEngine.getMarketDataEngine());

        assertThat(orderStatusArgumentCaptor.getValue().isEmpty(), is(true));
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

        executionEngine.execute(buy);

        final ArgumentCaptor<OrderStatus> orderStatusArgumentCaptor = ArgumentCaptor.forClass(OrderStatus.class);
        verify(executionEngine.getMarketDataEngine()).newOrder(buy);
        verify(executionEngine.getMarketDataEngine()).addOrder(orderStatusArgumentCaptor.capture());
        verifyNoMoreInteractions(executionEngine.getMarketDataEngine());

        assertThat(orderStatusArgumentCaptor.getValue().isEmpty(), is(true));

        assertThat(orderBook.isEmpty(Side.BUY), is(false));
        assertThat(orderBook.peek(Side.BUY), sameInstance(buy));
        assertThat(orderBook.size(Side.BUY), is(1));
        assertThat(orderBook.isEmpty(Side.SELL), is(true));
    }

    @Test
    public void testExecuteOrderValidMatch() {
        final ArgumentCaptor<Execution> tradeReport = ArgumentCaptor.forClass(Execution.class);
        final Order buy = order(orderQty(buy(), defaultOrdQty));
        final ExecutionEngine executionEngine = getExecutionEngine(buy);

        final Order newOrder = order(orderQty(sell(), defaultOrdQty - 1L));
        executionEngine.execute(newOrder);

        final OrderBook orderBook = executionEngine.getOrderBook();
        final MarketDataEngine marketDataEngine = executionEngine.getMarketDataEngine();

        verify(marketDataEngine).newOrder(newOrder);
        verify(marketDataEngine).execution(tradeReport.capture());

        assertThat(tradeReport.getValue().getExecID(), is(executionEngine.getCurExecID() - 1L));
        assertThat(tradeReport.getValue().getPrice(), is(defaultPrice));
        assertThat(tradeReport.getValue().getQuantity(), is(defaultOrdQty - 1L));

        assertThat(tradeReport.getValue().getNewOrderStatus().getOrder(), sameInstance(newOrder));
        assertThat(tradeReport.getValue().getLimitOrderStatus().getOrder(), sameInstance(buy));

        assertThat(tradeReport.getValue().getNewOrderStatus().isFilled(), is(true));

        assertThat(tradeReport.getValue().getLimitOrderStatus().isFilled(), is(false));
        assertThat(tradeReport.getValue().getLimitOrderStatus().getLeavesQty(), is(1L));

        assertThat(orderBook.isEmpty(Side.BUY), is(false));
        assertThat(orderBook.peek(Side.BUY), sameInstance(buy));
        assertThat(orderBook.isEmpty(Side.SELL), is(true));
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
        final OrderBook orderBook = OrderBooks.defaultOrderBook();
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
     * @param orders  {@link Order}s to add to {@link OrderBook}.
     *
     * @return an instance of {@link ExecutionEngine} with initialized {@link OrderBook}.
     */
    public static ExecutionEngine getExecutionEngine(final Order... orders) {
        final ExecutionEngine executionEngine = getExecutionEngine();
        final OrderBook orderBook = executionEngine.getOrderBook();
        for (final Order order : orders) {
            orderBook.insert(order);
        }
        return executionEngine;
    }


}
