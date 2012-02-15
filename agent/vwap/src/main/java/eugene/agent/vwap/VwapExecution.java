package eugene.agent.vwap;

import eugene.market.ontology.field.enums.Side;

import java.math.BigDecimal;
import java.util.List;

/**
 * Defines a single VWAP execution.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public interface VwapExecution {

    /**
     * Gets the quantity to traded.
     *
     * @return the quantity to trade.
     */
    Long getQuantity();

    /**
     * Gets the side to trade.
     *
     * @return side to trade.
     */
    Side getSide();

    /**
     * Gets a list of volume targets (as a ratio, e.g. 0.5), that define how much volume should be traded in each
     * period. The list should add up to 1.
     *
     * @return list of volume targets.
     */
    List<BigDecimal> getTargets();
}
