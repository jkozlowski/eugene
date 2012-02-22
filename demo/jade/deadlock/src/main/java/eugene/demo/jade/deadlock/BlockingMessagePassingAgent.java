/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.demo.jade.deadlock;

import eugene.demo.jade.deadlock.internal.BlockingMessagePassingBehaviour;
import jade.core.Agent;

/**
 * Implements an agent that waits for a message from a specified agent, then passes that message to another agent and
 * waits for a reply; After receiving a reply, it sends a reply to the first agent and finishes.
 *
 * Agent expects the following arguments:
 * <ol>
 * <li>Name of Agent to receive the message from.</li>
 * <li>Name of Agent to pass message to.</li>
 * </ol>
 *
 * @author Jakub D Kozlowski
 * @since 0.1.1
 */
public class BlockingMessagePassingAgent extends Agent {

    /**
     * Collects the arguments and starts {@link BlockingMessagePassingBehaviour}.
     */
    @Override
    protected void setup() {
        assert getArguments().length == 2;
        assert getArguments()[0] instanceof String;
        assert getArguments()[1] instanceof String;

        final String from = (String) getArguments()[0];
        final String to = (String) getArguments()[1];

        addBehaviour(new BlockingMessagePassingBehaviour(this, from, to));
    }
}
