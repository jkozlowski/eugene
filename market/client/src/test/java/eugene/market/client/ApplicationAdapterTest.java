package eugene.market.client;

import eugene.market.client.impl.DefaultSession;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.Logon;
import eugene.market.ontology.message.OrderCancelReject;
import eugene.market.ontology.message.data.AddOrder;
import eugene.market.ontology.message.data.DeleteOrder;
import eugene.market.ontology.message.data.OrderExecuted;
import jade.core.Agent;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockObjectFactory;
import org.testng.IObjectFactory;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;

/**
 * Tests {@link ApplicationAdapter}.
 *
 * @author Jakub D Kozlowski
 * @since 0.5
 */
@PrepareForTest({Agent.class, DefaultSession.class})
public class ApplicationAdapterTest {

    private static class TestApplicationAdapter extends ApplicationAdapter {
    }

    @Test
    public void testCoverage() {
        final Application application = new TestApplicationAdapter();
        application.onLogon(mock(Logon.class), mock(Agent.class), mock(DefaultSession.class));
        application.toApp(mock(ExecutionReport.class), mock(DefaultSession.class));
        application.toApp(mock(DeleteOrder.class), mock(DefaultSession.class));
        application.toApp(mock(AddOrder.class), mock(DefaultSession.class));
        application.toApp(mock(OrderExecuted.class), mock(DefaultSession.class));
        application.toApp(mock(OrderCancelReject.class), mock(DefaultSession.class));
    }

    @ObjectFactory
    public IObjectFactory getObjectFactory() {
        return new PowerMockObjectFactory();
    }
}
