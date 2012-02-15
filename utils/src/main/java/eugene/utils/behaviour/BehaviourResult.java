package eugene.utils.behaviour;

import eugene.utils.annotation.Nullable;
import jade.core.behaviours.Behaviour;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Thread safe class that can be used by {@link Behaviour}s to indicate the result of their operation and optionally
 * return a result.
 *
 * @author Jakub D Kozlowski
 * @since 0.4
 */
public class BehaviourResult<T> {

    /**
     * Returned from {@link Behaviour#onEnd()} if behaviour completed successfully.
     */
    public static final int SUCCESS = 0;

    /**
     * Returned from {@link Behaviour#onEnd()} if behaviour did not complete successfully.
     */
    public static final int FAILURE = 1;

    private int result;

    private T object;

    /**
     * Creates a {@link BehaviourResult} with <code>defaultResult</code> equal to {@link BehaviourResult#FAILURE}.
     */
    public BehaviourResult() {
        this(FAILURE);
    }

    /**
     * Creates a {@link BehaviourResult} with result equal to <code>defaultResult</code>.
     *
     * @param defaultResult default result.
     */
    public BehaviourResult(final int defaultResult) {
        checkArgument(SUCCESS == defaultResult || FAILURE == defaultResult);
        this.result = defaultResult;
        this.object = null;
    }

    /**
     * Gets the result.
     *
     * @return the result.
     */
    public synchronized int getResult() {
        return result;
    }

    /**
     * Gets the object.
     *
     * @return the object.
     */
    public synchronized T getObject() {
        return object;
    }

    /**
     * Sets the result to {@link BehaviourResult#FAILURE}.
     */
    public synchronized void fail() {
        fail(null);
    }

    /**
     * Sets the result to {@link BehaviourResult#FAILURE}.
     *
     * @param object value that will be returned from {@link BehaviourResult#getObject()}.
     */
    public synchronized void fail(@Nullable final T object) {
        result = FAILURE;
        this.object = object;
    }

    /**
     * Sets the result to {@link BehaviourResult#SUCCESS} and {@link BehaviourResult#getObject()} will return
     * null.
     */
    public synchronized void success() {
        success(null);
    }

    /**
     * Sets the result to {@link BehaviourResult#SUCCESS}.
     *
     * @param object value that will be returned from {@link BehaviourResult#getObject()}.
     */
    public synchronized void success(@Nullable final T object) {
        result = SUCCESS;
        this.object = object;
    }

    /**
     * Returns <code>true</code> if {@link BehaviourResult#getResult()} is equal to {@link BehaviourResult#SUCCESS}.
     *
     * @return <code>true</code> is the result is {@link BehaviourResult#SUCCESS}.
     */
    public synchronized boolean isSuccess() {
        return SUCCESS == result;
    }
}
