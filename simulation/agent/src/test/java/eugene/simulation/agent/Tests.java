package eugene.simulation.agent;

import eugene.market.ontology.MarketOntology;
import eugene.simulation.ontology.SimulationOntology;
import jade.core.behaviours.Behaviour;
import jade.util.Event;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import jade.wrapper.gateway.GatewayAgent;

/**
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class Tests {

    public static final int SIMULATION_LENGTH = 5 * 1000;

    public static final int TIMEOUT = 60000;

    public static final String SIMULATION_AGENT = "simulation-agent";
    
    public static AgentController initAgent(final AgentContainer container) throws StaleProxyException {
        final GatewayAgent a = new GatewayAgent();
        final AgentController controller = container.acceptNewAgent(SIMULATION_AGENT, a);
        a.getContentManager().registerLanguage(SimulationOntology.getCodec());
        a.getContentManager().registerOntology(SimulationOntology.getInstance());
        a.getContentManager().registerLanguage(MarketOntology.getCodec());
        a.getContentManager().registerOntology(MarketOntology.getInstance());
        controller.start();
        return controller;
    }

    public static void submit(final AgentController controller, final Behaviour b) throws StaleProxyException,
                                                                                          InterruptedException {
        final Event bEvent = new Event(-1, b);
        controller.putO2AObject(bEvent, AgentController.ASYNC);
        bEvent.waitUntilProcessed(Tests.TIMEOUT);
    }
}
