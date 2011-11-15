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

    public static final String BUY = "1";

    public static final String SELL = "1";

    public Side(String value) {
        super(value);
    }

    @Override
    @Slot(permittedValues = { BUY, SELL })
    public String getValue() {
        return super.getValue();
    }
}
