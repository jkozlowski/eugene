package eugene.market.book;

import com.google.common.primitives.Doubles;
import eugene.market.ontology.field.enums.OrdType;
import eugene.market.ontology.field.enums.Side;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.primitives.Longs.compare;

/**
 * Entry for a single order in an {@link OrderBook}.
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

    private final Long orderID;

    private final OrdType ordType;

    private final Side side;

    private final Long orderQty;

    private final Double price;

    /**
     * Constructs an {@link Order} with {@link System#nanoTime()} <code>entryTime</code>.
     *
     * @param orderID  new orderID.
     * @param ordType  new ordType.
     * @param side     new side.
     * @param orderQty new orderQty.
     * @param price    new price.
     */
    public Order(final Long orderID, final OrdType ordType, final Side side,
                 final Long orderQty, final Double price) {
        this(orderID, System.nanoTime(), ordType, side, orderQty, price);
    }

    /**
     * Default constructor.
     *
     * @param orderID   new orderID.
     * @param entryTime new entryTime.
     * @param ordType   new ordType.
     * @param side      new side.
     * @param orderQty  new orderQty.
     * @param price     new price.
     */
    public Order(final Long orderID, final Long entryTime, final OrdType ordType,
                 final Side side, final Long orderQty, final Double price) {
        checkNotNull(orderID);
        checkNotNull(entryTime);
        checkNotNull(ordType);
        checkNotNull(side);
        checkNotNull(orderQty);
        checkArgument(compare(orderQty, NO_QTY) == 1);
        checkNotNull(price);
        checkArgument((ordType.isLimit() && Doubles.compare(price, NO_PRICE) == 1) ||
                              (ordType.isMarket() && Doubles.compare(price, NO_PRICE) == 0));

        this.orderID = orderID;
        this.entryTime = entryTime;
        this.orderQty = orderQty;
        this.side = side;
        this.ordType = ordType;
        this.price = price;
    }

    /**
     * Gets the orderID.
     *
     * @return the orderID.
     */
    public Long getOrderID() {
        return orderID;
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

    @Override
    public int compareTo(Order o2) {
        checkNotNull(o2);
        checkArgument(getSide().equals(o2.getSide()));

        // Compare object references
        if (this == o2) {
            return SAME;
        }

        // LIMIT > MARKET
        if (ordType.isLimit() && o2.ordType.isMarket()) {
            return AFTER;
        }

        // MARKET < LIMIT
        if (ordType.isMarket() && o2.ordType.isLimit()) {
            return BEFORE;
        }

        // MARKET < MARKET if o1.entryTime < o2.entryTime, > if o1.entryTime < o2.entryTime
        if (ordType.isMarket() && o2.ordType.isMarket()) {
            return entryTime < o2.entryTime ? BEFORE
                    : (entryTime > o2.entryTime ? AFTER : SAME);
        }

        // LIMIT < LIMIT, if o1.price < o2.price
        if (price < o2.price) {
            return side.isBuy() ? AFTER : BEFORE;
        }

        // LIMIT > LIMIT, if o1.price > o2.price
        if (price > o2.price) {
            return side.isBuy() ? BEFORE : AFTER;
        }

        return entryTime < o2.entryTime ? BEFORE
                : (entryTime > o2.entryTime ? AFTER : SAME);
    }

    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder("Order[");
        b.append("entryTime=").append(entryTime);
        b.append(", orderID='").append(orderID).append('\'');
        b.append(", side=").append(side);
        b.append(", ordType=").append(ordType);
        b.append(", price=").append(price);
        b.append(", orderQty=").append(orderQty).append(']');
        return b.toString();
    }
}
