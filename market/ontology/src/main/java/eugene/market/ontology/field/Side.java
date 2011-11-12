package eugene.market.ontology.field;

import jade.content.onto.annotations.Element;

/**
 * Side of order.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
@Element(name = "54")
public final class Side extends Field<Character> {

    public static final Side BUY = new Side('1');

    public static final Side SELL = new Side('2');

    public Side(char c) {
        super(c);
    }
}
