package eugene.market.ontology.message;

import eugene.market.ontology.MarketOntology;
import eugene.market.ontology.Message;
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

    public final Set<Message> received = new HashSet<Message>();

    public final Set<ACLMessage> failed = new HashSet<ACLMessage>();

    final MessageTemplate template =
            and(MatchLanguage(MessageTest.LANGUAGE), and(MatchOntology(MarketOntology.getInstance()
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

            if (Action.class != ce.getClass()) {
                failed.add(msg);
                return;
            }

            final Action a = (Action) ce;
            if (!(a.getAction() instanceof Message)) {
                failed.add(msg);
                return;
            }

            final Message order = (Message) a.getAction();
            received.add(order);
        }
        catch (Exception e) {
            failed.add(msg);
        }
    }

    public boolean done() {
        return received.size() + failed.size() >= toReceive;
    }
}
