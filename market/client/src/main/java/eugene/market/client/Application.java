package eugene.market.client;

import eugene.market.ontology.Message;
import eugene.market.ontology.message.ExecutionReport;
import eugene.market.ontology.message.NewOrderSingle;
import eugene.market.ontology.message.OrderCancelReject;
import eugene.market.ontology.message.OrderCancelRequest;
import eugene.market.ontology.message.data.AddOrder;
import eugene.market.ontology.message.data.DeleteOrder;
import eugene.market.ontology.message.data.OrderExecuted;
import eugene.simulation.ontology.Start;
import eugene.simulation.ontology.Stop;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/**
 * Interface for receiving {@link Message}s sent through and received by {@link Session}. Depending on the
 * simulation, {@link Application} can receive {@link Message}s before receiving {@link Start} message. Nevertheless,
 * {@link Application} should not send any {@link Message}s before receiving {@link Start} message; failre to do so
 * may lead to unexpected behaviour.
 *
 * @author Jakub D Kozlowski
 * @since 0.4
 */
public interface Application {

    /**
     * Called when {@link Start} message has been received from the Simulation Agent. This method should be used by
     * {@link Application} implementations to register {@link Behaviour}s with the <code>agent</code>.
     *
     * @param start   {@link Start} message received from the Simulation Agent.
     * @param agent   {@link Agent} that is executing the this <code>session</code>.
     * @param session active {@link Session}.
     */
    void onStart(final Start start, final Agent agent, final Session session);

    /**
     * Called when {@link Stop} message has been received from the Simulation Agent. This method should be used by
     * {@link Application} implementations to unregister {@link Behaviour}s with the <code>agent</code>.
     *
     * @param stop    {@link Stop} message received from the Simulation Agent.
     * @param agent   {@link Agent} that is executing the this <code>session</code>.
     * @param session active {@link Session}.
     */
    void onStop(final Stop stop, final Agent agent, final Session session);

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

    /**
     * This callback receives {@link OrderCancelRequest} before it is sent.
     *
     * @param orderCancelRequest {@link OrderCancelRequest} to send.
     * @param session            {@link Session} that will send this <code>orderCancelRequest</code>.
     */
    void fromApp(final OrderCancelRequest orderCancelRequest, final Session session);
}
