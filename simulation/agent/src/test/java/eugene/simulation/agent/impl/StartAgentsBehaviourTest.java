/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.simulation.agent.impl;

import eugene.market.esma.MarketAgent;
import eugene.simulation.agent.Simulation;
import eugene.simulation.agent.Symbol;
import eugene.simulation.agent.Symbols;
import eugene.utils.behaviour.BehaviourResult;
import jade.core.AID;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.imtp.memory.MemoryProfile;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import static eugene.market.ontology.Defaults.defaultPrice;
import static eugene.market.ontology.Defaults.defaultSymbol;
import static eugene.market.ontology.Defaults.defaultTickSize;
import static eugene.simulation.agent.Tests.SIMULATION_AGENT;
import static eugene.simulation.agent.Tests.initAgent;
import static eugene.simulation.agent.Tests.submit;
import static jade.core.Runtime.instance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;

/**
 * Tests {@link StartAgentsBehaviour}.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class StartAgentsBehaviourTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullMarketAgent() {
        new StartAgentsBehaviour(null, mock(Symbol.class), Collections.singleton(new Agent()));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullSymbol() {
        new StartAgentsBehaviour(new BehaviourResult<AID>(), null, Collections.singleton(new Agent()));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullAgents() {
        new StartAgentsBehaviour(new BehaviourResult<AID>(), mock(Symbol.class), null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorEmptyAgents() {
        new StartAgentsBehaviour(new BehaviourResult<AID>(), mock(Symbol.class), Collections.EMPTY_SET);
    }

    @Test
    public void testStartAgentsBehaviour() throws ControllerException, InterruptedException {
        final Profile profile = new MemoryProfile();
        final AgentContainer container = instance().createMainContainer(profile);

        final AgentController controller = initAgent(container);

        final InitializeMarketAgentBehaviour init = new InitializeMarketAgentBehaviour(new MarketAgent(defaultSymbol));
        submit(controller, init);

        assertThat(init.getResult().getObject(), notNullValue());

        final CountDownLatch latch = new CountDownLatch(2);

        final Set<Agent> agents = Collections.synchronizedSet(new HashSet<Agent>());
        final BehaviourResult<Simulation> b1Result = new BehaviourResult<Simulation>();
        final Behaviour b1 = new OneShotBehaviour() {
            @Override
            public void action() {
                if (myAgent.getArguments().length == 1 && myAgent.getArguments()[0] instanceof Simulation) {
                    b1Result.success((Simulation) myAgent.getArguments()[0]);
                }
                latch.countDown();
            }
        };
        final Agent a1 = new Agent();
        a1.addBehaviour(b1);
        agents.add(a1);

        final BehaviourResult<Simulation> b2Result = new BehaviourResult<Simulation>();
        final Behaviour b2 = new OneShotBehaviour() {
            @Override
            public void action() {
                if (myAgent.getArguments().length == 1 && myAgent.getArguments()[0] instanceof Simulation) {
                    b2Result.success((Simulation) myAgent.getArguments()[0]);
                }
                latch.countDown();
            }
        };
        final Agent a2 = new Agent();
        a2.addBehaviour(b2);
        agents.add(a2);

        final Symbol symbol = Symbols.getSymbol(defaultSymbol, defaultTickSize, defaultPrice);
        final StartAgentsBehaviour start = new StartAgentsBehaviour(init.getResult(), symbol, agents);
        submit(controller, start);
        latch.await();

        assertThat(start.getStarted().size(), is(2));

        for (final String guid : start.getStarted()) {
            assertThat(container.getAgent(guid, AID.ISGUID), notNullValue());
        }

        assertThat(b1Result.isSuccess(), is(true));
        assertThat(b1Result.getObject().getMarketAgent().getLocalName(), is(
                init.getResult().getObject().getLocalName()));
        assertThat(b1Result.getObject().getSimulationAgent().getLocalName(), is(SIMULATION_AGENT));
        assertThat(b1Result.getObject().getSymbol(), sameInstance(symbol));
        assertThat(b2Result.isSuccess(), is(true));
        assertThat(b2Result.getObject().getMarketAgent().getLocalName(), is(
                init.getResult().getObject().getLocalName()));
        assertThat(b2Result.getObject().getSimulationAgent().getLocalName(), is(SIMULATION_AGENT));
        assertThat(b2Result.getObject().getSymbol(), sameInstance(symbol));

        container.kill();
    }
}
