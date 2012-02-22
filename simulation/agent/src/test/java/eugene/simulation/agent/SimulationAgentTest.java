/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.simulation.agent;

import eugene.market.book.Order;
import eugene.market.ontology.field.enums.OrdType;
import eugene.market.ontology.field.enums.Side;
import eugene.simulation.ontology.LogonComplete;
import eugene.simulation.ontology.SimulationOntology;
import eugene.simulation.ontology.Start;
import eugene.simulation.ontology.Started;
import eugene.simulation.ontology.Stop;
import jade.content.ContentElement;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.imtp.memory.MemoryProfile;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;

import static com.google.common.collect.Sets.newHashSet;
import static eugene.market.ontology.Defaults.defaultOrdQty;
import static eugene.market.ontology.Defaults.defaultPrice;
import static eugene.market.ontology.Defaults.defaultSymbol;
import static eugene.simulation.agent.Tests.SIMULATION_AGENT;
import static eugene.simulation.agent.Tests.SIMULATION_LENGTH;
import static jade.core.Runtime.instance;

/**
 * Tests {@link SimulationAgent}.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class SimulationAgentTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullSymbol() {
        new SimulationAgent(null, SIMULATION_LENGTH, new TreeSet<Order>(), Collections.singleton(new Agent()));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorInvalidLength() {
        new SimulationAgent(defaultSymbol, 0, new TreeSet<Order>(), Collections.singleton(new Agent()));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorEmptySymbol() {
        new SimulationAgent("", SIMULATION_LENGTH, new TreeSet<Order>(), Collections.singleton(new Agent()));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullInitialOrders() {
        new SimulationAgent(defaultSymbol, SIMULATION_LENGTH, null, Collections.singleton(new Agent()));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullAgents() {
        new SimulationAgent(defaultSymbol, SIMULATION_LENGTH, new TreeSet<Order>(), null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorEmptyAgents() {
        new SimulationAgent(defaultSymbol, SIMULATION_LENGTH, new TreeSet<Order>(), new TreeSet<Agent>());
    }

    @Test
    public void testSimulationAgent() throws StaleProxyException, InterruptedException {

        final Profile profile = new MemoryProfile();
        final AgentContainer container = instance().createMainContainer(profile);

        final Set<Order> orders = newHashSet();
        for (int i = 1; i < 11; i++) {
            orders.add(new Order(1L, OrdType.LIMIT, Side.BUY, defaultOrdQty, defaultPrice - i));
            orders.add(new Order(1L, OrdType.LIMIT, Side.SELL, defaultOrdQty, defaultPrice + i));
        }


        final CountDownLatch latch = new CountDownLatch(2);
        final Set<Agent> agents = Collections.synchronizedSet(new HashSet<Agent>());
        agents.add(getTestAgent(latch));
        agents.add(getTestAgent(latch));

        final SimulationAgent simulationAgent = new SimulationAgent(defaultSymbol, SIMULATION_LENGTH, orders, agents);
        final AgentController controller = container.acceptNewAgent(SIMULATION_AGENT, simulationAgent);
        controller.start();
        latch.await();

        container.kill();
    }
    
    private Agent getTestAgent(final CountDownLatch latch) {
        return new Agent() {
            @Override
            public void setup() {
                getContentManager().registerLanguage(SimulationOntology.getCodec());
                getContentManager().registerOntology(SimulationOntology.getInstance());

                final SequentialBehaviour seq = new SequentialBehaviour();
                seq.addSubBehaviour(new OneShotBehaviour() {
                    @Override
                    public void action() {
                        try {
                            final Simulation simulation = (Simulation) myAgent.getArguments()[0];
                            final Action action = new Action(new AID(simulation.getSimulationAgent().getLocalName(),
                                                                     AID.ISLOCALNAME), new LogonComplete());
                            final ACLMessage aclMessage = new ACLMessage(ACLMessage.INFORM);
                            aclMessage.setLanguage(SimulationOntology.LANGUAGE);
                            aclMessage.setOntology(SimulationOntology.getInstance().getName());
                            aclMessage.addReceiver(new AID(simulation.getSimulationAgent().getLocalName(),
                                                           AID.ISLOCALNAME));
                            myAgent.getContentManager().fillContent(aclMessage, action);
                            myAgent.send(aclMessage);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                seq.addSubBehaviour(new OneShotBehaviour() {
                    @Override
                    public void action() {
                        try {
                            final ACLMessage aclMessage = myAgent.blockingReceive();
                            final ContentElement ce = myAgent.getContentManager().extractContent(aclMessage);

                            if (!(ce instanceof Action) ||
                                    null == ((Action) ce).getAction() ||
                                    !(((Action) ce).getAction() instanceof Start)) {
                                return;
                            }

                            final ACLMessage reply = aclMessage.createReply();
                            reply.setLanguage(SimulationOntology.LANGUAGE);
                            reply.setOntology(SimulationOntology.NAME);
                            reply.setPerformative(ACLMessage.INFORM);
                            final Action a = new Action(aclMessage.getSender(), new Started());
                            myAgent.getContentManager().fillContent(reply, a);
                            myAgent.send(reply);
                        }
                        catch (CodecException e) {
                            e.printStackTrace();
                        }
                        catch (OntologyException e) {
                            e.printStackTrace();
                        }
                    }
                });

                seq.addSubBehaviour(new OneShotBehaviour() {
                    @Override
                    public void action() {
                        try {
                            final ACLMessage aclMessage = myAgent.blockingReceive();
                            final ContentElement ce = myAgent.getContentManager().extractContent(aclMessage);

                            if (!(ce instanceof Action) ||
                                    null == ((Action) ce).getAction() ||
                                    !(((Action) ce).getAction() instanceof Stop)) {
                                return;
                            }

                            final ACLMessage reply = aclMessage.createReply();
                            reply.setLanguage(SimulationOntology.LANGUAGE);
                            reply.setOntology(SimulationOntology.NAME);
                            reply.setPerformative(ACLMessage.INFORM);
                            final Action a = new Action(aclMessage.getSender(), new Started());
                            myAgent.getContentManager().fillContent(reply, a);
                            myAgent.send(reply);
                        }
                        catch (CodecException e) {
                            e.printStackTrace();
                        }
                        catch (OntologyException e) {
                            e.printStackTrace();
                        }
                    }
                });

                seq.addSubBehaviour(new OneShotBehaviour() {
                    @Override
                    public void action() {
                        latch.countDown();
                    }
                });

                addBehaviour(seq);
            }
        };
    }
}
