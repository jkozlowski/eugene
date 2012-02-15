package eugene.utils.behaviour;

import eugene.utils.behaviour.BehaviourResult;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;

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
        assertThat(result.getObject(), nullValue());
        assertThat(result.isSuccess(), is(false));
    }

    @Test
    public void testFailNotNull() {
        final BehaviourResult<Object> result = new BehaviourResult<Object>(BehaviourResult.SUCCESS);
        final Object o = mock(Object.class);
        result.fail(o);
        assertThat(result.getResult(), is(BehaviourResult.FAILURE));
        assertThat(result.getObject(), sameInstance(o));
        assertThat(result.isSuccess(), is(false));
    }
    
    @Test
    public void testSuccess() {
        final BehaviourResult result = new BehaviourResult(BehaviourResult.FAILURE);
        result.success();
        assertThat(result.getResult(), is(BehaviourResult.SUCCESS));
        assertThat(result.getObject(), nullValue());
        assertThat(result.isSuccess(), is(true));
    }

    @Test
    public void testSuccessNotNull() {
        final BehaviourResult<Object> result = new BehaviourResult<Object>(BehaviourResult.SUCCESS);
        final Object o = mock(Object.class);
        result.success(o);
        assertThat(result.getResult(), is(BehaviourResult.SUCCESS));
        assertThat(result.getObject(), sameInstance(o));
        assertThat(result.isSuccess(), is(true));
    }
}
