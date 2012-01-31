package eugene.integration;

import eugene.market.client.ApplicationAdapter;
import eugene.market.client.Session;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.OrderCancelReject;
import eugene.market.ontology.message.data.AddOrder;
import eugene.market.ontology.message.data.DeleteOrder;
import eugene.market.ontology.message.data.OrderExecuted;

import java.util.concurrent.CountDownLatch;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Counts down on a {@link CountDownLatch} whenever a message is received.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public final class CountingApplication extends ApplicationAdapter {
    
    public static final int TIMEOUT = 100000;

    private final CountDownLatch latch;

    public CountingApplication(final CountDownLatch latch) {
        checkNotNull(latch);
        this.latch = latch;
    }

    @Override
    public void toApp(final ExecutionReport executionReport, final Session session) {
        latch.countDown();
    }

    @Override
    public void toApp(final OrderCancelReject orderCancelReject, final Session session) {
        latch.countDown();
    }

    @Override
    public void toApp(final AddOrder addOrder, final Session session) {
        latch.countDown();
    }

    @Override
    public void toApp(final DeleteOrder deleteOrder, final Session session) {
        latch.countDown();
    }

    @Override
    public void toApp(final OrderExecuted orderExecuted, final Session session) {
        latch.countDown();
    }
}
