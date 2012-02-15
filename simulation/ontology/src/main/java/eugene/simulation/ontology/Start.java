package eugene.simulation.ontology;

import jade.content.AgentAction;
import jade.content.onto.annotations.Slot;

import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link Start} is sent by the Simulation Agents to a Trader Agent in order to activate the Trader Agent.
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

        if (startTime != null ? !startTime.equals(start.startTime) : start.startTime != null) return false;
        if (stopTime != null ? !stopTime.equals(start.stopTime) : start.stopTime != null) return false;

        return true;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = startTime != null ? startTime.hashCode() : 0;
        result = 31 * result + (stopTime != null ? stopTime.hashCode() : 0);
        return result;
    }
}
