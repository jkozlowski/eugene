package eugene.agent.noise;

import eugene.agent.noise.impl.PlaceOrderBehaviour;
import eugene.market.book.OrderBook;
import eugene.market.client.ApplicationAdapter;
import eugene.market.client.Session;
import eugene.market.ontology.MarketOntology;
import eugene.simulation.agent.Simulation;
import eugene.simulation.ontology.Start;
import jade.core.Agent;

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

    @Override
    public void setup() {

        checkNotNull(getArguments());
        checkArgument(getArguments().length == 1);
        checkArgument(getArguments()[0] instanceof Simulation);

        final Simulation simulation = (Simulation) getArguments()[0];

        getContentManager().registerLanguage(MarketOntology.getCodec());
        getContentManager().registerOntology(MarketOntology.getInstance());

        final OrderBook orderBook = defaultOrderBook();
        addBehaviour(
                initiate(
                        proxy(
                                orderBook(orderBook),
                                new ApplicationAdapter() {
                                    @Override
                                    public void onStart(final Start start, final Agent agent, final Session session) {
                                        agent.addBehaviour(new PlaceOrderBehaviour(orderBook, session));
                                    }
                                }
                        ),
                        simulation));
    }
}
