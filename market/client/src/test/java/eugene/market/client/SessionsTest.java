package eugene.market.client;

import eugene.market.client.impl.SessionInitiator;
import jade.core.Agent;
import org.testng.annotations.Test;

import static eugene.market.client.Sessions.initate;
import static eugene.market.ontology.Defaults.defaultSymbol;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.powermock.api.mockito.PowerMockito.mock;

/**
 * Tests {@link Sessions}.
 *
 * @author Jakub D Kozlowski
 * @since 0.5
 */
public class SessionsTest {

    @Test
    public void testInitiateCoverage() {
        assertThat(initate(mock(Agent.class), mock(Application.class), defaultSymbol), is(SessionInitiator.class));
    }
}
