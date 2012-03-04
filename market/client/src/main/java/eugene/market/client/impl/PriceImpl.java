/*
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */

package eugene.market.client.impl;

import eugene.market.client.TopOfBookApplication.Price;
import eugene.market.ontology.field.enums.Side;
import eugene.simulation.agent.Symbol;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default implementation of {@link Price}.
 */
public final class PriceImpl implements Price {

    private final BigDecimal price;

    private final Side side;

    private final Symbol symbol;

    /**
     * Default constructor.
     *
     * @param price  the price.
     * @param side   the side.
     * @param symbol the symbol.
     */
    public PriceImpl(final BigDecimal price, final Side side, final Symbol symbol) {
        checkNotNull(price);
        checkNotNull(side);
        checkNotNull(symbol);
        checkArgument(price.compareTo(BigDecimal.ZERO) > 0);
        this.price = price;
        this.side = side;
        this.symbol = symbol;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Side getSide() {
        return side;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal nextPrice(final BigDecimal priceMove) {
        checkArgument(priceMove.compareTo(BigDecimal.ZERO) > 0);
        return side.isBuy() ? add(priceMove) : subtract(priceMove);
    }

    @Override
    public BigDecimal prevPrice(BigDecimal priceMove) {
        checkArgument(priceMove.compareTo(BigDecimal.ZERO) > 0);
        return side.isBuy() ? subtract(priceMove) : add(priceMove);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Helper function to add {@code priceMove} to the current price.
     *
     * @param priceMove how much to move the price.
     *
     * @return {@code price + priceMove} rounded to a tick with {@link RoundingMode#HALF_UP}.
     */
    public BigDecimal add(final BigDecimal priceMove) {
        final BigDecimal tickSize = symbol.getTickSize();
        return price.add(priceMove).setScale(tickSize.scale(), RoundingMode.HALF_UP);
    }

    /**
     * Helper function to subtract {@code priceMove} from the current price.
     *
     * @param priceMove how much to move the price.
     *
     * @return {@code price - priceMove} rounded to a tick with {@link RoundingMode#HALF_UP}.
     */
    public BigDecimal subtract(final BigDecimal priceMove) {
        final BigDecimal tickSize = symbol.getTickSize();
        return price.subtract(priceMove).setScale(tickSize.scale(), RoundingMode.HALF_UP);
    }
}
