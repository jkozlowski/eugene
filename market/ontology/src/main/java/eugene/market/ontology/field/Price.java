package eugene.market.ontology.field;

import jade.content.onto.annotations.Element;

import java.math.BigDecimal;

/**
 * Price per unit of quantity.
 *
 * Following FIX specification, implemented using {@link BigDecimal}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
@Element(name = Price.TAG)
public final class Price extends Field<BigDecimal> {

    public static final String TAG = "44";

    public static final Integer TAGi = 44;

    public Price(BigDecimal value) {
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
