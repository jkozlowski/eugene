package eugene.market.client.impl;

import eugene.market.client.Application;
import eugene.market.client.ApplicationAdapter;
import eugene.market.client.Session;
import eugene.market.esma.MarketAgent;
import eugene.market.ontology.MarketOntology;
import eugene.market.ontology.Message;
import eugene.simulation.agent.Simulation;
import eugene.simulation.ontology.SimulationOntology;
import eugene.simulation.ontology.Start;
import eugene.simulation.ontology.Stop;
import eugene.utils.behaviour.BehaviourResult;
import jade.core.Agent;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static eugene.market.client.Applications.proxy;

/**
 * Starts the {@link SessionImpl} with the {@link MarketAgent}. The Agents should provide an implementation
 * of {@link Application} that will receive callbacks whenever {@link Message}s are received.
 *
 * @author Jakub D Kozlowski
 * @since 0.4
 */
public final class SessionInitiator extends SequentialBehaviour {

    private final Application application;

    private final Simulation simulation;

    private final BehaviourResult result = new BehaviourResult();
    
    private static final Logger LOG = LoggerFactory.getLogger(SessionInitiator.class);

    /**
     * Creates a {@link SessionImpl} that will start a session with the Market Agent for this
     * <code>simulation</code> and this <code>application</code>.
     *
     * @param application implementation of {@link Application} that will be passed to {@link SessionImpl}.
     * @param simulation  implementation of {@link Simulation} that will be run.
     */
    public SessionInitiator(final Application application, final Simulation simulation) {
        checkNotNull(application);
        checkNotNull(simulation);
        this.application = application;
        this.simulation = simulation;
    }

    @Override
    public void onStart() {
        myAgent.getContentManager().registerLanguage(MarketOntology.getCodec());
        myAgent.getContentManager().registerLanguage(SimulationOntology.getCodec());
        myAgent.getContentManager().registerOntology(MarketOntology.getInstance());
        myAgent.getContentManager().registerOntology(SimulationOntology.getInstance());

        final ParallelBehaviour parallel = new ParallelBehaviour();

        final Application proxy = proxy(application, new ApplicationAdapter() {
            
            @Override
            public void onStart(Start start, Agent agent, Session session) {
                LOG.info("Started: {}", agent.getAID().getLocalName());
            }
            
            @Override
            public void onStop(Stop stop, Agent agent, Session session) {
                LOG.info("Stopped: {}", agent.getAID().getLocalName());
                SessionInitiator.this.removeSubBehaviour(parallel);
            }
        });

        final Session session = new SessionImpl(simulation, myAgent, proxy);
        addSubBehaviour(new LogonBehaviour(session));

        parallel.addSubBehaviour(new MessageRoutingBehaviour(session));
        parallel.addSubBehaviour(new StartStopBehaviour(session));
        addSubBehaviour(parallel);
    }

    @Override
    public int onEnd() {
        return result.getResult();
    }
}
