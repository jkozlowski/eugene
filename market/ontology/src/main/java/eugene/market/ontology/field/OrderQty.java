package eugene.market.ontology.field;

import java.math.BigDecimal;

/**
 * Quantity ordered.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public final class OrderQty extends Field<BigDecimal> {

    public OrderQty(BigDecimal value) {
        super(value);
    }
}
