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

    private final OrderStatus buyOrderStatus;

    private final OrderStatus sellOrderStatus;

    private final Double price;

    private final Long quantity;

    private final Long execID;

    /**
     * Creates a {@link Execution} with this <code>execID</code>, <code>buyOrderStatus</code>,
     * <code>sellOrderStatus</code>, <code>price</code> and <code>quantity</code>.
     *
     * @param execID          unique identifier of this {@link Execution}.
     * @param buyOrderStatus  status of execution of the buy order.
     * @param sellOrderStatus status of execution of the sell order.
     * @param price           price of execution.
     * @param quantity        quantity of execution.
     */
    public Execution(final Long execID,
                     final OrderStatus buyOrderStatus,
                     final OrderStatus sellOrderStatus,
                     final Double price,
                     final Long quantity) {

        checkNotNull(execID);
        checkNotNull(buyOrderStatus);
        checkArgument(buyOrderStatus.getOrder().getSide().isBuy());
        checkNotNull(sellOrderStatus);
        checkArgument(sellOrderStatus.getOrder().getSide().isSell());
        checkNotNull(price);
        checkArgument(compare(price, Order.NO_PRICE) == 1);
        checkNotNull(quantity);
        checkArgument(Longs.compare(quantity, Order.NO_QTY) == 1);

        this.execID = execID;
        this.buyOrderStatus = buyOrderStatus;
        this.sellOrderStatus = sellOrderStatus;
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
    public OrderStatus getBuyOrderStatus() {
        return buyOrderStatus;
    }

    /**
     * Gets the status of execution of the sell order.
     *
     * @return status of execution of the sell order.
     */
    public OrderStatus getSellOrderStatus() {
        return sellOrderStatus;
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
