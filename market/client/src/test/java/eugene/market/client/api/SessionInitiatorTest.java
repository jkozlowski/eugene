package eugene.market.client.api;

import eugene.market.client.api.impl.behaviour.BehaviourResult;
import eugene.market.client.api.impl.behaviour.LogonBehaviour;
import jade.content.ContentManager;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAException;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockObjectFactory;
import org.testng.IObjectFactory;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;

import static eugene.market.ontology.Defaults.defaultSymbol;
import static jade.domain.DFService.searchUntilFound;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * Tests {@link SessionInitiator}.
 *
 * @author Jakub D Kozlowski
 * @since 0.4
 */
@PrepareForTest({Agent.class, DFService.class, DFAgentDescription.class})
public class SessionInitiatorTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullAgent() {
        new SessionInitiator(null, mock(Application.class), defaultSymbol);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullApplication() {
        new SessionInitiator(mock(Agent.class), null, defaultSymbol);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullSymbol() {
        new SessionInitiator(mock(Agent.class), mock(Application.class), null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorEmptySymbol() {
        new SessionInitiator(mock(Agent.class), mock(Application.class), "");
    }

    @Test
    public void testActionMarketAgentFound() throws FIPAException, CodecException, OntologyException {
        mockStatic(DFService.class);

        final AID marketAgentAID = mock(AID.class);
        final DFAgentDescription agentDescription = mock(DFAgentDescription.class);
        when(agentDescription.getName()).thenReturn(marketAgentAID);

        final AID dfAID = mock(AID.class);
        final Agent agent = mock(Agent.class);
        when(agent.getDefaultDF()).thenReturn(dfAID);
        final ContentManager contentManager = mock(ContentManager.class);
        when(agent.getContentManager()).thenReturn(contentManager);

        when(searchUntilFound(any(Agent.class), any(AID.class), any(DFAgentDescription.class),
                              any(SearchConstraints.class), anyInt()))
        .thenReturn(new DFAgentDescription[]{ agentDescription });

        final SessionInitiator initiator = new SessionInitiator(agent, mock(Application.class), defaultSymbol);
        initiator.action();
        
        verify(agent).addBehaviour(any(LogonBehaviour.class));
        assertThat(initiator.onEnd(), is(BehaviourResult.SUCCESS));
    }

    @Test
    public void testActionMarketAgentNotFound() throws FIPAException {
        mockStatic(DFService.class);

        final AID dfAID = mock(AID.class);
        final Agent agent = mock(Agent.class);
        when(agent.getDefaultDF()).thenReturn(dfAID);

        when(searchUntilFound(any(Agent.class), any(AID.class), any(DFAgentDescription.class),
                              any(SearchConstraints.class), anyInt()))
                .thenReturn(new DFAgentDescription[] { } );

        final SessionInitiator initiator = new SessionInitiator(agent, mock(Application.class), defaultSymbol);
        initiator.action();

        verify(agent, never()).addBehaviour(any(LogonBehaviour.class));
        assertThat(initiator.onEnd(), is(BehaviourResult.FAILURE));
    }

    @Test
    public void testActionMarketAgentNotFoundNull() throws FIPAException {
        mockStatic(DFService.class);

        final AID dfAID = mock(AID.class);
        final Agent agent = mock(Agent.class);
        when(agent.getDefaultDF()).thenReturn(dfAID);

        when(searchUntilFound(any(Agent.class), any(AID.class), any(DFAgentDescription.class),
                              any(SearchConstraints.class), anyInt()))
                .thenReturn(null);

        final SessionInitiator initiator = new SessionInitiator(agent, mock(Application.class), defaultSymbol);
        initiator.action();

        verify(agent, never()).addBehaviour(any(LogonBehaviour.class));
        assertThat(initiator.onEnd(), is(BehaviourResult.FAILURE));
    }

    @ObjectFactory
    public IObjectFactory getObjectFactory() {
        return new PowerMockObjectFactory();
    }
}
