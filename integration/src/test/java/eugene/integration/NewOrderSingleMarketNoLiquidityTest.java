/**
 * Copyright 2012 Jakub Dominik Kozlowski <mail@jakub-kozlowski.com>
 * Distributed under the The MIT License.
 * (See accompanying file LICENSE.txt)
 */
package eugene.integration;

import com.google.common.collect.Sets;
import eugene.market.book.Order;
import eugene.market.client.Application;
import eugene.market.client.ApplicationAdapter;
import eugene.market.client.OrderReference;
import eugene.market.client.OrderReferenceListener;
import eugene.market.client.Session;
import eugene.market.ontology.field.OrderQty;
import eugene.market.ontology.field.enums.ExecType;
import eugene.market.ontology.field.enums.OrdStatus;
import eugene.market.ontology.field.enums.OrdType;
import eugene.market.ontology.field.enums.Side;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.NewOrderSingle;
import eugene.simulation.agent.Simulation;
import eugene.simulation.agent.SimulationAgent;
import eugene.simulation.agent.Symbol;
import eugene.simulation.agent.Symbols;
import eugene.simulation.ontology.Start;
import jade.core.Agent;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static eugene.integration.CountingApplication.TIMEOUT;
import static eugene.market.client.Applications.proxy;
import static eugene.market.client.Sessions.initiate;
import static eugene.market.agent.AbstractMarketAgentTest.getNakedContainer;
import static eugene.market.ontology.Defaults.defaultOrdQty;
import static eugene.market.ontology.Defaults.defaultPrice;
import static eugene.market.ontology.Defaults.defaultSymbol;
import static eugene.market.ontology.Defaults.defaultTickSize;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

/**
 * Creates a {@link OrdType#MARKET} when there is no liquidity.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class NewOrderSingleMarketNoLiquidityTest {

    @Test
    public void newOrderSingleMarketNoLiquidity() throws ControllerException, InterruptedException {

        final AgentContainer container = getNakedContainer();

        final CountDownLatch latch = new CountDownLatch(1);
        final Application application = mock(Application.class);
        final OrderReferenceListener marketListener = mock(OrderReferenceListener.class);
        final AtomicReference<OrderReference> orderReference = new AtomicReference<OrderReference>();
        final Application proxy = proxy(
                application,
                new ApplicationAdapter() {
                    @Override
                    public void onStart(Start start, Agent agent, Session session) {
                        final NewOrderSingle newOrderSingle = new NewOrderSingle();
                        newOrderSingle.setOrdType(OrdType.MARKET.field());
                        newOrderSingle.setOrderQty(new OrderQty(defaultOrdQty));
                        newOrderSingle.setSide(Side.BUY.field());

                        orderReference.set(session.send(newOrderSingle, marketListener));
                    }
                }, new CountingApplication(latch));

        final Set<Agent> agents = Sets.newHashSet();
        agents.add(new Agent() {
            @Override
            public void setup() {
                final Simulation simulation = (Simulation) getArguments()[0];
                addBehaviour(initiate(proxy, simulation));
            }
        });

        final Symbol symbol = Symbols.getSymbol(defaultSymbol, defaultTickSize, defaultPrice);
        final SimulationAgent simulation = new SimulationAgent(symbol, TIMEOUT, Collections.EMPTY_SET, agents);
        final AgentController simulationController = container.acceptNewAgent(SimulationAgent.NAME, simulation);
        simulationController.start();

        latch.await(CountingApplication.TIMEOUT, TimeUnit.MILLISECONDS);

        final InOrder inOrder = inOrder(application, marketListener);

        final ArgumentCaptor<NewOrderSingle> newOrderSingle = ArgumentCaptor.forClass(NewOrderSingle.class);
        inOrder.verify(application).fromApp(newOrderSingle.capture(), any(Session.class));
        assertThat(newOrderSingle.getValue().getSymbol().getValue(), is(defaultSymbol));
        assertThat(newOrderSingle.getValue().getOrderQty().getValue(), is(defaultOrdQty));
        assertThat(newOrderSingle.getValue().getOrdType(), is(OrdType.MARKET.field()));
        assertThat(newOrderSingle.getValue().getSide(), is(Side.BUY.field()));

        // REJECTED ExecutionReport for Market received by market listener
        final ArgumentCaptor<ExecutionReport> rejectedExecutionReportMarketListener
                = ArgumentCaptor.forClass(ExecutionReport.class);
        inOrder.verify(marketListener).rejectedEvent(rejectedExecutionReportMarketListener.capture(),
                                                     eq(orderReference.get()),
                                                     any(Session.class));

        // REJECTED ExecutionReport for Market
        final ArgumentCaptor<ExecutionReport> rejectedMarketExecutionReport
                = ArgumentCaptor.forClass(ExecutionReport.class);
        inOrder.verify(application).toApp(rejectedMarketExecutionReport.capture(), any(Session.class));
        assertThat(rejectedMarketExecutionReport.getValue(), sameInstance(
                rejectedExecutionReportMarketListener.getValue()));
        assertThat(rejectedMarketExecutionReport.getValue().getAvgPx().getValue(), is(Order.NO_PRICE));
        assertThat(rejectedMarketExecutionReport.getValue().getClOrdID().getValue(), is(
                newOrderSingle.getValue().getClOrdID().getValue()));
        assertThat(rejectedMarketExecutionReport.getValue().getCumQty().getValue(), is(Order.NO_QTY));
        assertThat(rejectedMarketExecutionReport.getValue().getExecType(), is(ExecType.REJECTED.field()));
        assertThat(rejectedMarketExecutionReport.getValue().getLeavesQty().getValue(), is(defaultOrdQty));
        assertThat(rejectedMarketExecutionReport.getValue().getOrdStatus(), is(OrdStatus.REJECTED.field()));
        assertThat(rejectedMarketExecutionReport.getValue().getSide(), is(Side.BUY.field()));
        assertThat(rejectedMarketExecutionReport.getValue().getSymbol().getValue(), is(defaultSymbol));
        assertThat(rejectedMarketExecutionReport.getValue().getLastPx(), nullValue());
        assertThat(rejectedMarketExecutionReport.getValue().getLastQty(), nullValue());

        inOrder.verifyNoMoreInteractions();

        assertThat(orderReference.get().getClOrdID(), is(
                rejectedMarketExecutionReport.getValue().getClOrdID().getValue()));
        assertThat(orderReference.get().getOrdStatus(), is(OrdStatus.REJECTED));
        assertThat(orderReference.get().getAvgPx(), is(Order.NO_PRICE));
        assertThat(orderReference.get().getPrice(), is(Order.NO_PRICE));
        assertThat(orderReference.get().getSide(), is(Side.BUY));
        assertThat(orderReference.get().getCumQty(), is(0L));
        assertThat(orderReference.get().getLeavesQty(), is(defaultOrdQty));
        assertThat(orderReference.get().getOrdType(), is(OrdType.MARKET));

        container.kill();
    }
}
