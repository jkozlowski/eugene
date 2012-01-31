package eugene.market.client.impl;

import eugene.market.client.Application;
import eugene.market.client.Session;
import eugene.market.ontology.MarketOntology;
import eugene.market.ontology.Message;
import eugene.market.ontology.field.ClOrdID;
import eugene.market.ontology.field.Symbol;
import eugene.market.ontology.message.Logon;
import eugene.market.ontology.message.NewOrderSingle;
import eugene.market.ontology.message.OrderCancelRequest;
import eugene.simulation.agent.Simulation;
import jade.content.ContentElement;
import jade.content.ContentManager;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockObjectFactory;
import org.testng.IObjectFactory;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;

import java.util.Iterator;

import static eugene.market.ontology.Defaults.defaultClOrdID;
import static eugene.market.ontology.Defaults.defaultSymbol;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Tests {@link DefaultSession}.
 *
 * @author Jakub D Kozlowski
 * @since 0.4
 */
@PrepareForTest({Agent.class, AID.class, ContentManager.class})
public class DefaultSessionTest {

    private static final String defaultAgentID = "trader01";

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullSimulation() {
        new DefaultSession(null, mock(Agent.class), mock(Application.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullAgent() {
        new DefaultSession(mock(Simulation.class), null, mock(Application.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullApplication() {
        new DefaultSession(mock(Simulation.class), mock(Agent.class), null);
    }

    @Test
    public void testConstructor() {
        final Agent agent = mock(Agent.class);
        final Simulation simulation = mock(Simulation.class);
        final Application application = mock(Application.class);
        final Session session = new DefaultSession(simulation, agent, application);

        assertThat(session.getApplication(), sameInstance(application));
        assertThat(session.getSimulation(), sameInstance(simulation));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testExtractMessageNullAclMessage() {
        final Session session = new DefaultSession(mock(Simulation.class), mock(Agent.class), mock(Application.class));
        session.extractMessage(null, Logon.class);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testExtractMessageNullClazz() {
        final Session session = new DefaultSession(mock(Simulation.class), mock(Agent.class), mock(Application.class)
        );
        session.extractMessage(mock(ACLMessage.class), null);
    }

    @Test
    public void testExtractMessageCodecException() throws CodecException, OntologyException {
        final ACLMessage aclMessage = mock(ACLMessage.class);
        final Agent agent = mock(Agent.class);
        final ContentManager contentManager = mock(ContentManager.class);
        final Session session = new DefaultSession(mock(Simulation.class), agent, mock(Application.class));
        when(agent.getContentManager()).thenReturn(contentManager);
        when(contentManager.extractContent(aclMessage)).thenThrow(CodecException.class);

        assertThat(session.extractMessage(aclMessage, Logon.class), nullValue());
    }

    @Test
    public void testExtractMessageOntologyException() throws CodecException, OntologyException {
        final ACLMessage aclMessage = mock(ACLMessage.class);
        final Agent agent = mock(Agent.class);
        final ContentManager contentManager = mock(ContentManager.class);
        final Session session = new DefaultSession(mock(Simulation.class), agent, mock(Application.class));
        when(agent.getContentManager()).thenReturn(contentManager);
        when(contentManager.extractContent(aclMessage)).thenThrow(OntologyException.class);

        assertThat(session.extractMessage(aclMessage, Logon.class), nullValue());
    }

    @Test
    public void testExtractMessageOntologyNotAction() throws CodecException, OntologyException {
        final ACLMessage aclMessage = mock(ACLMessage.class);
        final Agent agent = mock(Agent.class);
        final ContentManager contentManager = mock(ContentManager.class);
        final Session session = new DefaultSession(mock(Simulation.class), agent, mock(Application.class));
        when(agent.getContentManager()).thenReturn(contentManager);
        when(contentManager.extractContent(aclMessage)).thenReturn(mock(ContentElement.class));

        assertThat(session.extractMessage(aclMessage, Logon.class), nullValue());
    }

    @Test
    public void testExtractMessageOntologyNullGetAction() throws CodecException, OntologyException {
        final ACLMessage aclMessage = mock(ACLMessage.class);
        final Agent agent = mock(Agent.class);
        final ContentManager contentManager = mock(ContentManager.class);
        final Session session = new DefaultSession(mock(Simulation.class), agent, mock(Application.class));
        when(agent.getContentManager()).thenReturn(contentManager);
        when(contentManager.extractContent(aclMessage)).thenReturn(mock(Action.class));

        assertThat(session.extractMessage(aclMessage, Logon.class), nullValue());
    }

    @Test
    public void testExtractMessageOntologyWrongClazz() throws CodecException, OntologyException {
        final ACLMessage aclMessage = mock(ACLMessage.class);
        final Agent agent = mock(Agent.class);
        final ContentManager contentManager = mock(ContentManager.class);
        final Action action = mock(Action.class);
        final Session session = new DefaultSession(mock(Simulation.class), agent, mock(Application.class));
        when(agent.getContentManager()).thenReturn(contentManager);
        when(contentManager.extractContent(aclMessage)).thenReturn(action);
        when(action.getAction()).thenReturn(mock(NewOrderSingle.class));

        assertThat(session.extractMessage(aclMessage, Logon.class), nullValue());
    }

    @Test
    public void testExtractMessageOntology() throws CodecException, OntologyException {
        final ACLMessage aclMessage = mock(ACLMessage.class);
        final Agent agent = mock(Agent.class);
        final ContentManager contentManager = mock(ContentManager.class);
        final Action action = mock(Action.class);
        final Logon logon = mock(Logon.class);
        final Session session = new DefaultSession(mock(Simulation.class), agent, mock(Application.class));
        when(agent.getContentManager()).thenReturn(contentManager);
        when(contentManager.extractContent(aclMessage)).thenReturn(action);
        when(action.getAction()).thenReturn(logon);

        assertThat(session.extractMessage(aclMessage, logon.getClass()), sameInstance(logon));
    }

    @Test
    public void testExtractMessageAsMessage() throws CodecException, OntologyException {
        final ACLMessage aclMessage = mock(ACLMessage.class);
        final Agent agent = mock(Agent.class);
        final ContentManager contentManager = mock(ContentManager.class);
        final Action action = mock(Action.class);
        final Logon logon = mock(Logon.class);
        final Session session = new DefaultSession(mock(Simulation.class), agent, mock(Application.class));
        when(agent.getContentManager()).thenReturn(contentManager);
        when(contentManager.extractContent(aclMessage)).thenReturn(action);
        when(action.getAction()).thenReturn(logon);

        final Message message = session.extractMessage(aclMessage, Message.class);
        assertThat(message, is(Logon.class));
        assertThat((Logon) message, sameInstance(logon));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testAclRequestNullMessage() {
        final Session session = new DefaultSession(mock(Simulation.class), mock(Agent.class), mock(Application.class)
        );
        session.aclRequest(null);
    }

    @Test
    public void testAclRequest() throws CodecException, OntologyException {
        final Message message = mock(Message.class);
        final AID to = mock(AID.class);
        final Simulation simulation = mock(Simulation.class);
        when(simulation.getMarketAgent()).thenReturn(to);
        final Agent agent = mock(Agent.class);
        final ContentManager contentManager = mock(ContentManager.class);
        final Session session = new DefaultSession(simulation, agent, mock(Application.class));
        when(agent.getContentManager()).thenReturn(contentManager);

        final ACLMessage aclMessage = session.aclRequest(message);

        assertThat(aclMessage.getPerformative(), is(ACLMessage.REQUEST));
        assertThat(aclMessage.getOntology(), sameInstance(MarketOntology.getInstance().getName()));
        assertThat(aclMessage.getLanguage(), is(MarketOntology.LANGUAGE));
        final Iterator i = aclMessage.getAllReceiver();
        final AID toActual = (AID) i.next();
        assertThat(toActual, sameInstance(to));
        assertThat(i.hasNext(), is(false));

        verify(agent).getContentManager();
        verify(contentManager).fillContent(Mockito.eq(aclMessage), Mockito.any(Action.class));
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testAclRequestThrowsCodecException() throws CodecException, OntologyException {
        final Message message = mock(Message.class);
        final Agent agent = mock(Agent.class);
        final ContentManager contentManager = mock(ContentManager.class);
        final Session session = new DefaultSession(mock(Simulation.class), agent, mock(Application.class));
        when(agent.getContentManager()).thenReturn(contentManager);
        doThrow(new CodecException("")).when(contentManager).fillContent(Mockito.any(ACLMessage.class),
                                                                         Mockito.any(Action.class));

        session.aclRequest(message);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testAclRequestThrowsOntologyException() throws CodecException, OntologyException {
        final Message message = mock(Message.class);
        final Agent agent = mock(Agent.class);
        final ContentManager contentManager = mock(ContentManager.class);
        final Session session = new DefaultSession(mock(Simulation.class), agent, mock(Application.class));
        when(agent.getContentManager()).thenReturn(contentManager);
        doThrow(new OntologyException("")).when(contentManager).fillContent(Mockito.any(ACLMessage.class),
                                                                            Mockito.any(Action.class));

        session.aclRequest(message);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testSendNewOrderSingleNullNewOrderSingle() {
        final Session session = new DefaultSession(mock(Simulation.class), mock(Agent.class), mock(Application.class));
        session.send((NewOrderSingle) null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSendNewOrderSingleIllegalSymbol() {
        final Simulation simulation = when(mock(Simulation.class).getSymbol()).thenReturn(defaultSymbol).getMock();
        final Session session = new DefaultSession(simulation, mock(Agent.class), mock(Application.class));
        final NewOrderSingle newOrderSingle = new NewOrderSingle();
        newOrderSingle.setSymbol(new Symbol("BARC.L"));
        session.send(newOrderSingle);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSendNewOrderSingleNullValueOfSymbol() {
        final Simulation simulation = when(mock(Simulation.class).getSymbol()).thenReturn(defaultSymbol).getMock();
        final Session session = new DefaultSession(simulation, mock(Agent.class), mock(Application.class));
        final NewOrderSingle newOrderSingle = new NewOrderSingle();
        newOrderSingle.setSymbol(new Symbol(null));
        session.send(newOrderSingle);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSendNewOrderSingleNotNullClOrdID() {
        final Session session = new DefaultSession(mock(Simulation.class), mock(Agent.class), mock(Application.class));
        final NewOrderSingle newOrderSingle = new NewOrderSingle();
        newOrderSingle.setClOrdID(new ClOrdID(defaultClOrdID));
        session.send(newOrderSingle);
    }

    @Test
    public void testSendNewOrderSingle() throws CodecException, OntologyException {
        final AID to = mock(AID.class);
        final Simulation simulation = mock(Simulation.class);
        when(simulation.getMarketAgent()).thenReturn(to);
        when(simulation.getSymbol()).thenReturn(defaultSymbol);
        final AID id = mock(AID.class);
        when(id.getName()).thenReturn(defaultAgentID);
        final Agent agent = mock(Agent.class);
        when(agent.getAID()).thenReturn(id);
        final ContentManager contentManager = mock(ContentManager.class);
        final Application application = mock(Application.class);
        final DefaultSession session = new DefaultSession(simulation, agent, application);
        when(agent.getContentManager()).thenReturn(contentManager);

        final NewOrderSingle newOrderSingle = new NewOrderSingle();
        session.send(newOrderSingle);

        verify(agent).getContentManager();
        verify(agent).send(Mockito.any(ACLMessage.class));
        verify(application).fromApp(newOrderSingle, session);
        assertThat(newOrderSingle.getSymbol().getValue(), is(defaultSymbol));
        assertThat(newOrderSingle.getClOrdID().getValue(), is(defaultAgentID + (session.getCurClOrdID() - 1L)));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testSendOrderCancelRequestNullOrderCancelRequest() {
        final Session session = new DefaultSession(mock(Simulation.class), mock(Agent.class), mock(Application.class));
        session.send((OrderCancelRequest) null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSendOrderCancelRequestIllegalSymbol() {
        final Simulation simulation = when(mock(Simulation.class).getSymbol()).thenReturn(defaultSymbol).getMock();
        final Session session = new DefaultSession(simulation, mock(Agent.class), mock(Application.class));
        final OrderCancelRequest orderCancelRequest = new OrderCancelRequest();
        orderCancelRequest.setSymbol(new Symbol("BARC.L"));
        session.send(orderCancelRequest);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSendOrderCancelRequestNullValueOfSymbol() {
        final Simulation simulation = when(mock(Simulation.class).getSymbol()).thenReturn(defaultSymbol).getMock();
        final Session session = new DefaultSession(simulation, mock(Agent.class), mock(Application.class));
        final OrderCancelRequest orderCancelRequest = new OrderCancelRequest();
        orderCancelRequest.setSymbol(new Symbol(null));
        session.send(orderCancelRequest);
    }

    @Test
    public void testOrderCancelRequest() throws CodecException, OntologyException {
        final AID to = mock(AID.class);
        final Simulation simulation = mock(Simulation.class);
        when(simulation.getMarketAgent()).thenReturn(to);
        when(simulation.getSymbol()).thenReturn(defaultSymbol);
        final AID id = mock(AID.class);
        when(id.getName()).thenReturn(defaultAgentID);
        final Agent agent = mock(Agent.class);
        when(agent.getAID()).thenReturn(id);
        final ContentManager contentManager = mock(ContentManager.class);
        final Application application = mock(Application.class);
        final DefaultSession session = new DefaultSession(simulation, agent, application);
        when(agent.getContentManager()).thenReturn(contentManager);

        final OrderCancelRequest orderCancelRequest = new OrderCancelRequest();
        session.send(orderCancelRequest);

        verify(agent).getContentManager();
        verify(agent).send(Mockito.any(ACLMessage.class));
        verify(application).fromApp(orderCancelRequest, session);
        assertThat(orderCancelRequest.getSymbol().getValue(), is(defaultSymbol));
    }

    @ObjectFactory
    public IObjectFactory getObjectFactory() {
        return new PowerMockObjectFactory();
    }
}
