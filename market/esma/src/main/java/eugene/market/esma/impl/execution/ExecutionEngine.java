package eugene.market.esma.impl.execution;

import com.google.common.primitives.Longs;
import eugene.market.book.Order;
import eugene.market.book.OrderBook;
import eugene.market.book.OrderStatus;
import eugene.market.book.impl.DefaultOrderBook;
import eugene.market.esma.impl.Orders;
import eugene.market.esma.impl.execution.MatchingEngine.MatchingResult;
import eugene.market.esma.impl.execution.data.MarketDataEngine;
import eugene.market.ontology.field.enums.Side;

import java.util.concurrent.atomic.AtomicLong;

import static com.google.common.base.Preconditions.checkNotNull;
import static eugene.market.book.OrderBooks.defaultOrderBook;

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

    private final AtomicLong curExecID = new AtomicLong(1L);

    private long messagesReceived = 0;

    private long lastNewOrderTime = System.currentTimeMillis();

    /**
     * Constructs a {@link ExecutionEngine} that will use {@link InsertionValidator}, {@link
     * DefaultOrderBook}, {@link MatchingEngine} and {@link MarketDataEngine}.
     */
    public ExecutionEngine() {
        this(InsertionValidator.getInstance(), defaultOrderBook(), MatchingEngine.getInstance(),
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

        final OrderStatus orderStatus = orderBook.insert(order);

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

        messagesReceived++;
        final long oldLastNewOrderTime = lastNewOrderTime;
        final long curTime = System.currentTimeMillis();
        if (curTime - oldLastNewOrderTime >= 1000) {
            lastNewOrderTime = curTime;
            System.out.print("Message rate=" + messagesReceived + " msg/min");
            messagesReceived = 0;
        }

        while (!orderBook.isEmpty(Side.BUY) && !orderBook.isEmpty(Side.SELL)) {

            final Order buyOrder = orderBook.peek(Side.BUY);
            final Order sellOrder = orderBook.peek(Side.SELL);

            final MatchingResult matchResult = matchingEngine.match(buyOrder, sellOrder);

            if (!matchResult.isMatch()) {
                break;
            }

            final Long orderQty = Longs.min(orderBook.getOrderStatus(buyOrder).getLeavesQty(),
                                            orderBook.getOrderStatus(sellOrder).getLeavesQty());

            final OrderStatus buyOrderStatus = orderBook.execute(Side.BUY, orderQty, matchResult.getPrice());
            final OrderStatus sellOrderStatus = orderBook.execute(Side.SELL, orderQty, matchResult.getPrice());

            final Execution execution = new Execution(curExecID.getAndIncrement(), buyOrderStatus, sellOrderStatus,
                                                      matchResult.getPrice(), orderQty);

            marketDataEngine.execution(execution);
        }

        System.out.println("Current price: buy=" +
                                   (orderBook.isEmpty(Side.BUY) ? "no price" : orderBook.peek(Side.BUY).getPrice()) +

                                   ", sell=" +
                                   (orderBook.isEmpty(Side.SELL) ? "no price" : orderBook.peek(Side.SELL).getPrice())
                                   + "; curExecID=" + curExecID.get() + "; curOrderID=" + Orders.curOrderID.get()
        );
    }

    /**
     * Gets curExecID.
     *
     * @return the curExecID.
     */
    public Long getCurExecID() {
        return curExecID.get();
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

