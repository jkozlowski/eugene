/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.market.client.impl;

import eugene.market.client.Application;
import eugene.simulation.agent.Simulation;
import jade.content.ContentManager;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.ParallelBehaviour;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockObjectFactory;
import org.testng.IObjectFactory;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;

import java.util.Iterator;

import static eugene.market.esma.AbstractMarketAgentTest.MARKET_AGENT;
import static eugene.market.ontology.Defaults.defaultSymbol;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Tests {@link SessionInitiator}.
 *
 * @author Jakub D Kozlowski
 * @since 0.4
 */
@PrepareForTest(Agent.class)
public class SessionInitiatorTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullApplication() {
        new SessionInitiator(null, mock(Simulation.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructorNullSimulation() {
        new SessionInitiator(mock(Application.class), null);
    }

    @Test
    public void testSessionInitiator() throws Exception {

        final Agent agent = mock(Agent.class);
        when(agent.getContentManager()).thenReturn(mock(ContentManager.class));

        final Simulation simulation = mock(Simulation.class);
        when(simulation.getSymbol()).thenReturn(defaultSymbol);
        when(simulation.getMarketAgent()).thenReturn(new AID(MARKET_AGENT, AID.ISGUID));
        final SessionInitiator initiator = new SessionInitiator(mock(Application.class), simulation);
        initiator.setAgent(agent);
        initiator.onStart();
        
        final Iterator<Behaviour> i = (Iterator<Behaviour>) initiator.getChildren().iterator();
        
        assertThat(i.next(), is(LogonBehaviour.class));
        
        final ParallelBehaviour b = (ParallelBehaviour) i.next();
        
        final Iterator<Behaviour> parallelI = (Iterator<Behaviour>) b.getChildren().iterator();
        assertThat(parallelI.next(), is(MessageRoutingBehaviour.class));
        assertThat(parallelI.next(), is(StartStopBehaviour.class));
        assertThat(parallelI.hasNext(), is(false));
    }

    @ObjectFactory
    public IObjectFactory getObjectFactory() {
        return new PowerMockObjectFactory();
    }
}
