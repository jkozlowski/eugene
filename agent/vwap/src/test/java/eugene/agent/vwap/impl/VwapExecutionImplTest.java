package eugene.agent.vwap.impl;

import eugene.agent.vwap.VwapExecution;
import eugene.market.ontology.field.enums.Side;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static eugene.market.ontology.Defaults.defaultOrdQty;
import static java.math.BigDecimal.valueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

/**
 * Tests {@link VwapExecutionImpl}.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public class VwapExecutionImplTest {

    private static final BigDecimal[] defaultTargets = new BigDecimal[]{
            valueOf(0.1D), valueOf(0.2D), valueOf(0.7D)
    };

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullQuantity() {
        new VwapExecutionImpl(null, Side.BUY, defaultTargets);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorEmptyQuantity() {
        new VwapExecutionImpl(0L, Side.BUY, defaultTargets);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullSide() {
        new VwapExecutionImpl(defaultOrdQty, null, defaultTargets);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullTargets() {
        new VwapExecutionImpl(defaultOrdQty, Side.BUY, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorEmptyTargets() {
        new VwapExecutionImpl(defaultOrdQty, Side.BUY, new BigDecimal[]{});
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorTargetsDoNotSumUpToOne() {
        new VwapExecutionImpl(defaultOrdQty, Side.BUY, valueOf(0.1D), valueOf(0.2D), valueOf(0.8D));
    }

    @Test
    public void testConstructor() {
        final VwapExecution execution = new VwapExecutionImpl(defaultOrdQty, Side.BUY, defaultTargets);
        assertThat(execution.getQuantity(), is(defaultOrdQty));
        assertThat(execution.getSide(), is(Side.BUY));
        assertThat(execution.getTargets().size(), is(defaultTargets.length));
        assertThat(execution.getTargets(), hasItems(defaultTargets));
    }
}
