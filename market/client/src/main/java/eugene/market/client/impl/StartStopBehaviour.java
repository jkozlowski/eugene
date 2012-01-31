package eugene.market.client.impl;

import com.google.common.annotations.VisibleForTesting;
import eugene.market.client.Session;
import eugene.simulation.ontology.SimulationOntology;
import eugene.simulation.ontology.Start;
import eugene.simulation.ontology.Started;
import eugene.simulation.ontology.Stop;
import eugene.simulation.ontology.Stopped;
import jade.content.AgentAction;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import static com.google.common.base.Preconditions.checkNotNull;
import static jade.lang.acl.MessageTemplate.MatchLanguage;
import static jade.lang.acl.MessageTemplate.MatchOntology;
import static jade.lang.acl.MessageTemplate.MatchSender;
import static jade.lang.acl.MessageTemplate.and;

/**
 * Starts and stops the {@link Session}.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class StartStopBehaviour extends CyclicBehaviour {

    private final MessageTemplate template;

    private final Session session;

    /**
     * Creates a {@link StartStopBehaviour} that will wait for {@link Start} and {@link Stop} messages.
     *
     * @param session session owner of this {@link StartStopBehaviour}.
     */
    public StartStopBehaviour(final Session session) {
        checkNotNull(session);
        this.session = session;
        this.template = and(MatchLanguage(SimulationOntology.LANGUAGE),
                            and(MatchOntology(SimulationOntology.getInstance().getName()),
                                MatchSender(session.getSimulation().getSimulationAgent())));
    }

    @Override
    public void action() {

        final ACLMessage aclMessage = myAgent.receive(template);

        if (null == aclMessage) {
            return;
        }

        final AgentAction message = session.extractMessage(aclMessage, AgentAction.class);
        if (null == message) {
            return;
        }

        if (message instanceof Start) {
            session.getApplication().onStart((Start) message, myAgent, session);
            reply(aclMessage, new Started());
        }

        if (message instanceof Stop) {
            session.getApplication().onStop((Stop) message, myAgent, session);
            reply(aclMessage, new Stopped());
        }
    }

    @VisibleForTesting
    public void reply(final ACLMessage aclMessage, final AgentAction message) {
        try {
            final Action action = new Action(session.getSimulation().getMarketAgent(), message);
            final ACLMessage reply = aclMessage.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setOntology(SimulationOntology.getInstance().getName());
            reply.setLanguage(SimulationOntology.LANGUAGE);

            myAgent.getContentManager().fillContent(reply, action);
            myAgent.send(reply);
        }
        catch (CodecException e) {
            throw new RuntimeException(e);
        }
        catch (OntologyException e) {
            throw new RuntimeException(e);
        }
    }
}
