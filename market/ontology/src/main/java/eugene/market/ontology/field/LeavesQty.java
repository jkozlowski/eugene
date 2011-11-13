package eugene.market.ontology.field;

import eugene.market.ontology.Field;
import jade.content.onto.annotations.Element;
import jade.content.onto.annotations.SuppressSlot;

/**
 * Quantity open for further execution. If the {@link OrdStatus} is {@link OrdStatus#CANCELLED} or {@link
 * OrdStatus#REJECTED} (in which case the order is no longer active) then {@link LeavesQty} could be 0, otherwise
 * {@link LeavesQty} = {@link OrderQty} - {@link CumQty}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
@Element(name = LeavesQty.TAG)
public final class LeavesQty extends Field<Long> {

    public static final String TAG = "151";

    public static final Integer TAGi = Integer.parseInt(TAG);

    /**
     * {@inheritDoc}
     */
    public LeavesQty() {
    }

    /**
     * {@inheritDoc}
     */
    public LeavesQty(Long value) {
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
