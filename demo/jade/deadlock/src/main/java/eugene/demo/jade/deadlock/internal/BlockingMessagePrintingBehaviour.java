package eugene.demo.jade.deadlock.internal;

import eugene.demo.jade.deadlock.BlockingPrintingAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

/**
 * Waits for a message from a specified agent, asserts that the message received is equal to the expected message,
 * prints the message, sends a reply and finishes.
 *
 * @author Jakub D Kozlowski
 * @since 0.1.1
 */
public class BlockingMessagePrintingBehaviour extends OneShotBehaviour {

    /**
     * Expected message.
     */
    private final String expected;

    /**
     * Name of Agent to receive the message from.
     */
    private final String from;

    /**
     * This constructor sets the owner agent for this
     * <code>BlockingMessagePrintingBehaviour</code>, along with the message
     * to expect and name of Agent to receive the message from.
     *
     * @param a        The agent this behaviour must belong to.
     * @param expected The message to expect
     * @param from     Name of Agent to send the message to.
     */
    public BlockingMessagePrintingBehaviour(final Agent a, final String expected, final String from) {
        super(a);
        assert null != expected;
        assert null != from;
        this.expected = expected;
        this.from = from;
    }

    @Override
    public void action() {
        final MessageTemplate template = MessageTemplate.MatchSender(new AID(from, AID.ISLOCALNAME));
        final ACLMessage msg = myAgent.blockingReceive(template);

        assert expected.equals(msg.getContent());

        final Logger LOG = Logger.getMyLogger(BlockingPrintingAgent.class.getName() + ":" + myAgent.getAMS());
        LOG.info(msg.toString());
    }
}
