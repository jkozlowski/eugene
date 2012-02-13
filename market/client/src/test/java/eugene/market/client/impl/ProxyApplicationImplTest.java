package eugene.market.client.impl;

import eugene.market.client.Application;
import eugene.market.client.ProxyApplication;
import eugene.market.client.Session;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.NewOrderSingle;
import eugene.market.ontology.message.OrderCancelReject;
import eugene.market.ontology.message.OrderCancelRequest;
import eugene.market.ontology.message.data.AddOrder;
import eugene.market.ontology.message.data.DeleteOrder;
import eugene.market.ontology.message.data.OrderExecuted;
import eugene.simulation.ontology.Start;
import eugene.simulation.ontology.Stop;
import jade.core.Agent;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.verifyNoMoreInteractions;

/**
 * Tests {@link ProxyApplicationImpl}.
 * 
 * @author Jakub D Kozlowski
 * @since 0.5
 */
public class ProxyApplicationImplTest {
    
    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullApplications() {
        new ProxyApplicationImpl(null);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorEmptyApplications() {
        new ProxyApplicationImpl(new Application[] {});
    }
    
    @Test
    public void testOnStart() {
        final Application application1 = mock(Application.class);
        final Application application2 = mock(Application.class);
        final Session session = mock(Session.class);
        final Agent agent = mock(Agent.class);
        final Start start = new Start();
        final ProxyApplication proxy = new ProxyApplicationImpl(application1, application2);

        proxy.onStart(start, agent, session);

        verify(application1).onStart(start, agent, session);
        verify(application2).onStart(start, agent, session);
        verifyNoMoreInteractions(application1, application2);
    }

    @Test
    public void testOnStop() {
        final Application application1 = mock(Application.class);
        final Application application2 = mock(Application.class);
        final Session session = mock(Session.class);
        final Agent agent = mock(Agent.class);
        final Stop start = new Stop();
        final ProxyApplication proxy = new ProxyApplicationImpl(application1, application2);

        proxy.onStop(start, agent, session);

        verify(application1).onStop(start, agent, session);
        verify(application2).onStop(start, agent, session);
        verifyNoMoreInteractions(application1, application2);
    }

    @Test
    public void testToAppExecutionReport() {
        final Application application1 = mock(Application.class);
        final Application application2 = mock(Application.class);
        final Session session = mock(Session.class);
        final ExecutionReport executionReport = new ExecutionReport();
        final ProxyApplication proxy = new ProxyApplicationImpl(application1, application2);

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
        final ProxyApplication proxy = new ProxyApplicationImpl(application1, application2);

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
        final ProxyApplication proxy = new ProxyApplicationImpl(application1, application2);

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
        final ProxyApplication proxy = new ProxyApplicationImpl(application1, application2);

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
        final ProxyApplication proxy = new ProxyApplicationImpl(application1, application2);

        proxy.toApp(orderExecuted, session);

        verify(application1).toApp(orderExecuted, session);
        verify(application2).toApp(orderExecuted, session);
        verifyNoMoreInteractions(application1, application2);
    }

    @Test
    public void testFromAppNewOrderSingle() {
        final Application application1 = mock(Application.class);
        final Application application2 = mock(Application.class);
        final Session session = mock(Session.class);
        final NewOrderSingle newOrderSingle = new NewOrderSingle();
        final ProxyApplication proxy = new ProxyApplicationImpl(application1, application2);

        proxy.fromApp(newOrderSingle, session);

        verify(application1).fromApp(newOrderSingle, session);
        verify(application2).fromApp(newOrderSingle, session);
        verifyNoMoreInteractions(application1, application2);
    }

    @Test
    public void testFromAppOrderCancelRequest() {
        final Application application1 = mock(Application.class);
        final Application application2 = mock(Application.class);
        final Session session = mock(Session.class);
        final OrderCancelRequest orderCancelRequest = new OrderCancelRequest();
        final ProxyApplication proxy = new ProxyApplicationImpl(application1, application2);

        proxy.fromApp(orderCancelRequest, session);

        verify(application1).fromApp(orderCancelRequest, session);
        verify(application2).fromApp(orderCancelRequest, session);
        verifyNoMoreInteractions(application1, application2);
    }
    
    @Test
    public void testAddApplicationNewApplication() {
        final Application application1 = mock(Application.class);
        final Application application2 = mock(Application.class);
        final Session session = mock(Session.class);
        final Agent agent = mock(Agent.class);
        final Start start = new Start();
        final ProxyApplication proxy = new ProxyApplicationImpl(application1);
        assertThat(proxy.addApplication(application2), is(true));

        proxy.onStart(start, agent, session);

        verify(application1).onStart(start, agent, session);
        verify(application2).onStart(start, agent, session);
        verifyNoMoreInteractions(application1, application2);
    }

    @Test
    public void testAddApplicationExistingApplication() {
        final Application application1 = mock(Application.class);
        final Application application2 = mock(Application.class);
        final Session session = mock(Session.class);
        final Agent agent = mock(Agent.class);
        final Start start = new Start();
        final ProxyApplication proxy = new ProxyApplicationImpl(application1);
        assertThat(proxy.addApplication(application2), is(true));
        assertThat(proxy.addApplication(application2), is(false));

        proxy.onStart(start, agent, session);

        verify(application1).onStart(start, agent, session);
        verify(application2).onStart(start, agent, session);
        verifyNoMoreInteractions(application1, application2);
    }

    @Test
    public void testRemoveApplicationNewApplication() {
        final Application application1 = mock(Application.class);
        final Application application2 = mock(Application.class);
        final Session session = mock(Session.class);
        final Agent agent = mock(Agent.class);
        final Start start = new Start();
        final ProxyApplication proxy = new ProxyApplicationImpl(application1, application2);
        assertThat(proxy.removeApplication(application2), is(true));

        proxy.onStart(start, agent, session);

        verify(application1).onStart(start, agent, session);
        verifyZeroInteractions(application2);
        verifyNoMoreInteractions(application1);
    }

    @Test
    public void testRemoveApplicationExistingApplication() {
        final Application application1 = mock(Application.class);
        final Application application2 = mock(Application.class);
        final Session session = mock(Session.class);
        final Agent agent = mock(Agent.class);
        final Start start = new Start();
        final ProxyApplication proxy = new ProxyApplicationImpl(application1, application2);
        assertThat(proxy.removeApplication(application2), is(true));
        assertThat(proxy.removeApplication(application2), is(false));

        proxy.onStart(start, agent, session);

        verify(application1).onStart(start, agent, session);
        verifyZeroInteractions(application2);
        verifyNoMoreInteractions(application1);
    }
}
