package eugene.market.client.api;

import eugene.market.client.api.impl.behaviour.BehaviourResult;
import eugene.market.esma.AbstractMarketAgentTest;
import jade.util.Event;
import jade.wrapper.AgentController;
import org.testng.annotations.Test;

import static eugene.market.ontology.Defaults.defaultSymbol;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.powermock.api.mockito.PowerMockito.mock;

/**
 * Tests {@link SessionInitiator} inside a JADE container.
 *
 * @author Jakub D Kozlowski
 * @since 0.4
 */
public class SessionInitiatorIntegrationTest extends AbstractMarketAgentTest {

    @Test
    public void testSessionInitiator() throws Exception {
        final SessionInitiator sessionInitiator = new SessionInitiator(traderAgent, mock(Application.class),
                                                                       defaultSymbol);
        final Event event = new Event(-1, sessionInitiator);
        gatewayAgentController.putO2AObject(event, AgentController.ASYNC);
        event.waitUntilProcessed();

        assertThat(sessionInitiator.onEnd(), is(BehaviourResult.SUCCESS));
    }
}
