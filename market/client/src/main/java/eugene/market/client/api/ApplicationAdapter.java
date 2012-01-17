package eugene.market.client.api;

import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.Logon;
import eugene.market.ontology.message.OrderCancelReject;
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
    public void onLogon(Logon logon, Agent agent) {
    }

    @Override
    public void toApp(ExecutionReport executionReport, Session session) {
    }

    @Override
    public void toApp(OrderCancelReject orderCancelReject, Session session) {
    }

    @Override
    public void toApp(AddOrder addOrder, Session session) {
    }

    @Override
    public void toApp(DeleteOrder deleteOrder, Session session) {
    }

    @Override
    public void toApp(OrderExecuted orderExecuted, Session session) {
    }
}
