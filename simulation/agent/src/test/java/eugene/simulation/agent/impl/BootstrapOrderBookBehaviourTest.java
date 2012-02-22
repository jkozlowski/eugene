/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.simulation.agent.impl;

import eugene.market.book.Order;
import eugene.market.esma.MarketAgent;
import eugene.market.ontology.field.enums.OrdType;
import eugene.market.ontology.field.enums.Side;
import eugene.market.ontology.message.ExecutionReport;
import eugene.utils.behaviour.BehaviourResult;
import jade.core.AID;
import jade.core.Profile;
import jade.imtp.memory.MemoryProfile;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static eugene.market.ontology.Defaults.defaultOrdQty;
import static eugene.market.ontology.Defaults.defaultPrice;
import static eugene.market.ontology.Defaults.defaultSymbol;
import static eugene.simulation.agent.Tests.initAgent;
import static eugene.simulation.agent.Tests.submit;
import static jade.core.Runtime.instance;
import static java.util.Collections.singleton;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;

/**
 * Tests {@link BootstrapOrderBookBehaviour}.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class BootstrapOrderBookBehaviourTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullMarketAgent() {
        new BootstrapOrderBookBehaviour(null, defaultSymbol, singleton(mock(Order.class)));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testContructorNullSymbol() {
        new BootstrapOrderBookBehaviour(new BehaviourResult<AID>(), null, singleton(mock(Order.class)));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorEmptySymbol() {
        new BootstrapOrderBookBehaviour(new BehaviourResult<AID>(), "", singleton(mock(Order.class)));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullOrders() {
        new BootstrapOrderBookBehaviour(new BehaviourResult<AID>(), defaultSymbol, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructorEmptyOrders() {
        new BootstrapOrderBookBehaviour(new BehaviourResult<AID>(), defaultSymbol, Collections.EMPTY_SET);
    }

    @Test
    public void testBootstrapOrderBookBehaviour() throws StaleProxyException, InterruptedException {

        final Profile profile = new MemoryProfile();
        final AgentContainer container = instance().createMainContainer(profile);

        final AgentController controller = initAgent(container);

        final InitializeMarketAgentBehaviour init = new InitializeMarketAgentBehaviour(new MarketAgent(defaultSymbol));
        submit(controller, init);

        assertThat(init.getResult().getObject(), notNullValue());

        final Set<Order> orders = newHashSet();
        for (int i = 1; i < 11; i++) {
            orders.add(new Order(1L, OrdType.LIMIT, Side.BUY, defaultOrdQty, defaultPrice - i));
            orders.add(new Order(1L, OrdType.LIMIT, Side.SELL, defaultOrdQty, defaultPrice + i));
        }

        final BootstrapOrderBookBehaviour bootstrap = new BootstrapOrderBookBehaviour(init.getResult(),
                                                                                      defaultSymbol, orders);
        submit(controller, bootstrap);
        assertThat(bootstrap.onEnd(), is(BehaviourResult.SUCCESS));
        assertThat(bootstrap.getResult().getObject(), notNullValue());
        assertThat(bootstrap.getResult().getObject().size(), is(orders.size()));

        for (final ExecutionReport newOrder : bootstrap.getResult().getObject()) {
            boolean yes = false;
            for (final Order o : orders) {
                if (compare(o, newOrder)) {
                    yes = true;
                    break;
                }
            }

            if (!yes) {
                assertThat(false, is(true));
            }
        }

        container.kill();
    }

    public boolean compare(final Order order, final ExecutionReport newOrderSingle) {

        if (!order.getOrderQty().equals(newOrderSingle.getLeavesQty().getValue())) {
            return false;
        }

        if (!order.getSide().field().equals(newOrderSingle.getSide())) {
            return false;
        }

        return true;
    }
}
