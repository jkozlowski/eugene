package eugene.agent.random;

import eugene.agent.random.impl.behaviour.PlaceOrderBehaviour;
import eugene.market.book.DefaultOrderBook;
import eugene.market.book.OrderBook;
import eugene.market.client.api.Application;
import eugene.market.client.api.ApplicationAdapter;
import eugene.market.ontology.message.Logon;
import jade.core.Agent;

import static eugene.market.client.api.Applications.orderBook;
import static eugene.market.client.api.Applications.proxy;

/**
 * Implements the Random Trader Agent.
 *
 * @author Jakub D Kozlowski
 * @since 0.5
 */
public class RandomTraderAgent extends Agent {
    
    public RandomTraderAgent(final String symbol) {

    }

    @Override
    public void setup() {
        final OrderBook orderBook = new DefaultOrderBook();
        final Application proxy = proxy(orderBook(orderBook), new ApplicationAdapter() {
            @Override
            public void onLogon(Logon logon, Agent agent) {
                agent.addBehaviour(new PlaceOrderBehaviour(agent, orderBook));
            }
        });
//        final SessionInitiator sessionInitiator = new SessionInitiator(this, proxy, )
    }
}
