package eugene.market.ontology.field;

import jade.content.onto.annotations.Element;

/**
 * Unique identifier for Order as assigned by the Agent originating the trade. Uniqueness must be guaranteed within a
 * single trading day.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
@Element(name = "11")
public final class ClOrdID extends Field<String> {

    public ClOrdID(String value) {
        super(value);
    }
}
