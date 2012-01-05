package eugene.market.book;

import eugene.market.ontology.field.enums.Side;

/**
 * Maintains lists of outstanding buy and sell {@link Order}s.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public interface OrderBook {

    /**
     * Inserts this <code>order</code> into this {@link OrderBook}.
     *
     * @param order order to insert.
     *
     * @return status of this {@link Order}.
     */
    public OrderStatus insertOrder(final Order order);

    /**
     * Executes {@link Order}s at the top of the book at this <code>price</code>.
     *
     * @param price price of execution.
     *
     * @return report of the trade execution.
     */
    public TradeReport execute(final Double price);

    /**
     * Cancels this <code>order</code>.
     *
     * @param order {@link Order} to cancel.
     *
     * @return {@link OrderStatus} of cancelled {@link Order} or <code>null</code> if <code>order</code> does not
     *         exist.
     */
    public OrderStatus cancel(final Order order);

    /**
     * Gets the size of the book on this <code>side</code>.
     *
     * @param side the size of the book will be returned for this <code>side</code>.
     *
     * @return size of the book on this <code>side</code>.
     */
    public int size(final Side side);

    /**
     * Checks if the book is empty on this <code>side</code>.
     *
     * @param side the side of the book to check.
     *
     * @return <code>true</code> is this <code>side</code> of the book is empty, <code>false</code> otherwise.
     */
    public boolean isEmpty(final Side side);

    /**
     * Gets the first {@link Order} on this <code>side</code>.
     *
     * @param side side of {@link Order} to get.
     *
     * @return first {@link Order} on this <code>side</code>.
     */
    public Order peek(final Side side);

    /**
     * Gets the {@link OrderStatus} for this <code>order</code>.
     *
     * @param order {@link Order} to return the {@link OrderStatus} for.
     *
     * @return the {@link OrderStatus} for this <code>order</code> or null if this {@link Order} is not in
     *         this {@link OrderBook}.
     */
    public OrderStatus getExecutionReport(final Order order);

    /**
     * Gets a sorted array of {@link Side#BUY} orders.
     *
     * @return sorted array of {@link Side#BUY} orders.
     */
    public Order[] getBuyOrders();

    /**
     * Gets a sorted array of {@link Side#SELL} orders.
     *
     * @return sorted array of {@link Side#SELL} orders.
     */
    public Order[] getSellOrders();
}
