/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.utils.behaviour;

import eugene.utils.behaviour.OneShotFinishableBehaviour;
import eugene.utils.behaviour.Task;
import jade.core.Agent;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests {@link OneShotFinishableBehaviour}.
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public class OneShotFinishableBehaviourTest {

    @Test
    public void testIsFirstNotRun() {
        final OneShotFinishableBehaviour b = new OneShotFinishableBehaviour(mock(Task.class));
        assertThat(b.isFirst(), is(true));
    }

    @Test
    public void testIsFirstAlreadyRun() {
        final Task task = mock(Task.class);
        final Agent agent = mock(Agent.class);
        final OneShotFinishableBehaviour b = new OneShotFinishableBehaviour(task);
        b.setAgent(agent);

        b.action();

        assertThat(b.isFirst(), is(false));
        verify(task).action(b, agent);
    }
    
    @Test
    public void testAtomic() {
        final Task task = mock(Task.class);
        final Agent agent = mock(Agent.class);
        final OneShotFinishableBehaviour b = new OneShotFinishableBehaviour(task);
        b.setAgent(agent);

        b.action();
        b.action();

        assertThat(b.isFirst(), is(false));
        verify(task).action(b, agent);    
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testDoneNotFinished() {
        final OneShotFinishableBehaviour b = new OneShotFinishableBehaviour(mock(Task.class));
        
        assertThat(b.done(), is(false));
        assertThat(b.onEnd(), is(-1));
    }
    
    @Test
    public void testFinish() {
        final Task task = mock(Task.class);
        final Agent agent = mock(Agent.class);
        final OneShotFinishableBehaviour b = new OneShotFinishableBehaviour(task);
        b.setAgent(agent);

        b.action();
        b.finish(1);

        assertThat(b.isFirst(), is(false));
        assertThat(b.done(), is(true));
        assertThat(b.onEnd(), is(1));
        verify(task).action(b, agent);
    }
    
    @Test(expectedExceptions = IllegalStateException.class)
    public void testReset() {
        final Task task = mock(Task.class);
        final Agent agent = mock(Agent.class);
        final OneShotFinishableBehaviour b = new OneShotFinishableBehaviour(task);
        b.setAgent(agent);

        b.action();
        b.finish(1);

        assertThat(b.isFirst(), is(false));
        assertThat(b.done(), is(true));
        assertThat(b.onEnd(), is(1));
        verify(task).action(b, agent);
        
        b.reset();
        
        assertThat(b.isFirst(), is(true));
        assertThat(b.done(), is(false));
        b.onEnd();
    }
}
