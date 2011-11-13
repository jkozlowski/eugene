package eugene.market.ontology.field;

import eugene.market.ontology.Field;
import jade.content.onto.annotations.Element;
import jade.content.onto.annotations.Slot;
import jade.content.onto.annotations.SuppressSlot;

/**
 * Side of order.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
@Element(name = Side.TAG)
public final class Side extends Field<String> {

    public static final String TAG = "54";

    public static final Integer TAGi = Integer.parseInt(TAG);

    public static final String BUY = "1";

    public static final String SELL = "2";

    /**
     * {@inheritDoc}
     */
    public Side() {
    }

    /**
     * {@inheritDoc}
     */
    public Side(String value) {
        super(value);
    }

    @Override
    @Slot(permittedValues = {BUY, SELL})
    public String getValue() {
        return super.getValue();
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
        return Boolean.TRUE;
    }
}
