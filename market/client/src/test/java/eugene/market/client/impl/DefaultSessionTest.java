package eugene.market.client.impl;

import eugene.market.client.Application;
import eugene.market.client.Session;
import eugene.market.ontology.MarketOntology;
import eugene.market.ontology.Message;
import eugene.market.ontology.message.Logon;
import eugene.market.ontology.message.NewOrderSingle;
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

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullAgent() {
        new DefaultSession(null, mock(AID.class), mock(Application.class), defaultSymbol);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullMarketAgent() {
        new DefaultSession(mock(Agent.class), null, mock(Application.class), defaultSymbol);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullApplication() {
        new DefaultSession(mock(Agent.class), mock(AID.class), null, defaultSymbol);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullSymbol() {
        new DefaultSession(mock(Agent.class), mock(AID.class), mock(Application.class), null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorEmptySymbol() {
        new DefaultSession(mock(Agent.class), mock(AID.class), mock(Application.class), "");
    }

    @Test
    public void testConstructor() {
        final Agent agent = mock(Agent.class);
        final AID marketAgent = mock(AID.class);
        final Application application = mock(Application.class);
        final Session session = new DefaultSession(agent, marketAgent, application, defaultSymbol);

        assertThat(session.getApplication(), sameInstance(application));
        assertThat(session.getSymbol(), is(defaultSymbol));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testExtractMessageNullAclMessage() {
        final Session session = new DefaultSession(mock(Agent.class), mock(AID.class), mock(Application.class),
                                                   defaultSymbol);
        session.extractMessage(null, Logon.class);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testExtractMessageNullClazz() {
        final Session session = new DefaultSession(mock(Agent.class), mock(AID.class), mock(Application.class),
                                                   defaultSymbol);
        session.extractMessage(mock(ACLMessage.class), null);
    }

    @Test
    public void testExtractMessageCodecException() throws CodecException, OntologyException {
        final ACLMessage aclMessage = mock(ACLMessage.class);
        final Agent agent = mock(Agent.class);
        final ContentManager contentManager = mock(ContentManager.class);
        final Session session = new DefaultSession(agent, mock(AID.class), mock(Application.class), defaultSymbol);
        when(agent.getContentManager()).thenReturn(contentManager);
        when(contentManager.extractContent(aclMessage)).thenThrow(CodecException.class);

        assertThat(session.extractMessage(aclMessage, Logon.class), nullValue());
    }

    @Test
    public void testExtractMessageOntologyException() throws CodecException, OntologyException {
        final ACLMessage aclMessage = mock(ACLMessage.class);
        final Agent agent = mock(Agent.class);
        final ContentManager contentManager = mock(ContentManager.class);
        final Session session = new DefaultSession(agent, mock(AID.class), mock(Application.class), defaultSymbol);
        when(agent.getContentManager()).thenReturn(contentManager);
        when(contentManager.extractContent(aclMessage)).thenThrow(OntologyException.class);

        assertThat(session.extractMessage(aclMessage, Logon.class), nullValue());
    }

    @Test
    public void testExtractMessageOntologyNotAction() throws CodecException, OntologyException {
        final ACLMessage aclMessage = mock(ACLMessage.class);
        final Agent agent = mock(Agent.class);
        final ContentManager contentManager = mock(ContentManager.class);
        final Session session = new DefaultSession(agent, mock(AID.class), mock(Application.class), defaultSymbol);
        when(agent.getContentManager()).thenReturn(contentManager);
        when(contentManager.extractContent(aclMessage)).thenReturn(mock(ContentElement.class));

        assertThat(session.extractMessage(aclMessage, Logon.class), nullValue());
    }

    @Test
    public void testExtractMessageOntologyNullGetAction() throws CodecException, OntologyException {
        final ACLMessage aclMessage = mock(ACLMessage.class);
        final Agent agent = mock(Agent.class);
        final ContentManager contentManager = mock(ContentManager.class);
        final Session session = new DefaultSession(agent, mock(AID.class), mock(Application.class), defaultSymbol);
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
        final Session session = new DefaultSession(agent, mock(AID.class), mock(Application.class), defaultSymbol);
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
        final Session session = new DefaultSession(agent, mock(AID.class), mock(Application.class), defaultSymbol);
        when(agent.getContentManager()).thenReturn(contentManager);
        when(contentManager.extractContent(aclMessage)).thenReturn(action);
        when(action.getAction()).thenReturn(logon);

        assertThat(session.extractMessage(aclMessage, logon.getClass()), sameInstance(logon));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testAclRequestNullMessage() {
        final Session session = new DefaultSession(mock(Agent.class), mock(AID.class), mock(Application.class),
                                                   defaultSymbol);
        session.aclRequest(null);
    }

    @Test
    public void testAclRequest() throws CodecException, OntologyException {
        final Message message = mock(Message.class);
        final AID to = mock(AID.class);
        final Agent agent = mock(Agent.class);
        final ContentManager contentManager = mock(ContentManager.class);
        final Session session = new DefaultSession(agent, to, mock(Application.class), defaultSymbol);
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
        final Session session = new DefaultSession(agent, mock(AID.class), mock(Application.class), defaultSymbol);
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
        final Session session = new DefaultSession(agent, mock(AID.class), mock(Application.class), defaultSymbol);
        when(agent.getContentManager()).thenReturn(contentManager);
        doThrow(new OntologyException("")).when(contentManager).fillContent(Mockito.any(ACLMessage.class),
                                                                            Mockito.any(Action.class));

        session.aclRequest(message);
    }

    @Test
    public void testSendNewOrderSingle() throws CodecException, OntologyException {
        final AID to = mock(AID.class);
        final Agent agent = mock(Agent.class);
        final ContentManager contentManager = mock(ContentManager.class);
        final Session session = new DefaultSession(agent, to, mock(Application.class), defaultSymbol);
        when(agent.getContentManager()).thenReturn(contentManager);

        final NewOrderSingle newOrderSingle = mock(NewOrderSingle.class);
        session.send(newOrderSingle);

        verify(agent).getContentManager();
        verify(agent).send(Mockito.any(ACLMessage.class));
    }

    @ObjectFactory
    public IObjectFactory getObjectFactory() {
        return new PowerMockObjectFactory();
    }
}
