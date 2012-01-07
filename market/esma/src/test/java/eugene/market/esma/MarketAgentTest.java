package eugene.market.esma;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAException;
import jade.util.Event;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicReference;

import static eugene.market.esma.MarketAgent.getDFAgentDescription;
import static eugene.market.ontology.Defaults.defaultSymbol;
import static jade.domain.DFService.searchUntilFound;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests {@link MarketAgent}.
 *
 * @author Jakub D Kozlowski
 * @since 0.4
 */
public class MarketAgentTest extends AbstractMarketAgentTest {

    public static final int TIMEOUT = 10 * 1000;

    @Test
    public void testMarketAgentRegistered() throws StaleProxyException, InterruptedException {
        final AtomicReference<DFAgentDescription> description = new
                AtomicReference<DFAgentDescription>(null);
        final Behaviour checkRegistered = new OneShotBehaviour() {
            @Override
            public void action() {
                try {
                    final DFAgentDescription agentDescription = getDFAgentDescription(defaultSymbol);

                    final SearchConstraints constraints = new SearchConstraints();
                    constraints.setMaxResults(-1L);

                    final DFAgentDescription[] results = searchUntilFound(myAgent, myAgent.getDefaultDF(),
                                                                          agentDescription, constraints,
                                                                          TIMEOUT);
                    if (results.length > 0) {
                        description.set(results[0]);
                    }
                }
                catch (FIPAException e) {
                    e.printStackTrace();
                }
            }
        };
        final Event traderEvent = new Event(-1, checkRegistered);
        traderAgentController.putO2AObject(traderEvent, AgentController.ASYNC);
        traderEvent.waitUntilProcessed();
        assertThat(marketAgentController.getName(), is(description.get().getName().getName()));
    }
    
    @Test(expectedExceptions = NullPointerException.class)
    public void testGetDFAgentDescriptionNullSymbol() {
        getDFAgentDescription(null);
    }
}
