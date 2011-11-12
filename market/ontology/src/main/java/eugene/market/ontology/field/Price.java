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
@Element(name = "44")
public final class Price extends Field<BigDecimal> {

    public Price(BigDecimal value) {
        super(value);
    }
}
