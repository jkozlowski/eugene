package eugene.agent.noise;

import eugene.agent.noise.impl.PlaceOrderBehaviour;
import eugene.market.book.OrderBook;
import eugene.market.client.ApplicationAdapter;
import eugene.market.client.Session;
import eugene.market.ontology.MarketOntology;
import eugene.market.ontology.message.Logon;
import jade.content.lang.sl.SLCodec;
import jade.core.Agent;

import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static eugene.market.book.OrderBooks.defaultOrderBook;
import static eugene.market.client.Applications.orderBook;
import static eugene.market.client.Applications.proxy;
import static eugene.market.client.Sessions.initiate;

/**
 * Implements the Noise Trader Agent.
 *
 * @author Jakub D Kozlowski
 * @since 0.5
 */
public class NoiseTraderAgent extends Agent {

    private static final Logger LOG = Logger.getLogger(NoiseTraderAgent.class.getName());

    private final String symbol;

    public NoiseTraderAgent(final String symbol) {
        checkNotNull(symbol);
        checkArgument(!symbol.isEmpty());
        this.symbol = symbol;
    }

    @Override
    public void setup() {
        getContentManager().registerLanguage(new SLCodec(), MarketOntology.LANGUAGE);
        getContentManager().registerOntology(MarketOntology.getInstance());

        final OrderBook orderBook = defaultOrderBook();
        addBehaviour(initiate(
                this,
                proxy(
                        orderBook(orderBook),
                        new ApplicationAdapter() {
                            @Override
                            public void onLogon(final Logon logon, final Agent agent, final Session session) {
                                agent.addBehaviour(new PlaceOrderBehaviour(orderBook, session));
                            }
                        }
                ), symbol
        ));
    }
}
