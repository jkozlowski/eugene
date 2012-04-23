/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.simulation.ontology;

import com.google.common.base.Objects;
import jade.content.AgentAction;
import jade.content.onto.annotations.Slot;

import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link Start} is sent by the Simulation Agent to a Trader Agent in order to activate the Trader Agent.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class Start implements AgentAction {

    private Date startTime;

    private Date stopTime;

    /**
     * Empty constructor.
     *
     * @since 0.7
     */
    public Start() {
        this.startTime = null;
        this.stopTime = null;
    }

    /**
     * Default constructor.
     *
     * @param startTime start time of the simulation.
     * @param stopTime  stop time of the simulation.
     *
     * @throws NullPointerException     if either parameter is null.
     * @throws IllegalArgumentException if <code>startTime</code> is after <code>stopTime</code>.
     * @since 0.7
     */
    public Start(final Date startTime, final Date stopTime) {
        checkNotNull(startTime);
        checkNotNull(stopTime);
        checkArgument(startTime.before(stopTime));
        this.startTime = (Date) startTime.clone();
        this.stopTime = (Date) stopTime.clone();
    }

    /**
     * Gets the startTime.
     *
     * @return the startTime.
     *
     * @since 0.7
     */
    @Slot
    public Date getStartTime() {
        return startTime;
    }

    /**
     * Sets the startTime.
     *
     * @param startTime new startTime.
     *
     * @throws NullPointerException     if <code>startTime</code> is null.
     * @throws IllegalArgumentException if <code>stopTime</code> is not null and <code>stopTime</code> is before
     *                                  <code>stopTime</code>.
     * @since 0.7
     */
    public void setStartTime(final Date startTime) {
        checkNotNull(startTime);
        checkArgument(null == stopTime || startTime.before(stopTime));
        this.startTime = (Date) startTime.clone();
    }

    /**
     * Gets the stopTime.
     *
     * @return the stopTime.
     *
     * @since 0.7
     */
    @Slot
    public Date getStopTime() {
        return stopTime;
    }

    /**
     * Sets the stopTime.
     *
     * @param stopTime new stopTime.
     *
     * @throws NullPointerException     if <code>stopTime</code> is null.
     * @throws IllegalArgumentException if <code>startTime</code> is not null and <code>stopTime</code> is before
     *                                  <code>stopTime</code>.
     * @since 0.7
     */
    public void setStopTime(final Date stopTime) {
        checkNotNull(stopTime);
        checkArgument(null == startTime || startTime.before(stopTime));
        this.stopTime = (Date) stopTime.clone();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Start start = (Start) o;

        if (!Objects.equal(startTime, start.startTime)) return false;
        if (!Objects.equal(stopTime, start.stopTime)) return false;

        return true;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(startTime, stopTime);
    }
}
