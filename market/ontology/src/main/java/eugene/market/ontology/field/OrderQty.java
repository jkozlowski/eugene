package eugene.market.ontology.field;

import eugene.market.ontology.Field;
import jade.content.onto.annotations.Element;

import java.math.BigDecimal;

/**
 * Quantity ordered.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
@Element(name = OrderQty.TAG)
public final class OrderQty extends Field<BigDecimal> {

    public static final String TAG = "38";

    public static final Integer TAGi = 38;

    public OrderQty(BigDecimal value) {
        super(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getTag() {
        return TAGi;
    }
}
