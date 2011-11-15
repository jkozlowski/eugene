package eugene.market.ontology.field;

import eugene.market.ontology.Field;
import jade.content.onto.annotations.Element;

/**
 * Unique identifier for Order as assigned by the Agent originating the trade. Uniqueness must be guaranteed within a
 * single trading day.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
@Element(name = ClOrdID.TAG)
public final class ClOrdID extends Field<String> {

    public static final String TAG = "11";

    public static final Integer TAGi = 11;

    public ClOrdID(String value) {
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
