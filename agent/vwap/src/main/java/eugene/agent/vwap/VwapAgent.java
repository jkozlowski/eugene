package eugene.agent.vwap;

import eugene.agent.vwap.impl.VwapBehaviour;
import eugene.market.book.OrderBook;
import eugene.market.client.ApplicationAdapter;
import eugene.market.client.ProxyApplication;
import eugene.market.client.Session;
import eugene.simulation.agent.Simulation;
import eugene.simulation.ontology.Start;
import eugene.simulation.ontology.Stop;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;

import java.util.Calendar;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static eugene.market.book.OrderBooks.defaultOrderBook;
import static eugene.market.client.Applications.orderBookApplication;
import static eugene.market.client.Applications.proxy;
import static eugene.market.client.Sessions.initiate;

/**
 * Implements the VWAP Trader agent.
 *
 * {@link VwapAgent} implements the following algorithm:
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public class VwapAgent extends Agent {

    private final VwapExecution vwapExecution;

    /**
     * Default constructor.
     *
     * @param vwapExecution configuration of the {@link VwapAgent}.
     *
     * @throws NullPointerException if <code>vwapExecution</code> is null.
     */
    public VwapAgent(final VwapExecution vwapExecution) {
        checkNotNull(vwapExecution);
        this.vwapExecution = vwapExecution;
    }

    @Override
    public void setup() {
        checkNotNull(getArguments());
        checkArgument(getArguments().length == 1);
        checkArgument(getArguments()[0] instanceof Simulation);

        final Simulation simulation = (Simulation) getArguments()[0];

        final OrderBook orderBook = defaultOrderBook();
        final ProxyApplication proxy = proxy(orderBookApplication(orderBook));
        proxy.addApplication(new ApplicationAdapter() {

            private Behaviour b = null;

            @Override
            public void onStart(final Start start, final Agent agent, final Session session) {
                final Calendar deadline = Calendar.getInstance();
                deadline.setTime(start.getStopTime());
                b = new VwapBehaviour(deadline, vwapExecution, orderBook, session);
                addBehaviour(b);
            }

            @Override
            public void onStop(final Stop stop, final Agent agent, final Session session) {
                if (null != b) {
                    agent.removeBehaviour(b);
                }
            }
        });
        addBehaviour(initiate(proxy, simulation));
    }
}
