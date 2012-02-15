package eugene.market.client.impl;

import eugene.market.client.OrderReference;
import eugene.market.client.OrderReferenceListener;
import eugene.market.client.OrderReferenceListenerProxy;
import eugene.market.client.Session;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.OrderCancelReject;
import org.testng.annotations.Test;

import static eugene.market.client.OrderReferenceListeners.proxy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.verifyNoMoreInteractions;

/**
 * Tests {@link OrderReferenceListenerProxyImpl}.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public class OrderReferenceListenerProxyImplTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullListeners() {
        new OrderReferenceListenerProxyImpl(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorEmptyListeners() {
        new OrderReferenceListenerProxyImpl(new OrderReferenceListener[]{});
    }

    @Test
    public void testNewEvent() {
        final OrderReferenceListener listener1 = mock(OrderReferenceListener.class);
        final OrderReferenceListener listener2 = mock(OrderReferenceListener.class);
        final Session session = mock(Session.class);
        final OrderReference ref = mock(OrderReference.class);
        final ExecutionReport executionReport = new ExecutionReport();
        final OrderReferenceListener proxy = proxy(listener1, listener2);

        proxy.newEvent(executionReport, ref, session);

        verify(listener1).newEvent(executionReport, ref, session);
        verify(listener2).newEvent(executionReport, ref, session);
        verifyNoMoreInteractions(listener1, listener2);
    }

    @Test
    public void testTradeEvent() {
        final OrderReferenceListener listener1 = mock(OrderReferenceListener.class);
        final OrderReferenceListener listener2 = mock(OrderReferenceListener.class);
        final Session session = mock(Session.class);
        final OrderReference ref = mock(OrderReference.class);
        final ExecutionReport executionReport = new ExecutionReport();
        final OrderReferenceListener proxy = proxy(listener1, listener2);

        proxy.tradeEvent(executionReport, ref, session);

        verify(listener1).tradeEvent(executionReport, ref, session);
        verify(listener2).tradeEvent(executionReport, ref, session);
        verifyNoMoreInteractions(listener1, listener2);
    }

    @Test
    public void testCanceledEvent() {
        final OrderReferenceListener listener1 = mock(OrderReferenceListener.class);
        final OrderReferenceListener listener2 = mock(OrderReferenceListener.class);
        final Session session = mock(Session.class);
        final OrderReference ref = mock(OrderReference.class);
        final ExecutionReport executionReport = new ExecutionReport();
        final OrderReferenceListener proxy = proxy(listener1, listener2);

        proxy.canceledEvent(executionReport, ref, session);

        verify(listener1).canceledEvent(executionReport, ref, session);
        verify(listener2).canceledEvent(executionReport, ref, session);
        verifyNoMoreInteractions(listener1, listener2);
    }

    @Test
    public void testCancelRejectedEvent() {
        final OrderReferenceListener listener1 = mock(OrderReferenceListener.class);
        final OrderReferenceListener listener2 = mock(OrderReferenceListener.class);
        final Session session = mock(Session.class);
        final OrderReference ref = mock(OrderReference.class);
        final OrderCancelReject cancelReject = new OrderCancelReject();
        final OrderReferenceListener proxy = proxy(listener1, listener2);

        proxy.cancelRejectedEvent(cancelReject, ref, session);

        verify(listener1).cancelRejectedEvent(cancelReject, ref, session);
        verify(listener2).cancelRejectedEvent(cancelReject, ref, session);
        verifyNoMoreInteractions(listener1, listener2);
    }

    @Test
    public void testRejectedEvent() {
        final OrderReferenceListener listener1 = mock(OrderReferenceListener.class);
        final OrderReferenceListener listener2 = mock(OrderReferenceListener.class);
        final Session session = mock(Session.class);
        final OrderReference ref = mock(OrderReference.class);
        final ExecutionReport executionReport = new ExecutionReport();
        final OrderReferenceListener proxy = proxy(listener1, listener2);

        proxy.rejectedEvent(executionReport, ref, session);

        verify(listener1).rejectedEvent(executionReport, ref, session);
        verify(listener2).rejectedEvent(executionReport, ref, session);
        verifyNoMoreInteractions(listener1, listener2);
    }
    
    @Test
    public void testAddListener() {
        final OrderReferenceListener listener1 = mock(OrderReferenceListener.class);
        final OrderReferenceListener listener2 = mock(OrderReferenceListener.class);
        final Session session = mock(Session.class);
        final OrderReference ref = mock(OrderReference.class);
        final ExecutionReport executionReport = new ExecutionReport();
        final OrderReferenceListenerProxy proxy = proxy(listener1);

        proxy.rejectedEvent(executionReport, ref, session);
        assertThat(proxy.addListener(listener2), is(true));
        proxy.rejectedEvent(executionReport, ref, session);

        verify(listener1, times(2)).rejectedEvent(executionReport, ref, session);
        verify(listener2).rejectedEvent(executionReport, ref, session);

        verifyNoMoreInteractions(listener1, listener2);
    }

    @Test
    public void testRemoveListener() {
        final OrderReferenceListener listener1 = mock(OrderReferenceListener.class);
        final OrderReferenceListener listener2 = mock(OrderReferenceListener.class);
        final Session session = mock(Session.class);
        final OrderReference ref = mock(OrderReference.class);
        final ExecutionReport executionReport = new ExecutionReport();
        final OrderReferenceListenerProxy proxy = proxy(listener1, listener2);

        proxy.rejectedEvent(executionReport, ref, session);
        assertThat(proxy.removeListener(listener2), is(true));
        proxy.rejectedEvent(executionReport, ref, session);

        verify(listener1, times(2)).rejectedEvent(executionReport, ref, session);
        verify(listener2).rejectedEvent(executionReport, ref, session);

        verifyNoMoreInteractions(listener1, listener2);
    }
}
