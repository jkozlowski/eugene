package eugene.simulation.ontology;

import org.testng.annotations.Test;

import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests {@link Start}.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public class StartTest {
    
    private static final int DIFFERENCE = 1;

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullStartTime() {
        new Start(null, Calendar.getInstance().getTime());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullStopTime() {
        new Start(Calendar.getInstance().getTime(), null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorStartTimeSameAsStopTime() {
        final Date date = Calendar.getInstance().getTime();
        new Start(date, date);
    }
    
    @Test(expectedExceptions = NullPointerException.class)
    public void testSetStartTimeNullStartTime() {
        final Start start = new Start();
        start.setStartTime(null);
    }

    @Test
    public void testSetStartStopNotSet() {
        final Start start = new Start();
        final Calendar startCalendar = Calendar.getInstance();
        start.setStartTime(startCalendar.getTime());
        assertThat(start.getStartTime(), is(startCalendar.getTime()));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSetStartTimeWrongStartTime() {
        final Calendar startCalendar = Calendar.getInstance();
        final Date stopTime = getStop(startCalendar);
        final Start start = new Start(startCalendar.getTime(), stopTime);
        
        start.setStartTime(stopTime);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testSetStopTimeNullStartTime() {
        final Start start = new Start();
        start.setStopTime(null);
    }

    @Test
    public void testSetStopStartNotSet() {
        final Start start = new Start();
        final Calendar stopCalendar = Calendar.getInstance();
        start.setStopTime(stopCalendar.getTime());
        assertThat(start.getStopTime(), is(stopCalendar.getTime()));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSetStopTimeWrongStopTime() {
        final Calendar startCalendar = Calendar.getInstance();
        final Date stopTime = getStop(startCalendar);
        final Start start = new Start(startCalendar.getTime(), stopTime);
        start.setStopTime(startCalendar.getTime());
    }
    
    public static Date getStop(final Calendar start) {
        final Calendar stopCalendar = (Calendar) start.clone();
        stopCalendar.add(Calendar.MILLISECOND, DIFFERENCE);
        return stopCalendar.getTime();
    }
}
