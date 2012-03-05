/*
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */

package eugene.simulation.agent.impl;

import eugene.simulation.agent.Symbol;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static eugene.market.ontology.Defaults.defaultPrice;
import static eugene.market.ontology.Defaults.defaultSymbol;
import static eugene.market.ontology.Defaults.defaultTickSize;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests {@link SymbolImpl}.
 *
 * @author Jakub D Kozlowski
 * @since 0.8
 */
public class SymbolImplTest {
    
    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullName() {
        new SymbolImpl(null, defaultTickSize, defaultPrice);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorEmptyName() {
        new SymbolImpl("", defaultTickSize, defaultPrice);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullTickSize() {
        new SymbolImpl(defaultSymbol, null, defaultPrice);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorZeroTickSize() {
        new SymbolImpl(defaultSymbol, BigDecimal.ZERO, defaultPrice);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullPrice() {
        new SymbolImpl(defaultSymbol, defaultTickSize, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorZeroDefaultPrice() {
        new SymbolImpl(defaultSymbol, defaultTickSize, BigDecimal.ZERO);
    }
    
    @Test
    public void testConstructor() {
        final Symbol symbol = new SymbolImpl(defaultSymbol, defaultTickSize, defaultPrice);
        assertThat(symbol.getDefaultPrice(), is(defaultPrice));
        assertThat(symbol.getName(), is(defaultSymbol));
        assertThat(symbol.getTickSize(), is(defaultTickSize));
    }
}
