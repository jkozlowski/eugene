package eugene.market.client;

import eugene.market.esma.MarketAgent;
import eugene.market.ontology.MarketOntology;
import eugene.market.ontology.Message;
import eugene.market.ontology.field.ClOrdID;
import eugene.market.ontology.field.Symbol;
import eugene.market.ontology.message.Logon;
import eugene.market.ontology.message.NewOrderSingle;
import eugene.market.ontology.message.OrderCancelRequest;
import eugene.simulation.agent.Simulation;
import jade.content.AgentAction;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

/**
 * {@link Session} is a communication channel between the Agents and {@link MarketAgent}.
 *
 * {@link Session} sends an appropriate {@link Logon} message to {@link MarketAgent} in order to initiate the
 * communication channel. {@link Session} will route all {@link Message}s (sent through it and received by it) to
 * the {@link Application}.
 *
 * {@link Session}s are valid for a single <code>symbol</code> only, therefore {@link NewOrderSingle} messages sent
 * through a {@link Session} should not have {@link NewOrderSingle#getSymbol()} equal to a different symbol,
 * than the one handled by this {@link Session}.
 *
 * {@link OrderReference}s returned from {@link #send(NewOrderSingle)} will be automatically updated.
 *
 * @author Jakub D Kozlowski
 * @since 0.5
 */
public interface Session {

    /**
     * Gets the current {@link Simulation}.
     *
     * @return the current {@link Simulation}.
     */
    Simulation getSimulation();

    /**
     * Gets the application.
     *
     * @return the application.
     */
    Application getApplication();

    /**
     * Gets the <code>type</code> message from this <code>aclMessage</code> <code>:content</code> slot.
     *
     * @param aclMessage {@link ACLMessage} to extract from.
     * @param type       type of message expected.
     *
     * @return extracted message, or <code>null</code> if <code>aclMessage</code> does not have a message of type
     *         <code>type</code> in <code>:content</code> slot.
     */
    <T extends AgentAction> T extractMessage(final ACLMessage aclMessage, final Class<T> type);

    /**
     * Gets the {@link ACLMessage} with <code>:receiver</code> slot set to the {@link AID} of the Market Agent,
     * <code>:content</code> slot set to <code>message</code> wrapped with {@link Action},
     * <code>:ontology</code> slot set to {@link MarketOntology#getName()}, <code>:language</code> slot set to {@link
     * MarketOntology#LANGUAGE} and {@link ACLMessage#REQUEST} performative.
     *
     * @param message {@link Message} to set the <code>:content</code> slot to.
     *
     * @return {@link ACLMessage#REQUEST} message.
     *
     * @throws RuntimeException if it was impossible to construct an {@link ACLMessage}.
     */
    ACLMessage aclRequest(final Message message) throws RuntimeException;

    /**
     * Sends this <code>newOrderSingle</code>. {@link Symbol} will be set to the symbol handled by this {@link
     * Session} and a unique {@link ClOrdID} will be generated.
     *
     * @param newOrderSingle {@link NewOrderSingle} to send.
     *
     * @return {@link OrderReference} for this <code>newOrderSingle</code>.
     *
     * @throws NullPointerException     if <code>newOrderSingle</code> is null.
     * @throws IllegalArgumentException if {@link NewOrderSingle#getSymbol()} is not null and it is not equal to the
     *                                  symbol handled by this {@link Session} or {@link NewOrderSingle#getClOrdID()}
     *                                  is not null.
     * @throws RuntimeException         if it was impossible to construct an {@link ACLMessage}.
     */
    OrderReference send(final NewOrderSingle newOrderSingle)
            throws NullPointerException, IllegalArgumentException, RuntimeException;

    /**
     * Sends this <code>orderCancelRequest</code>. {@link Symbol} will be set to the symbol handled by this {@link
     * Session}.
     *
     * @param orderCancelRequest {@link OrderCancelRequest} to send.
     *
     * @throws NullPointerException     if <code>orderCancelRequest</code> is null.
     * @throws IllegalArgumentException if {@link OrderCancelRequest#getSymbol()} is not null and it is not equal to
     *                                  the symbol handled by this {@link Session} or {@link
     *                                  OrderCancelRequest#getClOrdID()} is not null.
     * @throws RuntimeException         if it was impossible to construct an {@link ACLMessage}.
     */
    void send(final OrderCancelRequest orderCancelRequest)
            throws NullPointerException, IllegalArgumentException, RuntimeException;
}
