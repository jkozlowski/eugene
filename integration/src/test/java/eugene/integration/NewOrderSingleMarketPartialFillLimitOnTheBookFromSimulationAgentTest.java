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
import eugene.market.ontology.field.OrdType;
import eugene.market.ontology.field.OrderQty;
import eugene.market.ontology.field.enums.ExecType;
import eugene.market.ontology.field.enums.OrdStatus;
import eugene.market.ontology.field.enums.Side;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.NewOrderSingle;
import eugene.market.ontology.message.data.AddOrder;
import eugene.market.ontology.message.data.OrderExecuted;
import eugene.simulation.agent.Simulation;
import eugene.simulation.agent.SimulationAgent;
import eugene.simulation.ontology.Start;
import jade.core.Agent;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.testng.annotations.Test;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static eugene.integration.CountingApplication.TIMEOUT;
import static eugene.market.client.Applications.proxy;
import static eugene.market.client.Sessions.initiate;
import static eugene.market.esma.AbstractMarketAgentTest.getNakedContainer;
import static eugene.market.ontology.Defaults.defaultOrdQty;
import static eugene.market.ontology.Defaults.defaultOrderID;
import static eugene.market.ontology.Defaults.defaultPrice;
import static eugene.market.ontology.Defaults.defaultSymbol;
import static eugene.market.ontology.field.enums.OrdType.LIMIT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Sends a {@link OrdType#MARKET} order that is bigger than a matching {@link OrdType#LIMIT} submitted by the
 * Simulation Agent.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class NewOrderSingleMarketPartialFillLimitOnTheBookFromSimulationAgentTest {

    @Test
    public void newOrderSingleMarketPartialFillLimitOnTheBookFromSimulationAgent()
            throws ControllerException, InterruptedException {

        final AgentContainer container = getNakedContainer();

        final CountDownLatch latch = new CountDownLatch(5);

        final Set<Order> orders = Sets.newHashSet();
        orders.add(new Order(Long.valueOf(defaultOrderID), LIMIT, Side.BUY, defaultOrdQty, defaultPrice));

        final Application application = mock(Application.class);
        final OrderReferenceListener marketListener = mock(OrderReferenceListener.class);
        final AtomicReference<OrderReference> orderReference = new AtomicReference<OrderReference>();
        final Application proxy = proxy(
                application,
                new ApplicationAdapter() {
                    @Override
                    public void onStart(Start start, Agent agent, Session session) {

                        final NewOrderSingle newOrderSingleMarket = new NewOrderSingle();
                        newOrderSingleMarket.setOrdType(
                                eugene.market.ontology.field.enums.OrdType.MARKET.field());
                        newOrderSingleMarket.setOrderQty(new OrderQty(defaultOrdQty + 1L));
                        newOrderSingleMarket.setSide(Side.SELL.field());

                        orderReference.set(session.send(newOrderSingleMarket, marketListener));
                    }
                },
                new CountingApplication(latch)
        );

        final Set<Agent> agents = Sets.newHashSet();
        agents.add(new Agent() {
            @Override
            public void setup() {
                final Simulation simulation = (Simulation) getArguments()[0];
                addBehaviour(initiate(proxy, simulation));
            }
        });

        final SimulationAgent simulation = new SimulationAgent(defaultSymbol, TIMEOUT, orders, agents);
        final AgentController simulationController = container.acceptNewAgent(SimulationAgent.NAME, simulation);
        simulationController.start();

        latch.await(CountingApplication.TIMEOUT, TimeUnit.MILLISECONDS);

        final InOrder inOrder = inOrder(application, marketListener);

        // AddOrder for Limit
        final ArgumentCaptor<AddOrder> addOrder = ArgumentCaptor.forClass(AddOrder.class);
        inOrder.verify(application).toApp(addOrder.capture(), any(Session.class));
        assertThat(addOrder.getValue().getOrderID().getValue(), is(defaultOrderID));
        assertThat(addOrder.getValue().getPrice().getValue(), is(defaultPrice));
        assertThat(addOrder.getValue().getOrderQty().getValue(), is(defaultOrdQty));
        assertThat(addOrder.getValue().getSide(), is(Side.BUY.field()));
        assertThat(addOrder.getValue().getSymbol().getValue(), is(defaultSymbol));

        final ArgumentCaptor<NewOrderSingle> newOrderSingleCaptor = ArgumentCaptor.forClass(NewOrderSingle.class);
        verify(application).fromApp(newOrderSingleCaptor.capture(), any(Session.class));

        // Outgoing NewOrderSingle for the Market order
        final NewOrderSingle newOrderSingleMarket = newOrderSingleCaptor.getValue();
        assertThat(newOrderSingleMarket.getSymbol().getValue(), is(defaultSymbol));
        assertThat(newOrderSingleMarket.getOrderQty().getValue(), is(defaultOrdQty + 1L));
        assertThat(newOrderSingleMarket.getOrdType(), is(eugene.market.ontology.field.enums.OrdType.MARKET.field()));
        assertThat(newOrderSingleMarket.getPrice(), nullValue());
        assertThat(newOrderSingleMarket.getSide(), is(Side.SELL.field()));

        // NEW ExecutionReport for Market received by market listener
        final ArgumentCaptor<ExecutionReport> newExecutionReportMarketListener
                = ArgumentCaptor.forClass(ExecutionReport.class);
        inOrder.verify(marketListener).newEvent(newExecutionReportMarketListener.capture(), eq(orderReference.get()),
                                                any(Session.class));

        // NEW ExecutionReport for Market
        final ArgumentCaptor<ExecutionReport> newMarketExecutionReport = ArgumentCaptor.forClass(ExecutionReport.class);
        inOrder.verify(application).toApp(newMarketExecutionReport.capture(), any(Session.class));
        assertThat(newMarketExecutionReport.getValue(), sameInstance(newExecutionReportMarketListener.getValue()));
        assertThat(newMarketExecutionReport.getValue().getAvgPx().getValue(), is(Order.NO_PRICE));
        assertThat(newMarketExecutionReport.getValue().getClOrdID().getValue(),
                   is(newOrderSingleMarket.getClOrdID().getValue()));
        assertThat(newMarketExecutionReport.getValue().getCumQty().getValue(), is(Order.NO_QTY));
        assertThat(newMarketExecutionReport.getValue().getExecType(), is(ExecType.NEW.field()));
        assertThat(newMarketExecutionReport.getValue().getLeavesQty().getValue(), is(defaultOrdQty + 1L));
        assertThat(newMarketExecutionReport.getValue().getOrdStatus(), is(OrdStatus.NEW.field()));
        assertThat(newMarketExecutionReport.getValue().getSide(), is(Side.SELL.field()));
        assertThat(newMarketExecutionReport.getValue().getSymbol().getValue(), is(defaultSymbol));
        assertThat(newMarketExecutionReport.getValue().getLastPx(), nullValue());
        assertThat(newMarketExecutionReport.getValue().getLastQty(), nullValue());

        // TRADE ExecutionReport for Market received by market listener
        final ArgumentCaptor<ExecutionReport> tradeExecutionReportMarketListener
                = ArgumentCaptor.forClass(ExecutionReport.class);
        inOrder.verify(marketListener).tradeEvent(tradeExecutionReportMarketListener.capture(),
                                                  eq(orderReference.get()),
                                                  any(Session.class));

        // TRADE ExecutionReport for Market
        final ArgumentCaptor<ExecutionReport> tradeMarketExecutionReport = ArgumentCaptor.forClass(
                ExecutionReport.class);
        inOrder.verify(application).toApp(tradeMarketExecutionReport.capture(), any(Session.class));
        assertThat(tradeMarketExecutionReport.getValue(), sameInstance(tradeExecutionReportMarketListener.getValue()));
        assertThat(tradeMarketExecutionReport.getValue().getAvgPx().getValue(), is(defaultPrice));
        assertThat(tradeMarketExecutionReport.getValue().getClOrdID().getValue(),
                   is(newOrderSingleMarket.getClOrdID().getValue()));
        assertThat(tradeMarketExecutionReport.getValue().getCumQty().getValue(), is(defaultOrdQty));
        assertThat(tradeMarketExecutionReport.getValue().getExecType(), is(ExecType.TRADE.field()));
        assertThat(tradeMarketExecutionReport.getValue().getLeavesQty().getValue(), is(1L));
        assertThat(tradeMarketExecutionReport.getValue().getOrdStatus(), is(OrdStatus.PARTIALLY_FILLED.field()));
        assertThat(tradeMarketExecutionReport.getValue().getSide(), is(Side.SELL.field()));
        assertThat(tradeMarketExecutionReport.getValue().getSymbol().getValue(), is(defaultSymbol));
        assertThat(tradeMarketExecutionReport.getValue().getLastPx().getValue(), is(defaultPrice));
        assertThat(tradeMarketExecutionReport.getValue().getLastQty().getValue(), is(defaultOrdQty));

        // OrderExecuted for Limit
        final ArgumentCaptor<OrderExecuted> orderExecuted = ArgumentCaptor.forClass(OrderExecuted.class);
        inOrder.verify(application).toApp(orderExecuted.capture(), any(Session.class));
        assertThat(orderExecuted.getValue().getOrderID().getValue(), is(defaultOrderID));
        assertThat(orderExecuted.getValue().getLastPx().getValue(), is(defaultPrice));
        assertThat(orderExecuted.getValue().getLastQty().getValue(), is(defaultOrdQty));
        assertThat(orderExecuted.getValue().getLeavesQty().getValue(), is(Order.NO_QTY));
        assertThat(orderExecuted.getValue().getTradeID().getValue(), is(Long.valueOf(1L).toString()));

        // CANCELED ExecutionReport for Market received by market listener
        final ArgumentCaptor<ExecutionReport> cancelExecutionReportMarketListener
                = ArgumentCaptor.forClass(ExecutionReport.class);
        inOrder.verify(marketListener).canceledEvent(cancelExecutionReportMarketListener.capture(),
                                                     eq(orderReference.get()),
                                                     any(Session.class));

        // CANCELED ExecutionReport for Market
        final ArgumentCaptor<ExecutionReport> canceledExecutionReportCaptor
                = ArgumentCaptor.forClass(ExecutionReport.class);
        inOrder.verify(application).toApp(canceledExecutionReportCaptor.capture(), any(Session.class));
        final ExecutionReport canceledMarketExecutionReport = canceledExecutionReportCaptor.getValue();
        assertThat(canceledMarketExecutionReport, sameInstance(cancelExecutionReportMarketListener.getValue()));
        assertThat(canceledMarketExecutionReport.getAvgPx().getValue(), is(defaultPrice));
        assertThat(canceledMarketExecutionReport.getClOrdID().getValue(),
                   is(newOrderSingleMarket.getClOrdID().getValue()));
        assertThat(canceledMarketExecutionReport.getCumQty().getValue(), is(defaultOrdQty));
        assertThat(canceledMarketExecutionReport.getExecType(), is(ExecType.CANCELED.field()));
        assertThat(canceledMarketExecutionReport.getLeavesQty().getValue(), is(1L));
        assertThat(canceledMarketExecutionReport.getOrdStatus(), is(OrdStatus.CANCELED.field()));
        assertThat(canceledMarketExecutionReport.getSide(), is(Side.SELL.field()));
        assertThat(canceledMarketExecutionReport.getSymbol().getValue(), is(defaultSymbol));
        assertThat(canceledMarketExecutionReport.getLastPx(), nullValue());
        assertThat(canceledMarketExecutionReport.getLastQty(), nullValue());

        inOrder.verifyNoMoreInteractions();

        // Verify OrderReference
        assertThat(orderReference.get().getClOrdID(), is(newMarketExecutionReport.getValue().getClOrdID().getValue()));
        assertThat(orderReference.get().getOrdStatus(), is(OrdStatus.CANCELED));
        assertThat(orderReference.get().getAvgPx(), is(defaultPrice));
        assertThat(orderReference.get().getPrice(), is(Order.NO_PRICE));
        assertThat(orderReference.get().getSide(), is(Side.SELL));
        assertThat(orderReference.get().getCumQty(), is(defaultOrdQty));
        assertThat(orderReference.get().getLeavesQty(), is(1L));
        assertThat(orderReference.get().getOrdType(), is(eugene.market.ontology.field.enums.OrdType.MARKET));

        container.kill();
    }
}
