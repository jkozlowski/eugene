/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.simulation.agent.impl;

import eugene.market.esma.MarketAgent;
import eugene.market.ontology.MarketOntology;
import eugene.simulation.agent.Simulation;
import eugene.simulation.ontology.LogonComplete;
import eugene.simulation.ontology.SimulationOntology;
import eugene.utils.behaviour.BehaviourResult;
import jade.content.onto.basic.Action;
import jade.core.AID;
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

import java.util.HashSet;
import java.util.Set;

import static eugene.market.ontology.Defaults.defaultSymbol;
import static eugene.simulation.agent.Tests.initAgent;
import static eugene.simulation.agent.Tests.submit;
import static jade.core.Runtime.instance;
import static java.util.Collections.synchronizedSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Tests {@link ReceiveLogonCompleteMessages}.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class ReceiveLogonCompleteMessagesTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullStarted() {
        new ReceiveLogonCompleteMessages(null);
    }

    @Test
    public void testReceiveLogonCompleteMessages() throws ControllerException, InterruptedException {

        final Profile profile = new MemoryProfile();
        final AgentContainer container = instance().createMainContainer(profile);

        final AgentController controller = initAgent(container);

        final InitializeMarketAgentBehaviour init = new InitializeMarketAgentBehaviour(new MarketAgent(defaultSymbol));
        submit(controller, init);
        
        assertThat(init.getResult().getObject(), notNullValue());

        final Set<Agent> agents = synchronizedSet(new HashSet<Agent>());
        final BehaviourResult b1Result = new BehaviourResult();
        final Behaviour b1 = new OneShotBehaviour() {
            @Override
            public void action() {
                try {
                    final Simulation simulation = (Simulation) myAgent.getArguments()[0];
                    final Action action = new Action(new AID(simulation.getSimulationAgent().getLocalName(),
                                                             AID.ISLOCALNAME), new LogonComplete());
                    final ACLMessage aclMessage = new ACLMessage(ACLMessage.INFORM);
                    aclMessage.setLanguage(SimulationOntology.LANGUAGE);
                    aclMessage.setOntology(SimulationOntology.getInstance().getName());
                    aclMessage.addReceiver(new AID(simulation.getSimulationAgent().getLocalName(),AID.ISLOCALNAME));
                    myAgent.getContentManager().fillContent(aclMessage, action);
                    myAgent.send(aclMessage);
                }
                catch (Exception e) {
                    b1Result.fail();
                    e.printStackTrace();
                }
            }
        };
        final Agent a1 = new Agent() {
            @Override
            public void setup() {
                getContentManager().registerLanguage(MarketOntology.getCodec());
                getContentManager().registerLanguage(SimulationOntology.getCodec());
                getContentManager().registerOntology(MarketOntology.getInstance());
                getContentManager().registerOntology(SimulationOntology.getInstance());
                addBehaviour(b1);
            }
        };
        agents.add(a1);

        final BehaviourResult b2Result = new BehaviourResult();
        final Behaviour b2 = new OneShotBehaviour() {
            @Override
            public void action() {
                try {
                    final Simulation simulation = (Simulation) myAgent.getArguments()[0];
                    final Action action = new Action(new AID(simulation.getSimulationAgent().getLocalName(),AID.ISLOCALNAME), new LogonComplete());
                    final ACLMessage aclMessage = new ACLMessage(ACLMessage.INFORM);
                    aclMessage.setLanguage(SimulationOntology.LANGUAGE);
                    aclMessage.setOntology(SimulationOntology.getInstance().getName());
                    aclMessage.addReceiver(new AID(simulation.getSimulationAgent().getLocalName(),AID.ISLOCALNAME));
                    myAgent.getContentManager().fillContent(aclMessage, action);
                    myAgent.send(aclMessage);
                }
                catch (Exception e) {
                    b2Result.fail();
                    e.printStackTrace();
                }
            }
        };
        final Agent a2 = new Agent() {
            @Override
            public void setup() {
                getContentManager().registerLanguage(MarketOntology.getCodec());
                getContentManager().registerLanguage(SimulationOntology.getCodec());
                getContentManager().registerOntology(MarketOntology.getInstance());
                getContentManager().registerOntology(SimulationOntology.getInstance());
                addBehaviour(b2);
            }
        };
        agents.add(a2);

        final StartAgentsBehaviour start = new StartAgentsBehaviour(init.getResult(), defaultSymbol, agents);
        submit(controller, start);

        final ReceiveLogonCompleteMessages receive = new ReceiveLogonCompleteMessages(start.getResult());
        submit(controller, receive);
        
        assertThat(start.getStarted(), is(receive.getResult().getObject()));

        container.kill();
    }
}
