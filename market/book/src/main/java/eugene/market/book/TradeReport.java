package eugene.market.book;

import com.google.common.primitives.Longs;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.primitives.Doubles.compare;

/**
 * Summary of execution of two {@link Order}s.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
// TODO: Check if quantity is not bigger than any of the quantities.
public class TradeReport {

    private final OrderStatus buyOrderStatus;

    private final OrderStatus sellOrderStatus;

    private final Double price;

    private final Long quantity;

    /**
     * Creates a {@link TradeReport} with this <code>buyOrderStatus</code>,
     * <code>sellOrderStatus</code>, <code>price</code> and <code>quantity</code>.
     *
     * @param buyOrderStatus  status of execution of the buy order.
     * @param sellOrderStatus status of execution of the sell order.
     * @param price           price of execution.
     * @param quantity        quantity of execution.
     */
    public TradeReport(final OrderStatus buyOrderStatus,
                       final OrderStatus sellOrderStatus,
                       final Double price,
                       final Long quantity) {
        checkNotNull(buyOrderStatus);
        checkArgument(buyOrderStatus.getOrder().getSide().isBuy());
        checkNotNull(sellOrderStatus);
        checkArgument(sellOrderStatus.getOrder().getSide().isSell());
        checkNotNull(price);
        checkArgument(compare(price, Order.NO_PRICE) == 1);
        checkNotNull(quantity);
        checkArgument(Longs.compare(quantity, Order.NO_QTY) == 1);

        this.buyOrderStatus = buyOrderStatus;
        this.sellOrderStatus = sellOrderStatus;
        this.price = price;
        this.quantity = quantity;
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
