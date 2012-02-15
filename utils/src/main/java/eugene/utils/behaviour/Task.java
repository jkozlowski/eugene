package eugene.utils.behaviour;

import jade.core.Agent;

/**
 * A task to be performed inside a {@link FinishableBehaviour}.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public interface Task {

    /**
     * Performs the action.
     *
     * @param finishableBehaviour parent {@link FinishableBehaviour}.
     * @param agent               {@link Agent} executing <code>parent</code>.
     */
    void action(final FinishableBehaviour finishableBehaviour, final Agent agent);
}
