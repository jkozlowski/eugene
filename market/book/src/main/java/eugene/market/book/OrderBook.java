package eugene.market.book;

import eugene.market.ontology.field.enums.Side;

import java.util.SortedSet;

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
     * Executes the <code>orderQty</code> of the {@link Order} at the top of the book on this <code>side</code> at
     * this <code>price</code>.
     *
     * @param side     {@link Side} of the {@link Order} to execute.
     * @param orderQty quantity to execute.
     * @param price    price of execution.
     *
     * @return {@link OrderStatus} after the execution.
     *
     * @throws NullPointerException     if <code>side</code>, <code>orderQty</code> or <code>price</code> are
     *                                  <code>null</code>.
     * @throws IllegalArgumentException if <code>price <= {@link Order#NO_PRICE}</code>,
     *                                  or <code>orderQty <= {@link Order#NO_QTY}</code>,
     *                                  or <code>orderQty > {@link OrderStatus#getLeavesQty()}</code>
     *                                  or the {@link OrderBook} is empty on this <code>side</code>.
     */
    public OrderStatus execute(final Side side, final Long orderQty, final Double price) throws NullPointerException,
                                                                                                IllegalArgumentException;

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
    public OrderStatus getOrderStatus(final Order order);

    /**
     * Gets a {@link SortedSet} of {@link Side#BUY} orders.
     *
     * @return {@link SortedSet} of {@link Side#BUY} orders.
     */
    public SortedSet<Order> getBuyOrders();

    /**
     * Gets a {@link SortedSet} of {@link Side#SELL} orders.
     *
     * @return {@link SortedSet} of {@link Side#SELL} orders.
     */
    public SortedSet<Order> getSellOrders();
}
