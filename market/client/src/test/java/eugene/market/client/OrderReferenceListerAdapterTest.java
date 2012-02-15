package eugene.market.client;

import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.OrderCancelReject;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;

/**
 * Tests {@link OrderReferenceListenerAdapter}.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public class OrderReferenceListerAdapterTest {

    private static class TestOrderReferenceListener extends OrderReferenceListenerAdapter {
    }

    @Test
    public void testCoverage() {
        final OrderReferenceListener listener = new TestOrderReferenceListener();
        listener.newEvent(mock(ExecutionReport.class), mock(OrderReference.class), mock(Session.class));
        listener.canceledEvent(mock(ExecutionReport.class), mock(OrderReference.class), mock(Session.class));
        listener.rejectedEvent(mock(ExecutionReport.class), mock(OrderReference.class), mock(Session.class));
        listener.tradeEvent(mock(ExecutionReport.class), mock(OrderReference.class), mock(Session.class));
        listener.cancelRejectedEvent(mock(OrderCancelReject.class), mock(OrderReference.class), mock(Session.class));
    }
}
