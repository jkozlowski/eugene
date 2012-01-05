package eugene.market.esma.execution.data;

import eugene.market.book.OrderBook;

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
}
