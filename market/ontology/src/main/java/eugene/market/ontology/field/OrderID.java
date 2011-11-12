package eugene.market.ontology.field;

import jade.content.onto.annotations.Element;

/**
 * Unique identifier for Order as assigned by the Market. Uniqueness must be guaranteed within a single trading day.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
@Element(name = "37")
public final class OrderID extends Field<String> {

    public OrderID(String value) {
        super(value);
    }
}
