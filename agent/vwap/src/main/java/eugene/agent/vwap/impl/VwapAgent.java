/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.agent.vwap.impl;

import eugene.agent.vwap.VwapExecution;
import eugene.agent.vwap.impl.state.State;
import eugene.market.client.ApplicationAdapter;
import eugene.market.client.Applications;
import eugene.market.client.ProxyApplication;
import eugene.market.client.Session;
import eugene.market.client.TopOfBookApplication;
import eugene.simulation.agent.Simulation;
import eugene.simulation.ontology.Start;
import eugene.simulation.ontology.Stop;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;

import java.util.Calendar;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static eugene.market.client.Applications.proxy;
import static eugene.market.client.Sessions.initiate;

/**
 * Implements the VWAP Trader agent.
 *
 * {@link VwapAgent} implements the following algorithm:
 *
 * @author Jakub D Kozlowski
 * @since 0.7
 */
public class VwapAgent extends Agent {

    private final VwapExecution vwapExecution;
    
    private final State sendLimitState;

    /**
     * Default constructor.
     *
     * @param vwapExecution  configuration of the {@link VwapAgent}.
     * @param sendLimitState {@link State} that will be used by {@link VwapBehaviour} to send limit orders.
     *
     * @throws NullPointerException if any parameter is null.
     */
    public VwapAgent(final VwapExecution vwapExecution, final State sendLimitState) {
        checkNotNull(vwapExecution);
        checkNotNull(sendLimitState);
        this.vwapExecution = vwapExecution;
        this.sendLimitState = sendLimitState;
    }

    @Override
    public void setup() {
        checkNotNull(getArguments());
        checkArgument(getArguments().length == 1);
        checkArgument(getArguments()[0] instanceof Simulation);

        final Simulation simulation = (Simulation) getArguments()[0];

        final TopOfBookApplication topOfBook = Applications.topOfBookApplication(simulation.getSymbol());
        final ProxyApplication proxy = proxy(topOfBook);
        proxy.addApplication(new ApplicationAdapter() {

            private Behaviour b = null;

            @Override
            public void onStart(final Start start, final Agent agent, final Session session) {
                final Calendar deadline = Calendar.getInstance();
                deadline.setTime(start.getStopTime());
                b = new VwapBehaviour(deadline, vwapExecution, sendLimitState, topOfBook, session);
                addBehaviour(b);
            }

            @Override
            public void onStop(final Stop stop, final Agent agent, final Session session) {
                if (null != b) {
                    agent.removeBehaviour(b);
                }
            }
        });
        addBehaviour(initiate(proxy, simulation));
    }
}
