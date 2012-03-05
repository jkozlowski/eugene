/*
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */

package eugene.simulation.agent;

import eugene.simulation.agent.impl.SymbolImpl;
import org.testng.annotations.Test;

import static eugene.market.ontology.Defaults.defaultPrice;
import static eugene.market.ontology.Defaults.defaultSymbol;
import static eugene.market.ontology.Defaults.defaultTickSize;
import static eugene.simulation.agent.Symbols.getSymbol;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests {@link Symbols}.
 *
 * @author Jakub D Kozlowski
 * @since 0.8
 */
public class SymbolsTest {
    
    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testConstructor() {
        new Symbols();
    }
    
    @Test
    public void testGetSymbol() {
        assertThat(getSymbol(defaultSymbol, defaultTickSize, defaultPrice), is(SymbolImpl.class));
    }
}
