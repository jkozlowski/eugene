package eugene.demo.jade.deadlock.internal;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

import static jade.lang.acl.MessageTemplate.*;

/**
 * Sends a specified message to a specified agent, waits for a reply and finishes.
 *
 * @author Jakub D Kozlowski
 * @since 0.1.1
 */
public class BlockingMessageSendingBehaviour extends OneShotBehaviour {

    private final Logger LOG =
            Logger.getMyLogger(BlockingMessageSendingBehaviour.class.getName() + ":" + myAgent.getAMS());

    /**
     * Message to send.
     */
    private final String msg;

    /**
     * AID of Agent to send the message to.
     */
    private final AID to;

    /**
     * This constructor sets the owner agent for this
     * <code>BlockingMessageSendingBehaviour</code>, along with the message
     * to send and name of Agent to send the message to.
     *
     * @param a   The agent this behaviour must belong to.
     * @param msg The message to send.
     * @param to  Name of Agent to send the message to.
     */
    public BlockingMessageSendingBehaviour(final Agent a, final String msg, final String to) {
        super(a);
        assert null != msg;
        assert null != to;
        this.msg = msg;
        this.to = new AID(to, AID.ISLOCALNAME);
    }

    @Override
    public void action() {
        final String conversationId = "from:" + myAgent.getAMS() + "to:" + to;

        final ACLMessage aclMessage = new ACLMessage(ACLMessage.INFORM);
        aclMessage.setSender(myAgent.getAID());
        aclMessage.setContent(msg);
        aclMessage.setConversationId(conversationId);
        aclMessage.addReceiver(to);
        myAgent.send(aclMessage);

        LOG.info(aclMessage.toString());

        LOG.info("Waiting for a reply.");

        final MessageTemplate template = and(MatchConversationId(conversationId), MatchSender(to));
        final ACLMessage reply = myAgent.blockingReceive(template);

        LOG.info(reply.toString());
    }
}
