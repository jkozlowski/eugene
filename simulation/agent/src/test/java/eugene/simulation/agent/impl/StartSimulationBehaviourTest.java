/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.simulation.agent.impl;

import eugene.market.agent.MarketAgent;
import eugene.simulation.agent.Symbol;
import eugene.simulation.agent.Symbols;
import eugene.simulation.agent.Tests;
import eugene.simulation.ontology.SimulationOntology;
import eugene.simulation.ontology.Start;
import eugene.simulation.ontology.Started;
import eugene.utils.behaviour.BehaviourResult;
import jade.content.ContentElement;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.imtp.memory.MemoryProfile;
import jade.lang.acl.ACLMessage;
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
import static eugene.simulation.agent.Tests.initAgent;
import static eugene.simulation.agent.Tests.submit;
import static jade.core.Runtime.instance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Tests {@link StartSimulationBehaviour}.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class StartSimulationBehaviourTest {

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorZeroLength() {
        new StartSimulationBehaviour(0, new BehaviourResult<Set<String>>());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullGuids() {
        new StartSimulationBehaviour(1, null);
    }

    @Test
    public void testStartSimulationBehaviour() throws ControllerException, InterruptedException {

        final Profile profile = new MemoryProfile();
        final AgentContainer container = instance().createMainContainer(profile);

        final AgentController controller = initAgent(container);

        final InitializeMarketAgentBehaviour init = new InitializeMarketAgentBehaviour(new MarketAgent(defaultSymbol));
        submit(controller, init);

        assertThat(init.getResult().getObject(), notNullValue());

        final CountDownLatch latch = new CountDownLatch(2);

        final Set<Agent> agents = Collections.synchronizedSet(new HashSet<Agent>());
        final Behaviour b1 = new OneShotBehaviour() {

            @Override
            public void onStart() {
                myAgent.getContentManager().registerLanguage(SimulationOntology.getCodec());
                myAgent.getContentManager().registerOntology(SimulationOntology.getInstance());
            }

            @Override
            public void action() {
                try {
                    final ACLMessage aclMessage = myAgent.blockingReceive();
                    final ContentElement ce = myAgent.getContentManager().extractContent(aclMessage);

                    if (!(ce instanceof Action) ||
                            null == ((Action) ce).getAction() ||
                            !(((Action) ce).getAction() instanceof Start)) {
                        latch.countDown();
                        return;
                    }

                    final ACLMessage reply = aclMessage.createReply();
                    reply.setLanguage(SimulationOntology.LANGUAGE);
                    reply.setOntology(SimulationOntology.NAME);
                    reply.setPerformative(ACLMessage.INFORM);
                    final Action a = new Action(aclMessage.getSender(), new Started());
                    myAgent.getContentManager().fillContent(reply, a);
                    myAgent.send(reply);

                    latch.countDown();
                }
                catch (CodecException e) {
                    e.printStackTrace();
                    latch.countDown();
                }
                catch (OntologyException e) {
                    e.printStackTrace();
                    latch.countDown();
                }
            }
        };
        final Agent a1 = new Agent();
        a1.addBehaviour(b1);
        agents.add(a1);

        final Behaviour b2 = new OneShotBehaviour() {

            @Override
            public void onStart() {
                myAgent.getContentManager().registerLanguage(SimulationOntology.getCodec());
                myAgent.getContentManager().registerOntology(SimulationOntology.getInstance());
            }

            @Override
            public void action() {
                try {
                    final ACLMessage aclMessage = myAgent.blockingReceive();
                    final ContentElement ce = myAgent.getContentManager().extractContent(aclMessage);

                    if (!(ce instanceof Action) ||
                            null == ((Action) ce).getAction() ||
                            !(((Action) ce).getAction() instanceof Start)) {
                        latch.countDown();
                        return;
                    }

                    final ACLMessage reply = aclMessage.createReply();
                    final Action a = new Action(aclMessage.getSender(), new Started());
                    reply.setLanguage(SimulationOntology.LANGUAGE);
                    reply.setOntology(SimulationOntology.NAME);
                    reply.setPerformative(ACLMessage.INFORM);
                    myAgent.getContentManager().fillContent(reply, a);
                    myAgent.send(reply);

                    latch.countDown();
                }
                catch (CodecException e) {
                    e.printStackTrace();
                    latch.countDown();
                }
                catch (OntologyException e) {
                    e.printStackTrace();
                    latch.countDown();
                }
            }
        };
        final Agent a2 = new Agent();
        a2.addBehaviour(b2);
        agents.add(a2);

        final Symbol symbol = Symbols.getSymbol(defaultSymbol, defaultTickSize, defaultPrice);
        final StartAgentsBehaviour start = new StartAgentsBehaviour(init.getResult(), symbol, agents);
        submit(controller, start);

        final StartSimulationBehaviour startSimulation = new StartSimulationBehaviour(Tests.SIMULATION_LENGTH,
                                                                                      start.getResult());
        submit(controller, startSimulation);
        latch.await();

        assertThat(startSimulation.onEnd(), is(BehaviourResult.SUCCESS));
        container.kill();
    }
}
