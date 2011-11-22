package eugene.market.esma;

import eugene.market.esma.enums.OrdType;
import eugene.market.esma.enums.Side;
import jade.core.AID;

import javax.validation.constraints.NotNull;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An entry for a single order in an {@link OrderBook}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public class Order implements Comparable<Order> {

    /**
     * Indicates that this {@link Order} should appear before the second one.
     */
    public static final int BEFORE = -1;

    /**
     * Indicates that both orders are equivalent.
     *
     * This situation should not take place, but the constant is provided for completeness.
     */
    public static final int SAME = 0;

    /**
     * Indicates that this {@link Order} should appear after the second one.
     */
    public static final int AFTER = 1;

    /**
     * Indicates that there is no price.
     */
    public static final Double NO_PRICE = new Double(0.0D);

    /**
     * Indicates that there is no quantity.
     */
    public static final Long NO_QTY = new Long(0L);

    private final Long entryTime;

    private final String clOrdID;

    private final String symbol;

    private final AID aid;

    private final Side side;

    private final OrdType ordType;

    private final Double price;

    private final Long orderQty;

    private final ExecutionStatus executionStatus;

    /**
     * Constructs a with {@link System#nanoTime()} <code>entryTime</code>.
     *
     * @param orderQty new orderQty.
     * @param clOrdID  new clOrdID.
     * @param symbol   new symbol.
     * @param aid      aid of the agent submitting the order. Assumed local.
     * @param side     new side.
     * @param price    new price.
     * @param ordType  new ordType.
     */
    public Order(@NotNull final Long orderQty, @NotNull final String clOrdID, @NotNull final String symbol,
                 @NotNull final AID aid, @NotNull final Side side, @NotNull final OrdType ordType,
                 @NotNull final Double price) {
        this(orderQty, clOrdID, symbol, aid, side, ordType, price, System.nanoTime());
    }

    /**
     * Constructs a {@link OrdType#LIMIT} order.
     *
     * @param orderQty  new orderQty.
     * @param clOrdID   new clOrdID.
     * @param symbol    new symbol.
     * @param aid       aid of the agent submitting the order. Assumed local.
     * @param side      new side.
     * @param ordType   new ordType.
     * @param price     new price.
     * @param entryTime new entryTime.
     */
    public Order(@NotNull final Long orderQty, @NotNull final String clOrdID, @NotNull final String symbol,
                 @NotNull final AID aid, @NotNull final Side side, @NotNull final OrdType ordType,
                 @NotNull final Double price, @NotNull final Long entryTime) {
        checkNotNull(orderQty);
        checkNotNull(clOrdID, "clOrdID cannot be null");
        checkArgument(!clOrdID.isEmpty(), "clOrdID cannot be empty");
        checkNotNull(symbol, "symbol cannot be null");
        checkArgument(!symbol.isEmpty(), "symbol cannot be empty");
        checkNotNull(aid, "aid cannot be null");
        checkNotNull(side);
        checkNotNull(price);

        this.orderQty = orderQty;
        this.entryTime = entryTime;
        this.clOrdID = clOrdID;
        this.symbol = symbol;
        this.aid = (AID) aid.clone();
        this.side = side;
        this.ordType = ordType;
        this.price = price;
        this.executionStatus = new ExecutionStatus(this.orderQty);
    }

    /**
     * Gets the entryTime.
     *
     * @return the entryTime.
     */
    public Long getEntryTime() {
        return entryTime;
    }

    /**
     * Gets the clOrdID.
     *
     * @return the clOrdID.
     */
    public String getClOrdID() {
        return clOrdID;
    }

    /**
     * Gets the symbol.
     *
     * @return the symbol.
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Gets the aid.
     *
     * @return the aid.
     */
    public AID getAID() {
        return new AID(aid.getName(), AID.ISLOCALNAME);
    }

    /**
     * Gets the side.
     *
     * @return the side.
     */
    public Side getSide() {
        return side;
    }

    /**
     * Gets the ordType.
     *
     * @return the ordType.
     */
    public OrdType getOrdType() {
        return ordType;
    }

    /**
     * Gets the price.
     *
     * @return the price or {@link Order#NO_PRICE} if the order is of type {@link OrdType#MARKET}.
     */
    public Double getPrice() {
        return price;
    }

    /**
     * Gets the orderQty.
     *
     * @return the orderQty.
     */
    public Long getOrderQty() {
        return orderQty;
    }

    /**
     * Gets the openQuantity.
     *
     * @return the openQuantity.
     */
    public Long getOpenQuantity() {
        return executionStatus.openQuantity;
    }

    /**
     * Gets the executedQuantity.
     *
     * @return the executedQuantity or {@link Order#NO_QTY} if the order has not yet been matched.
     */
    public Long getExecutedQuantity() {
        return executionStatus.executedQuantity;
    }

    /**
     * Gets the avgExecutedPrice.
     *
     * @return the avgExecutedPrice or {@link Order#NO_PRICE} if the order has not yet been matched.
     */
    public Double getAvgExecutedPrice() {
        return executionStatus.avgExecutedPrice;
    }

    /**
     * Gets the lastExecutedPrice.
     *
     * @return the lastExecutedPrice or {@link Order#NO_PRICE} if the order has not yet been matched.
     */
    public Double getLastExecutedPrice() {
        return executionStatus.lastExecutedPrice;
    }

    /**
     * Gets the lastExecutedQuantity.
     *
     * @return the lastExecutedQuantity or {@link Order#NO_QTY} if the order has not yet been matched.
     */
    public Long getLastExecutedQuantity() {
        return executionStatus.lastExecutedQuantity;
    }

    /**
     * Returns <code>true</code> if <code>{@link Order#getOrderQty()} == {@link ExecutionStatus#getExecutedQuantity()
     * }</code>, <code>false</code> otherwise.
     *
     * @return true if filled, false otherwise.
     */
    public boolean isFilled() {
        return orderQty == executionStatus.executedQuantity;
    }

    /**
     * Executes this {@link Order} and updates {@link ExecutionStatus}.
     */
    public void execute(double price, long quantity) {
        executionStatus.avgExecutedPrice =
                ((quantity * price) + (executionStatus.avgExecutedPrice * executionStatus.executedQuantity))
                / (quantity + executionStatus.executedQuantity);

        executionStatus.openQuantity -= quantity;
        executionStatus.executedQuantity += quantity;
        executionStatus.lastExecutedPrice = price;
        executionStatus.lastExecutedQuantity = quantity;
    }

    /**
     * Returns <code>true</code> if <code>{@link OrdType#LIMIT} == {@link Order#getOrdType()}</code>.
     *
     * @return <code>true</code> if <code>{@link OrdType#LIMIT} == {@link Order#getOrdType()}</code>,
     *         <code>false</code> otherwise.
     */
    public boolean isOrdTypeLimit() {
        return OrdType.LIMIT == ordType;
    }

    /**
     * Returns <code>true</code> if <code>{@link OrdType#MARKET} == {@link Order#getOrdType()}</code>.
     *
     * @return <code>true</code> if <code>{@link OrdType#MARKET} == {@link Order#getOrdType()}</code>,
     *         <code>false</code> otherwise.
     */
    public boolean isOrdTypeMarket() {
        return OrdType.MARKET == ordType;
    }

    /**
     * Returns <code>true</code> if <code>{@link Side#BUY} == {@link Order#getSide()}</code>.
     *
     * @return <code>true</code> if <code>{@link Side#BUY} == {@link Order#getSide()}</code>,
     *         <code>false</code> otherwise.
     */
    public boolean isSideBuy() {
        return Side.BUY == side;
    }

    /**
     * Returns <code>true</code> if <code>{@link Side#SELL} == {@link Order#getSide()}</code>.
     *
     * @return <code>true</code> if <code>{@link Side#SELL} == {@link Order#getSide()}</code>,
     *         <code>false</code> otherwise.
     */
    public boolean isSideSell() {
        return Side.SELL == side;
    }

    @Override
    public int compareTo(Order o2) {
        checkNotNull(o2);
        checkArgument(getSide().equals(o2.getSide()));

        // Compare object references
        if (this == o2) {
            return SAME;
        }

        // LIMIT > MARKET
        if (isOrdTypeLimit() && o2.isOrdTypeMarket()) {
            return AFTER;
        }

        // MARKET < LIMIT
        if (isOrdTypeMarket() && o2.isOrdTypeLimit()) {
            return BEFORE;
        }

        // MARKET < MARKET if o1.entryTime < o2.entryTime, > if o1.entryTime < o2.entryTime
        if (isOrdTypeMarket() && o2.isOrdTypeMarket()) {
            return getEntryTime() < o2.getEntryTime() ? BEFORE
                                                      : (getEntryTime() > o2.getEntryTime() ? AFTER : SAME);
        }

        // LIMIT < LIMIT, if o1.price < o2.price
        if (getPrice() < o2.getPrice()) {
            return isSideBuy() ? AFTER : BEFORE;
        }

        // LIMIT > LIMIT, if o1.price > o2.price
        if (getPrice() > o2.getPrice()) {
            return isSideBuy() ? BEFORE : AFTER;
        }

        return getEntryTime() < o2.getEntryTime() ? BEFORE
                                                  : (getEntryTime() > o2.getEntryTime() ? AFTER : SAME);
    }

    @Override
    public String toString() {
        return "Order{" +
               "entryTime=" + entryTime +
               ", clOrdID='" + clOrdID + '\'' +
               ", symbol='" + symbol + '\'' +
               ", aid=" + aid +
               ", side=" + side +
               ", ordType=" + ordType +
               ", price=" + price +
               ", orderQty=" + orderQty +
               ", executionStatus=" + executionStatus +
               '}';
    }

    /**
     * Execution status of an {@link Order}.
     */
    public class ExecutionStatus {

        private Long openQuantity;

        private Long executedQuantity;

        private Double avgExecutedPrice;

        private Double lastExecutedPrice;

        private Long lastExecutedQuantity;

        public ExecutionStatus(@NotNull final Long orderQty) {
            this.openQuantity = orderQty;
            this.executedQuantity = NO_QTY;
            this.avgExecutedPrice = NO_PRICE;
            this.lastExecutedPrice = NO_PRICE;
            this.lastExecutedQuantity = NO_QTY;
        }
    }
}
