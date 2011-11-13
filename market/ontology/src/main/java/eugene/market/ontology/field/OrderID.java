package eugene.market.ontology.field;

import eugene.market.ontology.Field;
import jade.content.onto.annotations.Element;
import jade.content.onto.annotations.SuppressSlot;

/**
 * Unique identifier for Order as assigned by the Market. Uniqueness must be guaranteed within a single trading day.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
@Element(name = OrderID.TAG)
public final class OrderID extends Field<String> {

    public static final String TAG = "37";

    public static final Integer TAGi = Integer.parseInt(TAG);

    /**
     * {@inheritDoc}
     */
    public OrderID() {
    }

    /**
     * {@inheritDoc}
     */
    public OrderID(String value) {
        super(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressSlot
    public Integer getTag() {
        return TAGi;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressSlot
    public Boolean isEnumField() {
        return Boolean.FALSE;
    }
}
