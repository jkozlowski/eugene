package eugene.demo.jade.deadlock.internal;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

import static jade.lang.acl.MessageTemplate.MatchConversationId;
import static jade.lang.acl.MessageTemplate.MatchSender;
import static jade.lang.acl.MessageTemplate.and;

/**
 * Implements a message passing behaviour: blocks waiting for a message from a specified agent and passes it to another
 * agent and completes.
 *
 * @author Jakub D Kozlowski
 * @since 0.1.1
 */
public class BlockingMessagePassingBehaviour extends OneShotBehaviour {

    private final Logger LOG =
            Logger.getMyLogger(BlockingMessagePassingBehaviour.class.getName() + ":" + myAgent.getAMS());

    /**
     * AID of Agent to receive the message from.
     */
    private final AID from;

    /**
     * AID of Agent to pass the message to.
     */
    private final AID to;

    /**
     * This constructor sets the owner agent for this
     * <code>BlockingMessagePassingBehaviour</code>, along with the AIDs
     * of Agents to receive from and pass the message to.
     *
     * @param a    The agent this behaviour must belong to.
     * @param from Name of Agent to receive the message from.
     * @param to   Name of Agent to pass the message to.
     */
    public BlockingMessagePassingBehaviour(final Agent a, final String from, final String to) {
        super(a);
        assert null != from;
        assert null != to;
        this.from = new AID(from, AID.ISLOCALNAME);
        this.to = new AID(to, AID.ISLOCALNAME);
    }

    @Override
    public void action() {
        final MessageTemplate template = MatchSender(from);
        final ACLMessage received = myAgent.blockingReceive(template);

        final String passedConversationId = "from:" + myAgent.getAMS() + "to:" + to;
        final ACLMessage passedMessage = new ACLMessage(ACLMessage.INFORM);
        passedMessage.setSender(myAgent.getAID());
        passedMessage.setConversationId(passedConversationId);
        passedMessage.setContent(received.getContent());
        passedMessage.addReceiver(to);
        myAgent.send(passedMessage);


        LOG.info(passedMessage.toString());

        LOG.info("Waiting for reply");

        final MessageTemplate replyTemplate = and(MatchConversationId(passedConversationId), MatchSender(to));
        final ACLMessage reply = myAgent.blockingReceive(replyTemplate);

        LOG.info(reply.toString());

        final ACLMessage replyToReceived = received.createReply();
        myAgent.send(replyToReceived);
    }
}
