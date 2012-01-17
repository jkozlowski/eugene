package eugene.agent.noise;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@link Matcher}s for comparing whether a value is between two values.
 *
 * @author Jakub D Kozlowski
 * @since 0.5
 */
public final class BetweenMatcher<K> extends TypeSafeMatcher<K> {

    private final Comparable<? super K> min;

    private final Comparable<? super K> max;

    private BetweenMatcher(final K min, final K max) {
        checkNotNull(min);
        checkNotNull(max);
        this.min = (Comparable<? super K>) min;
        this.max = (Comparable<? super K>) max;
        checkArgument(this.max.compareTo(min) == 1);
    }

    @Override
    public boolean matchesSafely(K item) {
        return min.compareTo(item) <= 0 && max.compareTo(item) >= 0;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("not " + min + " and " + max);
    }

    public static <K> Matcher<K> between(final K min, final K max) {
        return new BetweenMatcher(min, max);
    }
}
