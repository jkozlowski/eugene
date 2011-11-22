package eugene.market.esma;

import javax.validation.constraints.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Interface used by {@link OrderBook} to match outstanding orders.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public interface MatchingEngine {


    /**
     * Indicates whether two {@link Order}s submitted to {@link MatchingEngine#match(OrderBook, Order, Order)} should
     * be executed and the execution price.
     */
    public final class MatchingResult {

        /**
         * Indicates that two {@link Order}s submitted to {@link MatchingEngine#match(OrderBook, Order,
         * Order)} should not
         * be executed.
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
         * Gets the price at which two {@link Order}s submitted to {@link MatchingEngine#match(OrderBook, Order,
         * Order)} should be executed.
         *
         * @return price of execution.
         *
         * @throws IllegalStateException if called on {@link MatchingResult} with {@link Match#NO}.
         */
        public Double getPrice() throws IllegalStateException {
            if (!match.isMatch()) {
                throw new IllegalStateException("This method should not be called if there was not match");
            }
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
     * Indicates whether two {@link Order}s submitted to {@link MatchingEngine#match(OrderBook, Order, Order)} match.
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

    /**
     * Matches <code>buyOrder</code> with <code>sellOrder</code> that belong to <code>orderBook</code>.
     *
     * @param orderBook {@link OrderBook} the {@link Order}s belong to.
     * @param buyOrder  buy {@link Order} to match.
     * @param sellOrder sell {@link Order} to match.
     *
     * @return {@link MatchingResult} with {@link Match#YES} and a price or {@link MatchingResult#NO_MATCH}.
     */
    @NotNull
    public MatchingResult match(@NotNull final OrderBook orderBook, @NotNull final Order buyOrder,
                                @NotNull final Order sellOrder);
}
