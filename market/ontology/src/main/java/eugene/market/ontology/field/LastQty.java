package eugene.market.ontology.field;

import eugene.market.ontology.Field;
import jade.content.onto.annotations.Element;
import jade.content.onto.annotations.SuppressSlot;

/**
 * Quantity executed.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
@Element(name = LastQty.TAG)
public final class LastQty extends Field<Long> {

    public static final String TAG = "32";

    public static final Integer TAGi = Integer.parseInt(TAG);

    /**
     * {@inheritDoc}
     */
    public LastQty() {
    }

    /**
     * {@inheritDoc}
     */
    public LastQty(Long value) {
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
