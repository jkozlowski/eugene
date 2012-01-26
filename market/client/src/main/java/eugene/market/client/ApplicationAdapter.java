package eugene.market.client;

import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.Logon;
import eugene.market.ontology.message.NewOrderSingle;
import eugene.market.ontology.message.OrderCancelReject;
import eugene.market.ontology.message.OrderCancelRequest;
import eugene.market.ontology.message.data.AddOrder;
import eugene.market.ontology.message.data.DeleteOrder;
import eugene.market.ontology.message.data.OrderExecuted;
import jade.core.Agent;

/**
 * Adapter for {@link Application}.
 *
 * @author Jakub D Kozlowski
 * @since 0.5
 */
public abstract class ApplicationAdapter implements Application {

    @Override
    public void onLogon(final Logon logon, final Agent agent, final Session session) {
    }

    @Override
    public void toApp(final ExecutionReport executionReport, final Session session) {
    }

    @Override
    public void toApp(final OrderCancelReject orderCancelReject, final Session session) {
    }

    @Override
    public void toApp(final AddOrder addOrder, final Session session) {
    }

    @Override
    public void toApp(final DeleteOrder deleteOrder, final Session session) {
    }

    @Override
    public void toApp(final OrderExecuted orderExecuted, final Session session) {
    }

    @Override
    public void fromApp(final NewOrderSingle newOrderSingle, final Session session) {
    }

    @Override
    public void fromApp(final OrderCancelRequest orderCancelRequest, final Session session) {
    }
}
