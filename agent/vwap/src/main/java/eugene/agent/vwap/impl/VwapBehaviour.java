/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.agent.vwap.impl;

import eugene.agent.vwap.VwapExecution;
import eugene.agent.vwap.impl.state.CancelLimitState;
import eugene.agent.vwap.impl.state.SendMarketState;
import eugene.agent.vwap.impl.state.State;
import eugene.market.client.OrderReference;
import eugene.market.client.OrderReferenceListenerProxy;
import eugene.market.client.Session;
import eugene.market.client.TopOfBookApplication;
import eugene.market.ontology.field.enums.Side;
import eugene.utils.behaviour.CyclicFinishableBehaviour;
import eugene.utils.behaviour.FinishableBehaviour;
import eugene.utils.behaviour.OneShotFinishableBehaviour;
import eugene.utils.behaviour.Task;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

import static com.google.common.base.Preconditions.checkNotNull;
import static eugene.market.client.OrderReferenceListeners.proxy;
import static eugene.market.client.TopOfBookApplication.ReturnDefaultPrice.NO;

/**
 * Implements the VWAP algorithm.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public final class VwapBehaviour extends FSMBehaviour {

    private static final Logger LOG = LoggerFactory.getLogger(VwapBehaviour.class);

    /**
     * Time threshold when we should send a market order for remaining quantity.
     */
    private static final long MARKET_THRESHOLD = 2000L;

    public static final String DECISION_STATE = "decision-state";

    public static final String SEND_LIMIT_STATE = "send-limit-state"; // Sends a limit order.

    public static final String CANCEL_LIMIT_STATE = "cancel-limit-state"; // Cancels previously created limit order.

    public static final String SEND_MARKET_STATE = "send-market-state"; // Sends a market order.

    public static final String END_STATE = "end-state";

    private static final String[] STATES = new String[]{
            DECISION_STATE, SEND_LIMIT_STATE, CANCEL_LIMIT_STATE, SEND_MARKET_STATE, END_STATE
    };

    public static final int GOTO_DECISION_STATE = 1;

    public static final int GOTO_SEND_LIMIT_STATE = 2;

    public static final int GOTO_SEND_MARKET_STATE = 3;

    public static final int GOTO_CANCEL_LIMIT_STATE = 4;

    public static final int GOTO_END_STATE = 6;

    private final Calendar deadline;

    private final VwapExecution vwapExecution;
    
    private final State sendLimitState;

    private final TopOfBookApplication topOfBook;

    private final Session session;

    final long LIMIT_AGE_THRESHOLD = 3000;

    /**
     * Default constructor.
     *
     * @param deadline      deadline for completion of the execution.
     * @param vwapExecution execution to perform.
     * @param topOfBook     {@link TopOfBookApplication} to check for current prices.
     * @param session       current session.
     *
     * @throws NullPointerException if any parameter is null.
     */
    public VwapBehaviour(final Calendar deadline, final VwapExecution vwapExecution,
                         final State sendLimitState, final TopOfBookApplication topOfBook, final Session session) {

        this.deadline = (Calendar) checkNotNull(deadline).clone();
        this.vwapExecution = checkNotNull(vwapExecution);
        this.sendLimitState = checkNotNull(sendLimitState);
        this.topOfBook = checkNotNull(topOfBook);
        this.session = checkNotNull(session);
    }

    @Override
    public void onStart() {

        final VwapStatus vwapStatus = new VwapStatus(deadline, vwapExecution);
        final OrderReferenceListenerProxy proxy = proxy(vwapStatus);
        LOG.info("Start trading: {}", vwapStatus);
        LOG.info("LIMIT_AGE_THRESHOLD={}", LIMIT_AGE_THRESHOLD);

        final State cancelLimitState = new CancelLimitState();
        final State sendMarketState = new SendMarketState();

        registerFirstState(new CyclicFinishableBehaviour(new Task() {

            @Override
            public void action(final FinishableBehaviour b, final Agent agent) {
                decisionState(b, vwapStatus);
            }
        }), DECISION_STATE);

        registerState(new OneShotFinishableBehaviour(new Task<OneShotFinishableBehaviour>() {

            @Override
            public void action(final OneShotFinishableBehaviour b, final Agent agent) {
                sendMarketState.enter(session, topOfBook, b, vwapStatus, proxy);
            }
        }), SEND_MARKET_STATE);

        registerState(new OneShotFinishableBehaviour(new Task<OneShotFinishableBehaviour>() {
            @Override
            public void action(final OneShotFinishableBehaviour b, final Agent agent) {
                sendLimitState.enter(session, topOfBook, b, vwapStatus, proxy);
            }
        }), SEND_LIMIT_STATE);

        registerState(new OneShotFinishableBehaviour(new Task<OneShotFinishableBehaviour>() {
            @Override
            public void action(final OneShotFinishableBehaviour b, final Agent agent) {
                cancelLimitState.enter(session, topOfBook, b, vwapStatus, proxy);
            }
        }), CANCEL_LIMIT_STATE);

        registerLastState(new OneShotBehaviour() {
            @Override
            public void action() {
                LOG.info("Finished: {}", vwapStatus);
            }
        }, END_STATE);

        registerTransition(DECISION_STATE, SEND_LIMIT_STATE, GOTO_SEND_LIMIT_STATE, STATES);
        registerTransition(DECISION_STATE, SEND_MARKET_STATE, GOTO_SEND_MARKET_STATE, STATES);
        registerTransition(DECISION_STATE, CANCEL_LIMIT_STATE, GOTO_CANCEL_LIMIT_STATE, STATES);
        registerTransition(DECISION_STATE, END_STATE, GOTO_END_STATE, STATES);

        registerDefaultTransition(SEND_LIMIT_STATE, DECISION_STATE);
        registerDefaultTransition(SEND_MARKET_STATE, DECISION_STATE);
        registerDefaultTransition(CANCEL_LIMIT_STATE, DECISION_STATE);
    }

    /**
     * Decides whether to send a limit order, cancel existing order, send a market order or do nothing.
     *
     * @param b
     * @param vwapStatus
     */
    private void decisionState(final FinishableBehaviour b, VwapStatus vwapStatus) {

        final Calendar now = Calendar.getInstance();
        final Long nowInMillis = now.getTimeInMillis();

        final VwapBucket bucket = vwapStatus.getCurrentBucket().peek();
        final Side side = vwapStatus.getVwapExecution().getSide();
        final OrderReference curOrder = vwapStatus.getCurrentOrder();

        // We are before the deadline, but we've traded the allocated volume.
        if (now.getTime().before(bucket.getDeadline()) &&
            bucket.getCumVolume().compareTo(vwapStatus.getCumVolume()) == 0) {

            return;
        }

        // We are before the deadline, we don't have a working order and we've not traded the allocated
        // volume.
        if (now.getTime().before(bucket.getDeadline()) &&
                bucket.getCumVolume().compareTo(vwapStatus.getCumVolume()) != 0 &&
                !vwapStatus.hasOrder()) {

            final long difference = bucket.getDeadline().getTime() - nowInMillis;

            // If we're behind the MARKET_THRESHOLD we should really send a market order to keep up with the
            // volume.
            if (difference < MARKET_THRESHOLD) {
                LOG.info("Decided to send a market order");
                b.finish(GOTO_SEND_MARKET_STATE);
                return;
            }
            else {
                b.finish(GOTO_SEND_LIMIT_STATE);
                LOG.info("Decided to send a limit order");
                return;
            }
        }

        // We are before the deadline and we have a limit order working, but it's not filled and it's already
        // older than LIMIT_AGE_THRESHOLD and it's price is not the best price.
        if (now.getTime().before(bucket.getDeadline()) &&
                bucket.getCumVolume().compareTo(vwapStatus.getCumVolume()) != 0 &&
                vwapStatus.hasOrder() &&
                nowInMillis - vwapStatus.getCurrentOrder().getCreationTime() > LIMIT_AGE_THRESHOLD &&
                // Could have the error here as well
                curOrder.getPrice().compareTo(topOfBook.getLastPrice(side, NO).getPrice()) != 0) {

            LOG.info("Decided to cancel a limit order");
            b.finish(GOTO_CANCEL_LIMIT_STATE);
            return;
        }

        // We passed the deadline.
        if (now.getTime().after(bucket.getDeadline())) {
            vwapStatus.getCurrentBucket().next();
            if (vwapStatus.getCurrentBucket().hasNext()) {
                LOG.info("Next bucket: {}", vwapStatus.getCurrentBucket().peek());
            }
            else {
                b.finish(GOTO_END_STATE);
            }
        }
    }

    @Override
    protected void handleStateEntered(Behaviour b) {
        LOG.info("Entered: '{}'", currentName);
    }
}
