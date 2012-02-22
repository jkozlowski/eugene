/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.utils.behaviour;

import eugene.utils.behaviour.FinishableBehaviour;
import eugene.utils.behaviour.Task;
import jade.core.Agent;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Tests {@link FinishableBehaviour}.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public class FinishableBehaviourTest {
    
    private static class TestFinishableBehaviour extends FinishableBehaviour {

        public TestFinishableBehaviour(final Task task) {
            super(task);
        }

        @Override
        public void action() {
        }
    }
    
    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullTask() {
        new TestFinishableBehaviour(null);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testDoneNotFinished() {
        final FinishableBehaviour b = new TestFinishableBehaviour(mock(Task.class));

        assertThat(b.done(), is(false));
        assertThat(b.onEnd(), is(-1));
    }

    @Test
    public void testFinish() {
        final Task task = mock(Task.class);
        final Agent agent = mock(Agent.class);
        final FinishableBehaviour b = new TestFinishableBehaviour(task);
        b.setAgent(agent);

        b.action();
        b.finish(1);

        assertThat(b.done(), is(true));
        assertThat(b.onEnd(), is(1));
        verifyZeroInteractions(task);
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testReset() {
        final Task task = mock(Task.class);
        final Agent agent = mock(Agent.class);
        final FinishableBehaviour b = new TestFinishableBehaviour(task);
        b.setAgent(agent);

        b.action();
        b.finish(1);

        assertThat(b.done(), is(true));
        assertThat(b.onEnd(), is(1));
        verifyZeroInteractions(task);

        b.reset();

        assertThat(b.done(), is(false));
        b.onEnd();
    }
}
