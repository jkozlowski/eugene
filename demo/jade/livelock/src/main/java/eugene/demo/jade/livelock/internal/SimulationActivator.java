package eugene.demo.jade.livelock.internal;

import eugene.demo.jade.livelock.ResourceServer;
import eugene.demo.jade.livelock.WorkerAgent;
import jade.osgi.service.agentFactory.AgentFactoryService;
import jade.osgi.service.runtime.JadeRuntimeService;
import jade.util.Logger;
import jade.wrapper.AgentController;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Starts the livelock simulation.
 *
 * @author Jakub D Kozlowski
 * @since 0.1.3
 */
public class SimulationActivator implements BundleActivator {

    private static Logger LOG = Logger.getMyLogger(SimulationActivator.class.getName());

    private final AgentFactoryService agentFactory = new AgentFactoryService();

    private BundleContext ctx;

    @Override
    public void start(BundleContext ctx) throws Exception {
        this.ctx = ctx;
        agentFactory.init(ctx.getBundle());
        final String filter = "(objectclass=" + JadeRuntimeService.class.getName() + ")";
        ctx.addServiceListener(sl, filter);
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        agentFactory.clean();
    }

    /**
     * ServiceListener.
     */
    private final ServiceListener sl = new ServiceListener() {

        @Override
        public void serviceChanged(ServiceEvent se) {
            final ServiceReference sr = se.getServiceReference();

            switch (se.getType()) {
                case ServiceEvent.REGISTERED:
                    agentFactory.init(ctx.getBundle());

                    try {
                        final JadeRuntimeService jade = (JadeRuntimeService) ctx.getService(sr);
                        final List<AgentController> controllers = new ArrayList<AgentController>();

                        final AgentController requestServer = jade.createNewAgent(ResourceServer.ID,
                                                                                  ResourceServer.class
                                                                                          .getName(), null,
                                                                                  ctx.getBundle().getSymbolicName());
                        controllers.add(requestServer);

                        final AgentController workerA = jade.createNewAgent("worker-a",
                                                                           WorkerAgent.class
                                                                                   .getName(), new Object[]{
                                "resource-a",
                                "resource-b"},
                                                                           ctx.getBundle().getSymbolicName());
                        controllers.add(workerA);

                        final AgentController workerB = jade.createNewAgent("worker-b",
                                                                           WorkerAgent.class
                                                                                   .getName(), new Object[]{
                                "resource-b",
                                "resource-a"},
                                                                           ctx.getBundle().getSymbolicName());
                        controllers.add(workerB);

                        for (final AgentController controller : controllers) {
                            controller.start();
                        }
                    }
                    catch (Exception e) {
                        LOG.log(Level.SEVERE, "Cannot start " + SimulationActivator.class
                                .getName() + ": JADE Runtime not available - '" + e.getMessage() + "'");
                    }
                    finally {
                        ctx.ungetService(sr);
                    }
                    break;
            }
        }
    };
}
