package eugene.agent.vwap.impl;

import eugene.agent.vwap.VwapExecution;
import eugene.market.book.Order;
import eugene.market.client.OrderReference;
import eugene.market.client.Session;
import eugene.market.ontology.field.LastPx;
import eugene.market.ontology.field.LastQty;
import eugene.market.ontology.message.ExecutionReport;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Iterator;
import java.util.SortedSet;

import static eugene.agent.vwap.VwapExecutions.newVwapExecution;
import static eugene.agent.vwap.impl.VwapStatus.getBucketSize;
import static eugene.market.ontology.Defaults.defaultLastPx;
import static eugene.market.ontology.Defaults.defaultOrdQty;
import static eugene.market.ontology.field.enums.Side.BUY;
import static java.math.BigDecimal.valueOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;

/**
 * Tests {@link VwapStatus}.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public class VwapStatusTest {

    private final BigDecimal[] defaultTargets = new BigDecimal[]{
            valueOf(0.08),
            valueOf(0.08),
            valueOf(0.08),
            valueOf(0.08),
            valueOf(0.08),
            valueOf(0.08),
            valueOf(0.08),
            valueOf(0.08),
            valueOf(0.08),
            valueOf(0.08),
            valueOf(0.08),
            valueOf(0.12)
    };
    
    private static final Calendar now;
    
    private static final Calendar deadline;

    static {
        deadline = Calendar.getInstance();
        deadline.add(Calendar.YEAR, 1);

        now = (Calendar) deadline.clone();
        now.setTimeInMillis(now.getTimeInMillis() - 352);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullDeadline() {
        new VwapStatus(null, mock(VwapExecution.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorVwapExecution() {
        new VwapStatus(deadline, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorDeadlinePassed() {
        final Calendar deadline = Calendar.getInstance();
        deadline.roll(Calendar.MONTH, false);
        new VwapStatus(deadline, mock(VwapExecution.class));
    }

    @Test
    public void testConstructor() {
        final VwapExecution execution = newVwapExecution(defaultOrdQty, BUY, defaultTargets);
        final VwapStatus vwapStatus = new VwapStatus(deadline, execution);
        assertThat(vwapStatus.getDeadline(), is(deadline));
        assertThat(vwapStatus.getVwapExecution(), sameInstance(execution));
        assertThat(vwapStatus.getVwapBuckets(), notNullValue());
        assertThat(vwapStatus.getCumVolume(), is(0L));
        assertThat(vwapStatus.getBucketSize(), notNullValue());
    }
    
    @Test(expectedExceptions = NullPointerException.class)
    public void testTradeEventNullLastQty() {
        final VwapExecution execution = newVwapExecution(defaultOrdQty, BUY, defaultTargets);
        final ExecutionReport executionReport = new ExecutionReport();
        final VwapStatus vwapStatus = new VwapStatus(deadline, execution);
        vwapStatus.tradeEvent(executionReport, mock(OrderReference.class), mock(Session.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testTradeEventNullLastQtyValue() {
        final VwapExecution execution = newVwapExecution(defaultOrdQty, BUY, defaultTargets);
        final ExecutionReport executionReport = new ExecutionReport();
        executionReport.setLastQty(new LastQty(null));
        final VwapStatus vwapStatus = new VwapStatus(deadline, execution);
        vwapStatus.tradeEvent(executionReport, mock(OrderReference.class), mock(Session.class));
    }


    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testTradeEventLastQtyValueZero() {
        final VwapExecution execution = newVwapExecution(defaultOrdQty, BUY, defaultTargets);
        final ExecutionReport executionReport = new ExecutionReport();
        executionReport.setLastQty(new LastQty(Order.NO_QTY));
        final VwapStatus vwapStatus = new VwapStatus(deadline, execution);
        vwapStatus.tradeEvent(executionReport, mock(OrderReference.class), mock(Session.class));
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testTradeEventOvertraded() {
        final VwapExecution execution = newVwapExecution(defaultOrdQty, BUY, defaultTargets);
        final ExecutionReport executionReport = new ExecutionReport();
        executionReport.setLastQty(new LastQty(defaultOrdQty + 1L));
        executionReport.setLastPx(new LastPx(defaultLastPx));
        final VwapStatus vwapStatus = new VwapStatus(deadline, execution);
        vwapStatus.tradeEvent(executionReport, mock(OrderReference.class), mock(Session.class));
    }

    @Test
    public void testTradeEventFinishedBucket() {
        final VwapExecution execution = newVwapExecution(defaultOrdQty, BUY, BigDecimal.ONE);
        final ExecutionReport executionReport = new ExecutionReport();
        executionReport.setLastQty(new LastQty(defaultOrdQty));
        executionReport.setLastPx(new LastPx(defaultLastPx));
        final VwapStatus vwapStatus = new VwapStatus(deadline, execution);
        vwapStatus.tradeEvent(executionReport, mock(OrderReference.class), mock(Session.class));
    }

    @Test
    public void testTradeEventNotFinishedBucket() {
        final VwapExecution execution = newVwapExecution(defaultOrdQty, BUY, BigDecimal.ONE);
        final ExecutionReport executionReport = new ExecutionReport();
        executionReport.setLastQty(new LastQty(defaultOrdQty - 1L));
        executionReport.setLastPx(new LastPx(defaultLastPx));
        final VwapStatus vwapStatus = new VwapStatus(deadline, execution);
        vwapStatus.tradeEvent(executionReport, mock(OrderReference.class), mock(Session.class));
    }
    
    @Test(expectedExceptions = NullPointerException.class)
    public void testGetBucketSizeNullNow() {
        getBucketSize(null, deadline, mock(VwapExecution.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testGetBucketSizeNullDeadline() {
        getBucketSize(Calendar.getInstance(), null, mock(VwapExecution.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testGetBucketSizeNullVwapExecution() {
        getBucketSize(Calendar.getInstance(), deadline, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetBucketSizeNowAfterDeadline() {
        final Calendar now = (Calendar) deadline.clone();
        now.add(Calendar.MONTH, 1);
        getBucketSize(now, deadline, mock(VwapExecution.class));
    }

    @Test
    public void testGetBucketSize() {
        final VwapExecution execution = newVwapExecution(defaultOrdQty, BUY, defaultTargets);
        assertThat(getBucketSize(now, deadline, execution), is(valueOf(29.33333).setScale(VwapStatus.SCALE)));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testGetBucketsNullNow() {
        VwapStatus.getBuckets(null, deadline, mock(VwapExecution.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testGetBucketsNullDeadline() {
        VwapStatus.getBuckets(Calendar.getInstance(), null, mock(VwapExecution.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testGetBucketsNullVwapExecution() {
        VwapStatus.getBuckets(Calendar.getInstance(), deadline, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetBucketsNowAfterDeadline() {
        final Calendar now = (Calendar) deadline.clone();
        now.add(Calendar.MONTH, 1);
        VwapStatus.getBuckets(now, deadline, mock(VwapExecution.class));
    }

    @Test
    public void testGetBucketsCheckDeadlines() {

        final VwapExecution execution = newVwapExecution(defaultOrdQty, BUY, defaultTargets);

        final SortedSet<VwapBucket> buckets = VwapStatus.getBuckets(now, deadline, execution);
        long curDeadline = now.getTimeInMillis();
        final Iterator<VwapBucket> i = buckets.iterator();
        assertThat(buckets.size(), is(defaultTargets.length));
        assertThat(i.next().getDeadline().getTime(), is((curDeadline += 29)));
        assertThat(i.next().getDeadline().getTime(), is((curDeadline += 30)));
        assertThat(i.next().getDeadline().getTime(), is((curDeadline += 29)));
        assertThat(i.next().getDeadline().getTime(), is((curDeadline += 29)));
        assertThat(i.next().getDeadline().getTime(), is((curDeadline += 30)));
        assertThat(i.next().getDeadline().getTime(), is((curDeadline += 29)));
        assertThat(i.next().getDeadline().getTime(), is((curDeadline += 29)));
        assertThat(i.next().getDeadline().getTime(), is((curDeadline += 30)));
        assertThat(i.next().getDeadline().getTime(), is((curDeadline += 29)));
        assertThat(i.next().getDeadline().getTime(), is((curDeadline += 29)));
        assertThat(i.next().getDeadline().getTime(), is((curDeadline += 30)));
        assertThat(i.next().getDeadline().getTime(), is((curDeadline += 29)));
        assertThat(i.hasNext(), is(false));

        long cumVolume = 0L;
        final Iterator<VwapBucket> i2 = buckets.iterator();
        assertThat(i2.next().getCumVolume(), is((cumVolume += 8)));
        assertThat(i2.next().getCumVolume(), is((cumVolume += 8)));
        assertThat(i2.next().getCumVolume(), is((cumVolume += 8)));
        assertThat(i2.next().getCumVolume(), is((cumVolume += 8)));
        assertThat(i2.next().getCumVolume(), is((cumVolume += 8)));
        assertThat(i2.next().getCumVolume(), is((cumVolume += 8)));
        assertThat(i2.next().getCumVolume(), is((cumVolume += 8)));
        assertThat(i2.next().getCumVolume(), is((cumVolume += 8)));
        assertThat(i2.next().getCumVolume(), is((cumVolume += 8)));
        assertThat(i2.next().getCumVolume(), is((cumVolume += 8)));
        assertThat(i2.next().getCumVolume(), is((cumVolume += 8)));
        assertThat(i2.next().getCumVolume(), is((cumVolume += 12)));
    }
    
    @Test
    public void testToString() {
        final BigDecimal[] singleTarget = new BigDecimal[]{
                valueOf(1.0)
        };

        final VwapExecution execution = newVwapExecution(defaultOrdQty, BUY, singleTarget);
        final VwapStatus vwapStatus = new VwapStatus(deadline, execution);

        assertThat(vwapStatus.toString(), notNullValue());
    }
}
