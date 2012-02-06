package eugene.simulation.agent.impl;

import eugene.simulation.agent.Simulation;
import eugene.utils.BehaviourResult;
import eugene.utils.Nullable;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Starts the {@link Agent}s.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class StartAgentsBehaviour extends OneShotBehaviour {

    private final BehaviourResult<AID> marketAgent;

    private final String symbol;

    private final Set<Agent> agents;

    private final BehaviourResult<Set<String>> result = new BehaviourResult<Set<String>>();

    public StartAgentsBehaviour(final BehaviourResult<AID> marketAgent, final String symbol, final Set<Agent> agents) {
        checkNotNull(marketAgent);
        checkNotNull(symbol);
        checkArgument(!symbol.isEmpty());
        checkNotNull(agents);
        checkArgument(!agents.isEmpty());
        this.marketAgent = marketAgent;
        this.symbol = symbol;
        this.agents = agents;
    }

    @Override
    public void action() {
        final Set<String> started = new HashSet<String>();
        try {
            int i = 0;
            for (final Agent a : agents) {
                final Simulation simulation = new SimulationImpl(myAgent.getAID(), marketAgent.getObject(), symbol);
                a.setArguments(new Simulation[]{simulation});
                final AgentContainer container = myAgent.getContainerController();
                final AgentController controller = container.acceptNewAgent(a.getClass().getName() + i++, a);
                controller.start();
                started.add(controller.getName());
            }

            result.success(started);
        }
        catch (StaleProxyException e) {
            result.fail(started);
        }
    }

    /**
     * Returns {@link AID#ISGUID}s of started agents.
     *
     * @return {@link AID#ISGUID}s of started agents.
     */
    @Nullable
    public Set<String> getStarted() {
        return result.getObject();
    }

    /**
     * Gets the {@link BehaviourResult} used by this {@link StartAgentsBehaviour}.
     *
     * @return {@link BehaviourResult} used by this {@link StartAgentsBehaviour}.
     */
    public BehaviourResult<Set<String>> getResult() {
        return result;
    }

    @Override
    public int onEnd() {
        return result.getResult();
    }
}
