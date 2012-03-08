/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.agent.vwap.impl;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.PeekingIterator;
import eugene.agent.vwap.VwapExecution;
import eugene.market.book.Order;
import eugene.market.client.OrderReference;
import eugene.market.client.OrderReferenceListenerAdapter;
import eugene.market.client.Session;
import eugene.market.ontology.message.ExecutionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterators.peekingIterator;
import static com.google.common.collect.Sets.newTreeSet;
import static eugene.market.client.OrderReference.NO_ORDER;
import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static java.util.Collections.unmodifiableSet;

/**
 * Tracks the status of execution of a {@link VwapExecution}.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public final class VwapStatus extends OrderReferenceListenerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(VwapStatus.class);

    private static final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss:SSS");

    /**
     * Precision of computation.
     */
    public static final int SCALE = 5;

    private final VwapExecution vwapExecution;

    private final Calendar deadline;

    private final BigDecimal bucketSize;

    private final Set<VwapBucket> buckets;

    private final PeekingIterator<VwapBucket> currentBucket;

    private Long cumVolume;

    private OrderReference currentOrder;

    /**
     * Default constructor.
     *
     * @param deadline      deadline for execution.
     * @param vwapExecution trade to execute.
     */
    public VwapStatus(final Calendar deadline, final VwapExecution vwapExecution) {
        checkNotNull(deadline);
        final Calendar now = Calendar.getInstance();
        checkArgument(deadline.after(now));
        checkNotNull(vwapExecution);
        this.deadline = (Calendar) deadline.clone();
        this.vwapExecution = vwapExecution;
        this.bucketSize = getBucketSize(now, deadline, vwapExecution);
        this.buckets = unmodifiableSet(getBuckets(now, deadline, vwapExecution));
        this.currentBucket = peekingIterator(this.buckets.iterator());
        this.cumVolume = 0L;
        this.currentOrder = NO_ORDER;
    }

    /**
     * Gets the vwapExecution.
     *
     * @return the vwapExecution.
     */
    public VwapExecution getVwapExecution() {
        return vwapExecution;
    }

    /**
     * Gets the deadline.
     *
     * @return the deadline.
     */
    public Calendar getDeadline() {
        return (Calendar) deadline.clone();
    }

    /**
     * Gets the buckets.
     *
     * @return the buckets.
     */
    public Set<VwapBucket> getVwapBuckets() {
        return buckets;
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
     * Checks if there is a working order.
     *
     * @return <code>true</code> if there is a working order, <code>false</code> otherwise.
     */
    public boolean hasOrder() {
        return !NO_ORDER.equals(currentOrder);
    }

    /**
     * Gets the current working order or {@link OrderReference#NO_ORDER} if there is no order working.
     *
     * @return current order or {@link OrderReference#NO_ORDER}.
     */
    public OrderReference getCurrentOrder() {
        return currentOrder;
    }

    /**
     * Gets the current bucket.
     *
     * @return the current bucket.
     */
    public PeekingIterator<VwapBucket> getCurrentBucket() {
        return currentBucket;
    }

    /**
     * Updates the currentOrder reference.
     */
    @Override
    public void createdEvent(final OrderReference orderReference) {
        checkState(NO_ORDER.equals(currentOrder));
        this.currentOrder = orderReference;
    }

    /**
     * Updates the cumVolume.
     */
    @Override
    public void tradeEvent(final ExecutionReport executionReport, final OrderReference orderReference,
                           final Session session) {

        checkState(orderReference.equals(currentOrder));
        checkNotNull(executionReport.getLastQty());
        checkNotNull(executionReport.getLastQty().getValue());
        checkArgument(executionReport.getLastQty().getValue().compareTo(Order.NO_QTY) > 0);

        this.cumVolume += executionReport.getLastQty().getValue();

        LOG.info("tradeEvent[clOrdID={}, lastQty={}, lastPx={}, cumVolume={}]",
                 new Object[]{
                         orderReference.getClOrdID(),
                         executionReport.getLastQty().getValue(),
                         executionReport.getLastPx().getValue(),
                         this.cumVolume
                 }
        );

        checkState(cumVolume.compareTo(vwapExecution.getQuantity()) <= 0);

        if (orderReference.getOrdStatus().isFilled()) {
            this.currentOrder = NO_ORDER;
        }

        if (currentBucket.peek().getCumVolume().compareTo(cumVolume) == 0) {
            LOG.info("Traded allocated volume: {}", cumVolume);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void canceledEvent(final ExecutionReport executionReport, final OrderReference orderReference,
                              final Session session) {
        checkState(orderReference.equals(currentOrder));
        this.currentOrder = NO_ORDER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rejectedEvent(final ExecutionReport executionReport, final OrderReference orderReference,
                              final Session session) {
        checkState(orderReference.equals(currentOrder));
        this.currentOrder = NO_ORDER;
    }

    /**
     * Gets the bucket size in milliseconds.
     *
     * @return bucket size.
     */
    public BigDecimal getBucketSize() {
        return bucketSize;
    }

    /**
     * Returns a sorted set of {@link VwapBucket}s.
     *
     * @param now           start time.
     * @param deadline      deadline for execution of <code>vwapExecution</code>.
     * @param vwapExecution parameters of the algo to execute.
     *
     * @return sorted set of {@link VwapBucket}s.
     *
     * @throws NullPointerException     if any argument is null.
     * @throws IllegalArgumentException if <code>now</code> is after <code>deadline</code>.
     */
    @VisibleForTesting
    static SortedSet<VwapBucket> getBuckets(final Calendar now, final Calendar deadline,
                                            final VwapExecution vwapExecution) {

        checkNotNull(now);
        checkNotNull(deadline);
        checkNotNull(vwapExecution);
        checkArgument(now.before(deadline));

        final SortedSet<VwapBucket> buckets = newTreeSet();
        final BigDecimal sizeOfBucketInMillis = getBucketSize(now, deadline, vwapExecution);
        final BigDecimal quantity = valueOf(vwapExecution.getQuantity()).setScale(SCALE);

        final Iterator<BigDecimal> targets = vwapExecution.getTargets().iterator();
        BigDecimal lastDeadline = valueOf(now.getTimeInMillis()).setScale(SCALE);
        BigDecimal cumQuantity = ZERO.setScale(SCALE);

        while (targets.hasNext()) {

            final BigDecimal target = targets.next().setScale(SCALE);

            if (!targets.hasNext()) {
                buckets.add(new VwapBucket(vwapExecution.getQuantity(), deadline));
                continue; // Not break, for test coverage
            }

            lastDeadline = lastDeadline.add(sizeOfBucketInMillis);
            final Calendar bucketDeadline = Calendar.getInstance();
            bucketDeadline.setTimeInMillis(lastDeadline.setScale(0, RoundingMode.HALF_UP).longValue());

            final BigDecimal targetVolume = quantity.multiply(target);
            cumQuantity = cumQuantity.add(targetVolume);
            final Long bucketCumVolume = cumQuantity.setScale(0, RoundingMode.HALF_UP).longValue();

            buckets.add(new VwapBucket(bucketCumVolume, bucketDeadline));
        }

        return buckets;
    }

    /**
     * Gets the bucket size in milliseconds for the trading period between <code>now</code> and <code>deadline</code>
     * and for {@link VwapExecution#getTargets()} many buckets.
     *
     * @param now           start of the trading period.
     * @param deadline      deadline for the execution.
     * @param vwapExecution volume targets.
     *
     * @return bucket size in milliseconds.
     */
    @VisibleForTesting
    static BigDecimal getBucketSize(final Calendar now, final Calendar deadline, final VwapExecution vwapExecution) {

        checkNotNull(now);
        checkNotNull(deadline);
        checkNotNull(vwapExecution);
        checkArgument(now.before(deadline));

        final BigDecimal difference = valueOf(deadline.getTimeInMillis() - now.getTimeInMillis()).setScale(SCALE);
        final BigDecimal noOfTargets = valueOf(vwapExecution.getTargets().size()).setScale(SCALE);
        return difference.divide(noOfTargets, RoundingMode.HALF_UP);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("VwapStatus");
        sb.append("[vwapExecution=").append(vwapExecution);
        sb.append(", deadline=").append(format.format(deadline.getTime()));
        sb.append(", bucketSize=").append(bucketSize);
        sb.append(", buckets=").append(buckets);
        sb.append(", cumVolume=").append(cumVolume);
        sb.append(']');
        return sb.toString();
    }
}
