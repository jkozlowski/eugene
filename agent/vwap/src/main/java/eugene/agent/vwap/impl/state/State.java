/*
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */

package eugene.agent.vwap.impl.state;

import eugene.agent.vwap.impl.VwapBehaviour;
import eugene.agent.vwap.impl.VwapStatus;
import eugene.market.client.OrderReferenceListener;
import eugene.market.client.OrderReferenceListenerProxy;
import eugene.market.client.Session;
import eugene.market.client.TopOfBookApplication;
import eugene.market.ontology.message.NewOrderSingle;
import eugene.utils.behaviour.FinishableBehaviour;
import eugene.utils.behaviour.OneShotFinishableBehaviour;

/**
 * Represents a state that a {@link VwapBehaviour} can be in.
 *
 * @author Jakub D Kozlowski
 * @since 0.8
 */
public interface State {

    /**
     * Enters the state.
     *
     * @param session    {@link Session} to use to send orders and cancellations.
     * @param b          {@link FinishableBehaviour} executing this {@link State}.
     * @param vwapStatus status of execution.
     * @param proxy      proxy that needs to be added as an {@link OrderReferenceListener} when sending orders using
     *                   {@link Session#send(NewOrderSingle, OrderReferenceListener)}.
     */
    void enter(final Session session, final TopOfBookApplication topOfBook,
               final OneShotFinishableBehaviour b, final VwapStatus vwapStatus,
               final OrderReferenceListenerProxy proxy);
}
