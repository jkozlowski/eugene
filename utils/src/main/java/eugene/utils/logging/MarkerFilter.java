package eugene.utils.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * Filters logging based on the marker.
 *
 * <p>Usage: inside logback.xml, when configuring an <code>&lt;appender/&gt;</code>:
 * <pre>
 *  &lt;filter class="eugene.utils.MarkerFilter"&gt;
 *      &lt;marker&gt;EXECUTION&lt;/marker&gt;
 *      &lt;onMatch&gt;ACCEPT&lt;/onMatch&gt;
 *      &lt;onMismatch&gt;DENY&lt;/onMismatch&gt;
 *  &lt;/filter&gt;
 * </pre>
 * </p>
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 *
 * @see <a href="http://stackoverflow.com/a/8759210">StackOverflow question</a>
 */
public final class MarkerFilter extends AbstractMatcherFilter<ILoggingEvent> {

    private Marker markerToMatch = null;

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public void start() {
        if (null != this.markerToMatch) {
            super.start();
        }
        else {
            addError("!!! no marker yet !!!");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FilterReply decide(ILoggingEvent event) {

        final Marker marker = event.getMarker();
        if (!isStarted()) {
            return FilterReply.NEUTRAL;
        }
        if (null == marker) {
            return onMismatch;
        }
        if (markerToMatch.contains(marker)) {
            return onMatch;
        }
        return onMismatch;
    }

    /**
     * Sets the marker to match.
     *
     * @param markerStr marker to match.
     */
    public void setMarker(String markerStr) {
        if (null != markerStr) {
            markerToMatch = MarkerFactory.getMarker(markerStr);
        }
    }
}
