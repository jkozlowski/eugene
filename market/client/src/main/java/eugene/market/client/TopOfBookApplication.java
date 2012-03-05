/*
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */

package eugene.market.client;

import eugene.market.book.OrderBook;
import eugene.market.ontology.field.enums.Side;

import java.math.BigDecimal;

/**
 * Tracks the prices at the top of the book.
 *
 * @author Jakub D Kozlowski
 * @since 0.8
 */
public interface TopOfBookApplication extends Application {

    /**
     * Indicates that this {@link TopOfBookApplication} did not observe any prices yet.
     */
    public static final Price NO_PRICE = new Price() {
        @Override
        public Side getSide() {
            throw new UnsupportedOperationException();
        }

        @Override
        public BigDecimal nextPrice(long ticks) throws IllegalArgumentException {
            throw new UnsupportedOperationException();
        }

        @Override
        public BigDecimal prevPrice(long ticks) throws IllegalArgumentException {
            throw new UnsupportedOperationException();
        }

        @Override
        public BigDecimal getPrice() {
            throw new UnsupportedOperationException();
        }
    };

    /**
     * Represents the current price at a side of the {@link OrderBook}.
     */
    public interface Price {

        /**
         * Gets the side of this {@link Price}.
         *
         * @return the side.
         */
        Side getSide();

        /**
         * Gets the price that is <code>ticks</code> better then the {@link #getPrice()}. If this {@link Price}
         * represents an bid price, then this method returns <code>{@link #getPrice()} + ticks * tickSize</code>.
         * Similarly, if this {@link Price} represents an ask price, this method returns <code>{@link #getPrice()} -
         * ticks * tickSize</code>.
         *
         * @param ticks number of ticks.
         *
         * @return a price better than the best price.
         *
         * @throws IllegalArgumentException if <code>ticks</code> is less than 1.
         */
        BigDecimal nextPrice(final long ticks) throws IllegalArgumentException;

        /**
         * Gets the price that is <code>ticks</code> worse than the {@link #getPrice()}. If this {@link Price}
         * represents an bid price, then this method returns <code>{@link #getPrice()} - ticks * tickSize</code>.
         * Similarly, if this {@link Price} represents an ask price, this method returns <code>{@link #getPrice()} +
         * ticks * tickSize</code>.
         *
         * @param ticks number of ticks.
         *
         * @return a price worse than the best price.
         *
         * @throws IllegalArgumentException if <code>ticks</code> is less than 1.
         */
        BigDecimal prevPrice(final long ticks) throws IllegalArgumentException;

        /**
         * Gets the current price.
         *
         * @return the current price.
         */
        BigDecimal getPrice();
    }

    /**
     * Gets the price at the top of the {@link OrderBook} on this <code>side</code>. If there is no liquidity at this
     * <code>side</code>, the latest previous price seen is returned. If this {@link TopOfBookApplication} did not
     * observe any prices yet, this method will return {@link #NO_PRICE}.
     *
     * @param side side to inspect.
     *
     * @return price at the top of the {@link OrderBook}.
     *
     * @throws NullPointerException  if <code>side</code> is empty.
     */
    Price getPrice(final Side side) throws NullPointerException;
}
