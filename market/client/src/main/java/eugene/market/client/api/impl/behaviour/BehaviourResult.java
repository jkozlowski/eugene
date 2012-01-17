package eugene.market.client.api.impl.behaviour;

import jade.core.behaviours.Behaviour;

import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Thread safe class that can be used by {@link Behaviour}s to indicate the result of their operation.
 *
 * @author Jakub D Kozlowski
 * @since 0.4
 */
public class BehaviourResult {

    /**
     * Returned from {@link Behaviour#onEnd()} if behaviour completed successfully.
     */
    public static final int SUCCESS = 0;

    /**
     * Returned from {@link Behaviour#onEnd()} if behaviour did not complete successfully.
     */
    public static final int FAILURE = 1;

    private final AtomicInteger result;

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
        this.result = new AtomicInteger(defaultResult);
    }

    /**
     * Gets the result.
     *
     * @return the result.
     */
    public int getResult() {
        return result.get();
    }

    /**
     * Sets the result to {@link BehaviourResult#FAILURE}.
     */
    public void fail() {
        result.set(FAILURE);
    }

    /**
     * Sets the result to {@link BehaviourResult#SUCCESS}.
     */
    public void success() {
        result.set(SUCCESS);
    }
}
