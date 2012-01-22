package eugene.market.client.impl;

import eugene.market.client.Application;
import eugene.market.esma.AbstractMarketAgentTest;
import eugene.market.ontology.message.Logon;
import jade.core.AID;
import jade.util.Event;
import jade.wrapper.AgentController;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import static eugene.market.ontology.Defaults.defaultSymbol;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;

/**
 * Tests {@link LogonBehaviour} inside a JADE container.
 *
 * @author Jakub D Kozlowski
 * @since 0.4
 */
public class LogonBehaviourIntegrationTest extends AbstractMarketAgentTest {

    @Test
    public void testLogonBehaviour() throws Exception {
        final AID marketAgent = new AID(MARKET_AGENT, AID.ISLOCALNAME);
        final Application application = mock(Application.class);
        final DefaultSession session = new DefaultSession(traderAgent, marketAgent, application, defaultSymbol);
        final LogonBehaviour logonBehaviour = new LogonBehaviour(traderAgent, session);
        final Event event = new Event(-1, logonBehaviour);
        gatewayAgentController.putO2AObject(event, AgentController.ASYNC);
        event.waitUntilProcessed();

        assertThat(logonBehaviour.onEnd(), is(BehaviourResult.SUCCESS));
        verify(application).onLogon(Mockito.any(Logon.class), Mockito.eq(traderAgent), Mockito.eq(session));
    }
}
