/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.client;

import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.OrderCancelReject;

/**
 * Adapter for {@link OrderReferenceListener}.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public abstract class OrderReferenceListenerAdapter implements OrderReferenceListener {

    /**
     * {@inheritDoc}
     */
    @Override
    public void createdEvent(final OrderReference orderReference) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void newEvent(final ExecutionReport executionReport, final OrderReference orderReference,
                         final Session session) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tradeEvent(final ExecutionReport executionReport, final OrderReference orderReference,
                           final Session session) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void canceledEvent(final ExecutionReport executionReport, final OrderReference orderReference,
                              final Session session) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rejectedEvent(final ExecutionReport executionReport, final OrderReference orderReference,
                              final Session session) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancelRejectedEvent(final OrderCancelReject orderCancelReject, final OrderReference orderReference,
                                    final Session session) {
    }
}
