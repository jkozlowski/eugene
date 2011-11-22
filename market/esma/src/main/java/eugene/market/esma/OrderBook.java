package eugene.market.esma;

import com.google.common.annotations.VisibleForTesting;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;

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
     * @return <code>true</code> if the insertion was successful, <code>false</code> otherwise.
     */
    public boolean insertOrder(final @NotNull Order order);

    /**
     * Gets the unmodifiable sorted collection of buy {@link Order}s.
     *
     * @return unmodifiable collection of buy {@link Order}s.
     *
     * @see Collections#unmodifiableCollection(Collection)
     */
    @VisibleForTesting
    public Collection<Order> getBuyOrders();

    /**
     * Gets the unmodifiable sorted collection of sell {@link Order}s.
     *
     * @return unmodifiable collection of sell {@link Order}s.
     *
     * @see Collections#unmodifiableCollection(Collection)
     */
    @VisibleForTesting
    public Collection<Order> getSellOrders();

    /**
     * Gets the last executed price.
     *
     * @return the lastMarketPrice.
     */
    public Double getLastMarketPrice();

    /**
     * Sets the last executed price.
     *
     * @param lastMarketPrice new last executed price.
     */
    public void setLastMarketPrice(final Double lastMarketPrice);
}
