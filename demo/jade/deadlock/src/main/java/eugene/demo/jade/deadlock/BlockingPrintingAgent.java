/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.demo.jade.deadlock;

import eugene.demo.jade.deadlock.internal.BlockingMessagePrintingBehaviour;
import eugene.demo.jade.deadlock.internal.BlockingMessageSendingBehaviour;
import jade.core.Agent;
import jade.core.behaviours.SequentialBehaviour;

/**
 * Implements an agent that sends a message to another agent and waits for a reply; After receiving a reply it waits
 * for a message from another agent, prints the received message and finishes.
 *
 * Agent expects the following arguments:
 * <ol>
 * <li>Name of Agent to pass message to.</li>
 * <li>Name of Agent to receive the message from and print.</li>
 * <li>Message to send.</li>
 * </ol>
 *
 * @author Jakub D Kozlowski
 * @since 0.1.1
 */
public class BlockingPrintingAgent extends Agent {

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
        sequence.addSubBehaviour(new BlockingMessageSendingBehaviour(this, msg, to));
        sequence.addSubBehaviour(new BlockingMessagePrintingBehaviour(this, msg, from));
        addBehaviour(sequence);
    }
}
