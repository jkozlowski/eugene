package eugene.demo.jade.messagepassing;

import eugene.demo.jade.messagepassing.internal.MessagePrintingBehaviour;
import eugene.demo.jade.messagepassing.internal.MessageSendingBehaviour;
import jade.core.Agent;
import jade.core.behaviours.SequentialBehaviour;

/**
 * Implements an agent that sends a message to another agent,
 * then waits for a message from another agent, prints
 * the received message and finishes.
 *
 * Agent expects the following arguments:
 * <ol>
 *     <li>AIR of Agent to pass message to.</li>
 *     <li>AID of Agent to receive the message from and print.</li>
 *     <li>Message to send.</li>
 * </ol>
 *
 * @author Jakub D Kozlowski
 */
public class PrintingAgent extends Agent {

    /**
     * Collects arguments and starts
     */
    @Override
    protected void setup() {
        assert 3 == getArguments().length;
        assert getArguments()[0] instanceof String;
        assert getArguments()[1] instanceof String;
        assert getArguments()[2] instanceof String;

        final String to = (String) getArguments()[0];
        final String from = (String) getArguments()[1];
        final String msg = (String) getArguments()[2];

        final SequentialBehaviour sequence = new SequentialBehaviour(this);
        sequence.addSubBehaviour(new MessageSendingBehaviour(this, msg, to));
        sequence.addSubBehaviour(new MessagePrintingBehaviour(this, msg, from));
        addBehaviour(sequence);
    }
}
