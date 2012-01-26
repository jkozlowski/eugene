package eugene.market.ontology.field.enums;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests {@link SessionStatus}.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class SessionStatusTest {

    @Test
    public void testField() {
        assertThat(SessionStatus.SESSION_ACTIVE.field(),
                   is(new eugene.market.ontology.field.SessionStatus(eugene.market.ontology.field.SessionStatus.SESSION_ACTIVE)));
    }
}
