package eugene.market.esma.execution;

import eugene.market.esma.enums.OrdType;
import eugene.market.esma.execution.book.Order;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Matches {@link Order}s.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public class MatchingEngine {

    private static final MatchingEngine INSTANCE = new MatchingEngine();

    private MatchingEngine() {
    }

    /**
     * Matches <code>buyOrder</code> with <code>sellOrder</code>.
     *
     * The following order pairs constitute a match:
     * <ul>
     * <li>{@link OrdType#MARKET} and {@link OrdType#LIMIT}.</li>
     * <li>{@link OrdType#LIMIT} and {@link OrdType#LIMIT}, if their prices cross.</li>
     * </ul>
     *
     * @param buyOrder  buy {@link Order} to match.
     * @param sellOrder sell {@link Order} to match.
     *
     * @return {@link MatchingResult} with {@link Match#YES} and a price or {@link MatchingResult#NO_MATCH}.
     *
     * @throws IllegalStateException if both <code>buyOrder</code> and <code>sellOrder</code> are {@link
     *                               OrdType#MARKET}.
     */
    public MatchingResult match(final Order buyOrder, final Order sellOrder) throws IllegalStateException {

        checkNotNull(buyOrder);
        checkNotNull(sellOrder);

        // MARKET vs MARKET
        checkState(buyOrder.getOrdType().isLimit() || sellOrder.getOrdType().isLimit());

        if (buyOrder.getOrdType().isMarket() || sellOrder.getOrdType().isMarket()
                || (buyOrder.getPrice() >= sellOrder.getPrice())) {
            final Double price = sellOrder.getOrdType().isLimit() ? sellOrder.getPrice() : buyOrder.getPrice();
            return new MatchingResult(Match.YES, price);
        }

        return MatchingResult.NO_MATCH;
    }

    /**
     * Gets the singleton instance of {@link MatchingEngine}.
     *
     * @return singleton instance of {@link MatchingEngine}.
     */
    public static MatchingEngine getInstance() {
        return INSTANCE;
    }

    /**
     * Indicates whether two {@link Order}s submitted to {@link MatchingEngine#match(Order, Order)}
     * should be executed and the execution price.
     */
    public static final class MatchingResult {

        /**
         * Indicates that two {@link Order}s submitted to {@link MatchingEngine#match(Order, Order)}
         * should not be executed.
         */
        public static final MatchingResult NO_MATCH = new MatchingResult(Match.NO, Order.NO_PRICE);

        private final Match match;

        private final Double price;

        public MatchingResult(final Match match, final Double price) {
            checkNotNull(match);
            checkNotNull(price);
            this.price = price;
            this.match = match;
        }

        /**
         * Indicates whether this {@link MatchingResult} is {@link Match#YES}.
         *
         * @return <code>true</code>
         */
        public boolean isMatch() {
            return match.isMatch();
        }

        /**
         * Gets the price at which two {@link Order}s submitted to {@link MatchingEngine#match(Order,
         * Order)} should be executed.
         *
         * @return price of execution.
         *
         * @throws IllegalStateException if called on {@link MatchingResult} with {@link Match#NO}.
         */
        public Double getPrice() throws IllegalStateException {
            checkState(match.isMatch());
            return price;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final MatchingResult that = (MatchingResult) o;

            if (match != that.match) {
                return false;
            }
            if (price != null ? !price.equals(that.price) : that.price != null) {
                return false;
            }

            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            int result = match != null ? match.hashCode() : 0;
            result = 31 * result + (price != null ? price.hashCode() : 0);
            return result;
        }
    }

    /**
     * Indicates whether two {@link Order}s submitted to {@link MatchingEngine#match(Order, Order)}
     * match.
     */
    public enum Match {
        YES, NO;

        /**
         * Indicates whether this {@link Match} is {@link Match#YES}.
         *
         * @return <code>true</code> this {@link Match} is {@link Match#YES}, <code>false</code> otherwise.
         */
        public boolean isMatch() {
            return YES == this;
        }
    }
}
