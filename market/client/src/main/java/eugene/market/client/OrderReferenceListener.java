package eugene.market.client;

import eugene.market.ontology.field.enums.ExecType;
import eugene.market.ontology.field.enums.OrdStatus;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.OrderCancelReject;
import eugene.market.ontology.message.OrderCancelRequest;

/**
 * Interface for receiving updates on a status of an {@link OrderReference}.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public interface OrderReferenceListener {

    /**
     * Noop {@link OrderReferenceListener}.
     */
    OrderReferenceListener EMPTY_LISTENER = new OrderReferenceListenerAdapter() {
    };

    /**
     * Indicates that an {@link ExecType#NEW} event has been received.
     *
     * @param executionReport original message.
     * @param orderReference  {@link OrderReference} that the message refers to.
     * @param session         active {@link Session}.
     */
    void newEvent(final ExecutionReport executionReport, final OrderReference orderReference, final Session session);

    /**
     * Indicates that an {@link ExecType#TRADE} event has been received. If {@link OrdStatus#FILLED},
     * then no more updates will be received after this message.
     *
     * @param executionReport original message.
     * @param orderReference  {@link OrderReference} that the message refers to.
     * @param session         active {@link Session}.
     */
    void tradeEvent(final ExecutionReport executionReport, final OrderReference orderReference, final Session session);

    /**
     * Indicates that an {@link ExecType#CANCELED} event has been received. No more updates will be received after
     * this message.
     *
     * @param executionReport original message.
     * @param orderReference  {@link OrderReference} that the message refers to.
     * @param session         active {@link Session}.
     */
    void canceledEvent(final ExecutionReport executionReport, final OrderReference orderReference,
                       final Session session);

    /**
     * Indicates that an {@link ExecType#REJECTED} event has been received. No more updates will be received after
     * this message.
     *
     * @param executionReport original message.
     * @param orderReference  {@link OrderReference} that the message refers to.
     * @param session         active {@link Session}.
     */
    void rejectedEvent(final ExecutionReport executionReport, final OrderReference orderReference,
                       final Session session);

    /**
     * Indicates that an {@link OrderCancelRequest} has been rejected.
     *
     * @param orderCancelReject original message.
     * @param orderReference    {@link OrderReference} that the message refers to.
     * @param session           active {@link Session}.
     */
    void cancelRejectedEvent(final OrderCancelReject orderCancelReject, final OrderReference orderReference,
                             final Session session);
}
