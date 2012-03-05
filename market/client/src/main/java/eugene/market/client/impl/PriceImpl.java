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
    public BigDecimal nextPrice(final long ticks) {
        checkArgument(ticks > 0);
        return side.isBuy() ? add(ticks) : subtract(ticks);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal prevPrice(final long ticks) {
        checkArgument(ticks > 0);
        return side.isSell() ? add(ticks) : subtract(ticks);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Helper function to add <code>ticks</code> to current price.
     *
     * @param ticks number of ticks to add.
     *
     * @return <code>price + ticks * tickSize</code>.
     */
    private BigDecimal add(final long ticks) {
        final BigDecimal tickSize = symbol.getTickSize();
        return price.add(tickSize.multiply(BigDecimal.valueOf(ticks)))
                .setScale(tickSize.scale(), RoundingMode.HALF_UP);
    }

    /**
     * Helper function to subtract <code>ticks</code> from current price.
     *
     * @param ticks number of ticks to subtract.
     *
     * @return <code>price - ticks * tickSize</code>.
     */
    private BigDecimal subtract(final long ticks) {
        final BigDecimal tickSize = symbol.getTickSize();
        return price.subtract(tickSize.multiply(BigDecimal.valueOf(ticks)))
                .setScale(tickSize.scale(), RoundingMode.HALF_UP);
    }
}
