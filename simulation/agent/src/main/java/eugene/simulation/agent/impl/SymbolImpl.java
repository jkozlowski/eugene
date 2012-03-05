/*
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */

package eugene.simulation.agent.impl;

import eugene.simulation.agent.Symbol;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default implementation of {@link Symbol}.
 *
 * @author Jakub D Kozlowski
 * @since 0.8
 */
public final class SymbolImpl implements Symbol {

    private final String name;

    private final BigDecimal tickSize;

    private final BigDecimal defaultPrice;

    /**
     * Default constructor
     *
     * @param name         name of this stock.
     * @param tickSize     tick size for this {@link Symbol}.
     * @param defaultPrice starting price.
     *
     * @return default {@link Symbol} implementation.
     *
     * @throws NullPointerException     if any parameter is null.
     * @throws IllegalArgumentException if <code>name</code> is empty.
     * @throws IllegalArgumentException if <code>tickSize</code> or <code>defaultPrice</code> are zero.
     */
    public SymbolImpl(final String name, final BigDecimal tickSize, final BigDecimal defaultPrice) {
        checkNotNull(name);
        checkNotNull(tickSize);
        checkNotNull(defaultPrice);
        checkArgument(!name.isEmpty());
        checkArgument(tickSize.compareTo(BigDecimal.ZERO) > 0);
        checkArgument(defaultPrice.compareTo(BigDecimal.ZERO) > 0);
        this.name = name;
        this.tickSize = tickSize;
        this.defaultPrice = defaultPrice;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getTickSize() {
        return tickSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getDefaultPrice() {
        return defaultPrice;
    }
}
