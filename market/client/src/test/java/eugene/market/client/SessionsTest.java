package eugene.market.client;

import eugene.market.client.impl.SessionInitiator;
import eugene.simulation.agent.Simulation;
import org.testng.annotations.Test;

import static eugene.market.client.Sessions.initiate;
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

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testConstructor() {
        new Sessions();
    }

    @Test
    public void testInitiateCoverage() {
        assertThat(initiate(mock(Application.class), mock(Simulation.class)), is(SessionInitiator.class));
    }
}
