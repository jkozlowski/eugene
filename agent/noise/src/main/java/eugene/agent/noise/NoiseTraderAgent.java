package eugene.agent.noise;

import eugene.agent.noise.impl.behaviour.PlaceOrderBehaviour;
import eugene.market.book.DefaultOrderBook;
import eugene.market.book.OrderBook;
import eugene.market.client.api.ApplicationAdapter;
import eugene.market.client.api.Session;
import eugene.market.client.api.SessionInitiator;
import eugene.market.ontology.message.Logon;
import jade.core.Agent;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static eugene.market.client.api.Applications.orderBook;
import static eugene.market.client.api.Applications.proxy;

/**
 * Implements the Noise Trader Agent.
 *
 * @author Jakub D Kozlowski
 * @since 0.5
 */
public class NoiseTraderAgent extends Agent {

    private final String symbol;

    public NoiseTraderAgent(final String symbol) {
        checkNotNull(symbol);
        checkArgument(!symbol.isEmpty());
        this.symbol = symbol;
    }

    @Override
    public void setup() {
        final OrderBook orderBook = new DefaultOrderBook();
        addBehaviour(new SessionInitiator(
                this,
                proxy(orderBook(orderBook), new ApplicationAdapter() {
                    @Override
                    public void onLogon(final Logon logon, final Agent agent, final Session session) {
                        agent.addBehaviour(new PlaceOrderBehaviour(agent, orderBook, session));
                    }
                }),
                symbol
        ));
    }
}
