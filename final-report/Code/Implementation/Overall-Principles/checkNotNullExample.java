import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;

/**
 * Does something with {@code s}.
 *
 * @param s string to do something with.
 * 
 * @throws NullPointerException     if {@code s} is null.
 * @throws IllegalArgumentException if {@code s} is empty.
 */
public void doSomething(final String s) {
  checkNotNull(s);
  checkArgument(!s.isEmpty());
  // Implementation of the method.
}
