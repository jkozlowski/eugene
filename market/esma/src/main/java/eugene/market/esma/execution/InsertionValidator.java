package eugene.market.esma.execution;

import eugene.market.ontology.field.enums.OrdType;
import eugene.market.esma.execution.book.Order;
import eugene.market.esma.execution.book.OrderBook;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Validates whether an {@link Order} should be inserted into an {@link OrderBook}. Rejects {@link OrdType#MARKET}
 * {@link Order}s if there is no liquidity on the opposite side of the book.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public class InsertionValidator {

    private static final InsertionValidator INSTANCE = new InsertionValidator();

    private InsertionValidator() {
    }

    /**
     * Validates whether this <code>order</code> should be inserted into this <code>orderBook</code>.
     *
     * @param orderBook {@link OrderBook} to insert into.
     * @param order     {@link Order} to validate.
     *
     * @return <code>true</code> if validation was successful, <code>false</code> otherwise.
     */
    public boolean validate(final OrderBook orderBook, final Order order) {
        checkNotNull(orderBook);
        checkNotNull(order);

        if (order.getOrdType().isLimit()) {
            return Boolean.TRUE;
        }

        if (orderBook.isEmpty(order.getSide().getOpposite())) {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    /**
     * Gets the singleton instance of {@link InsertionValidator}.
     *
     * @return singleton instance of {@link InsertionValidator}.
     */
    public static InsertionValidator getInstance() {
        return INSTANCE;
    }
}
