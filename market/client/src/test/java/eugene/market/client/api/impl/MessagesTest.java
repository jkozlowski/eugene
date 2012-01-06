package eugene.market.client.api.impl;

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

import static eugene.market.client.api.impl.Messages.aclRequest;
import static eugene.market.client.api.impl.Messages.extractMessage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Tests {@link Messages}.
 *
 * @author Jakub D Kozlowski
 * @since 0.4
 */
@PrepareForTest({Agent.class, AID.class, ContentManager.class})
public class MessagesTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void testAclRequestNullAgent() {
        aclRequest(null, mock(AID.class), mock(Message.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testAclRequestNullTo() {
        aclRequest(mock(Agent.class), null, mock(Message.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testAclRequestNullMessage() {
        aclRequest(mock(Agent.class), mock(AID.class), null);
    }

    @Test
    public void testAclRequest() throws CodecException, OntologyException {
        final Message message = mock(Message.class);
        final AID to = mock(AID.class);
        final Agent agent = mock(Agent.class);
        final ContentManager contentManager = mock(ContentManager.class);
        when(agent.getContentManager()).thenReturn(contentManager);

        final ACLMessage aclMessage = aclRequest(agent, to, message);

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
        final AID to = mock(AID.class);
        final Agent agent = mock(Agent.class);
        final ContentManager contentManager = mock(ContentManager.class);
        when(agent.getContentManager()).thenReturn(contentManager);
        doThrow(new CodecException("")).when(contentManager).fillContent(Mockito.any(ACLMessage.class),
                                                                         Mockito.any(Action.class));

        aclRequest(agent, to, message);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testAclRequestThrowsOntologyException() throws CodecException, OntologyException {
        final Message message = mock(Message.class);
        final AID to = mock(AID.class);
        final Agent agent = mock(Agent.class);
        final ContentManager contentManager = mock(ContentManager.class);
        when(agent.getContentManager()).thenReturn(contentManager);
        doThrow(new OntologyException("")).when(contentManager).fillContent(Mockito.any(ACLMessage.class),
                                                                            Mockito.any(Action.class));

        aclRequest(agent, to, message);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testExtractMessageNullAgent() {
        extractMessage(null, mock(ACLMessage.class), Logon.class);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testExtractMessageNullAclMessage() {
        extractMessage(mock(Agent.class), null, Logon.class);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testExtractMessageNullClazz() {
        extractMessage(mock(Agent.class), mock(ACLMessage.class), null);
    }

    @Test
    public void testExtractMessageCodecException() throws CodecException, OntologyException {
        final ACLMessage aclMessage = mock(ACLMessage.class);
        final Agent agent = mock(Agent.class);
        final ContentManager contentManager = mock(ContentManager.class);
        when(agent.getContentManager()).thenReturn(contentManager);
        when(contentManager.extractContent(aclMessage)).thenThrow(CodecException.class);

        assertThat(extractMessage(agent, aclMessage, Logon.class), nullValue());
    }

    @Test
    public void testExtractMessageOntologyException() throws CodecException, OntologyException {
        final ACLMessage aclMessage = mock(ACLMessage.class);
        final Agent agent = mock(Agent.class);
        final ContentManager contentManager = mock(ContentManager.class);
        when(agent.getContentManager()).thenReturn(contentManager);
        when(contentManager.extractContent(aclMessage)).thenThrow(OntologyException.class);

        assertThat(extractMessage(agent, aclMessage, Logon.class), nullValue());
    }

    @Test
    public void testExtractMessageOntologyNotAction() throws CodecException, OntologyException {
        final ACLMessage aclMessage = mock(ACLMessage.class);
        final Agent agent = mock(Agent.class);
        final ContentManager contentManager = mock(ContentManager.class);
        when(agent.getContentManager()).thenReturn(contentManager);
        when(contentManager.extractContent(aclMessage)).thenReturn(mock(ContentElement.class));

        assertThat(extractMessage(agent, aclMessage, Logon.class), nullValue());
    }

    @Test
    public void testExtractMessageOntologyNullGetAction() throws CodecException, OntologyException {
        final ACLMessage aclMessage = mock(ACLMessage.class);
        final Agent agent = mock(Agent.class);
        final ContentManager contentManager = mock(ContentManager.class);
        when(agent.getContentManager()).thenReturn(contentManager);
        when(contentManager.extractContent(aclMessage)).thenReturn(mock(Action.class));

        assertThat(extractMessage(agent, aclMessage, Logon.class), nullValue());
    }

    @Test
    public void testExtractMessageOntologyWrongClazz() throws CodecException, OntologyException {
        final ACLMessage aclMessage = mock(ACLMessage.class);
        final Agent agent = mock(Agent.class);
        final ContentManager contentManager = mock(ContentManager.class);
        final Action action = mock(Action.class);
        when(agent.getContentManager()).thenReturn(contentManager);
        when(contentManager.extractContent(aclMessage)).thenReturn(action);
        when(action.getAction()).thenReturn(mock(NewOrderSingle.class));

        assertThat(extractMessage(agent, aclMessage, Logon.class), nullValue());
    }

    @Test
    public void testExtractMessageOntology() throws CodecException, OntologyException {
        final ACLMessage aclMessage = mock(ACLMessage.class);
        final Agent agent = mock(Agent.class);
        final ContentManager contentManager = mock(ContentManager.class);
        final Action action = mock(Action.class);
        final Logon logon = mock(Logon.class);
        when(agent.getContentManager()).thenReturn(contentManager);
        when(contentManager.extractContent(aclMessage)).thenReturn(action);
        when(action.getAction()).thenReturn(logon);

        assertThat(extractMessage(agent, aclMessage, logon.getClass()), sameInstance(logon));
    }

    @ObjectFactory
    public IObjectFactory getObjectFactory() {
        return new PowerMockObjectFactory();
    }
}
