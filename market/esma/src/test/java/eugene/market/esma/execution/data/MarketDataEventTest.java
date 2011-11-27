package eugene.market.esma.execution.data;

import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests {@link MarketDataEvent}.
 *
 * @author Jakub D Kozlowski
 * @since 0.3
 */
public class MarketDataEventTest {

    private final Object object = mock(Object.class);

    private final MarketDataEvent marketDataEvent = new TestMarketDataEvent(Long.MIN_VALUE, Long.MAX_VALUE,
                                                                            object);

    public static final class TestMarketDataEvent extends MarketDataEvent<Object> {

        public TestMarketDataEvent(final Long eventId, final Long time,
                                   final Object object) {
            super(eventId, time, object);
        }

        @Override
        public void accept(MarketDataEventHandler handler) {
        }
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullEventId() {
        new TestMarketDataEvent(null, Long.MAX_VALUE, mock(Object.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullTime() {
        new TestMarketDataEvent(Long.MIN_VALUE, null, mock(Object.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullObject() {
        new TestMarketDataEvent(Long.MIN_VALUE, Long.MAX_VALUE, null);
    }

    @Test
    public void testGetEventId() {
        assertThat(marketDataEvent.getEventId(), is(Long.MIN_VALUE));
    }

    @Test
    public void testGetTime() {
        assertThat(marketDataEvent.getTime(), is(Long.MAX_VALUE));
    }

    @Test
    public void testGetObject() {
        assertThat(marketDataEvent.getObject(), sameInstance(object));
    }
}
