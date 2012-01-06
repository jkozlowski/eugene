package eugene.market.client.api.impl;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests {@link BehaviourResult}.
 *
 * @author Jakub D Kozlowski
 * @since 0.4
 */
public class BehaviourResultTest {

    @Test
    public void testDefaultConstructor() {
        final BehaviourResult behaviourResult = new BehaviourResult();
        assertThat(behaviourResult.getResult(), is(BehaviourResult.FAILURE));
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorIllegalDefaultResult() {
        new BehaviourResult(Integer.MAX_VALUE);
    }

    @Test
    public void testFail() {
        final BehaviourResult result = new BehaviourResult(BehaviourResult.SUCCESS);
        result.fail();
        assertThat(result.getResult(), is(BehaviourResult.FAILURE));
    }
    
    @Test
    public void testSuccess() {
        final BehaviourResult result = new BehaviourResult(BehaviourResult.FAILURE);
        result.success();
        assertThat(result.getResult(), is(BehaviourResult.SUCCESS));
    }
}
