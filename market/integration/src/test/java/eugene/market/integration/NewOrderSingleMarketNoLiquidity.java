package eugene.market.integration;

import eugene.market.book.Order;
import eugene.market.client.Application;
import eugene.market.client.ApplicationAdapter;
import eugene.market.client.Session;
import eugene.market.esma.AbstractMarketAgentTest;
import eugene.market.ontology.field.OrderQty;
import eugene.market.ontology.field.enums.ExecType;
import eugene.market.ontology.field.enums.OrdStatus;
import eugene.market.ontology.field.enums.OrdType;
import eugene.market.ontology.field.enums.Side;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.Logon;
import eugene.market.ontology.message.NewOrderSingle;
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
import static eugene.market.ontology.Defaults.defaultSymbol;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

/**
 * Creates a {@link OrdType#MARKET} when there is no liquidity.
 *
 * @author Jakub D Kozlowski
 * @since 0.6
 */
public class NewOrderSingleMarketNoLiquidity extends AbstractMarketAgentTest {

    @Test
    public void newOrderSingleMarketNoLiquidity() throws StaleProxyException, InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final Application application = mock(Application.class);
        final Application proxy = proxy(application,
            new ApplicationAdapter() {
                @Override
                public void onLogon(Logon logon, Agent agent, Session session) {
                    final NewOrderSingle newOrderSingle = new NewOrderSingle();
                    newOrderSingle.setOrdType(OrdType.MARKET.field());
                    newOrderSingle.setOrderQty(new OrderQty(defaultOrdQty));
                    newOrderSingle.setSide(Side.BUY.field());

                    session.send(newOrderSingle);
                }
            }, new CountingApplication(latch));

        submit(initiate(traderAgent, proxy, defaultSymbol));

        latch.await(CountingApplication.TIMEOUT, TimeUnit.MILLISECONDS);

        final InOrder inOrder = inOrder(application);

        final ArgumentCaptor<NewOrderSingle> newOrderSingle = ArgumentCaptor.forClass(NewOrderSingle.class);
        inOrder.verify(application).fromApp(newOrderSingle.capture(), any(Session.class));
        assertThat(newOrderSingle.getValue().getSymbol().getValue(), is(defaultSymbol));
        assertThat(newOrderSingle.getValue().getOrderQty().getValue(), is(defaultOrdQty));
        assertThat(newOrderSingle.getValue().getOrdType(), is(OrdType.MARKET.field()));
        assertThat(newOrderSingle.getValue().getSide(), is(Side.BUY.field()));

        final ArgumentCaptor<ExecutionReport> rejectedNewOrderSingle = ArgumentCaptor.forClass(ExecutionReport.class);
        inOrder.verify(application).toApp(rejectedNewOrderSingle.capture(), any(Session.class));
        assertThat(rejectedNewOrderSingle.getValue().getAvgPx().getValue(), is(Order.NO_PRICE));
        assertThat(rejectedNewOrderSingle.getValue().getClOrdID().getValue(), is(
                newOrderSingle.getValue().getClOrdID().getValue()));
        assertThat(rejectedNewOrderSingle.getValue().getCumQty().getValue(), is(Order.NO_QTY));
        assertThat(rejectedNewOrderSingle.getValue().getExecType(), is(ExecType.REJECTED.field()));
        assertThat(rejectedNewOrderSingle.getValue().getLeavesQty().getValue(), is(defaultOrdQty));
        assertThat(rejectedNewOrderSingle.getValue().getOrdStatus(), is(OrdStatus.REJECTED.field()));
        assertThat(rejectedNewOrderSingle.getValue().getSide(), is(Side.BUY.field()));
        assertThat(rejectedNewOrderSingle.getValue().getSymbol().getValue(), is(defaultSymbol));

        inOrder.verifyNoMoreInteractions();
    }
}
