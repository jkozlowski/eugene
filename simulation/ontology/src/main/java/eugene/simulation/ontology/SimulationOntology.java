package eugene.simulation.ontology;

import jade.content.lang.Codec;
import jade.content.lang.leap.LEAPCodec;
import jade.content.onto.BeanOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.domain.FIPANames.ContentLanguage;

/**
 * Defines the Simulation Ontology used to send messages between Trader Agents, Market Agent and Simulation Agent.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public final class SimulationOntology extends BeanOntology {

    /**
     * Language for this {@link SimulationOntology}.
     */
    public static final String LANGUAGE = ContentLanguage.FIPA_SL;

    /**
     * Singleton instance of {@link SimulationOntology}.
     */
    private static final SimulationOntology INSTANCE = new SimulationOntology();

    /**
     * Name of this ontology.
     */
    public static final String NAME = "simulation-ontology";


    private SimulationOntology() {
        super(NAME);

        try {
            add(LogonComplete.class, false);
            add(Start.class, false);
            add(Started.class, false);
            add(Stop.class, false);
            add(Stopped.class, false);
        }
        catch (OntologyException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets an instance of a {@link Codec} that should be used with {@link SimulationOntology}.
     *
     * @return instance of {@link Codec}.
     */
    public static Codec getCodec() {
        return new LEAPCodec();
    }

    /**
     * Returns the singleton instance of this {@link SimulationOntology}.
     *
     * @return Singleton instance of this {@link SimulationOntology}.
     */
    public static Ontology getInstance() {
        return INSTANCE;
    }
}
