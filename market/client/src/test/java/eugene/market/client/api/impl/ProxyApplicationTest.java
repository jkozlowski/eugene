package eugene.market.client.api.impl;

import eugene.market.client.api.Application;
import eugene.market.client.api.Session;
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

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.verifyNoMoreInteractions;

/**
 * Tests {@link ProxyApplication}.
 * 
 * @author Jakub D Kozlowski
 * @since 0.5
 */
@PrepareForTest({Agent.class, Session.class})
public class ProxyApplicationTest {
    
    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullApplications() {
        new ProxyApplication(null);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorEmptyApplications() {
        new ProxyApplication(new Application[] {});
    }
    
    @Test
    public void testOnLogon() {
        final Application application1 = mock(Application.class);
        final Application application2 = mock(Application.class);
        final Session session = mock(Session.class);
        final Agent agent = mock(Agent.class);
        final Logon logon = new Logon();
        final ProxyApplication proxy = new ProxyApplication(application1, application2);

        proxy.onLogon(logon, agent, session);

        verify(application1).onLogon(logon, agent, session);
        verify(application2).onLogon(logon, agent, session);
        verifyNoMoreInteractions(application1, application2);
    }

    @Test
    public void testToAppExecutionReport() {
        final Application application1 = mock(Application.class);
        final Application application2 = mock(Application.class);
        final Session session = mock(Session.class);
        final ExecutionReport executionReport = new ExecutionReport();
        final ProxyApplication proxy = new ProxyApplication(application1, application2);

        proxy.toApp(executionReport, session);

        verify(application1).toApp(executionReport, session);
        verify(application2).toApp(executionReport, session);
        verifyNoMoreInteractions(application1, application2);
    }

    @Test
    public void testToAppOrderCancelReject() {
        final Application application1 = mock(Application.class);
        final Application application2 = mock(Application.class);
        final Session session = mock(Session.class);
        final OrderCancelReject orderCancelReject = new OrderCancelReject();
        final ProxyApplication proxy = new ProxyApplication(application1, application2);

        proxy.toApp(orderCancelReject, session);

        verify(application1).toApp(orderCancelReject, session);
        verify(application2).toApp(orderCancelReject, session);
        verifyNoMoreInteractions(application1, application2);
    }
    
    @Test
    public void testToAppAddOrder() {
        final Application application1 = mock(Application.class);
        final Application application2 = mock(Application.class);
        final Session session = mock(Session.class);
        final AddOrder addOrder = new AddOrder();
        final ProxyApplication proxy = new ProxyApplication(application1, application2);

        proxy.toApp(addOrder, session);

        verify(application1).toApp(addOrder, session);
        verify(application2).toApp(addOrder, session);
        verifyNoMoreInteractions(application1, application2);    
    }

    @Test
    public void testToAppDeleteOrder() {
        final Application application1 = mock(Application.class);
        final Application application2 = mock(Application.class);
        final Session session = mock(Session.class);
        final DeleteOrder deleteOrder = new DeleteOrder();
        final ProxyApplication proxy = new ProxyApplication(application1, application2);

        proxy.toApp(deleteOrder, session);

        verify(application1).toApp(deleteOrder, session);
        verify(application2).toApp(deleteOrder, session);
        verifyNoMoreInteractions(application1, application2);
    }

    @Test
    public void testToAppOrderExecuted() {
        final Application application1 = mock(Application.class);
        final Application application2 = mock(Application.class);
        final Session session = mock(Session.class);
        final OrderExecuted orderExecuted = new OrderExecuted();
        final ProxyApplication proxy = new ProxyApplication(application1, application2);

        proxy.toApp(orderExecuted, session);

        verify(application1).toApp(orderExecuted, session);
        verify(application2).toApp(orderExecuted, session);
        verifyNoMoreInteractions(application1, application2);
    }

    @ObjectFactory
    public IObjectFactory getObjectFactory() {
        return new PowerMockObjectFactory();
    }
}
