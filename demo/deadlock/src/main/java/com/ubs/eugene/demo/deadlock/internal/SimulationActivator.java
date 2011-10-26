package com.ubs.eugene.demo.deadlock.internal;

import com.ubs.eugene.demo.deadlock.BlockingMessagePassingAgent;
import com.ubs.eugene.demo.deadlock.BlockingPrintingAgent;
import jade.osgi.service.agentFactory.AgentFactoryService;
import jade.osgi.service.runtime.JadeRuntimeService;
import jade.util.Logger;
import jade.wrapper.AgentController;
import org.osgi.framework.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Starts the deadlock simulation between 3 agents.
 *
 * @author Jakub D Kozlowski
 * @since 0.1.1
 */
public class SimulationActivator implements BundleActivator {

    private static Logger LOG = Logger.getMyLogger(SimulationActivator.class.getName());

    private final AgentFactoryService agentFactory = new AgentFactoryService();

    private static final String MESSAGE = "Hello World!";

    private static final String PRINTING_AGENT = BlockingPrintingAgent.class.getName();

    private static final String A_AGENT = BlockingMessagePassingAgent.class.getName() + ":A";

    private static final String B_AGENT = BlockingMessagePassingAgent.class.getName() + ":B";

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

                        final AgentController printer = jade.createNewAgent(PRINTING_AGENT,
                                                                            BlockingPrintingAgent.class
                                                                                    .getName(), new Object[]{A_AGENT,
                                                                                                             B_AGENT,
                                                                                                             MESSAGE},
                                                                            ctx.getBundle().getSymbolicName());
                        controllers.add(printer);

                        final AgentController a = jade.createNewAgent(A_AGENT,
                                                                      BlockingMessagePassingAgent.class
                                                                              .getName(), new Object[]{PRINTING_AGENT,
                                                                                                       B_AGENT},
                                                                      ctx.getBundle().getSymbolicName());
                        controllers.add(a);

                        final AgentController b = jade.createNewAgent(B_AGENT,
                                                                      BlockingMessagePassingAgent.class
                                                                              .getName(), new Object[]{A_AGENT,
                                                                                                       PRINTING_AGENT},
                                                                      ctx.getBundle().getSymbolicName());
                        controllers.add(b);

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
