package eugene.market.ontology.field;

import eugene.market.ontology.Field;
import jade.content.onto.annotations.Element;
import jade.content.onto.annotations.SuppressSlot;

/**
 * The unique ID assigned to the execution by the Market Agent.
 *
 * @author Jakub D Kozlowski
 * @since 0.5
 */
@Element(name = TradeID.TAG)
public final class TradeID extends Field<String> {

    public static final String TAG = "17";

    public static final Integer TAGi = Integer.parseInt(TAG);

    /**
     * {@inheritDoc}
     */
    public TradeID() {
    }

    /**
     * {@inheritDoc}
     */
    public TradeID(String value) {
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
