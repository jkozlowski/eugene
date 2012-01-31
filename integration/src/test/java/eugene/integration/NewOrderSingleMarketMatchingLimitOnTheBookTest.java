package eugene.integration;

import com.google.common.collect.Sets;
import eugene.market.book.Order;
import eugene.market.client.Application;
import eugene.market.client.ApplicationAdapter;
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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

/**
 * Sends a {@link OrdType#LIMIT} order and matching {@link OrdType#MARKET} order that matches the {@link OrderQty}.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class NewOrderSingleMarketMatchingLimitOnTheBookTest {

    @Test
    public void newOrderSingleMarketMatchingLimitOnTheBook() throws ControllerException, InterruptedException {

        final AgentContainer container = getNakedContainer();

        final CountDownLatch latch = new CountDownLatch(6);
        final Application application = mock(Application.class);
        final Application proxy = proxy(application,
            new ApplicationAdapter() {
                @Override
                public void onStart(Start start, Agent agent, Session session) {

                    final NewOrderSingle newOrderSingleLimit = new NewOrderSingle();
                    newOrderSingleLimit.setOrdType(eugene.market.ontology.field.enums.OrdType.LIMIT.field());
                    newOrderSingleLimit.setOrderQty(new OrderQty(defaultOrdQty));
                    newOrderSingleLimit.setPrice(new Price(defaultPrice));
                    newOrderSingleLimit.setSide(Side.BUY.field());

                    session.send(newOrderSingleLimit);

                    final NewOrderSingle newOrderSingleMarket = new NewOrderSingle();
                    newOrderSingleMarket.setOrdType(eugene.market.ontology.field.enums.OrdType.MARKET.field());
                    newOrderSingleMarket.setOrderQty(new OrderQty(defaultOrdQty));
                    newOrderSingleMarket.setSide(Side.SELL.field());

                    session.send(newOrderSingleMarket);
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

        final InOrder inOrder = inOrder(application);

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
        assertThat(newOrderSingleMarket.getOrderQty().getValue(), is(defaultOrdQty));
        assertThat(newOrderSingleMarket.getOrdType(), is(eugene.market.ontology.field.enums.OrdType.MARKET.field()));
        assertThat(newOrderSingleMarket.getPrice(), nullValue());
        assertThat(newOrderSingleMarket.getSide(), is(Side.SELL.field()));

        // Verify incoming messages

        // NEW ExecutionReport for Limit
        final ArgumentCaptor<ExecutionReport> newLimitExecutionReport = ArgumentCaptor.forClass(ExecutionReport.class);
        inOrder.verify(application).toApp(newLimitExecutionReport.capture(), any(Session.class));
        assertThat(newLimitExecutionReport.getValue().getAvgPx().getValue(), is(Order.NO_PRICE));
        assertThat(newLimitExecutionReport.getValue().getClOrdID().getValue(),
                   is(newOrderSingleLimit.getClOrdID().getValue()));
        assertThat(newLimitExecutionReport.getValue().getCumQty().getValue(), is(Order.NO_QTY));
        assertThat(newLimitExecutionReport.getValue().getExecType(), is(ExecType.NEW.field()));
        assertThat(newLimitExecutionReport.getValue().getLeavesQty().getValue(), is(defaultOrdQty));
        assertThat(newLimitExecutionReport.getValue().getOrdStatus(), is(OrdStatus.NEW.field()));
        assertThat(newLimitExecutionReport.getValue().getSide(), is(Side.BUY.field()));
        assertThat(newLimitExecutionReport.getValue().getSymbol().getValue(), is(defaultSymbol));

        // AddOrder for Limit
        final ArgumentCaptor<AddOrder> addOrder = ArgumentCaptor.forClass(AddOrder.class);
        inOrder.verify(application).toApp(addOrder.capture(), any(Session.class));
        assertThat(addOrder.getValue().getOrderID().getValue(), is(
                newLimitExecutionReport.getValue().getOrderID().getValue()));
        assertThat(addOrder.getValue().getPrice().getValue(), is(defaultPrice));
        assertThat(addOrder.getValue().getOrderQty().getValue(), is(defaultOrdQty));
        assertThat(addOrder.getValue().getSide(), is(Side.BUY.field()));
        assertThat(addOrder.getValue().getSymbol().getValue(), is(defaultSymbol));

        
        final ArgumentCaptor<ExecutionReport> executionReport = ArgumentCaptor.forClass(ExecutionReport.class);
        inOrder.verify(application, times(3)).toApp(executionReport.capture(), any(Session.class));

        // NEW ExecutionReport for Market
        final ExecutionReport newMarketExecutionReport = executionReport.getAllValues().get(0);
        assertThat(newMarketExecutionReport.getAvgPx().getValue(), is(Order.NO_PRICE));
        assertThat(newMarketExecutionReport.getClOrdID().getValue(),
                   is(newOrderSingleMarket.getClOrdID().getValue()));
        assertThat(newMarketExecutionReport.getCumQty().getValue(), is(Order.NO_QTY));
        assertThat(newMarketExecutionReport.getExecType(), is(ExecType.NEW.field()));
        assertThat(newMarketExecutionReport.getLeavesQty().getValue(), is(defaultOrdQty));
        assertThat(newMarketExecutionReport.getOrdStatus(), is(OrdStatus.NEW.field()));
        assertThat(newMarketExecutionReport.getSide(), is(Side.SELL.field()));
        assertThat(newMarketExecutionReport.getSymbol().getValue(), is(defaultSymbol));

        // TRADE ExecutionReport for Market
        final ExecutionReport tradeMarketExecutionReport = executionReport.getAllValues().get(1);
        assertThat(tradeMarketExecutionReport.getAvgPx().getValue(), is(defaultPrice));
        assertThat(tradeMarketExecutionReport.getClOrdID().getValue(),
                   is(newOrderSingleMarket.getClOrdID().getValue()));
        assertThat(tradeMarketExecutionReport.getCumQty().getValue(), is(defaultOrdQty));
        assertThat(tradeMarketExecutionReport.getExecType(), is(ExecType.TRADE.field()));
        assertThat(tradeMarketExecutionReport.getLeavesQty().getValue(), is(Order.NO_QTY));
        assertThat(tradeMarketExecutionReport.getOrdStatus(), is(OrdStatus.FILLED.field()));
        assertThat(tradeMarketExecutionReport.getSide(), is(Side.SELL.field()));
        assertThat(tradeMarketExecutionReport.getSymbol().getValue(), is(defaultSymbol));

        // TRADE ExecutionReport for Limit
        final ExecutionReport tradeLimitExecutionReport = executionReport.getAllValues().get(2);
        assertThat(tradeLimitExecutionReport.getAvgPx().getValue(), is(defaultPrice));
        assertThat(tradeLimitExecutionReport.getClOrdID().getValue(),
                   is(newOrderSingleLimit.getClOrdID().getValue()));
        assertThat(tradeLimitExecutionReport.getCumQty().getValue(), is(defaultOrdQty));
        assertThat(tradeLimitExecutionReport.getExecType(), is(ExecType.TRADE.field()));
        assertThat(tradeLimitExecutionReport.getLeavesQty().getValue(), is(Order.NO_QTY));
        assertThat(tradeLimitExecutionReport.getOrdStatus(), is(OrdStatus.FILLED.field()));
        assertThat(tradeLimitExecutionReport.getSide(), is(Side.BUY.field()));
        assertThat(tradeLimitExecutionReport.getSymbol().getValue(), is(defaultSymbol));

        // OrderExecuted for Limit
        final ArgumentCaptor<OrderExecuted> orderExecuted = ArgumentCaptor.forClass(OrderExecuted.class);
        inOrder.verify(application).toApp(orderExecuted.capture(), any(Session.class));
        assertThat(orderExecuted.getValue().getOrderID().getValue(),
                   is(newLimitExecutionReport.getValue().getOrderID().getValue()));
        assertThat(orderExecuted.getValue().getLastPx().getValue(), is(defaultPrice));
        assertThat(orderExecuted.getValue().getLastQty().getValue(), is(defaultOrdQty));
        assertThat(orderExecuted.getValue().getLeavesQty().getValue(), is(Order.NO_QTY));
        assertThat(orderExecuted.getValue().getTradeID().getValue(), is(Long.valueOf(1L).toString()));

        inOrder.verifyNoMoreInteractions();

        container.kill();
    }
}
