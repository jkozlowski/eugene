package eugene.market.book;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Longs;
import eugene.market.ontology.field.enums.OrdStatus;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.primitives.Doubles.compare;
import static eugene.market.ontology.field.enums.OrdStatus.FILLED;
import static eugene.market.ontology.field.enums.OrdStatus.PARTIALLY_FILLED;

/**
 * Status of execution of an {@link Order}.
 */
public class OrderStatus {

    private final OrdStatus ordStatus;

    private final Order order;

    private final Double avgPx;

    private final Long leavesQty;

    private final Long cumQty;

    /**
     * Constructs a not executed {@link OrderStatus} for this {@link Order}.
     *
     * @param order {@link Order} that this {@link OrderStatus} describes.
     */
    public OrderStatus(final Order order) {
        this(order, Order.NO_PRICE, order.getOrderQty(), Order.NO_QTY, OrdStatus.NEW);
    }

    /**
     * Default constructor.
     *
     * @param order     {@link Order} that this {@link OrderStatus} describes.
     * @param avgPx     average price of execution.
     * @param leavesQty outstanding quantity.
     * @param cumQty    executed quantity.
     */
    // TODO: check the ordStatus consistency.
    public OrderStatus(final Order order, final Double avgPx,
                       final Long leavesQty, final Long cumQty, final OrdStatus ordStatus) {
        checkNotNull(order);
        checkNotNull(avgPx);
        checkArgument(compare(avgPx, Order.NO_PRICE) >= 0);
        checkNotNull(leavesQty);
        checkArgument(compare(leavesQty, Order.NO_QTY) >= 0);
        checkArgument(compare(leavesQty, order.getOrderQty()) <= 0);
        checkNotNull(cumQty);
        checkArgument(compare(cumQty, Order.NO_QTY) >= 0);
        checkArgument(compare(cumQty, order.getOrderQty()) <= 0);
        checkArgument(compare(order.getOrderQty(), cumQty + leavesQty) == 0);
        checkNotNull(ordStatus);

        this.order = order;
        this.avgPx = avgPx;
        this.leavesQty = leavesQty;
        this.cumQty = cumQty;
        this.ordStatus = ordStatus;
    }

    /**
     * Gets the order.
     *
     * @return the order.
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Gets the avgPx.
     *
     * @return the avgPx.
     */
    public Double getAvgPx() {
        return avgPx;
    }

    /**
     * Gets the leavesQty.
     *
     * @return the leavesQty.
     */
    public Long getLeavesQty() {
        return leavesQty;
    }

    /**
     * Gets the cumQty.
     *
     * @return the cumQty.
     */
    public Long getCumQty() {
        return cumQty;
    }

    /**
     * Gets the ordStatus.
     *
     * @return the ordStatus.
     */
    public OrdStatus getOrdStatus() {
        return ordStatus;
    }

    /**
     * Checks if this {@link Order} has been filled.
     *
     * @return <code>true</code> if this {@link Order} has been filled, <code>false</code> otherwise.
     */
    public boolean isFilled() {
        return leavesQty.equals(Long.valueOf(0L));
    }

    /**
     * Checks if this {@link Order} has not been executed at all.
     *
     * @return <code>true</code> if this {@link Order} has not been executed at all, <code>false</code> otherwise.
     */
    public boolean isEmpty() {
        return order.getOrderQty().equals(leavesQty);
    }

    /**
     * Gets a copy of this {@link OrderStatus} with {@link OrdStatus#REJECTED}.
     *
     * @return new {@link OrderStatus}.
     */
    public OrderStatus reject() {
        return new OrderStatus(order, avgPx, leavesQty, cumQty, OrdStatus.REJECTED);
    }

    /**
     * Gets a copy of this {@link OrderStatus} with {@link OrdStatus#CANCELED}.
     *
     * @return new {@link OrderStatus}.
     */
    public OrderStatus cancel() {
        return new OrderStatus(order, avgPx, leavesQty, cumQty, OrdStatus.CANCELED);
    }

    /**
     * Executes this <code>quantity</code> of this {@link Order} at this <code>price</code>.
     *
     * @param price    price to execute at.
     * @param quantity quantity to execute
     *
     * @return status of execution.
     */
    // TODO: check if the status is executable.
    public OrderStatus execute(final Double price, final Long quantity) {
        checkNotNull(price);
        checkArgument(Doubles.compare(price, Order.NO_PRICE) == 1);
        checkNotNull(quantity);
        checkArgument(Longs.compare(quantity, Order.NO_QTY) == 1);
        checkArgument(Longs.compare(quantity, this.leavesQty) <= 0);

        final Double newAvgPx = ((quantity * price) + (this.avgPx * cumQty)) / (quantity + this.cumQty);
        final Long newLeavesQty = this.leavesQty - quantity;
        final Long newCumQty = this.cumQty + quantity;
        final OrdStatus newOrdStatus = newLeavesQty.equals(Long.valueOf(0L)) ? FILLED : PARTIALLY_FILLED;
        return new OrderStatus(order, newAvgPx, newLeavesQty, newCumQty, newOrdStatus);
    }
}
