package eugene.market.client.impl;

import com.google.common.annotations.VisibleForTesting;
import eugene.market.client.ApplicationAdapter;
import eugene.market.client.OrderReference;
import eugene.market.client.Session;
import eugene.market.ontology.field.enums.OrdStatus;
import eugene.market.ontology.message.ExecutionReport;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Updates currently active {@link OrderReference}s.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public final class OrderReferenceApplication extends ApplicationAdapter {

    private final ConcurrentMap<String, OrderReferenceImpl> orderReferenceMap;

    /**
     * Default constructor.
     */
    public OrderReferenceApplication() {
        this.orderReferenceMap = new ConcurrentHashMap<String, OrderReferenceImpl>();
    }

    /**
     * Add this <code>orderReference</code> to be tracked.
     *
     * @param orderReference {@link OrderReferenceImpl} to add.
     *
     * @throws NullPointerException if <code>orderReference</code> is null.
     */
    public void addOrderReference(final OrderReferenceImpl orderReference) throws NullPointerException {
        checkNotNull(orderReference);
        orderReferenceMap.putIfAbsent(orderReference.getClOrdID(), orderReference);
    }

    @Override
    public void toApp(final ExecutionReport executionReport, final Session session) {

        switch (OrdStatus.getOrdStatus(executionReport)) {

            case REJECTED:
                getOrderReference(executionReport).reject();
                orderReferenceMap.remove(executionReport.getClOrdID().getValue());
                break;

            case CANCELED:
                getOrderReference(executionReport).cancel();
                orderReferenceMap.remove(executionReport.getClOrdID().getValue());
                break;

            case PARTIALLY_FILLED:
            case FILLED:
                getOrderReference(executionReport).execute(executionReport.getLastPx().getValue(),
                                                           executionReport.getLastQty().getValue());
                if (getOrderReference(executionReport).getOrdStatus().isFilled()) {
                    orderReferenceMap.remove(executionReport.getClOrdID().getValue());
                }
                break;
        }
    }

    @VisibleForTesting
    public OrderReferenceImpl getOrderReference(final ExecutionReport executionReport) {

        checkNotNull(executionReport);
        checkNotNull(executionReport.getClOrdID());
        checkNotNull(executionReport.getClOrdID().getValue());

        final OrderReferenceImpl orderReference = orderReferenceMap.get(executionReport.getClOrdID().getValue());
        checkNotNull(orderReference);

        return orderReference;
    }
}
