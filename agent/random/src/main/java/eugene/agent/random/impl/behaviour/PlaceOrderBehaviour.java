package eugene.agent.random.impl.behaviour;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

import java.util.Random;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Places
 *
 * @author Jakub D Kozlowski
 * @since 0.5
 */
public class PlaceOrderBehaviour extends TickerBehaviour {
    
    public static final Integer MAX_SLEEP = 6000;
    
    public static final Integer MIN_SLEEP = 1000;
    
    private final Random random = new Random();

    /**
     * Constructs a {@link PlaceOrderBehaviour} that will be executed by this <code>agent</code>
     *
     * @param agent {@link Agent} that will execute this {@link PlaceOrderBehaviour}.
     */
    public PlaceOrderBehaviour(final Agent agent) {
        super(agent, new Random().nextInt(MAX_SLEEP));
        checkNotNull(agent);
    }
    
    @Override
    protected void onTick() {
        super.reset();
    }
}
