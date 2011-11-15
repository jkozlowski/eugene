package eugene.market.ontology.field;

import jade.content.onto.annotations.Element;

/**
 * Unique identifier for Order as assigned by the Market. Uniqueness must be guaranteed within a single trading day.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
@Element(name = OrderID.TAG)
public final class OrderID extends Field<String> {

    public static final String TAG = "37";

    public static final Integer TAGi = 37;

    public OrderID(String value) {
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
