package eugene.market.client.impl;

import eugene.market.client.Application;
import eugene.market.client.Session;
import eugene.market.ontology.Message;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.OrderCancelReject;
import eugene.market.ontology.message.data.AddOrder;
import eugene.market.ontology.message.data.DeleteOrder;
import eugene.market.ontology.message.data.OrderExecuted;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockObjectFactory;
import org.testng.IObjectFactory;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.when;


/**
 * Tests {@link MessageRoutingBehaviour}.
 *
 * @author Jakub D Kozlowski
 * @since 0.4
 */
@PrepareForTest({Agent.class, ACLMessage.class})
public class MessageRoutingBehaviourTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullAgent() {
        new MessageRoutingBehaviour(null, mock(Session.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullSession() {
        new MessageRoutingBehaviour(mock(Agent.class), null);
    }

    @Test
    public void testActionNullReceive() {
        final Agent agent = mock(Agent.class);
        final Application application = mock(Application.class);
        final Session session = mock(Session.class);
        when(session.getApplication()).thenReturn(application);
        final MessageRoutingBehaviour messageRoutingBehaviour = new MessageRoutingBehaviour(agent, session);

        messageRoutingBehaviour.action();
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
        when(session.getApplication()).thenReturn(application);
        when(session.extractMessage(aclMessage, Message.class)).thenReturn(null);
        final MessageRoutingBehaviour messageRoutingBehaviour = new MessageRoutingBehaviour(agent, session);

        messageRoutingBehaviour.action();
        verify(agent).receive(any(MessageTemplate.class));
        verify(session).extractMessage(aclMessage, Message.class);
        verifyZeroInteractions(application);
    }

    @Test
    public void testActionReceiveExecutionReport() {
        final Agent agent = mock(Agent.class);
        final ACLMessage aclMessage = mock(ACLMessage.class);
        when(agent.receive(any(MessageTemplate.class))).thenReturn(aclMessage);
        final ExecutionReport executionReport = new ExecutionReport();
        final Application application = mock(Application.class);
        final Session session = mock(Session.class);
        when(session.getApplication()).thenReturn(application);
        when(session.extractMessage(aclMessage, Message.class)).thenReturn(executionReport);
        final MessageRoutingBehaviour messageRoutingBehaviour = new MessageRoutingBehaviour(agent, session);

        messageRoutingBehaviour.action();
        verify(agent).receive(any(MessageTemplate.class));
        verify(session).extractMessage(aclMessage, Message.class);
        verify(application).toApp(executionReport, session);
        verifyNoMoreInteractions(application);
    }

    @Test
    public void testActionReceiveOrderCancelReject() {
        final Agent agent = mock(Agent.class);
        final ACLMessage aclMessage = mock(ACLMessage.class);
        when(agent.receive(any(MessageTemplate.class))).thenReturn(aclMessage);
        final OrderCancelReject orderCancelReject = new OrderCancelReject();
        final Application application = mock(Application.class);
        final Session session = mock(Session.class);
        when(session.getApplication()).thenReturn(application);
        when(session.extractMessage(aclMessage, Message.class)).thenReturn(orderCancelReject);
        final MessageRoutingBehaviour messageRoutingBehaviour = new MessageRoutingBehaviour(agent, session);

        messageRoutingBehaviour.action();
        verify(agent).receive(any(MessageTemplate.class));
        verify(session).extractMessage(aclMessage, Message.class);
        verify(application).toApp(orderCancelReject, session);
        verifyNoMoreInteractions(application);
    }

    @Test
    public void testActionReceiveAddOrder() {
        final Agent agent = mock(Agent.class);
        final ACLMessage aclMessage = mock(ACLMessage.class);
        when(agent.receive(any(MessageTemplate.class))).thenReturn(aclMessage);
        final AddOrder addOrder = new AddOrder();
        final Application application = mock(Application.class);
        final Session session = mock(Session.class);
        when(session.getApplication()).thenReturn(application);
        when(session.extractMessage(aclMessage, Message.class)).thenReturn(addOrder);
        final MessageRoutingBehaviour messageRoutingBehaviour = new MessageRoutingBehaviour(agent, session);

        messageRoutingBehaviour.action();
        verify(agent).receive(any(MessageTemplate.class));
        verify(session).extractMessage(aclMessage, Message.class);
        verify(application).toApp(addOrder, session);
        verifyNoMoreInteractions(application);
    }

    @Test
    public void testActionReceiveDeleteOrder() {
        final Agent agent = mock(Agent.class);
        final ACLMessage aclMessage = mock(ACLMessage.class);
        when(agent.receive(any(MessageTemplate.class))).thenReturn(aclMessage);
        final DeleteOrder deleteOrder = new DeleteOrder();
        final Application application = mock(Application.class);
        final Session session = mock(Session.class);
        when(session.getApplication()).thenReturn(application);
        when(session.extractMessage(aclMessage, Message.class)).thenReturn(deleteOrder);
        final MessageRoutingBehaviour messageRoutingBehaviour = new MessageRoutingBehaviour(agent, session);

        messageRoutingBehaviour.action();
        verify(agent).receive(any(MessageTemplate.class));
        verify(session).extractMessage(aclMessage, Message.class);
        verify(application).toApp(deleteOrder, session);
        verifyNoMoreInteractions(application);
    }

    @Test
    public void testActionReceiveOrderExecuted() {
        final Agent agent = mock(Agent.class);
        final ACLMessage aclMessage = mock(ACLMessage.class);
        when(agent.receive(any(MessageTemplate.class))).thenReturn(aclMessage);
        final OrderExecuted orderExecuted = new OrderExecuted();
        final Application application = mock(Application.class);
        final Session session = mock(Session.class);
        when(session.getApplication()).thenReturn(application);
        when(session.extractMessage(aclMessage, Message.class)).thenReturn(orderExecuted);
        final MessageRoutingBehaviour messageRoutingBehaviour = new MessageRoutingBehaviour(agent, session);

        messageRoutingBehaviour.action();
        verify(agent).receive(any(MessageTemplate.class));
        verify(session).extractMessage(aclMessage, Message.class);
        verify(application).toApp(orderExecuted, session);
        verifyNoMoreInteractions(application);
    }

    @Test
    public void testActionUnknownMessage() {
        final Agent agent = mock(Agent.class);
        final ACLMessage aclMessage = mock(ACLMessage.class);
        when(agent.receive(any(MessageTemplate.class))).thenReturn(aclMessage);
        final Message message = mock(Message.class);
        final Application application = mock(Application.class);
        final Session session = mock(Session.class);
        when(session.getApplication()).thenReturn(application);
        when(session.extractMessage(aclMessage, Message.class)).thenReturn(message);
        final MessageRoutingBehaviour messageRoutingBehaviour = new MessageRoutingBehaviour(agent, session);

        messageRoutingBehaviour.action();
        verify(agent).receive(any(MessageTemplate.class));
        verify(session).extractMessage(aclMessage, Message.class);
        verifyZeroInteractions(application);
    }

    @ObjectFactory
    public IObjectFactory getObjectFactory() {
        return new PowerMockObjectFactory();
    }
}
