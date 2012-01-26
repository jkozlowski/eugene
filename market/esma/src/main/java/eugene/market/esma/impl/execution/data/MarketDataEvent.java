package eugene.market.esma.impl.execution.data;

import eugene.market.book.Order;
import eugene.market.book.OrderBook;
import eugene.market.book.OrderStatus;
import eugene.market.esma.impl.execution.Execution;
import eugene.market.esma.impl.execution.ExecutionEngine;
import eugene.market.ontology.field.enums.OrdType;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Indicates that an operation has been applied to the {@link OrderBook}.
 */
public abstract class MarketDataEvent<T> {

    private final Long eventId;

    private final Long time;

    private final T object;

    /**
     * Constructs a {@link MarketDataEvent} with this <code>time</code> and <code>eventId</code>.
     *
     * @param eventId id of this event.
     * @param time    time to use.
     * @param object  object of this {@link MarketDataEvent}.
     */
    public MarketDataEvent(final Long eventId, final Long time, final T object) {
        checkNotNull(eventId);
        checkNotNull(time);
        checkNotNull(object);
        this.eventId = eventId;
        this.time = time;
        this.object = object;
    }

    /**
     * Gets the eventId.
     *
     * @return the eventId.
     */
    public Long getEventId() {
        return eventId;
    }

    /**
     * Gets the time.
     *
     * @return the time.
     */
    public Long getTime() {
        return time;
    }

    /**
     * Gets the object.
     *
     * @return the object.
     */
    public T getObject() {
        return object;
    }

    /**
     * Accepts this <code>handler</code>.
     * 
     * @param handler handler to accept.
     */
    public abstract void accept(final MarketDataEventHandler handler);

    /**
     * Indicates that a new {@link OrdType#LIMIT} {@link Order} has been inserted into the {@link OrderBook}.
     *
     * @author Jakub D Kozlowski
     * @since 0.6
     */
    public static final class AddOrderEvent extends MarketDataEvent<OrderStatus> {

        /**
         * {@inheritDoc}
         */
        public AddOrderEvent(final Long eventId, final OrderStatus orderStatus) {
            this(eventId, System.currentTimeMillis(), orderStatus);
            checkArgument(orderStatus.getOrder().getOrdType().isLimit());
        }

        /**
         * {@inheritDoc}
         */
        public AddOrderEvent(final Long eventId, final Long time, final OrderStatus orderStatus) {
            super(eventId, time, orderStatus);
        }

        @Override
        public void accept(final MarketDataEventHandler handler) {
            handler.handle(this);
        }
    }

    /**
     * Indicates that an {@link Order} has been cancelled.
     *
     * @author Jakub D Kozlowski
     * @since 0.3
     */
    public static class CancelOrderEvent extends MarketDataEvent<OrderStatus> {

        /**
         * {@inheritDoc}
         */
        public CancelOrderEvent(final Long eventId, final OrderStatus orderStatus) {
            this(eventId, System.currentTimeMillis(), orderStatus);
        }

        /**
         * {@inheritDoc}
         */
        public CancelOrderEvent(final Long eventId, final Long time, final OrderStatus orderStatus) {
            super(eventId, time, orderStatus);
        }

        @Override
        public void accept(final MarketDataEventHandler handler) {
            handler.handle(this);
        }
    }

    /**
     * Indicates that a pair of {@link Order}s have been executed.
     */
    public static final class ExecutionEvent extends MarketDataEvent<Execution> {

        /**
         * {@inheritDoc}
         */
        public ExecutionEvent(final Long eventId, final Execution execution) {
            this(eventId, System.currentTimeMillis(), execution);
        }

        /**
         * {@inheritDoc}
         */
        public ExecutionEvent(final Long eventId, final Long time, final Execution execution) {
            super(eventId, time, execution);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(MarketDataEventHandler handler) {
            handler.handle(this);
        }
    }

    /**
     * Indicates that a new {@link Order} has been accepted by {@link ExecutionEngine}.
     */
    public static final class NewOrderEvent extends MarketDataEvent<Order> {

        /**
         * {@inheritDoc}
         */
        public NewOrderEvent(final Long eventId, final Order order) {
            this(eventId, System.currentTimeMillis(), order);
        }

        /**
         * {@inheritDoc}
         */
        public NewOrderEvent(final Long eventId, final Long time, final Order order) {
            super(eventId, time, order);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(MarketDataEventHandler handler) {
            handler.handle(this);
        }
    }

    /**
     * Indicates that a {@link OrdType#MARKET} has been rejected by the {@link ExecutionEngine}.
     *
     * @author Jakub D Kozlowski
     * @since 0.6
     */
    public static final class RejectOrderEvent extends MarketDataEvent<Order> {

        /**
         * {@inheritDoc}
         */
        public RejectOrderEvent(final Long eventId, final Order order) {
            this(eventId, System.currentTimeMillis(), order);
        }

        /**
         * {@inheritDoc}
         */
        public RejectOrderEvent(final Long eventId, final Long time, final Order order) {
            super(eventId, time, order);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(MarketDataEventHandler handler) {
            handler.handle(this);
        }
    }
}
