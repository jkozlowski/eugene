/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.client.impl;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.primitives.Longs;
import eugene.market.book.Order;
import eugene.market.client.OrderReference;
import eugene.market.client.OrderReferenceListener;
import eugene.market.ontology.field.ClOrdID;
import eugene.market.ontology.field.enums.OrdStatus;
import eugene.market.ontology.field.enums.OrdType;
import eugene.market.ontology.field.enums.Side;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.primitives.Longs.compare;
import static eugene.market.ontology.field.enums.OrdStatus.FILLED;
import static eugene.market.ontology.field.enums.OrdStatus.PARTIALLY_FILLED;

/**
 * Default implementation of {@link OrderReference}.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public final class OrderReferenceImpl implements OrderReference {

    private final OrderReferenceListener listener;

    private final Long creationTime;

    private final String clOrdID;

    private final Side side;

    private final OrdType ordType;

    private final BigDecimal price;

    private final Long orderQty;

    private OrdStatus ordStatus;

    private BigDecimal avgPx;

    private Long leavesQty;

    private Long cumQty;

    /**
     * Default constructor.
     *
     * @param listener     {@link OrderReferenceListener} for this {@link OrderReferenceImpl}.
     * @param clOrdID      {@link ClOrdID} of this {@link OrderReferenceImpl}.
     * @param creationTime new creationTime.
     * @param ordType      new ordType.
     * @param side         new side.
     * @param orderQty     new orderQty.
     * @param price        new price.
     *
     * @throws NullPointerException     if either parameter is null.
     * @throws IllegalArgumentException if <code>clOrdID</code> is empty.
     * @throws IllegalArgumentException if <code>ordQty</code> is <code><= 0</code>.
     * @throws IllegalArgumentException if <code>ordType</code> is {@link OrdType#LIMIT} and <code>price</code> is
     *                                  <code><= 0</code>, or if <code>ordType</code> is {@link OrdType#MARKET} and
     *                                  <code>price</code> is <code>!= 0</code>.
     */
    public OrderReferenceImpl(final OrderReferenceListener listener, final String clOrdID, final Long creationTime,
                              final OrdType ordType, final Side side, final Long orderQty, final BigDecimal price)
            throws NullPointerException, IllegalArgumentException {

        checkNotNull(listener);
        checkNotNull(clOrdID);
        checkArgument(!clOrdID.isEmpty());
        checkNotNull(creationTime);
        checkNotNull(ordType);
        checkNotNull(side);
        checkNotNull(orderQty);
        checkArgument(compare(orderQty, Order.NO_QTY) == 1);
        checkNotNull(price);
        checkArgument((ordType.isLimit() && price.compareTo(Order.NO_PRICE) == 1) ||
                              (ordType.isMarket() && price.compareTo(Order.NO_PRICE) == 0));

        this.listener = listener;
        this.clOrdID = clOrdID;
        this.creationTime = creationTime;
        this.orderQty = orderQty;
        this.side = side;
        this.ordType = ordType;
        this.price = price;

        this.ordStatus = OrdStatus.NEW;
        this.avgPx = Order.NO_PRICE;
        this.leavesQty = orderQty;
        this.cumQty = Order.NO_QTY;
    }

    /**
     * Returns the listener.
     *
     * @return the listener.
     */
    public OrderReferenceListener getOrderReferenceListener() {
        return listener;
    }

    @Override
    public String getClOrdID() {
        return this.clOrdID;
    }

    @Override
    public Long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public Side getSide() {
        return this.side;
    }

    @Override
    public OrdType getOrdType() {
        return this.ordType;
    }

    @Override
    public BigDecimal getPrice() {
        return this.price;
    }

    @Override
    public Long getOrderQty() {
        return this.orderQty;
    }

    @Override
    public synchronized OrdStatus getOrdStatus() {
        return this.ordStatus;
    }

    @Override
    public synchronized BigDecimal getAvgPx() {
        return this.avgPx;
    }

    @Override
    public synchronized Long getLeavesQty() {
        return this.leavesQty;
    }

    @Override
    public synchronized Long getCumQty() {
        return this.cumQty;
    }

    /**
     * Sets the status of this {@link OrderReferenceImpl} to {@link OrdStatus#CANCELED}.
     *
     * @throws IllegalStateException if the current status is not either {@link OrdStatus#NEW},
     *                               {@link OrdStatus#PARTIALLY_FILLED} or {@link OrdStatus#FILLED}.
     */
    public synchronized void cancel() {
        checkState(this.ordStatus.isNew() || this.ordStatus.isPartiallyFilled() || this.ordStatus.isPartiallyFilled());
        this.ordStatus = OrdStatus.CANCELED;
    }

    /**
     * Sets the status of this {@link OrderReferenceImpl} to {@link OrdStatus#REJECTED}.
     *
     * @throws IllegalStateException if the current status is not {@link OrdStatus#NEW}.
     */
    public synchronized void reject() {
        checkState(this.ordStatus.isNew());
        this.ordStatus = OrdStatus.REJECTED;
    }

    /**
     * Executes this <code>quantity</code> of this {@link Order} at this <code>price</code>.
     *
     * @param price    price to execute at.
     * @param quantity quantity to execute
     */
    public synchronized void execute(final BigDecimal price, final Long quantity) {
        checkNotNull(price);
        checkArgument(price.compareTo(Order.NO_PRICE) == 1);
        checkNotNull(quantity);
        checkArgument(Longs.compare(quantity, Order.NO_QTY) == 1);
        checkArgument(Longs.compare(quantity, this.leavesQty) <= 0);
        checkState(this.ordStatus.isNew() || this.ordStatus.isPartiallyFilled());

        final BigDecimal quantityPrice = price.multiply(BigDecimal.valueOf(quantity));
        final BigDecimal avgPxCumQty = this.avgPx.multiply(BigDecimal.valueOf(this.cumQty));
        final BigDecimal quantityCumQty = BigDecimal.valueOf(quantity).add(BigDecimal.valueOf(this.cumQty));

        this.avgPx = quantityPrice.add(avgPxCumQty).divide(quantityCumQty, RoundingMode.HALF_UP);
        this.leavesQty = this.leavesQty - quantity;
        this.cumQty = this.cumQty + quantity;
        this.ordStatus = this.leavesQty.equals(Long.valueOf(0L)) ? FILLED : PARTIALLY_FILLED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final ToStringHelper toString = Objects.toStringHelper(OrderReferenceImpl.class);
        toString.add("creationTime", creationTime);
        toString.add("clOrdID", clOrdID);
        toString.add("side", side);
        toString.add("ordType", ordType);
        toString.add("price", price);
        toString.add("orderQty", orderQty);
        toString.add("ordStatus", ordStatus);
        toString.add("avgPx", avgPx);
        toString.add("leavesQty", leavesQty);
        toString.add("cumQty", cumQty);
        return toString.toString();
    }
}
