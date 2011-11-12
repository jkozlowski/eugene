package eugene.market.ontology.field;

import jade.content.onto.annotations.Element;
import jade.content.onto.annotations.Slot;

/**
 * Side of order.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
@Element(name = "54")
public final class Side extends Field<String> {

    public static final Side BUY = new Side("1");

    public static final Side SELL = new Side("1");

    public Side(String value) {
        super(value);
    }

    @Override
    @Slot(permittedValues = {BUY.getValue(), SELL.getValue()})
    public String getValue() {
        return super.getValue();
    }
}
