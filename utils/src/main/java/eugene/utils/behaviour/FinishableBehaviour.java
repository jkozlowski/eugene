package eugene.utils.behaviour;

import jade.core.behaviours.SimpleBehaviour;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * {@link FinishableBehaviour} implements a behaviour that can be finished. Extending classes should call {@link
 * #reset} when resetting the beheaviour.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public abstract class FinishableBehaviour extends SimpleBehaviour {

    /**
     * Indicates whether {@link #finished} has been called.
     */
    private boolean finished = false;

    /**
     * Value to return from {@link #onEnd()}.
     */
    private int end = -1;

    private final Task task;

    /**
     * Default constructor.
     *
     * @param task {@link Task} to perform.
     *
     * @throws NullPointerException if <code>task</code> is null.
     */
    public FinishableBehaviour(final Task task) {
        this.task = checkNotNull(task);
    }

    /**
     * Gets the task.
     *
     * @return the task.
     */
    Task getTask() {
        return task;
    }

    /**
     * This method should be called in order to terminate this behaviour.
     *
     * @param onEnd value to return from {@link #onEnd()}.
     */
    public final void finish(final int onEnd) {
        this.end = onEnd;
        this.finished = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        super.reset();
        this.finished = false;
        this.end = -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int onEnd() {
        checkState(done());
        return end;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean done() {
        return finished;
    }
}
