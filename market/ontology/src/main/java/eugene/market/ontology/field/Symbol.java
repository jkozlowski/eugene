package eugene.market.ontology.field;

import eugene.market.ontology.Field;
import jade.content.onto.annotations.Element;
import jade.content.onto.annotations.SuppressSlot;

/**
 * Ticker symbol. Common, "human understood" representation of the security.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
@Element(name = Symbol.TAG)
public final class Symbol extends Field<String> {

    public static final String TAG = "55";

    public static final Integer TAGi = Integer.parseInt(TAG);

    /**
     * {@inheritDoc}
     */
    public Symbol() {
    }

    /**
     * {@inheritDoc}
     */
    public Symbol(String value) {
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
