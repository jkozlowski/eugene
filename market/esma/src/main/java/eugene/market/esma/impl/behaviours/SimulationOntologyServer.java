package eugene.market.esma.impl.behaviours;

import eugene.market.esma.MarketAgent;
import eugene.simulation.ontology.SimulationOntology;
import eugene.simulation.ontology.Start;
import eugene.simulation.ontology.Started;
import eugene.simulation.ontology.Stop;
import eugene.simulation.ontology.Stopped;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.lang.acl.ACLMessage;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Accepts messages from {@link SimulationOntology}.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public final class SimulationOntologyServer {

    private final MarketAgent agent;

    /**
     * Constructs an instance of {@link SimulationOntologyServer} for this <code>agent</code>.
     *
     * @param agent owner of this {@link SimulationOntologyServer}.
     */
    public SimulationOntologyServer(final MarketAgent agent) {
        checkNotNull(agent);
        this.agent = agent;
    }

    /**
     * Accepts {@link Start} message.
     *
     * @param start   message to accept.
     * @param request original message.
     */
    public void serveStartRequest(final Start start, final ACLMessage request) {

        try {
            final ACLMessage reply = request.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setLanguage(SimulationOntology.LANGUAGE);
            reply.setOntology(SimulationOntology.NAME);
            agent.getContentManager().fillContent(reply, new Action(request.getSender(), new Started()));
            agent.send(reply);
        }
        catch (CodecException e) {
            throw new RuntimeException();
        }
        catch (OntologyException e) {
            throw new RuntimeException();
        }

    }

    /**
     * Accepts {@link Stop} message.
     *
     * @param stop    message to accept.
     * @param request original message.
     */
    public void serveStopRequest(final Stop stop, final ACLMessage request) {

        try {
            final ACLMessage reply = request.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setLanguage(SimulationOntology.LANGUAGE);
            reply.setOntology(SimulationOntology.NAME);
            agent.getContentManager().fillContent(reply, new Action(request.getSender(), new Stopped()));
            agent.send(reply);
        }
        catch (CodecException e) {
            throw new RuntimeException();
        }
        catch (OntologyException e) {
            throw new RuntimeException();
        }
    }
}
