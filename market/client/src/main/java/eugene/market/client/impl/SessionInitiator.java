package eugene.market.client.impl;

import eugene.market.client.Application;
import eugene.market.client.Session;
import eugene.market.esma.MarketAgent;
import eugene.market.ontology.Message;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAException;

import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static eugene.market.esma.MarketAgent.getDFAgentDescription;
import static jade.domain.DFService.searchUntilFound;

/**
 * Searches for the Market Agent and initiates the {@link DefaultSession}. The Agents should provide an implementation of
 * {@link Application} that will receive callbacks whenever {@link Message}s are received.
 *
 * @author Jakub D Kozlowski
 * @since 0.4
 */
public final class SessionInitiator extends OneShotBehaviour {

    private final Logger LOG = Logger.getLogger(SessionInitiator.class.getName());

    public static final int RETRY = 3;

    public static final int TIMEOUT = 10 * 1000;

    private final Application application;

    private final String symbol;

    private final BehaviourResult result = new BehaviourResult();

    /**
     * Creates a {@link DefaultSession} that will search for the Market Agent for this <code>symbol</code> and that will
     * pass this {@link Application} to {@link DefaultSession}.
     *
     * @param agent       {@link Agent} that will execute this {@link SessionInitiator}.
     * @param application implementation of {@link Application} that will be passed to {@link DefaultSession}.
     * @param symbol      symbol that the Market Agent needs to handle.
     */
    public SessionInitiator(final Agent agent, final Application application, final String symbol) {
        super(agent);
        checkNotNull(agent);
        checkNotNull(application);
        checkNotNull(symbol);
        checkArgument(!symbol.isEmpty());
        this.application = application;
        this.symbol = symbol;
    }

    @Override
    public void action() {
        final AID marketAgent = getMarketAgent();
        if (null == marketAgent) {
            LOG.severe("Market agent for " + symbol + " not found");
            return;
        }
        
        final Session session = new DefaultSession(myAgent, marketAgent, application, symbol);

        myAgent.addBehaviour(new LogonBehaviour(myAgent, session));
        result.success();
    }

    /**
     * Tries to find the {@link MarketAgent} {@link SessionInitiator#RETRY} times,
     * with {@link SessionInitiator#TIMEOUT} delay between retries.
     *
     * @return {@link AID} of the {@link MarketAgent} or <code>null</code> if not found.
     */
    private AID getMarketAgent() {
        try {
            final DFAgentDescription agentDescription = getDFAgentDescription(symbol);
            final SearchConstraints constraints = new SearchConstraints();
            constraints.setMaxResults(-1L);
            for (int i = 0; i < RETRY; i++) {
                final DFAgentDescription[] results = searchUntilFound(myAgent, myAgent.getDefaultDF(), agentDescription,
                                                                      constraints, TIMEOUT);
                if (null != results && results.length > 0) {
                    return results[0].getName();
                }
            }
        }
        catch (FIPAException e) {
            LOG.severe(e.toString());
        }

        return null;
    }

    @Override
    public int onEnd() {
        return result.getResult();
    }
}
