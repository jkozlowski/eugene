package eugene.market.ontology.message;

import eugene.market.ontology.MarketOntology;
import eugene.market.ontology.Message;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.HashSet;
import java.util.Set;

/**
 * Utility {@link Behaviour} for testing {@link Message}s.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public class SenderBehaviour extends OneShotBehaviour {

    public final Set<Message> sent = new HashSet<Message>();

    public final Set<Message> failed = new HashSet<Message>();

    private final Set<Message> toSend;

    public SenderBehaviour(final Set<Message> toSend) {
        this.toSend = toSend;
    }

    @Override
    public void action() {
        for (Message msg : toSend) {
            try {
                final Action a = new Action(new AID(NewOrderSingleTest.RECEIVER_AGENT, AID.ISLOCALNAME), msg);
                final ACLMessage aclMessage = new ACLMessage(ACLMessage.REQUEST);
                aclMessage.addReceiver(new AID(NewOrderSingleTest.RECEIVER_AGENT, AID.ISLOCALNAME));
                aclMessage.setOntology(MarketOntology.getInstance().getName());
                aclMessage.setLanguage(MarketOntology.LANGUAGE);
                myAgent.getContentManager().fillContent(aclMessage, a);
                myAgent.send(aclMessage);
                sent.add(msg);
            }
            catch (Exception e1) {
                failed.add(msg);
            }
        }
    }
}
