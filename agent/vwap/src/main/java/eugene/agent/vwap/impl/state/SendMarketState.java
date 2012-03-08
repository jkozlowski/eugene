/*
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */

package eugene.agent.vwap.impl.state;

import eugene.agent.vwap.impl.VwapStatus;
import eugene.market.client.OrderReference;
import eugene.market.client.OrderReferenceListenerAdapter;
import eugene.market.client.OrderReferenceListenerProxy;
import eugene.market.client.Session;
import eugene.market.client.TopOfBookApplication;
import eugene.market.ontology.field.enums.OrdType;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.NewOrderSingle;
import eugene.utils.behaviour.OneShotFinishableBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkState;
import static eugene.agent.vwap.impl.VwapBehaviour.GOTO_DECISION_STATE;
import static eugene.market.client.Messages.newMarket;

/**
 * Sends a new {@link OrdType#MARKET} order.
 *
 * @author Jakub D Kozlowski
 * @since 0.8
 */
public final class SendMarketState implements State {

    private static final Logger LOG = LoggerFactory.getLogger(SendMarketState.class);

    @Override
    public void enter(final Session session, final TopOfBookApplication topOfBook, final OneShotFinishableBehaviour b,
                      final VwapStatus vwapStatus, final OrderReferenceListenerProxy proxy) {

        checkState(!vwapStatus.hasOrder());

        final Long orderQty = vwapStatus.getCurrentBucket().peek().getCumVolume() - vwapStatus.getCumVolume();
        final NewOrderSingle newOrderSingle = newMarket(vwapStatus.getVwapExecution().getSide(), orderQty);

        proxy.addListener(new FinishListener(b, proxy));

        final OrderReference newOrder = session.send(newOrderSingle, proxy);
        LOG.info("Sent: {}", newOrder);
    }

    private static final class FinishListener extends OrderReferenceListenerAdapter {

        private final OneShotFinishableBehaviour b;

        private final OrderReferenceListenerProxy proxy;

        public FinishListener(final OneShotFinishableBehaviour b, final OrderReferenceListenerProxy proxy) {
            this.b = b;
            this.proxy = proxy;
        }

        @Override
        public void newEvent(ExecutionReport executionReport, OrderReference orderReference,
                             Session session) {

            b.finish(GOTO_DECISION_STATE);
            LOG.info("Accepted: {}", orderReference);
            proxy.removeListener(this);
        }

        @Override
        public void canceledEvent(ExecutionReport executionReport, OrderReference orderReference,
                                  Session session) {

            b.finish(GOTO_DECISION_STATE);
            LOG.info("Canceled: {}", orderReference);
            proxy.removeListener(this);
        }

        @Override
        public void rejectedEvent(ExecutionReport executionReport, OrderReference orderReference,
                                  Session session) {
            b.finish(GOTO_DECISION_STATE);
            LOG.info("Rejected: {}", orderReference);
            proxy.removeListener(this);
        }
    }
}
