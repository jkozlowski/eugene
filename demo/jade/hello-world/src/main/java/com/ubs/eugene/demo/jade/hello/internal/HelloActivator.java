package com.ubs.eugene.demo.jade.hello.internal;

import com.ubs.eugene.demo.jade.hello.HelloAgent;
import jade.osgi.service.agentFactory.AgentFactoryService;
import jade.osgi.service.runtime.JadeRuntimeService;
import jade.wrapper.AgentController;
import org.osgi.framework.*;

/**
 * HelloWorld BundleActivator.
 *
 * @author Jakub D Kozlowski
 * @since 0.1
 */
public final class HelloActivator implements BundleActivator {


    private final AgentFactoryService agentFactory = new AgentFactoryService();

    private BundleContext context;

    /**
     * Called whenever the OSGi framework starts this bundle.
     *
     */
    public void start(final BundleContext context) throws Exception {
        this.context = context;

        final String filter = "(objectclass=" + JadeRuntimeService.class.getName() + ")";
        try {
            context.addServiceListener(sl, filter);
        } catch (InvalidSyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called whenever the OSGi framework stops this bundle.
     *
     */
    public void stop(BundleContext bc) throws Exception {
        if (null != agentFactory) {
            agentFactory.clean();
        }
    }

    /**
     * ServiceListener.
     *
     */
    private final ServiceListener sl = new ServiceListener() {
        @Override
        public void serviceChanged(ServiceEvent se) {
            final ServiceReference sr = se.getServiceReference();
            switch (se.getType()) {
                case ServiceEvent.REGISTERED:
                    agentFactory.init(context.getBundle());
                    final JadeRuntimeService jrs = (JadeRuntimeService) context.getService(sr);
                    AgentController ac;
                    try {
                        ac = jrs.createNewAgent("helloworld-agent", HelloAgent.class.getName(), null, context.getBundle().getSymbolicName());
                        ac.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    };
}
