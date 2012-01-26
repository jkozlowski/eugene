package eugene.market.esma.impl.execution;

import com.google.common.primitives.Longs;
import eugene.market.book.Order;
import eugene.market.book.OrderStatus;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.primitives.Doubles.compare;

/**
 * Summary of execution of two {@link Order}s.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public class Execution {

    private final OrderStatus newOrderStatus;

    private final OrderStatus limitOrderStatus;

    private final Double price;

    private final Long quantity;

    private final Long execID;

    /**
     * Creates a {@link Execution} with this <code>execID</code>, <code>newOrderStatus</code>,
     * <code>limitOrderStatus</code>, <code>price</code> and <code>quantity</code>.
     *
     * @param execID           unique identifier of this {@link Execution}.
     * @param newOrderStatus   status of execution of the new order.
     * @param limitOrderStatus status of execution of the limit order.
     * @param price            price of execution.
     * @param quantity         quantity of execution.
     */
    public Execution(final Long execID,
                     final OrderStatus newOrderStatus,
                     final OrderStatus limitOrderStatus,
                     final Double price,
                     final Long quantity) {

        checkNotNull(execID);
        checkNotNull(newOrderStatus);
        checkNotNull(limitOrderStatus);
        checkArgument(limitOrderStatus.getOrder().getOrdType().isLimit());
        checkArgument(newOrderStatus.getOrder().getSide().getOpposite().equals(limitOrderStatus.getOrder().getSide()));
        checkNotNull(price);
        checkArgument(compare(price, Order.NO_PRICE) > 0);
        checkNotNull(quantity);
        checkArgument(Longs.compare(quantity, Order.NO_QTY) > 0);

        this.execID = execID;
        this.newOrderStatus = newOrderStatus;
        this.limitOrderStatus = limitOrderStatus;
        this.price = price;
        this.quantity = quantity;
    }

    /**
     * Gets the execID.
     *
     * @return the execID.
     */
    public Long getExecID() {
        return execID;
    }

    /**
     * Gets the status of execution of the buy order.
     *
     * @return status of execution of the buy order.
     */
    public OrderStatus getNewOrderStatus() {
        return newOrderStatus;
    }

    /**
     * Gets the status of execution of the sell order.
     *
     * @return status of execution of the sell order.
     */
    public OrderStatus getLimitOrderStatus() {
        return limitOrderStatus;
    }

    /**
     * Gets the price.
     *
     * @return the price.
     */
    public Double getPrice() {
        return price;
    }

    /**
     * Gets the quantity.
     *
     * @return the quantity.
     */
    public Long getQuantity() {
        return quantity;
    }
}
