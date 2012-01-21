package eugene.market.client;

import eugene.market.esma.MarketAgent;
import eugene.market.ontology.MarketOntology;
import eugene.market.ontology.Message;
import eugene.market.ontology.message.Logon;
import eugene.market.ontology.message.NewOrderSingle;
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
 * @author Jakub D Kozlowski
 * @since 0.5
 */
public interface Session {

    /**
     * Gets the {@link AID} of the {@link MarketAgent}.
     *
     * @return the {@link AID} of the {@link MarketAgent}.
     */
    AID getMarketAgent();

    /**
     * Gets the application.
     *
     * @return the application.
     */
    Application getApplication();

    /**
     * Gets the symbol.
     *
     * @return the symbol.
     */
    String getSymbol();

    /**
     * Gets the {@link Message} from this <code>aclMessage</code> <code>:content</code> slot.
     *
     * @param aclMessage {@link ACLMessage} to extract from.
     * @param type       type of {@link Message} expected.
     *
     * @return extracted {@link Message}, or <code>null</code> if <code>aclMessage</code> does not have a {@link
     *         Message} of type <code>type</code> in <code>:content</code> slot.
     */
    <T extends Message> T extractMessage(final ACLMessage aclMessage, final Class<T> type);

    /**
     * Gets the {@link ACLMessage} with <code>:receiver</code> slot set to <code>to</code>, <code>:content</code>
     * slot set to <code>message</code> wrapped with {@link Action}, <code>:ontology</code> slot set to {@link
     * MarketOntology#getName()}, <code>:language</code> slot set to {@link MarketOntology#LANGUAGE} and {@link
     * ACLMessage#REQUEST} performative.
     *
     * @param message {@link Message} to set the <code>:content</code> slot to.
     *
     * @return {@link ACLMessage#REQUEST} message.
     *
     * @throws RuntimeException if it is impossible to construct an {@link ACLMessage}.
     */
    ACLMessage aclRequest(final Message message) throws RuntimeException;

    /**
     * Sends this <code>newOrderSingle</code>. If {@link NewOrderSingle#getSymbol()} is null, it will be set
     *
     * @param newOrderSingle {@link NewOrderSingle} to send.
     *
     * @throws NullPointerException     if <code>newOrderSingle</code> is null.
     * @throws IllegalArgumentException if {@link NewOrderSingle#getSymbol()} is not null and it is not equal to the
     *                                  symbol handled by this {@link Session}.
     */
    void send(final NewOrderSingle newOrderSingle) throws NullPointerException, IllegalArgumentException;
}
