package eugene.market.ontology.field;

import jade.content.onto.annotations.Element;
import jade.content.onto.annotations.Slot;

/**
 * Order type.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
@Element(name = "40")
public final class OrdType extends Field<String> {

    public static final OrdType MARKET = new OrdType("1");

    public static final OrdType LIMIT = new OrdType("2");

    public OrdType(String value) {
        super(value);
    }

    @Override
    @Slot(mandatory = true, permittedValues = {LIMIT.getValue(), MARKET.getValue()})
    public String getValue() {
        return super.getValue();
    }
}
