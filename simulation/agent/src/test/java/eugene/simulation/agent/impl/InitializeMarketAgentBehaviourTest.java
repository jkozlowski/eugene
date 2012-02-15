package eugene.simulation.agent.impl;

import eugene.market.esma.MarketAgent;
import eugene.utils.behaviour.BehaviourResult;
import jade.core.Profile;
import jade.imtp.memory.MemoryProfile;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import org.testng.annotations.Test;

import static eugene.market.ontology.Defaults.defaultSymbol;
import static eugene.simulation.agent.Tests.initAgent;
import static eugene.simulation.agent.Tests.submit;
import static jade.core.Runtime.instance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Tests {@link InitializeMarketAgentBehaviour}.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class InitializeMarketAgentBehaviourTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullMarketAgent() {
        new InitializeMarketAgentBehaviour(null);
    }

    @Test
    public void testInitializeMarketAgentBehaviour() throws ControllerException, InterruptedException {
        final Profile profile = new MemoryProfile();
        final AgentContainer container = instance().createMainContainer(profile);

        final AgentController controller = initAgent(container);

        final InitializeMarketAgentBehaviour init = new InitializeMarketAgentBehaviour(new MarketAgent(defaultSymbol));
        submit(controller, init);

        assertThat(init.onEnd(), is(BehaviourResult.SUCCESS));
        assertThat(init.getResult().getObject(), notNullValue());

        final AgentController marketAgent = container.getAgent(init.getResult().getObject().getLocalName());
        assertThat(marketAgent, notNullValue());

        container.kill();
    }
}
