/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.client.impl;

import com.google.common.annotations.VisibleForTesting;
import eugene.market.client.ApplicationAdapter;
import eugene.market.client.OrderReference;
import eugene.market.client.Session;
import eugene.market.ontology.field.ClOrdID;
import eugene.market.ontology.field.enums.OrdStatus;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.OrderCancelReject;

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

        final OrderReferenceImpl ref = getOrderReference(executionReport.getClOrdID());

        switch (OrdStatus.getOrdStatus(executionReport)) {

            case NEW:
                ref.getOrderReferenceListener().newEvent(executionReport, ref, session);
                break;

            case REJECTED:
                ref.reject();
                ref.getOrderReferenceListener().rejectedEvent(executionReport, ref, session);
                orderReferenceMap.remove(executionReport.getClOrdID().getValue());
                break;

            case CANCELED:
                ref.cancel();
                ref.getOrderReferenceListener().canceledEvent(executionReport, ref, session);
                orderReferenceMap.remove(executionReport.getClOrdID().getValue());
                break;

            case PARTIALLY_FILLED:
            case FILLED:
                ref.execute(executionReport.getLastPx().getValue(), executionReport.getLastQty().getValue());
                ref.getOrderReferenceListener().tradeEvent(executionReport, ref, session);
                if (ref.getOrdStatus().isFilled()) {
                    orderReferenceMap.remove(executionReport.getClOrdID().getValue());
                }
                break;
        }
    }

    @Override
    public void toApp(final OrderCancelReject orderCancelReject, final Session session) {
        final OrderReferenceImpl ref = getOrderReference(orderCancelReject.getClOrdID());
        ref.getOrderReferenceListener().cancelRejectedEvent(orderCancelReject, ref, session);
    }

    @VisibleForTesting
    OrderReferenceImpl getOrderReference(final ClOrdID clOrdID) {

        checkNotNull(clOrdID);
        checkNotNull(clOrdID.getValue());

        return orderReferenceMap.get(clOrdID.getValue());
    }
}
