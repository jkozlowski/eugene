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
import eugene.market.ontology.field.ClOrdID;
import eugene.market.ontology.field.OrderQty;
import eugene.market.ontology.field.Price;
import eugene.market.ontology.field.enums.ExecType;
import eugene.market.ontology.field.enums.OrdStatus;
import eugene.market.ontology.field.enums.OrdType;
import eugene.market.ontology.field.enums.Side;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.NewOrderSingle;
import eugene.market.ontology.message.OrderCancelRequest;
import eugene.market.ontology.message.data.AddOrder;
import eugene.market.ontology.message.data.DeleteOrder;
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
import static eugene.market.esma.AbstractMarketAgentTest.getNakedContainer;
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
 * Creates a {@link OrdType#LIMIT} order and cancels it.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class NewOrderSingleLimitOrderCancelRequestTest {

    @Test
    public void newOrderSingleLimitOrderCancelRequest() throws ControllerException, InterruptedException {

        final AgentContainer container = getNakedContainer();

        final CountDownLatch latch = new CountDownLatch(4);
        final Application application = mock(Application.class);
        final OrderReferenceListener listener = mock(OrderReferenceListener.class);
        final AtomicReference<OrderReference> orderReference = new AtomicReference<OrderReference>();

        final Application proxy = proxy(
                application,
                new ApplicationAdapter() {
                    @Override
                    public void onStart(Start start, Agent agent, Session session) {

                        final NewOrderSingle newOrderSingle = new NewOrderSingle();
                        newOrderSingle.setOrdType(OrdType.LIMIT.field());
                        newOrderSingle.setOrderQty(new OrderQty(defaultOrdQty));
                        newOrderSingle.setPrice(new Price(defaultPrice));
                        newOrderSingle.setSide(Side.BUY.field());

                        orderReference.set(session.send(newOrderSingle, listener));

                        final OrderCancelRequest orderCancel = new OrderCancelRequest();
                        orderCancel.setClOrdID(new ClOrdID(orderReference.get().getClOrdID()));
                        orderCancel.setOrderQty(new OrderQty(orderReference.get().getOrderQty()));
                        orderCancel.setSide(orderReference.get().getSide().field());

                        session.send(orderCancel);
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

        final Symbol symbol = Symbols.getSymbol(defaultSymbol, defaultTickSize, defaultPrice);
        final SimulationAgent simulation = new SimulationAgent(symbol, TIMEOUT, Collections.EMPTY_SET, agents);
        final AgentController simulationController = container.acceptNewAgent(SimulationAgent.NAME, simulation);
        simulationController.start();

        latch.await(TIMEOUT, TimeUnit.MILLISECONDS);

        final InOrder inOrder = inOrder(application, listener);

        final ArgumentCaptor<NewOrderSingle> newOrderSingle = ArgumentCaptor.forClass(NewOrderSingle.class);
        inOrder.verify(application).fromApp(newOrderSingle.capture(), any(Session.class));
        assertThat(newOrderSingle.getValue().getSymbol().getValue(), is(defaultSymbol));
        assertThat(newOrderSingle.getValue().getOrderQty().getValue(), is(defaultOrdQty));
        assertThat(newOrderSingle.getValue().getOrdType(), is(OrdType.LIMIT.field()));
        assertThat(newOrderSingle.getValue().getPrice().getValue(), is(defaultPrice));
        assertThat(newOrderSingle.getValue().getSide(), is(Side.BUY.field()));

        // NEW ExecutionReport
        final ArgumentCaptor<ExecutionReport> newExecutionReportListener
                = ArgumentCaptor.forClass(ExecutionReport.class);
        inOrder.verify(listener).newEvent(newExecutionReportListener.capture(), eq(orderReference.get()),
                                          any(Session.class));

        final ArgumentCaptor<ExecutionReport> newExecutionReport = ArgumentCaptor.forClass(ExecutionReport.class);
        inOrder.verify(application).toApp(newExecutionReport.capture(), any(Session.class));
        assertThat(newExecutionReport.getValue(), sameInstance(newExecutionReportListener.getValue()));
        assertThat(newExecutionReport.getValue().getAvgPx().getValue(), is(Order.NO_PRICE));
        assertThat(newExecutionReport.getValue().getClOrdID().getValue(), is(
                newOrderSingle.getValue().getClOrdID().getValue()));
        assertThat(newExecutionReport.getValue().getCumQty().getValue(), is(Order.NO_QTY));
        assertThat(newExecutionReport.getValue().getExecType(), is(ExecType.NEW.field()));
        assertThat(newExecutionReport.getValue().getLeavesQty().getValue(), is(defaultOrdQty));
        assertThat(newExecutionReport.getValue().getOrdStatus(), is(OrdStatus.NEW.field()));
        assertThat(newExecutionReport.getValue().getSide(), is(Side.BUY.field()));
        assertThat(newExecutionReport.getValue().getSymbol().getValue(), is(defaultSymbol));
        assertThat(newExecutionReport.getValue().getLastPx(), nullValue());
        assertThat(newExecutionReport.getValue().getLastQty(), nullValue());

        // AddOrder for the limit order
        final ArgumentCaptor<AddOrder> addOrder = ArgumentCaptor.forClass(AddOrder.class);
        inOrder.verify(application).toApp(addOrder.capture(), any(Session.class));
        assertThat(addOrder.getValue().getOrderID().getValue(), is(
                newExecutionReport.getValue().getOrderID().getValue()));
        assertThat(addOrder.getValue().getPrice().getValue(), is(defaultPrice));
        assertThat(addOrder.getValue().getOrderQty().getValue(), is(defaultOrdQty));
        assertThat(addOrder.getValue().getSide(), is(Side.BUY.field()));
        assertThat(addOrder.getValue().getSymbol().getValue(), is(defaultSymbol));

        // CANCELED ExecutionReport
        final ArgumentCaptor<ExecutionReport> cancelExecutionReportListener
                = ArgumentCaptor.forClass(ExecutionReport.class);
        inOrder.verify(listener).canceledEvent(cancelExecutionReportListener.capture(), eq(orderReference.get()),
                                               any(Session.class));

        final ArgumentCaptor<ExecutionReport> cancelExecutionReport = ArgumentCaptor.forClass(ExecutionReport.class);
        inOrder.verify(application).toApp(cancelExecutionReport.capture(), any(Session.class));
        assertThat(cancelExecutionReport.getValue().getAvgPx().getValue(), is(Order.NO_PRICE));
        assertThat(cancelExecutionReport.getValue().getClOrdID().getValue(), is(
                newOrderSingle.getValue().getClOrdID().getValue()));
        assertThat(cancelExecutionReport.getValue().getCumQty().getValue(), is(Order.NO_QTY));
        assertThat(cancelExecutionReport.getValue().getExecType(), is(ExecType.CANCELED.field()));
        assertThat(cancelExecutionReport.getValue().getLeavesQty().getValue(), is(defaultOrdQty));
        assertThat(cancelExecutionReport.getValue().getOrdStatus(), is(OrdStatus.CANCELED.field()));
        assertThat(cancelExecutionReport.getValue().getSide(), is(Side.BUY.field()));
        assertThat(cancelExecutionReport.getValue().getSymbol().getValue(), is(defaultSymbol));
        assertThat(cancelExecutionReport.getValue().getLastPx(), nullValue());
        assertThat(cancelExecutionReport.getValue().getLastQty(), nullValue());

        final ArgumentCaptor<DeleteOrder> deleteOrder = ArgumentCaptor.forClass(DeleteOrder.class);
        inOrder.verify(application).toApp(deleteOrder.capture(), any(Session.class));
        assertThat(deleteOrder.getValue().getOrderID().getValue(), is(
                newExecutionReport.getValue().getOrderID().getValue()));

        inOrder.verifyNoMoreInteractions();

        // Check OrderReference
        assertThat(orderReference.get().getClOrdID(), is(newExecutionReport.getValue().getClOrdID().getValue()));
        assertThat(orderReference.get().getOrdStatus(), is(OrdStatus.CANCELED));
        assertThat(orderReference.get().getAvgPx(), is(Order.NO_PRICE));
        assertThat(orderReference.get().getPrice(), is(defaultPrice));
        assertThat(orderReference.get().getSide(), is(Side.BUY));
        assertThat(orderReference.get().getCumQty(), is(0L));
        assertThat(orderReference.get().getLeavesQty(), is(defaultOrdQty));
        assertThat(orderReference.get().getOrdType(), is(OrdType.LIMIT));

        container.kill();
    }
}
