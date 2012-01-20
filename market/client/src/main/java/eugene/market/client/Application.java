package eugene.market.client;

import eugene.market.ontology.Message;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.Logon;
import eugene.market.ontology.message.NewOrderSingle;
import eugene.market.ontology.message.OrderCancelReject;
import eugene.market.ontology.message.data.AddOrder;
import eugene.market.ontology.message.data.DeleteOrder;
import eugene.market.ontology.message.data.OrderExecuted;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/**
 * Interface for receiving {@link Message}s sent through and received by {@link Session}.
 *
 * @author Jakub D Kozlowski
 * @since 0.4
 */
public interface Application {

    /**
     * Called when a valid {@link Logon} has been established with the Market Agent. This method should be used by
     * {@link Application} implementations to register {@link Behaviour}s with the <code>agent</code>.
     *
     * @param logon   {@link Logon} message received from the Market Agent.
     * @param agent   {@link Agent} that is executing the this <code>session</code>.
     * @param session active {@link Session}.
     */
    void onLogon(final Logon logon, final Agent agent, final Session session);

    /**
     * This callback receives {@link ExecutionReport} for the application.
     *
     * @param executionReport received {@link ExecutionReport}.
     * @param session         {@link Session} that received this <code>executionReport</code>.
     */
    void toApp(final ExecutionReport executionReport, final Session session);

    /**
     * This callback receives {@link OrderCancelReject} for the application.
     *
     * @param orderCancelReject received {@link OrderCancelReject}.
     * @param session           {@link Session} that received this <code>orderCancelReject</code>.
     */
    void toApp(final OrderCancelReject orderCancelReject, final Session session);

    /**
     * This callback receives {@link AddOrder} for the application.
     *
     * @param addOrder received {@link AddOrder}.
     * @param session  {@link Session} that received this <code>addOrder</code>.
     */
    void toApp(final AddOrder addOrder, final Session session);

    /**
     * This callback receives {@link DeleteOrder} for the application.
     *
     * @param deleteOrder received {@link DeleteOrder}.
     * @param session     {@link Session} that received this <code>deleteOrder</code>.
     */
    void toApp(final DeleteOrder deleteOrder, final Session session);

    /**
     * This callback receives {@link OrderExecuted} for the application.
     *
     * @param orderExecuted received {@link OrderExecuted}.
     * @param session       {@link Session} that received this <code>orderExecuted</code>.
     */
    void toApp(final OrderExecuted orderExecuted, final Session session);

    /**
     * This callback receives {@link NewOrderSingle} before it is sent.
     *
     * @param newOrderSingle {@link NewOrderSingle} to send.
     * @param session        {@link Session} that will send this <code>newOrderSingle</code>.
     */
    void fromApp(final NewOrderSingle newOrderSingle, final Session session);
}
