package eugene.market.esma.impl.execution;

import com.google.common.primitives.Longs;
import eugene.market.book.Order;
import eugene.market.book.OrderBook;
import eugene.market.book.OrderStatus;
import eugene.market.book.impl.DefaultOrderBook;
import eugene.market.esma.impl.execution.MatchingEngine.MatchingResult;
import eugene.market.esma.impl.execution.data.MarketDataEngine;
import eugene.market.ontology.field.OrderID;
import eugene.market.ontology.field.enums.OrdType;

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

    private final AtomicLong curOrderID = new AtomicLong(1L);

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
     * Executes this <code>newOrder</code>.
     *
     * Quantity not executed is is dealt with differently, depending on the <code>ordType</code>:
     * <ul>
     * <li>If {@link OrdType#MARKET}</li> the quantity not executed is cancelled.</li>
     * <li>If {@link OrdType#LIMIT} the quantity not executed is put on the {@link OrderBook}</li>
     * </ul>
     *
     * @return final status of this {@link Order}.
     */
    public OrderStatus execute(final Order newOrder) {

        checkNotNull(newOrder);

        OrderStatus newOrderStatus = new OrderStatus(newOrder);

        if (!insertionValidator.validate(orderBook, newOrder)) {
            final OrderStatus rejectedOrderStatus = newOrderStatus.reject();
            marketDataEngine.reject(rejectedOrderStatus);
            return rejectedOrderStatus;
        }

        marketDataEngine.newOrder(newOrderStatus);

        while (!newOrderStatus.isFilled() && !orderBook.isEmpty(newOrder.getSide().getOpposite())) {

            final Order limitOrder = orderBook.peek(newOrder.getSide().getOpposite());
            final MatchingResult matchingResult = matchingEngine.match(newOrder, limitOrder);

            if (!matchingResult.isMatch()) {
                break;
            }

            final Double execPrice = matchingResult.getPrice();

            final Long execQty = Longs.min(newOrderStatus.getLeavesQty(),
                                           orderBook.getOrderStatus(limitOrder).getLeavesQty());

            newOrderStatus = newOrderStatus.execute(execPrice, execQty);
            final OrderStatus limitOrderStatus = orderBook.execute(limitOrder.getSide(), execQty, execPrice);

            final Execution execution = new Execution(curExecID.getAndIncrement(), newOrderStatus, limitOrderStatus,
                                                      execPrice, execQty);

            marketDataEngine.execution(execution);
        }

        if (newOrder.getOrdType().isLimit() && !newOrderStatus.isFilled()) {
            orderBook.insert(newOrder, newOrderStatus);
            marketDataEngine.addOrder(newOrderStatus);
        }

        if (newOrder.getOrdType().isMarket() && !newOrderStatus.isFilled()) {
            newOrderStatus = newOrderStatus.cancel();
            marketDataEngine.cancel(newOrderStatus);
        }

        return newOrderStatus;
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
     * Gets curExecID.
     *
     * @return the curExecID.
     */
    public Long getCurExecID() {
        return curExecID.get();
    }

    /**
     * Returns a unique {@link OrderID}.
     *
     * @return unique {@link OrderID}.
     */
    public Long getOrderID() {
        return curOrderID.getAndIncrement();
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

