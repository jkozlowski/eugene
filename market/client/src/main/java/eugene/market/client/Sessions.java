package eugene.market.client;

import eugene.market.client.impl.SessionInitiator;
import eugene.market.esma.MarketAgent;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/**
 * Factory for {@link Behaviour} that will initiate a {@link Session} with the {@link MarketAgent}.
 *
 * @author Jakub D Kozlowski
 * @since 0.5
 */
public final class Sessions {

    private Sessions() {
    }

    /**
     * Gets a {@link Behaviour} that will initiate a {@link Session} with the {@link MarketAgent} for this
     * <code>symbol</code>, that will route messages to this <code>application</code>.
     *
     * Clients should run the returned behaviour using {@link Agent#addBehaviour(Behaviour)}.
     *
     * @param agent       {@link Agent} that will execute this {@link Session}.
     * @param application {@link Application} to route messages to.
     * @param symbol      symbol for this {@link Session}.
     *
     * @return {@link Session} initiator.
     *
     * @throws NullPointerException     if <code>agent</code>, <code>application</code> or <code>symbol</code> are
     *                                  null.
     * @throws IllegalArgumentException if <code>symbol</code> is empty.
     */
    public static Behaviour initate(final Agent agent, final Application application,
                                    final String symbol) throws NullPointerException, IllegalArgumentException {
        return new SessionInitiator(agent, application, symbol);
    }
}
