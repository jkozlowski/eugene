/*
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */

package eugene.simulation.agent;

import eugene.simulation.agent.impl.SymbolImpl;

import java.math.BigDecimal;

/**
 * Factory for {@link Symbol}s.
 *
 * @author Jakub D Kozlowski
 * @since 0.8
 */
public final class Symbols {

    private static final String ERROR_MSG = "This class should not be instantiated";

    /**
     * This class should not be instantiated.
     *
     * @throws UnsupportedOperationException this class should not be instantiated.
     */
    public Symbols() {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    /**
     * Gets a default {@link Symbol} implementation.
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
    public static Symbol getSymbol(final String name, final BigDecimal tickSize, final BigDecimal defaultPrice)
            throws NullPointerException, IllegalArgumentException {
        return new SymbolImpl(name, tickSize, defaultPrice);
    }
}
