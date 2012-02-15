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
import eugene.market.ontology.field.Price;
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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

/**
 * Sends a {@link OrdType#LIMIT} order and bigger {@link OrdType#MARKET} order.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class NewOrderSingleMarketPartialFillTest {

    @Test
    public void newOrderSingleMarketPartialFill() throws ControllerException, InterruptedException {

        final AgentContainer container = getNakedContainer();

        final CountDownLatch latch = new CountDownLatch(7);
        final Application application = mock(Application.class);
        final AtomicReference<OrderReference> limitOrderReference = new AtomicReference<OrderReference>();
        final AtomicReference<OrderReference> marketOrderReference = new AtomicReference<OrderReference>();
        final OrderReferenceListener limitListener = mock(OrderReferenceListener.class);
        final OrderReferenceListener marketListener = mock(OrderReferenceListener.class);
        final Application proxy = proxy(application,
                                        new ApplicationAdapter() {
                                            @Override
                                            public void onStart(Start start, Agent agent, Session session) {

                                                final NewOrderSingle newOrderSingleLimit = new NewOrderSingle();
                                                newOrderSingleLimit.setOrdType(
                                                        eugene.market.ontology.field.enums.OrdType.LIMIT.field());
                                                newOrderSingleLimit.setOrderQty(new OrderQty(defaultOrdQty));
                                                newOrderSingleLimit.setPrice(new Price(defaultPrice));
                                                newOrderSingleLimit.setSide(Side.BUY.field());

                                                limitOrderReference.set(session.send(newOrderSingleLimit,
                                                                                     limitListener));

                                                final NewOrderSingle newOrderSingleMarket = new NewOrderSingle();
                                                newOrderSingleMarket.setOrdType(
                                                        eugene.market.ontology.field.enums.OrdType.MARKET.field());
                                                newOrderSingleMarket.setOrderQty(new OrderQty(defaultOrdQty + 1L));
                                                newOrderSingleMarket.setSide(Side.SELL.field());

                                                marketOrderReference.set(session.send(newOrderSingleMarket,
                                                                                      marketListener));
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

        final SimulationAgent simulation = new SimulationAgent(defaultSymbol, TIMEOUT, Collections.EMPTY_SET, agents);
        final AgentController simulationController = container.acceptNewAgent(SimulationAgent.NAME, simulation);
        simulationController.start();

        latch.await(CountingApplication.TIMEOUT, TimeUnit.MILLISECONDS);

        final InOrder inOrder = inOrder(application, limitListener, marketListener);

        // Verify outgoing messages
        final ArgumentCaptor<NewOrderSingle> newOrderSingleCaptor = ArgumentCaptor.forClass(NewOrderSingle.class);
        inOrder.verify(application, times(2)).fromApp(newOrderSingleCaptor.capture(), any(Session.class));

        // NewOrderSingle for the Limit order
        final NewOrderSingle newOrderSingleLimit = newOrderSingleCaptor.getAllValues().get(0);
        assertThat(newOrderSingleLimit.getSymbol().getValue(), is(defaultSymbol));
        assertThat(newOrderSingleLimit.getOrderQty().getValue(), is(defaultOrdQty));
        assertThat(newOrderSingleLimit.getOrdType(), is(eugene.market.ontology.field.enums.OrdType.LIMIT.field()));
        assertThat(newOrderSingleLimit.getPrice().getValue(), is(defaultPrice));
        assertThat(newOrderSingleLimit.getSide(), is(Side.BUY.field()));

        // NewOrderSingle for the Market order
        final NewOrderSingle newOrderSingleMarket = newOrderSingleCaptor.getAllValues().get(1);
        assertThat(newOrderSingleMarket.getSymbol().getValue(), is(defaultSymbol));
        assertThat(newOrderSingleMarket.getOrderQty().getValue(), is(defaultOrdQty + 1L));
        assertThat(newOrderSingleMarket.getOrdType(), is(eugene.market.ontology.field.enums.OrdType.MARKET.field()));
        assertThat(newOrderSingleMarket.getPrice(), nullValue());
        assertThat(newOrderSingleMarket.getSide(), is(Side.SELL.field()));

        // Verify incoming messages

        // NEW ExecutionReport for Limit received by the limit listener
        final ArgumentCaptor<ExecutionReport> newExecutionReportLimitListener
                = ArgumentCaptor.forClass(ExecutionReport.class);
        inOrder.verify(limitListener).newEvent(newExecutionReportLimitListener.capture(), eq(limitOrderReference.get()),
                                               any(Session.class));

        // NEW ExecutionReport for Limit
        final ArgumentCaptor<ExecutionReport> newLimitExecutionReport = ArgumentCaptor.forClass(ExecutionReport.class);
        inOrder.verify(application).toApp(newLimitExecutionReport.capture(), any(Session.class));
        assertThat(newLimitExecutionReport.getValue(), sameInstance(newExecutionReportLimitListener.getValue()));
        assertThat(newLimitExecutionReport.getValue().getAvgPx().getValue(), is(Order.NO_PRICE));
        assertThat(newLimitExecutionReport.getValue().getClOrdID().getValue(),
                   is(newOrderSingleLimit.getClOrdID().getValue()));
        assertThat(newLimitExecutionReport.getValue().getCumQty().getValue(), is(Order.NO_QTY));
        assertThat(newLimitExecutionReport.getValue().getExecType(), is(ExecType.NEW.field()));
        assertThat(newLimitExecutionReport.getValue().getLeavesQty().getValue(), is(defaultOrdQty));
        assertThat(newLimitExecutionReport.getValue().getOrdStatus(), is(OrdStatus.NEW.field()));
        assertThat(newLimitExecutionReport.getValue().getSide(), is(Side.BUY.field()));
        assertThat(newLimitExecutionReport.getValue().getSymbol().getValue(), is(defaultSymbol));
        assertThat(newLimitExecutionReport.getValue().getLastPx(), nullValue());
        assertThat(newLimitExecutionReport.getValue().getLastQty(), nullValue());

        // AddOrder for Limit
        final ArgumentCaptor<AddOrder> addOrder = ArgumentCaptor.forClass(AddOrder.class);
        inOrder.verify(application).toApp(addOrder.capture(), any(Session.class));
        assertThat(addOrder.getValue().getOrderID().getValue(), is(
                newLimitExecutionReport.getValue().getOrderID().getValue()));
        assertThat(addOrder.getValue().getPrice().getValue(), is(defaultPrice));
        assertThat(addOrder.getValue().getOrderQty().getValue(), is(defaultOrdQty));
        assertThat(addOrder.getValue().getSide(), is(Side.BUY.field()));
        assertThat(addOrder.getValue().getSymbol().getValue(), is(defaultSymbol));

        // NEW ExecutionReport for Market received by the market listener
        final ArgumentCaptor<ExecutionReport> newExecutionReportMarketListener
                = ArgumentCaptor.forClass(ExecutionReport.class);
        inOrder.verify(marketListener).newEvent(newExecutionReportMarketListener.capture(),
                                                eq(marketOrderReference.get()),
                                                any(Session.class));

        // NEW ExecutionReport for Market
        final ArgumentCaptor<ExecutionReport> newMarketExecutionReport = ArgumentCaptor.forClass(ExecutionReport.class);
        inOrder.verify(application).toApp(newMarketExecutionReport.capture(), any(Session.class));
        assertThat(newMarketExecutionReport.getValue(), is(newExecutionReportMarketListener.getValue()));
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

        // TRADE ExecutionReport for Market received by the market listener
        final ArgumentCaptor<ExecutionReport> tradeExecutionReportMarketListener
                = ArgumentCaptor.forClass(ExecutionReport.class);
        inOrder.verify(marketListener).tradeEvent(tradeExecutionReportMarketListener.capture(),
                                                  eq(marketOrderReference.get()),
                                                  any(Session.class));

        // TRADE ExecutionReport for Market
        final ArgumentCaptor<ExecutionReport> tradeMarketExecutionReport
                = ArgumentCaptor.forClass(ExecutionReport.class);
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

        // TRADE ExecutionReport for Limit received by the limit listener
        final ArgumentCaptor<ExecutionReport> tradeExecutionReportLimitListener
                = ArgumentCaptor.forClass(ExecutionReport.class);
        inOrder.verify(limitListener).tradeEvent(tradeExecutionReportLimitListener.capture(),
                                                 eq(limitOrderReference.get()),
                                                 any(Session.class));

        // TRADE ExecutionReport for Limit
        final ArgumentCaptor<ExecutionReport> tradeLimitExecutionReport
                = ArgumentCaptor.forClass(ExecutionReport.class);
        inOrder.verify(application).toApp(tradeLimitExecutionReport.capture(), any(Session.class));
        assertThat(tradeLimitExecutionReport.getValue(), sameInstance(tradeExecutionReportLimitListener.getValue()));
        assertThat(tradeLimitExecutionReport.getValue().getAvgPx().getValue(), is(defaultPrice));
        assertThat(tradeLimitExecutionReport.getValue().getClOrdID().getValue(),
                   is(newOrderSingleLimit.getClOrdID().getValue()));
        assertThat(tradeLimitExecutionReport.getValue().getCumQty().getValue(), is(defaultOrdQty));
        assertThat(tradeLimitExecutionReport.getValue().getExecType(), is(ExecType.TRADE.field()));
        assertThat(tradeLimitExecutionReport.getValue().getLeavesQty().getValue(), is(Order.NO_QTY));
        assertThat(tradeLimitExecutionReport.getValue().getOrdStatus(), is(OrdStatus.FILLED.field()));
        assertThat(tradeLimitExecutionReport.getValue().getSide(), is(Side.BUY.field()));
        assertThat(tradeLimitExecutionReport.getValue().getSymbol().getValue(), is(defaultSymbol));
        assertThat(tradeLimitExecutionReport.getValue().getLastPx().getValue(), is(defaultPrice));
        assertThat(tradeLimitExecutionReport.getValue().getLastQty().getValue(), is(defaultOrdQty));

        // OrderExecuted for Limit
        final ArgumentCaptor<OrderExecuted> orderExecuted = ArgumentCaptor.forClass(OrderExecuted.class);
        inOrder.verify(application).toApp(orderExecuted.capture(), any(Session.class));
        assertThat(orderExecuted.getValue().getOrderID().getValue(),
                   is(newLimitExecutionReport.getValue().getOrderID().getValue()));
        assertThat(orderExecuted.getValue().getLastPx().getValue(), is(defaultPrice));
        assertThat(orderExecuted.getValue().getLastQty().getValue(), is(defaultOrdQty));
        assertThat(orderExecuted.getValue().getLeavesQty().getValue(), is(Order.NO_QTY));
        assertThat(orderExecuted.getValue().getTradeID().getValue(), is(Long.valueOf(1L).toString()));

        // CANCELED ExecutionReport for Market received by market listener
        final ArgumentCaptor<ExecutionReport> cancelExecutionReportMarketListener
                = ArgumentCaptor.forClass(ExecutionReport.class);
        inOrder.verify(marketListener).canceledEvent(cancelExecutionReportMarketListener.capture(),
                                                     eq(marketOrderReference.get()),
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

        // Verify OrderReferences
        assertThat(limitOrderReference.get().getClOrdID(), is(
                newLimitExecutionReport.getValue().getClOrdID().getValue()));
        assertThat(limitOrderReference.get().getOrdStatus(), is(OrdStatus.FILLED));
        assertThat(limitOrderReference.get().getAvgPx(), is(defaultPrice));
        assertThat(limitOrderReference.get().getPrice(), is(defaultPrice));
        assertThat(limitOrderReference.get().getSide(), is(Side.BUY));
        assertThat(limitOrderReference.get().getCumQty(), is(defaultOrdQty));
        assertThat(limitOrderReference.get().getLeavesQty(), is(0L));
        assertThat(limitOrderReference.get().getOrdType(), is(eugene.market.ontology.field.enums.OrdType.LIMIT));

        assertThat(marketOrderReference.get().getClOrdID(),
                   is(newMarketExecutionReport.getValue().getClOrdID().getValue()));
        assertThat(marketOrderReference.get().getOrdStatus(), is(OrdStatus.CANCELED));
        assertThat(marketOrderReference.get().getAvgPx(), is(defaultPrice));
        assertThat(marketOrderReference.get().getPrice(), is(Order.NO_PRICE));
        assertThat(marketOrderReference.get().getSide(), is(Side.SELL));
        assertThat(marketOrderReference.get().getCumQty(), is(defaultOrdQty));
        assertThat(marketOrderReference.get().getLeavesQty(), is(1L));
        assertThat(marketOrderReference.get().getOrdType(), is(eugene.market.ontology.field.enums.OrdType.MARKET));

        container.kill();
    }
}
