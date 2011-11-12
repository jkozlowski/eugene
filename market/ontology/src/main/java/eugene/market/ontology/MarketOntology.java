package eugene.market.ontology;

import eugene.market.ontology.field.Side;
import jade.content.onto.BeanOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;

/**
 * Defines the Market Ontology used to send messages between Agents and the Market.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public final class MarketOntology extends BeanOntology {

    /**
     * Singleton instance of {@link MarketOntology}.
     */
    private static final MarketOntology INSTANCE = new MarketOntology();

    /**
     * Name of this ontology.
     */
    public static final String NAME = "market-ontology";

    /**
     * Private constructor.
     *
     */
    private MarketOntology() {
        super(NAME);

        try {
            add(Side.class, false);
        }
        catch (OntologyException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the singleton instance of this {@link MarketOntology}.
     *
     * @return Singleton instance of this {@link MarketOntology}.
     */
    public static Ontology getInstance() {
        return INSTANCE;
    }
}
