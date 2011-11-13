package eugene.market.ontology.field;

import eugene.market.ontology.Field;
import jade.content.onto.annotations.Element;
import jade.content.onto.annotations.SuppressSlot;

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

    public static final Integer TAGi = Integer.parseInt(TAG);

    /**
     * {@inheritDoc}
     */
    public ClOrdID() {
    }

    /**
     * {@inheritDoc}
     */
    public ClOrdID(String value) {
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
