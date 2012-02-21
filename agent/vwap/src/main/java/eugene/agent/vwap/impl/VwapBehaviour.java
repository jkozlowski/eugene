package eugene.agent.vwap.impl;

import com.google.common.collect.PeekingIterator;
import eugene.agent.vwap.VwapExecution;
import eugene.market.book.Order;
import eugene.market.book.OrderBook;
import eugene.market.client.OrderReference;
import eugene.market.client.OrderReferenceListenerAdapter;
import eugene.market.client.OrderReferenceListenerProxy;
import eugene.market.client.Session;
import eugene.market.ontology.field.ClOrdID;
import eugene.market.ontology.field.OrderQty;
import eugene.market.ontology.field.enums.OrdType;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.NewOrderSingle;
import eugene.market.ontology.message.OrderCancelReject;
import eugene.market.ontology.message.OrderCancelRequest;
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
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterators.peekingIterator;
import static eugene.market.client.Messages.newLimit;
import static eugene.market.client.OrderReferenceListeners.proxy;

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

    private static final String DECISION_STATE = "decision-state";

    private static final String SEND_LIMIT_STATE = "send-limit-state"; // Sends a limit order.

    private static final String CANCEL_LIMIT_STATE = "cancel-limit-state"; // Cancels previously created limit order.

    private static final String SEND_MARKET_STATE = "send-market-state"; // Sends a market order.

    private static final String END_STATE = "end-state";

    private static final String[] STATES = new String[]{
            DECISION_STATE, SEND_LIMIT_STATE, CANCEL_LIMIT_STATE, SEND_MARKET_STATE, END_STATE
    };

    private static final int GOTO_DECISION_STATE = 1;

    private static final int GOTO_SEND_LIMIT_STATE = 2;

    private static final int GOTO_SEND_MARKET_STATE = 3;

    private static final int GOTO_CANCEL_LIMIT_STATE = 4;

    private static final int GOTO_END_STATE = 6;

    private OrderReference currentOrder = null;

    private final Calendar deadline;

    private final VwapExecution vwapExecution;

    private final OrderBook orderBook;

    private final Session session;

    /**
     * Default constructor.
     *
     * @param deadline      deadline for completion of the execution.
     * @param vwapExecution execution to perform.
     * @param orderBook     {@link OrderBook} to check for current prices.
     * @param session       current session.
     *
     * @throws NullPointerException if any parameter is null.
     */
    public VwapBehaviour(final Calendar deadline, final VwapExecution vwapExecution, final OrderBook orderBook,
                         final Session session) {

        this.deadline = (Calendar) checkNotNull(deadline).clone();
        this.vwapExecution = checkNotNull(vwapExecution);
        this.orderBook = checkNotNull(orderBook);
        this.session = checkNotNull(session);
    }

    @Override
    public void onStart() {
        final VwapStatus vwapStatus = new VwapStatus(deadline, vwapExecution);
        final OrderReferenceListenerProxy proxy = proxy(vwapStatus);
        LOG.info("Start trading: {}", vwapStatus);

//        final long LIMIT_AGE_THRESHOLD = vwapStatus.getBucketSize().divideToIntegralValue(valueOf(2L)).longValue();
        final long LIMIT_AGE_THRESHOLD = 3000;
        LOG.info("LIMIT_AGE_THRESHOLD={}", LIMIT_AGE_THRESHOLD);

        final PeekingIterator<VwapBucket> bucketIterator = peekingIterator(vwapStatus.getVwapBuckets().iterator());

        registerFirstState(new CyclicFinishableBehaviour(new Task() {

            @Override
            public void action(final FinishableBehaviour b, final Agent agent) {

                final Calendar now = Calendar.getInstance();
                final Long nowInMillis = now.getTimeInMillis();

                final VwapBucket bucket = bucketIterator.peek();

                // We are before the deadline, but we've traded the allocated volume.
                if (now.getTime().before(bucket.getDeadline()) &&
                        bucket.getCumVolume().compareTo(vwapStatus.getCumVolume()) == 0) {

                    if (null != currentOrder) {
                        checkState(currentOrder.getOrdStatus().isFilled());
                        currentOrder = null;
                    }
                    return;
                }

                // We are before the deadline, we don't have a working order and we've not traded the allocated
                // volume.
                if (now.getTime().before(bucket.getDeadline()) &&
                        bucket.getCumVolume().compareTo(vwapStatus.getCumVolume()) != 0 &&
                        null == currentOrder) {

                    final long difference = bucketIterator.peek().getDeadline().getTime() - nowInMillis;

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
                        null != currentOrder &&
                        nowInMillis - currentOrder.getCreationTime() > LIMIT_AGE_THRESHOLD &&
                        // Could have the error here as well
                        currentOrder.getPrice().compareTo(orderBook.peek(currentOrder.getSide()).getPrice()) != 0) {

                    LOG.info("Decided to cancel a limit order");
                    b.finish(GOTO_CANCEL_LIMIT_STATE);
                    return;
                }

                // We passed the deadline.
                if (now.getTime().after(bucket.getDeadline())) {
                    bucketIterator.next();
                    if (bucketIterator.hasNext()) {
                        LOG.info("Next bucket: {}", bucketIterator.peek());
                    }
                    else {
                        b.finish(GOTO_END_STATE);
                    }
                }
            }
        }), DECISION_STATE);

        registerState(new OneShotFinishableBehaviour(new Task() {

            @Override
            public void action(final FinishableBehaviour b, final Agent agent) {
                checkState(null == currentOrder);

                final NewOrderSingle newOrderSingle = new NewOrderSingle();
                newOrderSingle.setSide(vwapExecution.getSide().field());
                newOrderSingle.setOrderQty(new OrderQty(
                        bucketIterator.peek().getCumVolume() - vwapStatus.getCumVolume()));
                newOrderSingle.setOrdType(OrdType.MARKET.field());

                proxy.addListener(new OrderReferenceListenerAdapter() {

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
                        currentOrder = null;
                        proxy.removeListener(this);
                    }

                    @Override
                    public void rejectedEvent(ExecutionReport executionReport, OrderReference orderReference,
                                              Session session) {
                        b.finish(GOTO_DECISION_STATE);
                        LOG.info("Rejected: {}", orderReference);
                        currentOrder = null;
                        proxy.removeListener(this);
                    }
                });

                currentOrder = session.send(newOrderSingle, proxy);
                LOG.info("Sent: {}", currentOrder);
            }
        }), SEND_MARKET_STATE);

        registerState(new OneShotFinishableBehaviour(new Task() {
            @Override
            public void action(final FinishableBehaviour b, final Agent agent) {
                checkState(null == currentOrder);

                final Order topOfBook = orderBook.peek(vwapExecution.getSide());

                if (null == topOfBook) {
                    b.finish(GOTO_DECISION_STATE);
                    LOG.info("No price");
                    return;
                }

                final Long ordQty = bucketIterator.peek().getCumVolume() - vwapStatus.getCumVolume();
                final NewOrderSingle newOrderSingle = newLimit(vwapExecution.getSide(), topOfBook.getPrice(), ordQty);

                proxy.addListener(new OrderReferenceListenerAdapter() {
                    @Override
                    public void newEvent(ExecutionReport executionReport, OrderReference orderReference,
                                         Session session) {
                        b.finish(GOTO_DECISION_STATE);
                        LOG.info("Accepted: {}", orderReference);
                        proxy.removeListener(this);
                    }

                    @Override
                    public void rejectedEvent(ExecutionReport executionReport, OrderReference orderReference,
                                              Session session) {
                        b.finish(GOTO_DECISION_STATE);
                        LOG.info("Rejected: {}", orderReference);
                        proxy.removeListener(this);
                    }
                });

                currentOrder = session.send(newOrderSingle, proxy);
                checkNotNull(currentOrder);
                LOG.info("Sent: {}", currentOrder);
            }
        }), SEND_LIMIT_STATE);

        registerState(new OneShotFinishableBehaviour(new Task() {
            @Override
            public void action(final FinishableBehaviour b, final Agent agent) {

                checkNotNull(currentOrder);
                checkState(currentOrder.getOrdType().isLimit());
                final OrderCancelRequest cancelRequest = new OrderCancelRequest();
                cancelRequest.setClOrdID(new ClOrdID(currentOrder.getClOrdID()));
                cancelRequest.setOrderQty(new OrderQty(currentOrder.getOrderQty()));
                cancelRequest.setSide(currentOrder.getSide().field());

                proxy.addListener(new OrderReferenceListenerAdapter() {

                    @Override
                    public void cancelRejectedEvent(OrderCancelReject orderCancelReject, OrderReference orderReference,
                                                    Session session) {
                        b.finish(GOTO_DECISION_STATE);
                        LOG.info("Cancel Rejected: {}, {}", orderCancelReject, orderReference);
                        proxy.removeListener(this);
                    }

                    @Override
                    public void canceledEvent(ExecutionReport executionReport, OrderReference orderReference,
                                              Session session) {
                        b.finish(GOTO_DECISION_STATE);
                        LOG.info("Canceled: {}", orderReference);
                        currentOrder = null;
                        proxy.removeListener(this);
                    }
                });

                session.send(cancelRequest);
                LOG.info("Cancelling: {}", currentOrder);
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

    @Override
    protected void handleStateEntered(Behaviour b) {
        LOG.info("Entered: '{}'", currentName);
    }
}
