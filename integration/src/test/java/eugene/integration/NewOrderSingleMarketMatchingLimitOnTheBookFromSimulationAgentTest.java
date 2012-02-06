package eugene.integration;

import com.google.common.collect.Sets;
import eugene.market.book.Order;
import eugene.market.client.Application;
import eugene.market.client.ApplicationAdapter;
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

import static eugene.integration.CountingApplication.TIMEOUT;
import static eugene.market.client.Applications.proxy;
import static eugene.market.client.Sessions.initiate;
import static eugene.market.esma.AbstractMarketAgentTest.getNakedContainer;
import static eugene.market.ontology.Defaults.defaultOrdQty;
import static eugene.market.ontology.Defaults.defaultOrderID;
import static eugene.market.ontology.Defaults.defaultPrice;
import static eugene.market.ontology.Defaults.defaultSymbol;
import static eugene.market.ontology.field.enums.OrdType.LIMIT;
import static eugene.market.ontology.field.enums.OrdType.MARKET;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Sends a {@link OrdType#MARKET} order that matches {@link OrdType#LIMIT} sent by the Simulation Agent.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class NewOrderSingleMarketMatchingLimitOnTheBookFromSimulationAgentTest {

    @Test
    public void newOrderSingleMarketMatchingLimitOnTheBookFromSimulationAgent()
            throws ControllerException, InterruptedException {

        final AgentContainer container = getNakedContainer();

        final CountDownLatch latch = new CountDownLatch(4);

        final Set<Order> orders = Sets.newHashSet();
        orders.add(new Order(Long.valueOf(defaultOrderID), LIMIT, Side.BUY, defaultOrdQty, defaultPrice));

        final Application application = mock(Application.class);
        final Application proxy = proxy(
                application,
                new ApplicationAdapter() {
                    @Override
                    public void onStart(Start start, Agent agent, Session session) {

                        final NewOrderSingle newOrderSingleMarket = new NewOrderSingle();
                        newOrderSingleMarket.setOrdType(MARKET.field());
                        newOrderSingleMarket.setOrderQty(new OrderQty(defaultOrdQty));
                        newOrderSingleMarket.setSide(Side.SELL.field());

                        session.send(newOrderSingleMarket);
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

        final InOrder inOrder = inOrder(application);

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
        assertThat(newOrderSingleMarket.getOrderQty().getValue(), is(defaultOrdQty));
        assertThat(newOrderSingleMarket.getOrdType(), is(MARKET.field()));
        assertThat(newOrderSingleMarket.getPrice(), nullValue());
        assertThat(newOrderSingleMarket.getSide(), is(Side.SELL.field()));

        final ArgumentCaptor<ExecutionReport> executionReport = ArgumentCaptor.forClass(ExecutionReport.class);
        inOrder.verify(application, times(2)).toApp(executionReport.capture(), any(Session.class));

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

        // OrderExecuted for Limit
        final ArgumentCaptor<OrderExecuted> orderExecuted = ArgumentCaptor.forClass(OrderExecuted.class);
        inOrder.verify(application).toApp(orderExecuted.capture(), any(Session.class));
        assertThat(orderExecuted.getValue().getOrderID().getValue(), is(defaultOrderID));
        assertThat(orderExecuted.getValue().getLastPx().getValue(), is(defaultPrice));
        assertThat(orderExecuted.getValue().getLastQty().getValue(), is(defaultOrdQty));
        assertThat(orderExecuted.getValue().getLeavesQty().getValue(), is(Order.NO_QTY));
        assertThat(orderExecuted.getValue().getTradeID().getValue(), is(Long.valueOf(1L).toString()));

        inOrder.verifyNoMoreInteractions();

        container.kill();
    }
}
