package eugene.market.client.api;

import jade.core.AID;
import jade.core.Agent;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockObjectFactory;
import org.testng.IObjectFactory;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;

import static eugene.market.ontology.Defaults.defaultSymbol;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.powermock.api.mockito.PowerMockito.mock;

/**
 * Tests {@link Session}.
 *
 * @author Jakub D Kozlowski
 * @since 0.4
 */
@PrepareForTest({Agent.class, AID.class})
public class SessionTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullAgent() {
        new Session(null, mock(AID.class), mock(Application.class), defaultSymbol);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullMarketAgent() {
        new Session(mock(Agent.class), null, mock(Application.class), defaultSymbol);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullApplication() {
        new Session(mock(Agent.class), mock(AID.class), null, defaultSymbol);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullSymbol() {
        new Session(mock(Agent.class), mock(AID.class), mock(Application.class), null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorEmptySymbol() {
        new Session(mock(Agent.class), mock(AID.class), mock(Application.class), "");
    }

    @Test
    public void testConstructor() {
        final Agent agent = mock(Agent.class);
        final AID marketAgent = mock(AID.class);
        final Application application = mock(Application.class);
        final Session session = new Session(agent, marketAgent, application, defaultSymbol);

        assertThat(session.getMarketAgent(), sameInstance(marketAgent));
        assertThat(session.getApplication(), sameInstance(application));
        assertThat(session.getSymbol(), is(defaultSymbol));
    }

    @ObjectFactory
    public IObjectFactory getObjectFactory() {
        return new PowerMockObjectFactory();
    }
}
