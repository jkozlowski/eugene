package eugene.agent.random.impl.behaviour;

import eugene.market.client.api.ApplicationAdapter;
import eugene.market.client.api.Session;
import eugene.market.ontology.message.Logon;
import eugene.market.ontology.message.data.AddOrder;
import eugene.market.ontology.message.data.DeleteOrder;
import eugene.market.ontology.message.data.OrderExecuted;
import jade.core.Agent;

/**
 *
 *
 * @author Jakub D Kozlowski
 * @since 0.5
 */
public class RandomTraderApplication extends ApplicationAdapter {



    @Override
    public void onLogon(Logon logon, Agent agent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void toApp(AddOrder addOrder, Session session) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void toApp(DeleteOrder deleteOrder, Session session) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void toApp(OrderExecuted orderExecuted, Session session) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
