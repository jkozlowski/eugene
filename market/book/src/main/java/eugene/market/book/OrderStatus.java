package eugene.market.book;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.primitives.Doubles.compare;

/**
 * Status of execution of an {@link Order}.
 */
public class OrderStatus {

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
        this(order, Order.NO_PRICE, order.getOrderQty(), Order.NO_QTY);
    }

    /**
     * Default constructor.
     *
     * @param order     {@link Order} that this {@link OrderStatus} describes.
     * @param avgPx     average price of execution.
     * @param leavesQty outstanding quantity.
     * @param cumQty    executed quantity.
     */
    public OrderStatus(final Order order, final Double avgPx,
                       final Long leavesQty, final Long cumQty) {
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

        this.order = order;
        this.avgPx = avgPx;
        this.leavesQty = leavesQty;
        this.cumQty = cumQty;
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
     * Checks if this {@link Order} has been filled.
     *
     * @return <code>true</code> if this {@link Order} has been filled, <code>false</code> otherwise.
     */
    public boolean isFilled() {
        return 0 == leavesQty;
    }

    /**
     * Checks if this {@link Order} has not been executed at all.
     *
     * @return <code>true</code> if this {@link Order} has not been executed at all, <code>false</code> otherwise.
     */
    public boolean isEmpty() {
        return order.getOrderQty() == leavesQty;
    }

    /**
     * Executes this <code>quantity</code> of this {@link Order} at this <code>price</code>.
     *
     * @param price    price to execute at.
     * @param quantity quantity to execute
     *
     * @return status of execution.
     */
    public OrderStatus execute(final Double price, final Long quantity) {
        checkNotNull(price);
        checkArgument(compare(price, Order.NO_PRICE) == 1);
        checkNotNull(quantity);
        checkArgument(compare(quantity, Order.NO_QTY) == 1);
        checkArgument(compare(quantity, this.leavesQty) <= 0);

        final Double avgPx = ((quantity * price) + (this.avgPx * cumQty)) / (quantity + this.cumQty);
        return new OrderStatus(order, avgPx, this.leavesQty - quantity, this.cumQty + quantity);
    }
}
