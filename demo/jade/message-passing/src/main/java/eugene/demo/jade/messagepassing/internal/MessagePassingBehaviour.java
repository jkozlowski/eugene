package eugene.demo.jade.messagepassing.internal;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Implements a message passing behaviour:
 * blocks waiting for a message from a specified agent and
 * passes it to another agent and completes.
 *
 * @author Jakub D Kozlowski
 */
public class MessagePassingBehaviour extends OneShotBehaviour {

    /**
     * AID of Agent to receive the message from.
     *
     */
    private final String from;

    /**
     * AID of Agent to pass the message to.
     *
     */
    private final String to;

    /**
     * This constructor sets the owner agent for this
     * <code>MessagePassingBehaviour</code>, along with the AIDs
     * of Agents to receive from and pass the message to.
     *
     * @param a The agent this behaviour must belong to.
     * @param from AID of Agent to receive the message from.
     * @param to AID of Agent to pass the message to.
     */
    public MessagePassingBehaviour(final Agent a, final String from, final String to) {
        super(a);
        assert null != from;
        assert null != to;
        this.from = from;
        this.to = to;
    }

    @Override
    public void action() {
        final MessageTemplate template = MessageTemplate.MatchSender(new AID(from, AID.ISLOCALNAME));
        final ACLMessage received = myAgent.blockingReceive(template);

        final ACLMessage aclMessage = new ACLMessage(ACLMessage.INFORM);
        aclMessage.setSender(myAgent.getAID());
        aclMessage.setContent(received.getContent());
        aclMessage.addReceiver(new AID(to, AID.ISLOCALNAME));
        myAgent.send(aclMessage);
    }
}
