package eugene.utils.behaviour;

import com.google.common.annotations.VisibleForTesting;
import jade.core.Agent;

/**
 * Atomic behaviour that executes {@link Task#action(FinishableBehaviour, Agent)} once, but does not terminate until
 * {@link #finished (int)} is called. This behaviour can be reset using {@link #reset()}.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public final class OneShotFinishableBehaviour extends FinishableBehaviour {

    /**
     * Indicates whether {@link Task#action(FinishableBehaviour, Agent)} has been called.
     */
    private boolean first = true;

    /**
     * Default constructor.
     *
     * @param task {@link Task} to perform.
     */
    public OneShotFinishableBehaviour(final Task task) {
        super(task);
    }

    /**
     * Returns <code>true</code> is this {@link OneShotFinishableBehaviour} has not been run yet.
     *
     * @return <code>true</code> is this behaviour has not been run yet, <code>false</code> otherwise.
     */
    @VisibleForTesting
    final boolean isFirst() {
        return first;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void action() {
        if (this.first) {
            this.first = false;
            getTask().action(this, myAgent);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void reset() {
        super.reset();
        this.first = true;
    }
}
