package eugene.market.esma.execution;

import eugene.market.esma.enums.Side;
import eugene.market.esma.execution.MatchingEngine.MatchingResult;
import eugene.market.esma.execution.book.DefaultOrderBook;
import eugene.market.esma.execution.book.Order;
import eugene.market.esma.execution.book.OrderBook;
import eugene.market.esma.execution.book.OrderStatus;
import eugene.market.esma.execution.book.TradeReport;
import eugene.market.esma.execution.data.MarketDataEngine;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Accepts {@link Order}s to be executed on an {@link OrderBook}, validates then executes them and logs all
 * operations in the {@link MarketDataEngine}.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public class ExecutionEngine {

    private final OrderBook orderBook;

    private final MatchingEngine matchingEngine;

    private final InsertionValidator insertionValidator;

    private final MarketDataEngine marketDataEngine;

    /**
     * Constructs a {@link ExecutionEngine} that will use {@link InsertionValidator}, {@link
     * DefaultOrderBook}, {@link MatchingEngine} and {@link MarketDataEngine}.
     */
    public ExecutionEngine() {
        this(InsertionValidator.getInstance(), new DefaultOrderBook(), MatchingEngine.getInstance(),
             new MarketDataEngine());
    }

    /**
     * Constructs an {@link ExecutionEngine} that will use this <code>insertionValidator</code> to validate
     * {@link Order}s before inserting them to this <code>orderBook</code>, this <code>matchingEngine</code> to match
     * {@link Order}s and this <code>marketDataEngine</code> to store executions.
     *
     * @param insertionValidator {@link InsertionValidator} to use.
     * @param orderBook          {@link OrderBook} to use.
     * @param matchingEngine     {@link MatchingEngine} to use.
     * @param marketDataEngine   {@link MarketDataEngine} to use.
     */
    public ExecutionEngine(final InsertionValidator insertionValidator,
                           final OrderBook orderBook,
                           final MatchingEngine matchingEngine,
                           final MarketDataEngine marketDataEngine) {
        checkNotNull(orderBook);
        checkNotNull(matchingEngine);
        checkNotNull(insertionValidator);
        checkNotNull(marketDataEngine);

        this.orderBook = orderBook;
        this.matchingEngine = matchingEngine;
        this.insertionValidator = insertionValidator;
        this.marketDataEngine = marketDataEngine;
    }

    /**
     * Inserts this <code>order</code> into the {@link OrderBook}.
     *
     * @param order order to insert.
     *
     * @return <code>true</code> if insertion was successful, <code>null</code> otherwise.
     */
    public OrderStatus insertOrder(final Order order) {

        checkNotNull(order);

        if (!insertionValidator.validate(orderBook, order)) {
            return null;
        }

        final OrderStatus orderStatus = orderBook.insertOrder(order);

        marketDataEngine.newOrder(order);

        execute();

        return orderStatus;
    }

    /**
     * Cancels this <code>order</code>.
     *
     * @param order {@link Order} to cancel.
     *
     * @return {@link OrderStatus} of cancelled {@link Order} or <code>null</code> if <code>order</code> does not
     *         exist.
     */
    public OrderStatus cancel(final Order order) {
        checkNotNull(order);

        final OrderStatus orderStatus = orderBook.cancel(order);

        if (null != orderStatus) {
            marketDataEngine.cancel(orderStatus);
        }

        return orderStatus;
    }

    /**
     * Executes matching {@link Order}s in <code>orderBook</code>.
     */
    public void execute() {

        while (!orderBook.isEmpty(Side.BUY) && !orderBook.isEmpty(Side.SELL)) {

            final Order buyOrder = orderBook.peek(Side.BUY);
            final Order sellOrder = orderBook.peek(Side.SELL);

            final MatchingResult matchResult = matchingEngine.match(buyOrder, sellOrder);

            if (!matchResult.isMatch()) {
                break;
            }

            final TradeReport tradeReport = orderBook.execute(matchResult.getPrice());
            marketDataEngine.trade(tradeReport);
        }
    }

    /**
     * Gets the {@link InsertionValidator} implementation used by this {@link ExecutionEngine}.
     *
     * @return implementation of {@link InsertionValidator}.
     */
    public InsertionValidator getInsertionValidator() {
        return insertionValidator;
    }

    /**
     * Gets the {@link OrderBook} implementation used by this {@link ExecutionEngine}.
     *
     * @return implementation of {@link OrderBook}.
     */
    public OrderBook getOrderBook() {
        return orderBook;
    }

    /**
     * Gets the {@link MatchingEngine} implementation used by this {@link ExecutionEngine}.
     *
     * @return implementation of {@link MatchingEngine}.
     */
    public MatchingEngine getMatchingEngine() {
        return this.matchingEngine;
    }

    /**
     * Gets the {@link MarketDataEngine} implementation used by this {@link MarketDataEngine}.
     *
     * @return implementation of {@link MarketDataEngine}.
     */
    public MarketDataEngine getMarketDataEngine() {
        return marketDataEngine;
    }
}

