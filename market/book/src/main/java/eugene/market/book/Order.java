/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.book;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import eugene.market.ontology.field.enums.OrdType;
import eugene.market.ontology.field.enums.Side;

import java.math.BigDecimal;

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
    public static final BigDecimal NO_PRICE = BigDecimal.ZERO;

    /**
     * Indicates that there is no quantity.
     */
    public static final Long NO_QTY = Long.valueOf(0L);

    private final Long entryTime;

    private final Long orderID;

    private final OrdType ordType;

    private final Side side;

    private final Long orderQty;

    private final BigDecimal price;

    private final int hashCode;

    /**
     * Constructs an {@link Order} with {@link System#nanoTime()} <code>entryTime</code>.
     *
     * @see {@link Order#Order(Long, Long, OrdType, Side, Long, BigDecimal)}.
     */
    public Order(final Long orderID, final OrdType ordType, final Side side,
                 final Long orderQty, final BigDecimal price) {
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
     *
     * @throws NullPointerException     if either parameter is null.
     * @throws IllegalArgumentException if <code>ordQty</code> is <code><= 0</code>.
     * @throws IllegalArgumentException if <code>ordType</code> is {@link OrdType#LIMIT} and <code>price</code> is
     *                                  <code><= 0</code>, or if <code>ordType</code> is {@link OrdType#MARKET} and
     *                                  <code>price</code> is <code>!= 0</code>.
     */
    public Order(final Long orderID, final Long entryTime, final OrdType ordType,
                 final Side side, final Long orderQty, final BigDecimal price)
            throws NullPointerException, IllegalArgumentException {

        checkNotNull(orderID);
        checkNotNull(entryTime);
        checkNotNull(ordType);
        checkNotNull(side);
        checkNotNull(orderQty);
        checkArgument(compare(orderQty, NO_QTY) == 1);
        checkNotNull(price);
        checkArgument((ordType.isLimit() && price.compareTo(NO_PRICE) == 1) ||
                              (ordType.isMarket() && price.compareTo(NO_PRICE) == 0));

        this.orderID = orderID;
        this.entryTime = entryTime;
        this.orderQty = orderQty;
        this.side = side;
        this.ordType = ordType;
        this.price = price;

        int hashCode = entryTime.hashCode();
        hashCode = 31 * hashCode + orderID.hashCode();
        hashCode = 31 * hashCode + ordType.hashCode();
        hashCode = 31 * hashCode + side.hashCode();
        hashCode = 31 * hashCode + orderQty.hashCode();
        hashCode = 31 * hashCode + price.hashCode();
        this.hashCode = hashCode;
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
    public BigDecimal getPrice() {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Order order = (Order) o;

        if (!entryTime.equals(order.entryTime)) return false;
        if (!orderID.equals(order.orderID)) return false;
        if (!ordType.equals(order.ordType)) return false;
        if (!orderQty.equals(order.orderQty)) return false;
        if (!price.equals(order.price)) return false;
        if (side != order.side) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public int compareTo(Order o2) {

        checkNotNull(o2);
        checkArgument(side.equals(o2.side));

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
        if (price.compareTo(o2.price) < 0) {
            return side.isBuy() ? AFTER : BEFORE;
        }

        // LIMIT > LIMIT, if o1.price > o2.price
        if (price.compareTo(o2.price) > 0) {
            return side.isBuy() ? BEFORE : AFTER;
        }

        return entryTime < o2.entryTime ? BEFORE
                : (entryTime > o2.entryTime ? AFTER : SAME);
    }

    @Override
    public String toString() {
        final ToStringHelper toString = Objects.toStringHelper(Order.class);
        toString.add("entryTime", entryTime);
        toString.add("orderID", orderID);
        toString.add("side", side);
        toString.add("ordType", ordType);
        toString.add("price", price);
        toString.add("orderQty", orderQty);
        return toString.toString();
    }
}
