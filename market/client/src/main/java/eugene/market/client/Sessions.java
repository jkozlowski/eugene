package eugene.market.client;

import com.google.common.annotations.VisibleForTesting;
import eugene.market.client.impl.SessionInitiator;
import eugene.market.esma.MarketAgent;
import eugene.simulation.agent.Simulation;
import eugene.simulation.agent.SimulationAgent;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/**
 * Factory for {@link Behaviour}s that initiate a {@link Session} with the {@link MarketAgent} and the {@link
 * SimulationAgent}.
 *
 * @author Jakub D Kozlowski
 * @since 0.5
 */
public final class Sessions {

    private static final String ERROR_MESSAGE = "This class should not be instantiated";

    /**
     * This constructor should not be invoked. It is only visible for the purposes of keeping global test coverage
     * high.
     *
     * @throws UnsupportedOperationException this constructor should not be invoked.
     */
    @VisibleForTesting
    public Sessions() throws UnsupportedOperationException {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    /**
     * Gets a {@link Behaviour} that will initiate a {@link Session} for this <code>simulation</code> and
     * <code>application</code> with the {@link MarketAgent} and the {@link SimulationAgent}.
     *
     * Clients should run the returned behaviour using {@link Agent#addBehaviour(Behaviour)}.
     *
     * @param application {@link Application} to route messages to.
     * @param simulation  parameters of the experiment that will be run.
     *
     * @return {@link Session} initiator.
     *
     * @throws NullPointerException if <code>application</code> or <code>simulation</code> are null.
     * @see Session
     * @see Application
     */
    public static Behaviour initiate(final Application application, final Simulation simulation)
            throws NullPointerException {
        return new SessionInitiator(application, simulation);
    }
}
