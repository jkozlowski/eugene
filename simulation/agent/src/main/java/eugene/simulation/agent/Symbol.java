/*
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */

package eugene.simulation.agent;

import eugene.utils.annotation.Immutable;

import java.math.BigDecimal;

/**
 * Stores static information about a stock symbol.
 *
 * @author Jakub D Kozlowski
 * @since 0.8
 */
@Immutable
public interface Symbol {

    /**
     * Name of the stock.
     *
     * @return name of the stock.
     */
    String getName();

    /**
     * Gets the tick size for this {@link Symbol}.
     * 
     * @return the tick size.
     */
    BigDecimal getTickSize();

    /**
     * Gets the default price for this {@link Symbol}.
     *
     * @return the default price.
     */
    BigDecimal getDefaultPrice();
}
