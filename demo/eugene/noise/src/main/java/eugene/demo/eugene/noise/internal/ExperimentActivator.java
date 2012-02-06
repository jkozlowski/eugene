package eugene.demo.eugene.noise.internal;

import com.google.common.collect.Sets;
import eugene.agent.noise.NoiseTraderAgent;
import eugene.market.book.OrderBook;
import eugene.simulation.agent.Simulation;
import eugene.simulation.agent.SimulationAgent;
import jade.core.Agent;
import jade.osgi.service.agentFactory.AgentFactoryService;
import jade.osgi.service.runtime.JadeRuntimeService;
import jade.wrapper.AgentController;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import java.util.Set;

import static eugene.market.book.OrderBooks.defaultOrderBook;
import static eugene.market.client.Applications.orderBookApplication;
import static eugene.market.client.Applications.proxy;
import static eugene.market.client.Applications.topOfBookPrinterApplication;
import static eugene.market.client.Sessions.initiate;
import static java.util.Collections.EMPTY_SET;

/**
 * Starts a simulation with {@link ExperimentActivator#NUMBER_OF_TRADERS} {@link NoiseTraderAgent}s that runs for
 * {@link ExperimentActivator#LENGTH} period of time.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public class ExperimentActivator implements BundleActivator {

    private static final int NUMBER_OF_TRADERS = 5;

    private static final String SYMBOL = "VOD.L";

    private static final int LENGTH = 60 * 1000;

    private BundleContext ctx;

    private final AgentFactoryService agentFactory = new AgentFactoryService();

    @Override
    public void start(BundleContext context) throws Exception {
        this.ctx = context;
        agentFactory.init(ctx.getBundle());
        final String filter = "(objectclass=" + JadeRuntimeService.class.getName() + ")";
        ctx.addServiceListener(sl, filter);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }

    private void startSimulation(final ServiceReference sr) {

        try {
            final Set<Agent> agents = Sets.newHashSet();
            for (int i = 0; i < NUMBER_OF_TRADERS; i++) {
                agents.add(new NoiseTraderAgent());
            }

            agents.add(new Agent() {
                @Override
                public void setup() {
                    final Simulation simulation = (Simulation) getArguments()[0];
                    final OrderBook orderBook = defaultOrderBook();
                    addBehaviour(initiate(proxy(orderBookApplication(orderBook), topOfBookPrinterApplication(
                            orderBook)), simulation));
                }
            });

            final JadeRuntimeService jade = (JadeRuntimeService) ctx.getService(sr);
            final SimulationAgent simulationAgent = new SimulationAgent(SYMBOL, LENGTH, EMPTY_SET, agents);
            final AgentController controller = jade.acceptNewAgent(SimulationAgent.NAME, simulationAgent);
            controller.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            ctx.ungetService(sr);
        }
    }

    private final ServiceListener sl = new ServiceListener() {

        @Override
        public void serviceChanged(ServiceEvent se) {
            final ServiceReference sr = se.getServiceReference();

            switch (se.getType()) {
                case ServiceEvent.REGISTERED:
                    agentFactory.init(ctx.getBundle());
                    startSimulation(sr);
                    break;
            }
        }
    };
}
