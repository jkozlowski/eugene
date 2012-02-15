package eugene.agent.vwap;

import eugene.agent.vwap.impl.VwapExecutionImpl;
import eugene.market.ontology.field.enums.Side;

import java.math.BigDecimal;

/**
 * Factory for creating {@link VwapExecution}s.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public final class VwapExecutions {

    private static final String ERROR_MSG = "This class should not be instantiated";

    /**
     * This class should not be instantiated.
     *
     * @throws UnsupportedOperationException this class should not be instantiated.
     */
    public VwapExecutions() {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    /**
     * Gets a new {@link VwapExecution}.
     *
     * @param quantity quantity to trade.
     * @param side     side to trade.
     * @param targets  volume targets.
     *
     * @return instance of {@link VwapExecution}.
     */
    public static VwapExecution newVwapExecution(final Long quantity, final Side side, final BigDecimal... targets) {
        return new VwapExecutionImpl(quantity, side, targets);
    }
}
