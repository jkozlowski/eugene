package eugene.market.client.impl;

import eugene.market.client.Application;
import eugene.simulation.agent.Simulation;
import eugene.simulation.agent.SimulationAgent;
import eugene.simulation.ontology.LogonComplete;
import eugene.simulation.ontology.SimulationOntology;
import eugene.utils.BehaviourResult;
import jade.content.ContentElement;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import org.testng.annotations.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static eugene.market.esma.AbstractMarketAgentTest.MARKET_AGENT;
import static eugene.market.esma.AbstractMarketAgentTest.getContainer;
import static eugene.market.esma.AbstractMarketAgentTest.submit;
import static eugene.market.ontology.Defaults.defaultSymbol;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.verifyZeroInteractions;

/**
 * Tests {@link LogonBehaviour}.
 *
 * @author Jakub D Kozlowski
 * @since 0.4
 */
public class LogonBehaviourTest {

    public static final int TIMEOUT = 100000;

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullSession() {
        new LogonBehaviour(null);
    }

    @Test
    public void testLogonBehaviour() throws Exception {

        final AgentContainer container = getContainer();
        final String containerName = container.getName();

        final CountDownLatch latch = new CountDownLatch(1);

        final BehaviourResult result = new BehaviourResult();
        final Agent simulationAgent = new Agent() {
            @Override
            public void setup() {
                getContentManager().registerLanguage(SimulationOntology.getCodec());
                getContentManager().registerOntology(SimulationOntology.getInstance());
                addBehaviour(new SimpleBehaviour() {

                    @Override
                    public void action() {
                        final ACLMessage aclMessage = myAgent.receive();

                        if (null == aclMessage) {
                            return;
                        }

                        try {
                            final ContentElement c = myAgent.getContentManager().extractContent(aclMessage);
                            if (c instanceof Action && ((Action) c).getAction() instanceof LogonComplete) {
                                result.success();
                                latch.countDown();
                            }
                        }
                        catch (CodecException e) {
                            e.printStackTrace();
                        }
                        catch (OntologyException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public boolean done() {
                        return result.isSuccess();
                    }
                });
            }
        };
        final AgentController simulationController = container.acceptNewAgent(SimulationAgent.NAME, simulationAgent);
        simulationController.start();


        final Simulation simulation = mock(Simulation.class);
        when(simulation.getSimulationAgent()).thenReturn(new AID(SimulationAgent.NAME.concat("@" + containerName),
                                                                 AID.ISGUID));
        when(simulation.getSymbol()).thenReturn(defaultSymbol);
        when(simulation.getMarketAgent()).thenReturn(new AID(MARKET_AGENT.concat("@" + containerName),
                                                             AID.ISGUID));
        final Application application = mock(Application.class);

        final Behaviour b = new SequentialBehaviour() {

            private LogonBehaviour logon;

            @Override
            public void onStart() {
                final SessionImpl session = new SessionImpl(simulation, myAgent, application);
                logon = new LogonBehaviour(session);
                addSubBehaviour(logon);
            }

            @Override
            public int onEnd() {
                return logon.onEnd();
            }
        };

        submit(b, container);

        latch.await(TIMEOUT, TimeUnit.MILLISECONDS);

        assertThat(result.getResult(), is(BehaviourResult.SUCCESS));

        assertThat(b.onEnd(), is(BehaviourResult.SUCCESS));
        verifyZeroInteractions(application);

        container.kill();
    }
}
