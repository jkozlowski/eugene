package eugene.market.ontology.field;

import eugene.market.ontology.Field;
import jade.content.onto.annotations.Element;
import jade.content.onto.annotations.SuppressSlot;

/**
 * Total quantity (e.g. number of shares) filled.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
@Element(name = CumQty.TAG)
public final class CumQty extends Field<Long> {

    public static final String TAG = "14";

    public static final Integer TAGi = Integer.parseInt(TAG);

    /**
     * {@inheritDoc}
     */
    public CumQty() {
    }

    /**
     * {@inheritDoc}
     */
    public CumQty(Long value) {
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
