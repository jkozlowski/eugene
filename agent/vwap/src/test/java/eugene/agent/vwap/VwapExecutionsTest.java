/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.agent.vwap;

import eugene.agent.vwap.impl.VwapExecutionImpl;
import eugene.market.ontology.field.enums.Side;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static eugene.agent.vwap.VwapExecutions.newVwapExecution;
import static eugene.market.ontology.Defaults.defaultOrdQty;
import static java.math.BigDecimal.valueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests {@link VwapExecutions}.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public class VwapExecutionsTest {
    
    private static final BigDecimal[] defaultTargets = new BigDecimal[] {
            valueOf(0.1D), valueOf(0.2D), valueOf(0.7D)
    };
    
    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testConstructor() {
        new VwapExecutions();
    }
    
    @Test
    public void testNewVwapExecution() {
        assertThat(newVwapExecution(defaultOrdQty, Side.BUY, defaultTargets), is(VwapExecutionImpl.class));
    }
}
