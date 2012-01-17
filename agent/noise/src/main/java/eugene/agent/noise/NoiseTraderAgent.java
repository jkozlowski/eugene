package eugene.agent.noise;

import eugene.agent.noise.impl.PlaceOrderBehaviour;
import eugene.market.book.DefaultOrderBook;
import eugene.market.book.OrderBook;
import eugene.market.client.Application;
import eugene.market.client.ApplicationAdapter;
import eugene.market.client.Session;
import eugene.market.ontology.MarketOntology;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.Logon;
import eugene.market.ontology.message.NewOrderSingle;
import eugene.market.ontology.message.OrderCancelReject;
import eugene.market.ontology.message.data.AddOrder;
import eugene.market.ontology.message.data.DeleteOrder;
import eugene.market.ontology.message.data.OrderExecuted;
import jade.content.lang.sl.SLCodec;
import jade.core.Agent;

import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static eugene.market.client.Applications.orderBook;
import static eugene.market.client.Applications.proxy;
import static eugene.market.client.Sessions.initate;

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

        final OrderBook orderBook = new DefaultOrderBook();
        addBehaviour(initate(
                this,
                proxy(
                        orderBook(orderBook),
                        new ApplicationAdapter() {
                            @Override
                            public void onLogon(final Logon logon, final Agent agent, final Session session) {
                                agent.addBehaviour(new PlaceOrderBehaviour(orderBook, session));
                            }
                        },
                        new Application() {

                            @Override
                            public void onLogon(Logon logon, Agent agent, Session session) {
//                                LOG.info(logon.toString());
                            }

                            @Override
                            public void toApp(ExecutionReport executionReport, Session session) {
//                                LOG.info(executionReport.toString());
                            }

                            @Override
                            public void toApp(OrderCancelReject orderCancelReject, Session session) {
//                                LOG.info(orderCancelReject.toString());
                            }

                            @Override
                            public void toApp(AddOrder addOrder, Session session) {
//                                LOG.info(addOrder.toString());
                            }

                            @Override
                            public void toApp(DeleteOrder deleteOrder, Session session) {
//                                LOG.info(deleteOrder.toString());
                            }

                            @Override
                            public void toApp(OrderExecuted orderExecuted, Session session) {
//                                LOG.info(orderExecuted.toString());
                            }

                            @Override
                            public void fromApp(NewOrderSingle newOrderSingle, Session session) {
//                                LOG.info(newOrderSingle.toString());
                            }
                        }
                ),
                symbol
        ));
    }
}
