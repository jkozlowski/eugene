package eugene.market.client.impl.session;

import eugene.market.client.Application;
import eugene.market.client.Session;
import eugene.market.client.impl.Messages;
import eugene.market.esma.MarketAgent;
import eugene.market.ontology.Message;
import eugene.market.ontology.message.Logon;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link DefaultSession} is a communication channel between the Agents and the Market Agent.
 *
 * {@link DefaultSession} sends an appropriate {@link Logon} message to the Market Agent in order to initiate the
 * communication channel. {@link DefaultSession} will route all {@link Message}s (sent through it
 * and received by it) to {@link Application}.
 *
 * @author Jakub D Kozlowski
 * @since 0.4
 */
public final class DefaultSession implements Session {

    private final Agent agent;

    private final AID marketAgent;

    private final Application application;

    private final String symbol;

    /**
     * Creates a {@link DefaultSession} that will route all messages to this <code>application</code> and will be
     * executed by this <code>agent</code>.
     *
     * @param agent       {@link Agent} that will execute this {@link DefaultSession}.
     * @param marketAgent {@link AID} of the {@link MarketAgent} to communicate with.
     * @param application implementation of {@link Application} that this {@link DefaultSession} will route {@link
     *                    Message}s to.
     * @param symbol      symbol handled by this {@link DefaultSession}.
     */
    public DefaultSession(final Agent agent, final AID marketAgent, final Application application,
                          final String symbol) {
        checkNotNull(agent);
        checkNotNull(marketAgent);
        checkNotNull(application);
        checkNotNull(symbol);
        checkArgument(!symbol.isEmpty());
        this.agent = agent;
        this.marketAgent = marketAgent;
        this.application = application;
        this.symbol = symbol;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    // TODO: clone the AID.
    public AID getMarketAgent() {
        return marketAgent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Application getApplication() {
        return application;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSymbol() {
        return symbol;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Message> T extractMessage(final ACLMessage aclMessage, final Class<T> type) {
        return Messages.extractMessage(agent, aclMessage, type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ACLMessage aclRequest(final Message message) {
        return Messages.aclRequest(agent, marketAgent, message);
    }
}
