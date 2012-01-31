package eugene.simulation.ontology;

import jade.content.AgentAction;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.onto.basic.Action;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.HashSet;
import java.util.Set;

import static jade.lang.acl.MessageTemplate.MatchLanguage;
import static jade.lang.acl.MessageTemplate.MatchOntology;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.and;

/**
 * @author Jakub D Kozlowski
 */
public class ReceiverBehaviour extends SimpleBehaviour {

    public final int toReceive;

    public final Set<AgentAction> received = new HashSet<AgentAction>();

    public final Set<ACLMessage> failed = new HashSet<ACLMessage>();

    final MessageTemplate template =
            and(MatchLanguage(SimulationOntology.LANGUAGE), and(MatchOntology(SimulationOntology.getInstance()
                                                                                      .getName()),
                                                                MatchPerformative(ACLMessage.REQUEST)));

    public ReceiverBehaviour(int toReceive) {
        this.toReceive = toReceive;
    }

    @Override
    public void action() {
        final ACLMessage msg = myAgent.blockingReceive(template);
        try {
            ContentElement ce = myAgent.getContentManager().extractContent(msg);

            if (Action.class != ce.getClass() || !(((Action) ce).getAction() instanceof AgentAction)) {
                failed.add(msg);
                return;
            }

            received.add((AgentAction) ((Action) ce).getAction());
        }
        catch (Exception e) {
            failed.add(msg);
        }
    }

    public boolean done() {
        return received.size() + failed.size() >= toReceive;
    }
}
