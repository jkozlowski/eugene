package eugene.market.integration;

import eugene.market.book.Order;
import eugene.market.client.Application;
import eugene.market.client.ApplicationAdapter;
import eugene.market.client.Session;
import eugene.market.esma.AbstractMarketAgentTest;
import eugene.market.ontology.field.OrdType;
import eugene.market.ontology.field.OrderQty;
import eugene.market.ontology.field.Price;
import eugene.market.ontology.field.enums.ExecType;
import eugene.market.ontology.field.enums.OrdStatus;
import eugene.market.ontology.field.enums.Side;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.Logon;
import eugene.market.ontology.message.NewOrderSingle;
import eugene.market.ontology.message.data.AddOrder;
import eugene.market.ontology.message.data.OrderExecuted;
import jade.core.Agent;
import jade.wrapper.StaleProxyException;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.testng.annotations.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static eugene.market.client.Applications.proxy;
import static eugene.market.client.Sessions.initiate;
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
 * Sends a {@link OrdType#LIMIT} order and bigger {@link OrdType#MARKET} order.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class NewOrderSingleMarketPartialFill extends AbstractMarketAgentTest {

    @Test
    public void newOrderSingleMarketPartialFill() throws StaleProxyException, InterruptedException {
        final CountDownLatch latch = new CountDownLatch(6);
        final Application application = mock(Application.class);
        final Application proxy = proxy(application,
            new ApplicationAdapter() {
                @Override
                public void onLogon(Logon logon, Agent agent, Session session) {

                    final NewOrderSingle newOrderSingleLimit = new NewOrderSingle();
                    newOrderSingleLimit.setOrdType(
                            eugene.market.ontology.field.enums.OrdType.LIMIT.field());
                    newOrderSingleLimit.setOrderQty(new OrderQty(defaultOrdQty));
                    newOrderSingleLimit.setPrice(new Price(defaultPrice));
                    newOrderSingleLimit.setSide(Side.BUY.field());

                    session.send(newOrderSingleLimit);

                    final NewOrderSingle newOrderSingleMarket = new NewOrderSingle();
                    newOrderSingleMarket.setOrdType(
                            eugene.market.ontology.field.enums.OrdType.MARKET.field());
                    newOrderSingleMarket.setOrderQty(new OrderQty(defaultOrdQty + 1L));
                    newOrderSingleMarket.setSide(Side.SELL.field());

                    session.send(newOrderSingleMarket);
                }
            }, new CountingApplication(latch));

        submit(initiate(traderAgent, proxy, defaultSymbol));

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
        assertThat(newOrderSingleMarket.getOrderQty().getValue(), is(defaultOrdQty + 1L));
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
        assertThat(newMarketExecutionReport.getLeavesQty().getValue(), is(defaultOrdQty + 1L));
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
        assertThat(tradeMarketExecutionReport.getLeavesQty().getValue(), is(1L));
        assertThat(tradeMarketExecutionReport.getOrdStatus(), is(OrdStatus.PARTIALLY_FILLED.field()));
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

        // TRADE ExecutionReport for Market
        final ArgumentCaptor<ExecutionReport> canceledExecutionReportCaptor = ArgumentCaptor.forClass(ExecutionReport.class);
        inOrder.verify(application).toApp(canceledExecutionReportCaptor.capture(), any(Session.class));
        final ExecutionReport canceledMarketExecutionReport = canceledExecutionReportCaptor.getValue();
        assertThat(canceledMarketExecutionReport.getAvgPx().getValue(), is(defaultPrice));
        assertThat(canceledMarketExecutionReport.getClOrdID().getValue(),
                   is(newOrderSingleMarket.getClOrdID().getValue()));
        assertThat(canceledMarketExecutionReport.getCumQty().getValue(), is(defaultOrdQty));
        assertThat(canceledMarketExecutionReport.getExecType(), is(ExecType.CANCELED.field()));
        assertThat(canceledMarketExecutionReport.getLeavesQty().getValue(), is(1L));
        assertThat(canceledMarketExecutionReport.getOrdStatus(), is(OrdStatus.CANCELED.field()));
        assertThat(canceledMarketExecutionReport.getSide(), is(Side.SELL.field()));
        assertThat(canceledMarketExecutionReport.getSymbol().getValue(), is(defaultSymbol));

        inOrder.verifyNoMoreInteractions();
    }
}
