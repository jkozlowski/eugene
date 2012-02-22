/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.utils.behaviour;

/**
 * Atomic behaviour that must be executed until {@link #finish(int)} is called.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public final class CyclicFinishableBehaviour extends FinishableBehaviour {

    /**
     * {@inheritDoc}
     */
    public CyclicFinishableBehaviour(final Task task) {
        super(task);
    }

    @Override
    public void action() {
        getTask().action(this, myAgent);
    }
}
