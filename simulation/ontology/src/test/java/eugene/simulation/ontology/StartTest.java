/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.simulation.ontology;

import com.google.common.base.Objects;
import org.testng.annotations.Test;

import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

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
    
    @Test
    public void testEquals() {
        final Calendar startCalendar = Calendar.getInstance();
        final Date stopTime = getStop(startCalendar);

        final Calendar laterStartCalendar = Calendar.getInstance();
        laterStartCalendar.add(Calendar.HOUR, 1);

        final Calendar laterStopCalendar = Calendar.getInstance();
        laterStopCalendar.add(Calendar.HOUR, 2);

        final Start start = new Start();
        assertThat(start, equalTo(start));
        assertThat(new Start(), not(equalTo(null)));
        assertThat(new Start(), not(equalTo(new Object())));
        assertThat(new Start(startCalendar.getTime(), stopTime),
                   not(equalTo(new Start(startCalendar.getTime(), laterStopCalendar.getTime()))));
        assertThat(new Start(laterStartCalendar.getTime(), laterStopCalendar.getTime()),
                   not(equalTo(new Start(startCalendar.getTime(), laterStopCalendar.getTime()))));

        assertThat(new Start(laterStartCalendar.getTime(), laterStopCalendar.getTime()),
                   equalTo(new Start(laterStartCalendar.getTime(), laterStopCalendar.getTime())));
    }
    
    @Test
    public void testHashCode() {
        final Calendar startCalendar = Calendar.getInstance();
        final Date stopTime = getStop(startCalendar);
        assertThat(new Start(startCalendar.getTime(), stopTime).hashCode(),
                   is(Objects.hashCode(startCalendar.getTime(), stopTime)));
    }
    
    public static Date getStop(final Calendar start) {
        final Calendar stopCalendar = (Calendar) start.clone();
        stopCalendar.add(Calendar.MILLISECOND, DIFFERENCE);
        return stopCalendar.getTime();
    }
}
