/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.agent.impl.execution;

import eugene.market.book.Order;
import eugene.market.ontology.field.enums.OrdType;
import eugene.market.ontology.field.enums.Side;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;
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
     * Matches <code>newOrder</code> with <code>limitOrder</code>.
     *
     * The following order pairs constitute a match:
     * <ul>
     * <li>if <code>newOrder</code> is {@link OrdType#MARKET}, the match price will be that of the
     * <code>limitOrder</code>.</li>
     * <li>if <code>newOrder</code> is {@link OrdType#LIMIT} it's price crosses with <code>limitOrder</code>,
     * the match price will be that of the <code>limitOrder</code>.</li>
     * </ul>
     *
     * @param newOrder   new {@link Order} to match.
     * @param limitOrder limit {@link Order} to match.
     *
     * @return {@link MatchingResult} with {@link Match#YES} and a price or {@link MatchingResult#NO_MATCH}.
     *
     * @throws IllegalArgumentException if <code>newOrder</code> and <code>limitOrder</code> have the same {@link
     *                                  Side} or <code>limitOrder</code> is not {@link OrdType#LIMIT}.
     */
    public MatchingResult match(final Order newOrder, final Order limitOrder) throws IllegalStateException {

        checkNotNull(newOrder);
        checkNotNull(limitOrder);
        checkArgument(newOrder.getSide().getOpposite().equals(limitOrder.getSide()));
        checkArgument(limitOrder.getOrdType().isLimit());

        if (newOrder.getOrdType().isMarket()) {
            return new MatchingResult(Match.YES, limitOrder.getPrice());
        }

        if (newOrder.getSide().isBuy() && newOrder.getPrice().compareTo(limitOrder.getPrice()) >= 0) {
            return new MatchingResult(Match.YES, limitOrder.getPrice());
        }

        if (newOrder.getSide().isSell() && newOrder.getPrice().compareTo(limitOrder.getPrice()) <= 0) {
            return new MatchingResult(Match.YES, limitOrder.getPrice());
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

        private final BigDecimal price;

        public MatchingResult(final Match match, final BigDecimal price) {
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
        public BigDecimal getPrice() throws IllegalStateException {
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
            if (price != null ? price.compareTo(that.price) != 0 : that.price != null) {
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
