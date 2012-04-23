/*
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */

package eugene.utils.behaviour;

import jade.core.Agent;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests {@link CyclicFinishableBehaviour}.
 *
 * @author Jakub D Kozlowski
 * @since 1.0
 */
public class CyclicFinishableBehaviourTest {
    
    @Test
    public void testAction() {
        final Task task = mock(Task.class);
        new CyclicFinishableBehaviour(task).action();
        verify(task).action(Mockito.any(FinishableBehaviour.class), Mockito.any(Agent.class));
    }
}
