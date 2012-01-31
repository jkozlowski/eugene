package eugene.market.client.impl;

import eugene.market.client.Application;
import eugene.market.client.Session;
import eugene.market.ontology.Message;
import eugene.simulation.agent.Simulation;
import eugene.simulation.ontology.SimulationOntology;
import eugene.simulation.ontology.Start;
import eugene.simulation.ontology.Started;
import eugene.simulation.ontology.Stop;
import eugene.simulation.ontology.Stopped;
import jade.content.AgentAction;
import jade.content.ContentManager;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.mockito.ArgumentCaptor;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockObjectFactory;
import org.testng.IObjectFactory;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.verifyZeroInteractions;

/**
 * Tests {@link StartStopBehaviour}.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
@PrepareForTest({Agent.class, ACLMessage.class})
public class StartStopBehaviourTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullSession() {
        new StartStopBehaviour(null);
    }

    @Test
    public void testActionNullReceive() {
        final Agent agent = mock(Agent.class);
        final Application application = mock(Application.class);
        final Session session = mock(Session.class);
        when(session.getSimulation()).thenReturn(mock(Simulation.class));
        when(session.getApplication()).thenReturn(application);
        final StartStopBehaviour startStopBehaviour = new StartStopBehaviour(session);
        startStopBehaviour.setAgent(agent);

        startStopBehaviour.action();
        verify(agent).receive(any(MessageTemplate.class));
        verify(session, never()).extractMessage(any(ACLMessage.class), any(Class.class));
        verifyZeroInteractions(application);
    }

    @Test
    public void testActionNullExtractedMessage() {
        final Agent agent = mock(Agent.class);
        final ACLMessage aclMessage = mock(ACLMessage.class);
        when(agent.receive(any(MessageTemplate.class))).thenReturn(aclMessage);
        final Application application = mock(Application.class);
        final Session session = mock(Session.class);
        when(session.getSimulation()).thenReturn(mock(Simulation.class));
        when(session.getApplication()).thenReturn(application);
        when(session.extractMessage(aclMessage, Message.class)).thenReturn(null);
        final StartStopBehaviour startStopBehaviour = new StartStopBehaviour(session);
        startStopBehaviour.setAgent(agent);

        startStopBehaviour.action();
        verify(agent).receive(any(MessageTemplate.class));
        verify(session).extractMessage(aclMessage, AgentAction.class);
        verifyZeroInteractions(application);
    }

    @Test
    public void testActionReceiveStart() throws CodecException, OntologyException {
        final Agent agent = mock(Agent.class);
        final ContentManager manager = mock(ContentManager.class);
        when(agent.getContentManager()).thenReturn(manager);
        final ACLMessage aclMessage = mock(ACLMessage.class);
        final ACLMessage reply = mock(ACLMessage.class);
        when(aclMessage.createReply()).thenReturn(reply);
        when(agent.receive(any(MessageTemplate.class))).thenReturn(aclMessage);
        final Start start = new Start();
        final Application application = mock(Application.class);
        final Session session = mock(Session.class);
        when(session.getSimulation()).thenReturn(mock(Simulation.class));
        when(session.getApplication()).thenReturn(application);
        when(session.extractMessage(aclMessage, AgentAction.class)).thenReturn(start);
        final StartStopBehaviour startStopBehaviour = new StartStopBehaviour(session);
        startStopBehaviour.setAgent(agent);

        startStopBehaviour.action();

        verify(aclMessage).createReply();

        verify(agent).receive(any(MessageTemplate.class));
        verify(session).extractMessage(aclMessage, AgentAction.class);
        verify(application).onStart(start, agent, session);
        verifyNoMoreInteractions(application);

        final ArgumentCaptor<Action> actionCaptor = ArgumentCaptor.forClass(Action.class);
        verify(manager).fillContent(eq(reply), actionCaptor.capture());
        assertThat(actionCaptor.getValue().getAction(), is(Started.class));

        verify(agent).send(reply);
    }

    @Test
    public void testActionReceiveStop() throws CodecException, OntologyException {
        final Agent agent = mock(Agent.class);
        final ContentManager manager = mock(ContentManager.class);
        when(agent.getContentManager()).thenReturn(manager);
        final ACLMessage aclMessage = mock(ACLMessage.class);
        final ACLMessage reply = mock(ACLMessage.class);
        when(aclMessage.createReply()).thenReturn(reply);
        when(agent.receive(any(MessageTemplate.class))).thenReturn(aclMessage);
        final Stop stop = new Stop();
        final Application application = mock(Application.class);
        final Session session = mock(Session.class);
        when(session.getSimulation()).thenReturn(mock(Simulation.class));
        when(session.getApplication()).thenReturn(application);
        when(session.extractMessage(aclMessage, AgentAction.class)).thenReturn(stop);
        final StartStopBehaviour startStopBehaviour = new StartStopBehaviour(session);
        startStopBehaviour.setAgent(agent);

        startStopBehaviour.action();

        verify(aclMessage).createReply();

        verify(agent).receive(any(MessageTemplate.class));
        verify(session).extractMessage(aclMessage, AgentAction.class);
        verify(application).onStop(stop, agent, session);
        verifyNoMoreInteractions(application);

        final ArgumentCaptor<Action> actionCaptor = ArgumentCaptor.forClass(Action.class);
        verify(manager).fillContent(eq(reply), actionCaptor.capture());
        assertThat(actionCaptor.getValue().getAction(), is(Stopped.class));

        verify(agent).send(reply);
    }

    @Test
    public void testReply() throws CodecException, OntologyException {
        final Agent agent = mock(Agent.class);
        final ContentManager manager = mock(ContentManager.class);
        when(agent.getContentManager()).thenReturn(manager);
        final ACLMessage aclMessage = mock(ACLMessage.class);
        final ACLMessage reply = mock(ACLMessage.class);
        when(aclMessage.createReply()).thenReturn(reply);
        final AgentAction action = mock(AgentAction.class);
        final Simulation simulation = when(mock(Simulation.class).getSimulationAgent()).thenReturn(mock(AID.class)).getMock();
        final Session session = when(mock(Session.class).getSimulation()).thenReturn(simulation).getMock();
        
        final StartStopBehaviour startStopBehaviour = new StartStopBehaviour(session);
        startStopBehaviour.setAgent(agent);

        startStopBehaviour.reply(aclMessage, action);
        
        verify(aclMessage).createReply();
        verify(reply).setPerformative(ACLMessage.INFORM);
        verify(reply).setLanguage(SimulationOntology.getCodec().getName());
        verify(reply).setOntology(SimulationOntology.NAME);
        verify(reply, never()).addReceiver(any(AID.class));

        final ArgumentCaptor<Action> actionCaptor = ArgumentCaptor.forClass(Action.class);
        verify(manager).fillContent(eq(reply), actionCaptor.capture());
        assertThat((AgentAction) actionCaptor.getValue().getAction(), sameInstance(action));
        verify(agent).send(reply);
    }

    @Test
    public void testActionUnknownMessage() {
        final Agent agent = PowerMockito.mock(Agent.class);
        final ContentManager manager = mock(ContentManager.class);
        when(agent.getContentManager()).thenReturn(manager);
        final ACLMessage aclMessage = PowerMockito.mock(ACLMessage.class);
        when(agent.receive(any(MessageTemplate.class))).thenReturn(aclMessage);
        final AgentAction message = mock(AgentAction.class);
        final Application application = PowerMockito.mock(Application.class);
        final Session session = PowerMockito.mock(Session.class);
        when(session.getSimulation()).thenReturn(PowerMockito.mock(Simulation.class));
        when(session.getApplication()).thenReturn(application);
        when(session.extractMessage(aclMessage, AgentAction.class)).thenReturn(message);
        final StartStopBehaviour startStopBehaviour = new StartStopBehaviour(session);
        startStopBehaviour.setAgent(agent);

        startStopBehaviour.action();
        verify(agent).receive(any(MessageTemplate.class));
        verify(session).extractMessage(aclMessage, AgentAction.class);
        verifyZeroInteractions(application);
    }

    @ObjectFactory
    public IObjectFactory getObjectFactory() {
        return new PowerMockObjectFactory();
    }
}
