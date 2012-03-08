/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.utils.behaviour;

import jade.core.Agent;

/**
 * A task to be performed inside a {@link FinishableBehaviour}.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public interface Task<K extends FinishableBehaviour> {

    /**
     * Performs the action.
     *
     * @param finishableBehaviour parent {@link FinishableBehaviour}.
     * @param agent               {@link Agent} executing <code>parent</code>.
     */
    void action(final K finishableBehaviour, final Agent agent);
}
