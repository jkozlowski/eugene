package eugene.agent.vwap.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Defines a quantity that should be traded by a deadline.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public final class VwapBucket implements Comparable<VwapBucket> {

    private final Long cumVolume;

    private final Calendar deadline;

    private final int hashCode;

    private static final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss:SSS");

    /**
     * Default constructor.
     *
     * @param cumVolume amount of shares that should be traded by the <code>deadline</code>.
     * @param deadline  deadline for trading the shares.
     *
     * @throws NullPointerException     if either argument is null.
     * @throws IllegalArgumentException if <code>cumVolume <= 0</code>.
     */
    public VwapBucket(final Long cumVolume, final Calendar deadline) {
        checkNotNull(cumVolume);
        checkArgument(cumVolume > 0);
        checkNotNull(deadline);

        this.cumVolume = cumVolume;
        this.deadline = (Calendar) deadline.clone();

        hashCode = deadline != null ? deadline.hashCode() : 0;
    }

    /**
     * Gets the cumVolume.
     *
     * @return the cumVolume.
     */
    public Long getCumVolume() {
        return cumVolume;
    }

    /**
     * Gets the deadline.
     *
     * @return a new instance of {@link Date} that contains the deadline.
     */
    public Date getDeadline() {
        return deadline.getTime();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(VwapBucket bucket) {
        checkNotNull(bucket);
        return deadline.compareTo(bucket.deadline);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final VwapBucket that = (VwapBucket) o;

        if (!cumVolume.equals(that.cumVolume)) return false;
        if (!deadline.equals(that.deadline)) return false;

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return hashCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("VwapBucket");
        sb.append("[cumVolume=").append(cumVolume);
        sb.append(", deadline=").append(format.format(deadline.getTime()));
        sb.append(']');
        return sb.toString();
    }
}
