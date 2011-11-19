package jade.imtp.memory;

import jade.core.Profile;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static jade.core.Runtime.instance;
import static jade.imtp.memory.TestAgent.getRandomName;

/**
 * Tests {@link MemoryIMTPManager}.
 *
 * @author Jakub D Kozlowski
 * @since 0.2
 */
public class MemoryIMTPManagerTest {

    /**
     * Tests whether attempting to create an agent on a platform that has been terminated throws StaleProxyException.
     */
    @Test(expected = StaleProxyException.class)
    public void testAgentContainerKill() throws StaleProxyException {
        final Profile profile = new MemoryProfile();
        final AgentContainer agentContainer = instance().createMainContainer(profile);
        final AgentController testAgent =
                agentContainer.createNewAgent(getRandomName(), TestAgent.class.getName(), new Object[0]);
        testAgent.start();
        testAgent.kill();
        agentContainer.kill();
        agentContainer.createNewAgent(getRandomName(), TestAgent.class.getName(), new Object[0]);
    }

    /**
     * Tests launching multiple main containers.
     *
     */
    @Test
    public void testLaunchingMultipleMainContainers() throws StaleProxyException, InterruptedException {

        final List<AgentContainer> agentContainers = new ArrayList<AgentContainer>();

        for (int i = 0; i < 5; i++) {
            final Profile profile = new MemoryProfile();
            final AgentContainer agentContainer = instance().createMainContainer(profile);
            agentContainers.add(agentContainer);
            final AgentController testAgent =
                    agentContainer.createNewAgent(getRandomName(), TestAgent.class.getName(), new Object[0]);
            testAgent.start();
        }

        for (AgentContainer agentContainer : agentContainers) {
            agentContainer.kill();
        }
    }
}
