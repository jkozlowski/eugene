package jade.imtp.memory;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.util.Logger;

import java.util.Random;

/**
 * Implements a test agent that logs {@link TestAgent#TICK_MESSAGE} every {@link TestAgent#TICK} milliseconds.
 *
 * @author Jakub D Kozlowski
 */
public class TestAgent extends Agent {

    private static final Random random = new Random();

    /**
     * Default tick.
     */
    public static final long TICK = 500;

    /**
     * Default tick message.
     */
    public static final String TICK_MESSAGE = "Tick!";

    private final Logger LOG = Logger.getMyLogger(TestAgent.class.getName() + "#" + getAID());

    @Override
    public void setup() {
        addBehaviour(new TickerBehaviour(this, TICK) {

            @Override
            protected void onTick() {
            }
        });
    }

    /**
     * Gets a random agent name.
     *
     * @return a random agent name.
     */
    public static String getRandomName() {
        return new StringBuilder(TestAgent.class.getName()).append("#").append(random.nextInt()).toString();
    }
}
