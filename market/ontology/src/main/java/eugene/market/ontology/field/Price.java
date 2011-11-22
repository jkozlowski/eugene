package eugene.market.ontology.field;

import eugene.market.ontology.Field;
import jade.content.onto.annotations.Element;
import jade.content.onto.annotations.SuppressSlot;

import java.math.BigDecimal;

/**
 * Price per unit of quantity.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
@Element(name = Price.TAG)
public final class Price extends Field<Double> {

    public static final String TAG = "44";

    public static final Integer TAGi = Integer.parseInt(TAG);

    /**
     * {@inheritDoc}
     */
    public Price() {
    }

    /**
     * {@inheritDoc}
     */
    public Price(Double value) {
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
