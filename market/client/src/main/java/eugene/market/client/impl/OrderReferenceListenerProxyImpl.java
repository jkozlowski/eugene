package eugene.market.client.impl;

import com.google.common.base.Function;
import eugene.market.client.OrderReference;
import eugene.market.client.OrderReferenceListener;
import eugene.market.client.OrderReferenceListenerProxy;
import eugene.market.client.Session;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.OrderCancelReject;

import java.util.concurrent.CopyOnWriteArrayList;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Default implementation of {@link OrderReferenceListenerProxy}.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public final class OrderReferenceListenerProxyImpl implements OrderReferenceListenerProxy {

    private final CopyOnWriteArrayList<OrderReferenceListener> listeners;

    /**
     * Creates a {@link OrderReferenceListenerProxyImpl} that will proxy events to these <code>listeners</code>.
     *
     * @param listeners {@link OrderReferenceListener}s to proxy events to.
     */
    public OrderReferenceListenerProxyImpl(final OrderReferenceListener... listeners) {
        checkNotNull(listeners);
        checkArgument(listeners.length > 0);
        this.listeners = new CopyOnWriteArrayList<OrderReferenceListener>(listeners);
    }

    @Override
    public void newEvent(final ExecutionReport executionReport, final OrderReference orderReference,
                         final Session session) {
        forAll(new Function<OrderReferenceListener, Void>() {
            @Override
            public Void apply(final OrderReferenceListener listener) {
                listener.newEvent(executionReport, orderReference, session);
                return null;
            }
        });
    }

    @Override
    public void tradeEvent(final ExecutionReport executionReport, final OrderReference orderReference,
                           final Session session) {
        forAll(new Function<OrderReferenceListener, Void>() {
            @Override
            public Void apply(final OrderReferenceListener listener) {
                listener.tradeEvent(executionReport, orderReference, session);
                return null;
            }
        });
    }

    @Override
    public void canceledEvent(final ExecutionReport executionReport, final OrderReference orderReference,
                              final Session session) {
        forAll(new Function<OrderReferenceListener, Void>() {
            @Override
            public Void apply(final OrderReferenceListener listener) {
                listener.canceledEvent(executionReport, orderReference, session);
                return null;
            }
        });
    }

    @Override
    public void rejectedEvent(final ExecutionReport executionReport, final OrderReference orderReference,
                              final Session session) {
        forAll(new Function<OrderReferenceListener, Void>() {
            @Override
            public Void apply(final OrderReferenceListener listener) {
                listener.rejectedEvent(executionReport, orderReference, session);
                return null;
            }
        });
    }

    @Override
    public void cancelRejectedEvent(final OrderCancelReject orderCancelReject, final OrderReference orderReference,
                                    final Session session) {
        forAll(new Function<OrderReferenceListener, Void>() {
            @Override
            public Void apply(final OrderReferenceListener listener) {
                listener.cancelRejectedEvent(orderCancelReject, orderReference, session);
                return null;
            }
        });
    }

    private void forAll(final Function<OrderReferenceListener, Void> function) {
        for (final OrderReferenceListener listener : listeners) {
            function.apply(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addListener(OrderReferenceListener listener) {
        return listeners.addIfAbsent(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeListener(OrderReferenceListener listener) {
        return listeners.remove(listener);
    }
}
