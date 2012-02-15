package eugene.agent.vwap.impl;

import org.testng.annotations.Test;

import java.util.Calendar;

import static eugene.market.ontology.Defaults.defaultOrdQty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests {@link VwapBucket}.
 * 
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public class VwapBucketTest {
    
    private static final Calendar deadline;

    static {
        deadline = Calendar.getInstance();
        deadline.add(Calendar.YEAR, 1);
    }
    
    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullCumVolume() {
        new VwapBucket(null, deadline);
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorZeroCumVolume() {
        new VwapBucket(0L, deadline);
    }
    
    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullDeadline() {
        new VwapBucket(defaultOrdQty, null);
    }
    
    @Test
    public void testConstructor() {
        final VwapBucket bucket = new VwapBucket(defaultOrdQty, deadline);
        assertThat(bucket.getCumVolume(), is(defaultOrdQty));
        assertThat(bucket.getDeadline(), is(deadline.getTime()));
    }
    
    @Test(expectedExceptions = NullPointerException.class)
    public void testCompareToNullBucket() {
        new VwapBucket(defaultOrdQty, deadline).compareTo(null);
    }
    
    @Test
    public void testCompareTo() {
        final Calendar now = Calendar.getInstance();
        now.roll(Calendar.MONTH, true);
        final VwapBucket nowBucket = new VwapBucket(defaultOrdQty, now);
        assertThat(new VwapBucket(defaultOrdQty, deadline).compareTo(nowBucket), is(deadline.compareTo(now)));
    }
}
