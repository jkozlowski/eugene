/*
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */

package eugene.market.client;

import eugene.market.book.OrderBook;
import eugene.market.ontology.field.enums.Side;
import eugene.simulation.agent.Symbol;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
        public BigDecimal nextPrice(BigDecimal priceMove) {
            throw new UnsupportedOperationException();
        }

        @Override
        public BigDecimal prevPrice(BigDecimal priceMove) {
            throw new UnsupportedOperationException();
        }

        @Override
        public BigDecimal getPrice() {
            throw new UnsupportedOperationException();
        }
    };

    /**
     * Indicates whether {@link Symbol#getDefaultPrice()} should be returned if this {@link TopOfBookApplication} has
     * not observed any prices yet.
     */
    public enum ReturnDefaultPrice {
        YES, NO
    }

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
         * Gets the price that is {@code priceMove} better than this {@link Price}. If this {@link Price}
         * represents an bid price, then this method returns <code>{@link #getPrice()} + priceMove</code>.
         * Similarly, if this {@link Price} represents an ask price, this method returns <code>{@link #getPrice()} -
         * priceMove</code>. The resulting price (not the {@code priceMove}) is rounded to the next tick using {@link
         * RoundingMode#HALF_UP}.
         *
         * @param priceMove how much to move the price.
         *
         * @return a price better than this {@link Price}.
         */
        BigDecimal nextPrice(final BigDecimal priceMove);

        /**
         * Gets the price that is {@code priceMove} worse than this {@link Price}. If this {@link Price}
         * represents an bid price, then this method returns <code>{@link #getPrice()} - priceMove</code>.
         * Similarly, if this {@link Price} represents an ask price, this method returns <code>{@link #getPrice()} +
         * priceMove</code>. The resulting price (not the {@code priceMove}) is rounded to the next tick using {@link
         * RoundingMode#HALF_UP}.
         *
         * @param priceMove how much to move the price.
         *
         * @return a price worse than this {@link Price}.
         */
        BigDecimal prevPrice(final BigDecimal priceMove);

        /**
         * Gets the current price.
         *
         * @return the current price.
         */
        BigDecimal getPrice();
    }

    /**
     * Utility method for calling <code>getLastPrice(side, ReturnDefaultPrice.YES)</code>.
     *
     * @see #getLastPrice(Side, ReturnDefaultPrice)
     */
    Price getLastPrice(final Side side) throws NullPointerException;

    /**
     * Gets the price at the top of the {@link OrderBook} on this <code>side</code>. If there is no liquidity at this
     * <code>side</code>, the latest previous price seen is returned. If this {@link TopOfBookApplication} did not
     * observe any prices yet, this method will either return {@link #NO_PRICE} or {@link Symbol#getDefaultPrice()},
     * depending on <code>returnDefaultPrice</code>.
     *
     * @param side               side to inspect.
     * @param returnDefaultPrice whether to return the {@link Symbol#getDefaultPrice()} if no prices have
     *                           been observed yet.
     *
     * @return appropriate price.
     *
     * @throws NullPointerException if either parameter is null.
     */
    Price getLastPrice(final Side side, final ReturnDefaultPrice returnDefaultPrice) throws NullPointerException;

    /**
     * @see OrderBook#isEmpty(Side)
     */
    boolean isEmpty(final Side side);

    /**
     * Checks if the book is not empty on both sides.
     *
     * @return <code>true</code> is book has at least one order on both sides, <code>false</code> otherwise.
     */
    boolean hasBothSides();

    /**
     * Gets the difference between the current bid and ask prices. More specifically,
     * this method returns <code>getLastPrice(Side.SELL, ReturnDefaultPrice.NO) - getLastPrice(Side.BUY,
     * ReturnDefaultPrice.NO)</code>.
     *
     * @return the spread.
     *
     * @throws IllegalStateException if {@link #hasBothSides()} returns <code>false</code>.
     */
    BigDecimal getSpread();

    /**
     * Gets the {@link OrderBook} that this {@link TopOfBookApplication} updates.
     *
     * @return updated {@link OrderBook}
     */
    OrderBook getOrderBook();
}
